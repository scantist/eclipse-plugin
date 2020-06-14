package org.scantist.sca.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;
import org.osgi.service.prefs.Preferences;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.scantist.sca.Activator;
import org.scantist.sca.model.*;

import com.google.gson.Gson;
import com.scantist.ci.models.scan.result.ScanResultComponent;
import com.scantist.ci.models.scan.result.ScanResultResponse;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ComponentView extends ViewPart {
	private final Logger logger = LogManager.getLogger(this.getClass());
	Preferences prefs = new InstanceScope().getNode(Activator.PLUGIN_ID);

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.scantist.sca.views.ComponentView";

	@Inject
	IWorkbench workbench;

	private TableViewer viewer;
	private ComponentTableContentProvider contentProvider;
	private Action refreshAction;
	private int scanID = 0;
	private Gson g = new Gson();
	ComponentModel[] results = new ComponentModel[] {};

	@Override
	public void createPartControl(Composite parent) {
		final GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parent.setLayout(parentLayout);

		viewer = new TableViewer(parent, (SWT.VIRTUAL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL));

		viewer.setUseHashlookup(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		contentProvider = new ComponentTableContentProvider(viewer);
		viewer.setContentProvider(contentProvider);
		this.createColumns();
		this.refreshInput();

		makeActions();
		contributeToActionBars();
	}

	private void createColumns() {
		final NameColumnLabelProvider nameColumnLabelProvider = new NameColumnLabelProvider();
		nameColumnLabelProvider.addColumnTo(viewer);
		final LicenseColumnLabelProvider licenseColumnLabelProvider = new LicenseColumnLabelProvider(300, SWT.LEFT);
		licenseColumnLabelProvider.addColumnTo(viewer);
		final VulnerabilityCountColumnLabelProvider vulnerabilityCountColumnLabelProvider = new VulnerabilityCountColumnLabelProvider(
				150, SWT.CENTER, contentProvider);
		vulnerabilityCountColumnLabelProvider.addColumnTo(viewer);
	}

	public void refreshInput() {
		Job job = new Job("Fetching scan result...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("start task", 100);

				if (!viewer.getTable().isDisposed()) {
					refreshComponentModelList(scanID);
				}

				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						viewer.setItemCount(results.length);
						viewer.setInput(results);
						viewer.refresh();
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

	}

	private void refreshComponentModelList(final int scanID) {
		List<ComponentModel> componentModels = new ArrayList<ComponentModel>();
		if (scanID != 0) {
			componentModels = getScanResults(HttpClientBuilder.create().build(), scanID, 0);
		}
		if (componentModels != null) {
			results = componentModels.toArray(new ComponentModel[componentModels.size()]);
		}
	}

	private List<ComponentModel> getScanResults(HttpClient httpClient, int scanId, int count) {
		List<ComponentModel> componentList = new ArrayList<ComponentModel>();
		if (count == 30) {
			// wait for maximum 30 minutes.
			logger.error("------------");
			logger.error("SCAN TIME OUT!");
			logger.error("------------");
			return componentList;
		}

		String scantistCIToken = prefs.get("token", "");
		String scantistCIScanResultsEndPoint = prefs.get("serverUrl", "https://api.scantist.io/") + "ci-scan-results/";
		HttpGet scanResultRequest = new HttpGet(scantistCIScanResultsEndPoint + "?scan_id=" + scanId);
		logger.info("Query scanResults at: " + scantistCIScanResultsEndPoint);
		scanResultRequest.addHeader("Authorization", scantistCIToken);

		try {

			HttpResponse scanResultResponse = httpClient.execute(scanResultRequest);
			int scanResultResponseStatusCode = scanResultResponse.getStatusLine().getStatusCode();
			String scanResultResult = EntityUtils.toString(scanResultResponse.getEntity(), "UTF-8");
			if (scanResultResponseStatusCode == 200) {
				ScanResultResponse scanResult = g.fromJson(scanResultResult, ScanResultResponse.class);
				if ("finished".equals(scanResult.getStatus())) {
					logger.info("------------");
					logger.info("Scan finished");
					logger.info("------------");

					for (ScanResultComponent c : scanResult.results.components) {
						ComponentModel component = new ComponentModel(c.getLibrary(), c.getVersion(), c.getLicense(),
								new int[] { c.getVulnerabilities(), 0, 0 });
						componentList.add(component);
					}

					return componentList;
				} else if ("failed".equals(scanResult.getStatus())) {
					logger.error("------------");
					logger.error("SCAN FAILED!");
					logger.error("------------");
					return componentList;
				} else {
					logger.info("Scan is running");
					TimeUnit.SECONDS.sleep(10);
					count += 1;
					return getScanResults(httpClient, scanId, count);
				}
			}
			logger.error("------------");
			logger.error("API ERROR!");
			logger.error("------------");
			logger.error(
					"scanResultResponse status: " + scanResultResponseStatusCode + ", detail: " + scanResultResult);
			return componentList;
		} catch (IOException e) {
			logger.error("getScanResults IOException error: \n{}", ExceptionUtils.getStackTrace(e));
		} catch (InterruptedException e) {
			logger.error("getScanResults InterruptedException error: \n{}", ExceptionUtils.getStackTrace(e));
		} finally {
			scanResultRequest.releaseConnection();
		}
		return componentList;
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
	}

	private void makeActions() {
		refreshAction = new Action() {
			public void run() {
				refreshInput();
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh the result");
		refreshAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void setScanID(int scanID) {
		this.scanID = scanID;
	}
}
