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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.testng.annotations.Test;

@Test(groups="functional")
public class OfficeProcessOutputTest {

    public void dontShowWarnings() throws UnsupportedEncodingException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ManagedOfficeProcessSettings settings = new ManagedOfficeProcessSettings(UnoUrl.socket(2022));
        settings.setRedirectStderr(buf);
        ManagedOfficeProcess proc = new ManagedOfficeProcess(settings);
        try {
            proc.startAndWait();
        } finally {
            proc.stopAndWait();
        }
        assertEquals(buf.toString(Charset.defaultCharset().name()), "");
    }
}
