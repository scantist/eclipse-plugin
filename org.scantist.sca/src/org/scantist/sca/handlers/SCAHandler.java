package org.scantist.sca.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.scantist.ci.Application;
import com.scantist.ci.CommandParameters;

import org.scantist.sca.Activator;
import org.scantist.sca.views.*;

public class SCAHandler extends AbstractHandler {

	int scanID = 0;
	Preferences prefs = new InstanceScope().getNode(Activator.PLUGIN_ID);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		Shell shell = window.getShell();
		// File standard dialog
		FileDialog fileDialog = new FileDialog(shell);
		// Set the text
		fileDialog.setText("Select POM/Gradle File");
		// Set filter on pom or gradle files
		fileDialog.setFilterExtensions(new String[] { "pom.xml", "build.gradle" });
		// Put in a readable name for the filter
		fileDialog.setFilterNames(new String[] { "Pom File (pom.xml)", "Gradle File (build.gradle)" });
		// Open Dialog and save result of selection
		String selected = fileDialog.open();
		File file = new File(selected);
		System.out.println(file.getParent());

		ProjectDetailDialog projectDetailDialog = new ProjectDetailDialog(shell);
		projectDetailDialog.setServerAddress(prefs.get("serverUrl", projectDetailDialog.getServerAddress()));
		projectDetailDialog.setAccessToken(prefs.get("token", projectDetailDialog.getAccessToken()));
		projectDetailDialog.setProjectName(prefs.get("projectName", projectDetailDialog.getProjectName()));
		projectDetailDialog.create();
		if (projectDetailDialog.open() == Window.OK) {
			System.out.println(projectDetailDialog.getServerAddress());
			System.out.println(projectDetailDialog.getAccessToken());
			System.out.println(projectDetailDialog.getProjectName());
			savePluginSettings(projectDetailDialog.getServerAddress(), projectDetailDialog.getAccessToken(),
					projectDetailDialog.getProjectName());

			String[] args = { "-t", projectDetailDialog.getAccessToken(), "-serverUrl",
					projectDetailDialog.getServerAddress(), "-scanType", "source_code", "-f", file.getParent(),
					"-project_name", projectDetailDialog.getProjectName(), "--debug" };

			Job job = new Job("Extract Dependency Tree") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("start task", 100);

					CommandParameters commandParameters = new CommandParameters();
					commandParameters.parseCommandLine(args);
					scanID = new Application().run(commandParameters);
					// sync with UI
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (scanID == 0) {
								MessageDialog.openInformation(window.getShell(), "Error",
										"Cannot trigger SCA scan. Please check log file for detail.");
							} else {
								MessageDialog.openInformation(window.getShell(), "Completed",
										"Scan " + scanID + " is successfully created.");
								try {
									ComponentView componentView = (ComponentView) window.getActivePage()
											.showView("org.scantist.sca.views.ComponentView");
									componentView.setScanID(scanID);
									componentView.refreshInput();
								} catch (PartInitException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});

					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}

		return null;
	}

	private void savePluginSettings(String serverUrl, String token, String projectName) {
		// saves plugin preferences at the workspace level
		Preferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID); // does all the above behind the scenes

		prefs.put("serverUrl", serverUrl);
		prefs.put("token", token);
		prefs.put("projectName", projectName);

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
