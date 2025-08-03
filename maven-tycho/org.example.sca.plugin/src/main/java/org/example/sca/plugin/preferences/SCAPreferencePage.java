package org.example.sca.plugin.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.example.sca.plugin.Activator;

/**
 * SCA Plugin Preferences Page
 * Provides UI for configuring DevSecOps token and import URL
 */
public class SCAPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public SCAPreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Configure SCA (Software Composition Analysis) scanning settings");
    }

    @Override
    public void createFieldEditors() {
        // DevSecOps Token field
        addField(new StringFieldEditor(
            SCAPreferences.DEVSECOPS_TOKEN,
            "DevSecOps Token:",
            getFieldEditorParent()
        ) {
            @Override
            protected void doFillIntoGrid(org.eclipse.swt.widgets.Composite parent, int numColumns) {
                super.doFillIntoGrid(parent, numColumns);
                // Make the text field use password echo character for security
                getTextControl().setEchoChar('*');
            }
        });

        // DevSecOps Import URL field
        addField(new StringFieldEditor(
            SCAPreferences.DEVSECOPS_IMPORT_URL,
            "DevSecOps Import URL:",
            getFieldEditorParent()
        ));
    }

    @Override
    public void init(IWorkbench workbench) {
        // Initialize the preference page
    }

    @Override
    public boolean performOk() {
        boolean result = super.performOk();
        
        if (result) {
            // Show confirmation message
            org.eclipse.jface.dialogs.MessageDialog.openInformation(
                getShell(),
                "SCA Settings",
                "SCA plugin settings have been saved successfully."
            );
        }
        
        return result;
    }
}
