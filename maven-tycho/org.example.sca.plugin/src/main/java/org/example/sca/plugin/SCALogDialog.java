package org.example.sca.plugin;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Dialog to display SCA scan logs in real-time
 */
public class SCALogDialog extends Dialog {
    
    private Text logText;
    private Button scanButton;
    private Label statusLabel;
    private String projectPath;
    private StringBuilder logContent;
    
    public SCALogDialog(Shell parentShell, String projectPath) {
        super(parentShell);
        this.projectPath = projectPath;
        this.logContent = new StringBuilder();
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("SCA Scan - Software Composition Analysis");
        shell.setSize(800, 600);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));
        
        // Status label
        statusLabel = new Label(container, SWT.NONE);
        statusLabel.setText("Ready to scan project: " + (projectPath != null ? projectPath : "No project selected"));
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        // Scan button
        scanButton = new Button(container, SWT.PUSH);
        scanButton.setText("Start SCA Scan");
        scanButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        
        // Log area
        Label logLabel = new Label(container, SWT.NONE);
        logLabel.setText("Scan Log:");
        logLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        logText = new Text(container, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData logData = new GridData(SWT.FILL, SWT.FILL, true, true);
        logData.heightHint = 400;
        logData.widthHint = 750;
        logText.setLayoutData(logData);
        logText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
        logText.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GREEN));
        logText.setFont(new org.eclipse.swt.graphics.Font(parent.getDisplay(), "Courier New", 9, SWT.NORMAL));
        
        // Initial log message
        appendLog("SCA Scanner ready. Click 'Start SCA Scan' to begin vulnerability analysis...\n");
        
        // Setup scan button action
        scanButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                startScaScan();
            }
        });
        
        return container;
    }
    
    /**
     * Start SCA scan in background thread
     */
    private void startScaScan() {
        if (projectPath == null) {
            appendLog("[ERROR] No project selected for scanning\n");
            return;
        }
        
        // Disable scan button during scan
        scanButton.setEnabled(false);
        scanButton.setText("Scanning...");
        statusLabel.setText("Status: SCA scan in progress...");
        
        // Clear previous log
        logContent.setLength(0);
        logText.setText("");
        appendLog("=== SCA SCAN STARTED ===\n\n");
        
        // Run scan in background thread
        Thread scanThread = new Thread(() -> {
            try {
                String pluginDir = System.getProperty("java.io.tmpdir") + "/eclipse-sca-plugin";
                
                // Create SCA helper with log callback
                SCAHelper scaHelper = new SCAHelper(this::appendLog);
                
                // Download SCA detector if needed
                if (!scaHelper.scaDetectorExists(pluginDir)) {
                    appendLog("SCA detector not found, downloading...\n");
                    if (!scaHelper.downloadScaDetector(pluginDir)) {
                        appendLog("[ERROR] Failed to download SCA detector\n");
                        resetUI();
                        return;
                    }
                } else {
                    appendLog("SCA detector found, proceeding with scan...\n");
                }
                
                // Run the scan
                int exitCode = scaHelper.runScaScan(projectPath, pluginDir);
                
                // Final status
                if (exitCode == 0) {
                    appendLog("\n=== SCA SCAN COMPLETED SUCCESSFULLY ===\n");
                    appendLog("Check the 'sca_report' folder in your project for detailed results.\n");
                } else {
                    appendLog("\n=== SCA SCAN FAILED ===\n");
                    appendLog("Exit code: " + exitCode + "\n");
                }
                
            } catch (Exception ex) {
                appendLog("[ERROR] Exception during scan: " + ex.getMessage() + "\n");
                ex.printStackTrace();
            } finally {
                // Re-enable UI
                resetUI();
            }
        });
        
        scanThread.setDaemon(true);
        scanThread.start();
    }
    
    /**
     * Reset UI after scan completion
     */
    private void resetUI() {
        Display.getDefault().asyncExec(() -> {
            if (!scanButton.isDisposed()) {
                scanButton.setEnabled(true);
                scanButton.setText("Start SCA Scan");
                statusLabel.setText("Scan completed. Ready for next scan.");
            }
        });
    }
    
    /**
     * Append log message to the log area (thread-safe)
     */
    private void appendLog(String message) {
        Display.getDefault().asyncExec(() -> {
            if (!logText.isDisposed()) {
                // Ensure message ends with newline for proper formatting
                String formattedMessage = message;
                if (!formattedMessage.endsWith("\n") && !formattedMessage.endsWith("\r\n")) {
                    formattedMessage += "\n";
                }
                logContent.append(formattedMessage);
                logText.setText(logContent.toString());
                // Auto-scroll to bottom
                logText.setSelection(logText.getCharCount());
            }
        });
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // Only create Close button
        createButton(parent, CANCEL, "Close", true);
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(800, 600);
    }
}
