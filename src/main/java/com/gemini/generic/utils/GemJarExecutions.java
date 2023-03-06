package com.gemini.generic.utils;

import com.gemini.generic.api.utils.ApiInvocationImpl;
import com.gemini.generic.reporting.GemEcoUpload;
import com.gemini.generic.reporting.GemTestReporter;
import io.github.classgraph.ResourceList;
import org.testng.TestNG;
import org.testng.xml.internal.Parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

class GemJarExecutions {

    static void executeGemJar() {
        ResourceList testngResource = CommonUtils.getFilesWithExtensionMap("xml").get("testng.xml");
        String jsonApiHealthCheck = System.getProperty("cucumberApiHealthCheck");
        if (jsonApiHealthCheck != null &&
                (jsonApiHealthCheck.equalsIgnoreCase("y") ||
                        jsonApiHealthCheck.equalsIgnoreCase("true"))) {

            GemJarExecutions.testHealthCheckWithJSON();
        } else if (testngResource != null) {
            GemJarExecutions.runTestNG();
        } else {
            GemJarExecutions.runCucmberProject();
        }


    }

    static void runCucmberProject() {
        String runMode = System.getProperty("run_mode");


        String threadCount = System.getProperty("thread-count");

        threadCount = runMode != null ? runMode.equals("Sequence") ? "1" : threadCount != null ? threadCount : "5" : "5";


        String cucumberTestng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n" +
                "<suite name=\"All TestCases\"  parallel=\"methods\" data-provider-thread-count=\"" + threadCount + "\" verbose=\"10\">\n" +
                "    <test name=\"JewelAPI\">\n" +
                "        <classes>\n" +
                "            <class name=\"com.gemini.generic.bdd.GemJarCucumberBase\"/>\n" +
                "        </classes>\n" +
                "    </test>\n" +
                "</suite>";
        GemJarGlobalVar.jarexecution = true;
        TestNG testNG = new TestNG();
        testNG.setUseDefaultListeners(false);
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(cucumberTestng.getBytes());
            testNG.setXmlSuites(new Parser(is).parseToList());
            testNG.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static void runTestNG() {
        GemJarGlobalVar.jarexecution = true;
        TestNG testNG = new TestNG();
        testNG.setUseDefaultListeners(false);
        try {
            String testngString = CommonUtils.getFilesWithExtensionMap("xml").get("testng.xml").get(0).getContentAsString();
            ByteArrayInputStream is = new ByteArrayInputStream(testngString.getBytes());
            testNG.setXmlSuites(new Parser(is).parseToList());
            testNG.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    static void testHealthCheckWithJSON() {
        String projectName = System.getProperty("projectName");
        if (projectName == null) {
            projectName = "GemJar";
        }

        String env = System.getProperty("env");
        if (env == null) {
            env = "BETA";
        }

        String reportLocation = System.getProperty("loc");
        if (reportLocation == null) {
            reportLocation = System.getProperty("user.home") + "/GemJar";

        }

        GemTestReporter.startSuite(projectName, env, projectName + env + UUID.randomUUID());

        String path = System.getProperty("path");

        if (!(path == null)) {
            File fr = null;
            try {
                fr = new File(path);

                ApiInvocationImpl.healthCheck(fr);
                GemTestReporter.endSuite(reportLocation);
                GemEcoUpload.postNewRecord();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("Path cannot be null. Enter a valid file path");
        }
    }

}
