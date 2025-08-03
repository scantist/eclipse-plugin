package org.example.sca.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.example.sca.plugin.SCAHelper;
import org.example.sca.plugin.SCALogDialog;

/**
 * Handler for the SCA Scan command.
 * Performs actual SCA scanning with real-time log display.
 */
public class ScaScanHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            // Get current project path
            String projectPath = SCAHelper.getCurrentProjectPath();
            
            if (projectPath == null) {
                MessageDialog.openWarning(
                    HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
                    "SCA Scan",
                    "No open project found. Please open a project first to perform SCA scanning."
                );
                return null;
            }
            
            // Open SCA scan dialog with real-time logging
            SCALogDialog scanDialog = new SCALogDialog(
                HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
                projectPath
            );
            
            scanDialog.open();
            
        } catch (Exception e) {
            MessageDialog.openError(
                HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
                "SCA Scan Error",
                "An error occurred while starting SCA scan: " + e.getMessage()
            );
            e.printStackTrace();
        }
        
        return null;
    }
}
