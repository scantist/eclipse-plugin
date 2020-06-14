package org.scantist.sca.handlers;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ProjectDetailDialog extends TitleAreaDialog {

    private Text txtServerAddress;
    private Text txtAccessToken;
    private Text txtProjectName;

    private String serverAddress = "https://api.scantist.io/";
    private String accessToken = "";
    private String projectName = "";

    public ProjectDetailDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Project Detail");
        setMessage("Please enter integration detail with Scantist SCA", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createServerAddress(container);
        createAccessToken(container);
        createProjectName(container);

        return area;
    }

    private void createServerAddress(Composite container) {
        Label lbtServerAddress = new Label(container, SWT.NONE);
        lbtServerAddress.setText("Server Address:");

        GridData dataServerAddress = new GridData();
        dataServerAddress.grabExcessHorizontalSpace = true;
        dataServerAddress.horizontalAlignment = GridData.FILL;

        txtServerAddress = new Text(container, SWT.BORDER);
        txtServerAddress.setText(serverAddress);
        txtServerAddress.setLayoutData(dataServerAddress);
    }

    private void createAccessToken(Composite container) {
        Label lbtAccessToken = new Label(container, SWT.NONE);
        lbtAccessToken.setText("Access Token:");

        GridData dataAccessToken = new GridData();
        dataAccessToken.grabExcessHorizontalSpace = true;
        dataAccessToken.horizontalAlignment = GridData.FILL;
        txtAccessToken = new Text(container, SWT.BORDER);
        txtAccessToken.setText(accessToken);
        txtAccessToken.setLayoutData(dataAccessToken);
    }
    
    private void createProjectName(Composite container) {
        Label lbtProjectName = new Label(container, SWT.NONE);
        lbtProjectName.setText("Project Name:");

        GridData dataProjectName = new GridData();
        dataProjectName.grabExcessHorizontalSpace = true;
        dataProjectName.horizontalAlignment = GridData.FILL;
        txtProjectName = new Text(container, SWT.BORDER);
        txtProjectName.setText(projectName);
        txtProjectName.setLayoutData(dataProjectName);
    }



    @Override
    protected boolean isResizable() {
        return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
    	serverAddress = txtServerAddress.getText();
    	accessToken = txtAccessToken.getText();
    	projectName = txtProjectName.getText();

    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getAccessToken() {
        return accessToken;
    }
    
    public String getProjectName() {
        return projectName;
    }

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
    
    
}