package com.gemini.generic.api.utils;

import com.gemini.generic.reporting.GemTestReporter;
import com.gemini.generic.tdd.TestCaseData;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.gemini.generic.utils.GemJarUtils;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;

public class GemJarHealthCheckBase {
    @BeforeSuite
    public void beforeSuite(ITestContext iTestContext) throws IOException {
        GemJarUtils.initializeGemJARGlobalVariables();
        GemTestReporter.startSuite(GemJarGlobalVar.projectName, GemJarGlobalVar.environment, GemJarGlobalVar.s_run_id);
    }

    @BeforeTest
    public void beforeTest() {
    }

    @BeforeClass
    public void beforeClass() {
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        String testcaseName = method.getName();
        String testClassName = method.getClass().getSimpleName();
        TestCaseData.setCurrentTestCaseData(testcaseName);
    }

    @AfterMethod
    public void afterMethod() {

    }

    @AfterClass
    public void afterClass() {

    }

    @AfterTest
    public void afterTest() {
    }

    @AfterSuite
    public void afterSuite() {
        try {
            GemTestReporter.endSuite(GemJarGlobalVar.reportLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}