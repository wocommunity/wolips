package org.objectstyle.woproject;
/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

import java.util.*;

import junit.framework.*;

/** 
 * Test runner to run batches of test suites. 
 * 
 * @author Andrei Adamchik
 */
public class WOProjectRunner extends junit.textui.TestRunner {
    protected ArrayList suites = new ArrayList();
    protected ArrayList statsList = new ArrayList();
    protected int failureCount;
    protected int errorCount;


    /** 
     * Runs all internal test suites.
     * 
     * @return <code>true</code> if all tests succeeded, 
     * <code>false</code> otherwise.
     */
    public boolean runAll() {
        failureCount = 0;
        errorCount = 0;
        
        Iterator it = suites.iterator();
        while (it.hasNext()) {
            runSuite((TestSuite) it.next());
        }
        printAll();
        return !hasFailures();
    }
    

    /** Adds TestSuite to the list of suites. */
    public void addSuite(TestSuite suite) {
        suites.add(suite);
    }

    /** 
     * Returns true if there were either errors or failures
     * during test run. 
     */
    public boolean hasFailures() {
        return (errorCount + failureCount) > 0;
    }

    /** Prints all test results to stdout. */
    public void printAll() {
        writer().println();
        writer().println();
        writer().println("Test Runs");
        writer().println("======================================================");

        Iterator it = statsList.iterator();
        int runCount = 0;
        failureCount = 0;
        errorCount = 0;
        long totalTime = 0;
        while (it.hasNext()) {
            SuiteStatistics stats = (SuiteStatistics) it.next();
            writer().print("Suite: " + stats.suiteName);
            writer().println(" - " + elapsedTimeAsString(stats.elapsedTime) + " sec.");
            runCount += stats.result.runCount();
            failureCount += stats.result.failureCount();
            errorCount += stats.result.errorCount();
            totalTime += stats.elapsedTime;
            print(stats.result);
        }

        writer().println();
        writer().println("Totals:");
        writer().println("======================================================");
        writer().println("Test Runs: " + runCount);
        writer().println("Error count: " + errorCount);
        writer().println("Failure count: " + failureCount);
        writer().println("Total time: " + elapsedTimeAsString(totalTime) + " sec.");
        writer().println();
    }

    public void runSuite(TestSuite suite) {
        TestResult result = createTestResult();
        result.addListener(this);

        long startTime = System.currentTimeMillis();
        suite.run(result);
        long endTime = System.currentTimeMillis();

        SuiteStatistics stats = new SuiteStatistics();
        stats.result = result;
        stats.elapsedTime = endTime - startTime;
        stats.suiteName = suite.getName();
        statsList.add(stats);
    }

    public void printErrors(TestResult result) {
        if (result.errorCount() != 0) {
            if (result.errorCount() == 1)
                writer().println("There was " + result.errorCount() + " error:");
            else
                writer().println("There were " + result.errorCount() + " errors:");

            int i = 1;
            for (Enumeration e = result.errors(); e.hasMoreElements(); i++) {
                TestFailure failure = (TestFailure) e.nextElement();
                writer().println(i + ") " + failure.failedTest());
                writer().print(getFilteredTrace(failure.thrownException()));
            }
        }
    }

    public void printFailures(TestResult result) {
        if (result.failureCount() != 0) {
            if (result.failureCount() == 1)
                writer().println("There was " + result.failureCount() + " failure:");
            else
                writer().println("There were " + result.failureCount() + " failures:");
            int i = 1;
            for (Enumeration e = result.failures(); e.hasMoreElements(); i++) {
                TestFailure failure = (TestFailure) e.nextElement();
                writer().print(i + ") " + failure.failedTest());
                writer().print(getFilteredTrace(failure.thrownException()));
            }
        }
    }

    public void printHeader(TestResult result) {}

    class SuiteStatistics {
        TestResult result;
        long elapsedTime;
        String suiteName;
    }
}