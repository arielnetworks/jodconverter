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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.annotations.Test;

@Test
public class RetryableTest {
    public void noRetry() throws Exception {
        try {
            new CountDown(0).execute(0, 10, 0);
        } catch (RetryTimeoutException e) {
            fail();
        }
    }

    public void success() throws Exception {
        try {
            new CountDown(2).execute(0, 100, 200);
        } catch (RetryTimeoutException e) {
            fail();
        }
    }

    public void timeout() throws Exception {
        try {
            new CountDown(2).execute(0, 100, 50);
            fail();
        } catch (RetryTimeoutException e) {
            // ok
        }
    }

    public void delay() throws Exception {
        try {
            new CountDown(2).execute(100, 10, 50);
            fail();
        } catch (RetryTimeoutException e) {
            // ok
        }
    }

    public void interruptedSuccess() throws Exception {
        final boolean[] timeout = new boolean[] {false};
        final boolean[] interrupted = new boolean[] {false};
        Thread th = new Thread() {
            public void run() {
                try {
                    new CountDown(2).execute(0, 100, 200);
                } catch (RetryTimeoutException e) {
                    timeout[0] = true;
                } catch (Exception e) {
                    assert false : e;
                }
                interrupted[0] = Thread.currentThread().interrupted();
            }
        };
        th.start();
        th.interrupt();
        th.join();
        assertFalse(timeout[0]);
        assertTrue(interrupted[0]);
    }

    public void interruptedTimeout() throws Exception {
        final boolean[] timeout = new boolean[] {false};
        final boolean[] interrupted = new boolean[] {false};
        Thread th = new Thread() {
            public void run() {
                try {
                    new CountDown(3).execute(0, 100, 50);
                } catch (RetryTimeoutException e) {
                    timeout[0] = true;
                } catch (Exception e) {
                    assert false : e;
                }
                interrupted[0] = Thread.currentThread().interrupted();
            }
        };
        th.start();
        th.interrupt();
        th.join();
        assertTrue(timeout[0]);
        assertTrue(interrupted[0]);
    }

    private static class CountDown extends Retryable {
        private int n;
        public CountDown(int n) {
            this.n = n;
        }
        protected void attempt() throws TemporaryException {
            if (n == 0) {
                return;
            }
            n--;
            throw new TemporaryException(new Exception());
        }
    }
}
