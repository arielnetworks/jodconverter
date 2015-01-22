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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.testng.annotations.Test;

@Test
public class OfficeProcessOutputTest {

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

    public void checkVersion() throws IOException, InterruptedException {
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

    private static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (int n = in.read(); n >= 0; n = in.read()) {
            bytes.write(n);
        }
        return bytes.toByteArray();
    }
}
