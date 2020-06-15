package org.scantist.sca.model;

public class ComponentModel {
	private final String name;
	private final String version;
    private final String license;
    private final int vulnerabilityCount;

    public ComponentModel(final String name, final String version, final String license, final int vulnerabilityCount) {
    	this.name = name;
    	this.version = version;
        this.license = license;
        this.vulnerabilityCount = vulnerabilityCount;
    }

    public String getVersion() {
    	return version;
    }
    
    public String getLicense() {
        return license;
    }

    public int getVulnerabilityCount() {
        return vulnerabilityCount;
    }

    public String getName() {
        return name;
    }

}