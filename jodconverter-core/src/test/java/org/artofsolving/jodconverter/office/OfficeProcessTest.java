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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.artofsolving.jodconverter.ReflectionUtils;
import org.artofsolving.jodconverter.process.LinuxProcessManager;
import org.artofsolving.jodconverter.process.ProcessManager;
import org.artofsolving.jodconverter.process.ProcessQuery;
import org.artofsolving.jodconverter.util.PlatformUtils;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test
public class OfficeProcessTest {

    public void dontShowWarnings() throws IOException {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        ManagedOfficeProcessSettings settings = new ManagedOfficeProcessSettings(UnoUrl.socket(2022));
        settings.setRedirectStdout(stdout);
        settings.setRedirectStderr(stderr);
        ManagedOfficeProcess proc = new ManagedOfficeProcess(settings);
        try {
            proc.startAndWait();
        } finally {
            proc.stopAndWait();
        }
        assertEquals(stdout.toByteArray(), new byte[0]);
        assertEquals(stderr.toByteArray(), new byte[0]);
    }

    public void pumpOutput() throws IOException, InterruptedException {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        ManagedOfficeProcessSettings settings = new ManagedOfficeProcessSettings(UnoUrl.socket(2022));
        settings.setRedirectStdout(stdout);
        settings.setRedirectStderr(stderr);
        ManagedOfficeProcess proc = new ManagedOfficeProcess(settings, new String[]{"--version", "--headless"});
        try {
            proc.startAndWait();
        } catch (OfficeException e) {
            // ignore
        } finally {
            proc.stopAndWait();
        }

        String executable = OfficeUtils.getOfficeExecutable(OfficeUtils.getDefaultOfficeHome()).getAbsolutePath();
        Process expected = new ProcessBuilder(executable, "--version", "--headless").start();
        expected.waitFor();

        assertEquals(stdout.toByteArray(), readFully(expected.getInputStream()));
        assertEquals(stderr.toByteArray(), readFully(expected.getErrorStream()));
    }

    public void foundExistingProcessAndKill_Linux() throws Exception {
        if (!PlatformUtils.isLinux()) {
            throw new SkipException("LinuxProcessManager can only be tested on Linux");
        }

        UnoUrl unoUrl = UnoUrl.socket(2022);
        ProcessQuery processQuery = new ProcessQuery("soffice.bin", unoUrl.getAcceptString());
        ManagedOfficeProcessSettings settings = new ManagedOfficeProcessSettings(unoUrl);
        settings.setProcessManager(new LinuxProcessManager());
        settings.setKillExistingProcess(true);
        ManagedOfficeProcess proc = new ManagedOfficeProcess(settings);
        try {
            proc.startAndWait();

            long procPid = settings.getProcessManager().findPid(processQuery);
            assertNotEquals(procPid, ProcessManager.PID_NOT_FOUND);
            assertNotEquals(procPid, ProcessManager.PID_UNKNOWN);

            ManagedOfficeProcess proc2 = new ManagedOfficeProcess(settings);
            try {
                proc2.startAndWait();
                long proc2Pid = settings.getProcessManager().findPid(processQuery);
                assertNotEquals(proc2Pid, ProcessManager.PID_NOT_FOUND);
                assertNotEquals(proc2Pid, ProcessManager.PID_UNKNOWN);
                assertFalse(((OfficeProcess) ReflectionUtils.getPrivateField(proc, "process")).isRunning());
            } finally {
                proc2.stopAndWait();
            }

        } finally {
            proc.stopAndWait();
        }
    }

    public void foundExistingProcessAndError_Linux() throws Exception {
        if (!PlatformUtils.isLinux()) {
            throw new SkipException("LinuxProcessManager can only be tested on Linux");
        }

        UnoUrl unoUrl = UnoUrl.socket(2022);
        ProcessQuery processQuery = new ProcessQuery("soffice.bin", unoUrl.getAcceptString());
        ManagedOfficeProcessSettings settings = new ManagedOfficeProcessSettings(unoUrl);
        settings.setProcessManager(new LinuxProcessManager());
        settings.setKillExistingProcess(false);
        ManagedOfficeProcess proc = new ManagedOfficeProcess(settings);
        try {
            proc.startAndWait();

            long procPid = settings.getProcessManager().findPid(processQuery);
            assertNotEquals(procPid, ProcessManager.PID_NOT_FOUND);
            assertNotEquals(procPid, ProcessManager.PID_UNKNOWN);

            ManagedOfficeProcess proc2 = new ManagedOfficeProcess(settings);
            try {
                proc2.startAndWait();
                fail();
            } catch (OfficeException e) {
                // ok
            } finally {
                try {
                    proc2.stopAndWait();
                } catch (Exception e) {}
            }

        } finally {
            proc.stopAndWait();
        }
    }

    private static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (int n = in.read(); n >= 0; n = in.read()) {
            bytes.write(n);
        }
        return bytes.toByteArray();
    }
}
