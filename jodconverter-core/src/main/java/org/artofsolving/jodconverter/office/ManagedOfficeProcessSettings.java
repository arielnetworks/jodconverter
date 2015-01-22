//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2012 Mirko Nasato and contributors
//
// JODConverter is Open Source software, you can redistribute it and/or
// modify it under either (at your option) of the following licenses
//
// 1. The GNU Lesser General Public License v3 (or later)
//    -> http://www.gnu.org/licenses/lgpl-3.0.txt
// 2. The Apache License, Version 2.0
//    -> http://www.apache.org/licenses/LICENSE-2.0.txt
//
package org.artofsolving.jodconverter.office;

import java.io.File;
import java.io.OutputStream;

import org.artofsolving.jodconverter.process.ProcessManager;
import org.artofsolving.jodconverter.process.PureJavaProcessManager;

class ManagedOfficeProcessSettings {

    public static final long DEFAULT_RETRY_INTERVAL = 250L;

    private final UnoUrl unoUrl;
    private File officeHome = OfficeUtils.getDefaultOfficeHome();
    private String[] runAsArgs;
    private File templateProfileDir;
    private File workDir = new File(System.getProperty("java.io.tmpdir"));
    private ProcessManager processManager = new PureJavaProcessManager();
    private long retryTimeout = DefaultOfficeManagerConfiguration.DEFAULT_RETRY_TIMEOUT;
    private long retryInterval = DEFAULT_RETRY_INTERVAL;
    private OutputStream redirectStdout;
    private OutputStream redirectStderr;
    private boolean killExistingProcess;

    public ManagedOfficeProcessSettings(UnoUrl unoUrl) {
        this.unoUrl = unoUrl;
    }

    public UnoUrl getUnoUrl() {
        return unoUrl;
    }

    public File getOfficeHome() {
        return officeHome;
    }

    public void setOfficeHome(File officeHome) {
        this.officeHome = officeHome;
    }

    public String[] getRunAsArgs() {
		return runAsArgs;
	}

    public void setRunAsArgs(String[] runAsArgs) {
		this.runAsArgs = runAsArgs;
	}

    public File getTemplateProfileDir() {
        return templateProfileDir;
    }

    public void setTemplateProfileDir(File templateProfileDir) {
        this.templateProfileDir = templateProfileDir;
    }

    public File getWorkDir() {
        return workDir;
    }

    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public long getRetryTimeout() {
        return retryTimeout;
    }

    public void setRetryTimeout(long retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public void setRedirectStdout(OutputStream redirectStdout) {
        this.redirectStdout = redirectStdout;
    }

    public OutputStream getRedirectStdout() {
        return redirectStdout;
    }

    public void setRedirectStderr(OutputStream redirectStderr) {
        this.redirectStderr = redirectStderr;
    }

    public OutputStream getRedirectStderr() {
        return redirectStderr;
    }

    public void setKillExistingProcess(boolean killExistingProcess) {
        this.killExistingProcess = killExistingProcess;
    }

    public boolean isKillExistingProcess() {
        return killExistingProcess;
    }
}
