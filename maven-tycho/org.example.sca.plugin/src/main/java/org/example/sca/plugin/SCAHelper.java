package org.example.sca.plugin;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.example.sca.plugin.preferences.SCAPreferences;

/**
 * SCA Helper class for Eclipse plugin
 * Handles SCA scanning functionality including download, execution, and logging
 */
public class SCAHelper {
    
    private static final String BOM_DETECT_JAR_NAME = "sca-bom-detect.jar";
    private static final String BOM_DETECTOR_URL = "https://download.scantist.io/" + BOM_DETECT_JAR_NAME;
    
    private Consumer<String> logCallback;
    
    public SCAHelper(Consumer<String> logCallback) {
        this.logCallback = logCallback != null ? logCallback : msg -> System.out.println("[SCA] " + msg);
    }
    
    /**
     * Log message with callback
     */
    private void log(String message) {
        String logMessage = "[SCA] " + message;
        if (logCallback != null) {
            logCallback.accept(logMessage);
        }
        System.out.println(logMessage);
    }
    
    /**
     * Download SCA detector JAR if not exists
     */
    public boolean downloadScaDetector(String targetDir) {
        try {
            Path targetPath = Paths.get(targetDir, BOM_DETECT_JAR_NAME);
            
            // Check if already exists
            if (Files.exists(targetPath)) {
                log("SCA detector already exists at: " + targetPath);
                return true;
            }
            
            // Create directory if not exists
            Files.createDirectories(Paths.get(targetDir));
            
            log("Downloading SCA detector to: " + targetPath);
            
            URL url = new URL(BOM_DETECTOR_URL);
            try (InputStream inputStream = url.openStream();
                 FileOutputStream outputStream = new FileOutputStream(targetPath.toFile())) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            log("SCA detector download completed successfully");
            return true;
            
        } catch (Exception e) {
            log("Error downloading SCA detector: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Run SCA scan on project with DevSecOps integration
     */
    public int runScaScan(String projectPath, String pluginDir) {
        try {
            String jarPath = Paths.get(pluginDir, BOM_DETECT_JAR_NAME).toString();
            String reportDir = projectPath + "/sca_report";
            
            log("Starting SCA scan on project: " + projectPath);
            log("Using SCA detector: " + jarPath);
            
            // Get DevSecOps settings from preferences
            String devsecopsToken = SCAPreferences.getDevSecOpsToken();
            String devsecopsImportUrl = SCAPreferences.getDevSecOpsImportUrl();
            
            log("DevSecOps Token: " + (devsecopsToken.isEmpty() ? "[NOT SET - Local scan only]" : "[SET]"));
            log("DevSecOps Import URL: " + devsecopsImportUrl);
            
            // Clean old reports
            cleanReportFolder(reportDir);
            
            // Build command with DevSecOps integration
            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-jar");
            command.add(jarPath);
            command.add("-f");
            command.add(projectPath);
            command.add("--debug");
            command.add("-report");
            command.add("json");
            
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            
            // Set DevSecOps parameters as environment variables (not command-line args)
            Map<String, String> env = processBuilder.environment();
            if (!devsecopsToken.isEmpty()) {
                env.put("DEVSECOPS_TOKEN", devsecopsToken);
                log("DevSecOps integration enabled - results will be uploaded");
                
                if (!devsecopsImportUrl.isEmpty()) {
                    env.put("DEVSECOPS_IMPORT_URL", devsecopsImportUrl);
                }
            } else {
                log("DevSecOps integration disabled - local scan only");
            }
            
            processBuilder.directory(new File(projectPath));
            processBuilder.redirectErrorStream(false);
            
            log("Executing SCA scan command...");
            Process process = processBuilder.start();
            
            // Handle stdout in separate thread
            Thread stdoutThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log("STDOUT: " + line);
                    }
                } catch (IOException e) {
                    log("Error reading stdout: " + e.getMessage());
                }
            });
            stdoutThread.start();
            
            // Handle stderr in separate thread
            Thread stderrThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log("STDERR: " + line);
                    }
                } catch (IOException e) {
                    log("Error reading stderr: " + e.getMessage());
                }
            });
            stderrThread.start();
            
            // Wait for process completion
            int exitCode = process.waitFor();
            
            // Wait for log threads to complete
            stdoutThread.join(5000); // 5 second timeout
            stderrThread.join(5000);
            
            log("SCA scan completed with exit code: " + exitCode);
            
            if (exitCode == 0) {
                log("SCA scan successful! Check report at: " + reportDir);
            } else {
                log("SCA scan failed with exit code: " + exitCode);
            }
            
            return exitCode;
            
        } catch (Exception e) {
            log("Error running SCA scan: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Clean old report folder
     */
    private void cleanReportFolder(String reportDir) {
        try {
            Path reportPath = Paths.get(reportDir);
            if (Files.exists(reportPath)) {
                log("Cleaning old reports from: " + reportDir);
                Files.walk(reportPath)
                    .sorted((a, b) -> b.compareTo(a)) // Reverse order to delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log("Warning: Could not delete " + path + ": " + e.getMessage());
                        }
                    });
                log("Report cleanup completed");
            } else {
                log("No existing report folder to clean");
            }
        } catch (Exception e) {
            log("Error cleaning report folder: " + e.getMessage());
        }
    }
    
    /**
     * Check if SCA detector exists
     */
    public boolean scaDetectorExists(String pluginDir) {
        Path jarPath = Paths.get(pluginDir, BOM_DETECT_JAR_NAME);
        return Files.exists(jarPath);
    }
    
    /**
     * Get current workspace project path
     */
    public static String getCurrentProjectPath() {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        if (projects.length > 0) {
            // Return the first open project
            for (IProject project : projects) {
                if (project.isOpen()) {
                    return project.getLocation().toString();
                }
            }
        }
        return null;
    }
}
