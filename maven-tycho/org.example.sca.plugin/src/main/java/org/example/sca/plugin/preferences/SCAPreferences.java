package org.example.sca.plugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.example.sca.plugin.Activator;

/**
 * SCA Plugin Preferences Store
 * Manages configuration for DevSecOps token and import URL
 */
public class SCAPreferences extends AbstractPreferenceInitializer {
    
    // Preference keys
    public static final String DEVSECOPS_TOKEN = "devsecops_token";
    public static final String DEVSECOPS_IMPORT_URL = "devsecops_import_url";
    
    // Default values
    public static final String DEFAULT_DEVSECOPS_TOKEN = "";
    public static final String DEFAULT_DEVSECOPS_IMPORT_URL = "https://api-app.scantist.io/v2/scans/ci-scan/";
    
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(DEVSECOPS_TOKEN, DEFAULT_DEVSECOPS_TOKEN);
        store.setDefault(DEVSECOPS_IMPORT_URL, DEFAULT_DEVSECOPS_IMPORT_URL);
    }
    
    /**
     * Get DevSecOps token from preferences
     */
    public static String getDevSecOpsToken() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        return store.getString(DEVSECOPS_TOKEN);
    }
    
    /**
     * Set DevSecOps token in preferences
     */
    public static void setDevSecOpsToken(String token) {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(DEVSECOPS_TOKEN, token);
    }
    
    /**
     * Get DevSecOps import URL from preferences
     */
    public static String getDevSecOpsImportUrl() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        return store.getString(DEVSECOPS_IMPORT_URL);
    }
    
    /**
     * Set DevSecOps import URL in preferences
     */
    public static void setDevSecOpsImportUrl(String url) {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(DEVSECOPS_IMPORT_URL, url);
    }
}
