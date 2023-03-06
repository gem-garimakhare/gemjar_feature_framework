package com.gemini.generic.tdd;

import com.gemini.generic.api.utils.ProjectSampleJson;
import com.gemini.generic.exception.GemException;
import com.gemini.generic.reporting.GemTestReporter;
import com.gemini.generic.ui.utils.DriverManager;
import com.gemini.generic.utils.CommonExceptionHandler;
import com.gemini.generic.utils.CommonUtils;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.gemini.generic.utils.GemJarUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;

@Listeners(GemJarTestngTestFilter.class)
public class GemjarTestngBase extends GemJarUtils {

    /*
    1 . validate jewel credentials, user
     */
    @BeforeSuite
    public void beforeSuite(ITestContext iTestContext, @Optional String reportType, @Optional String reportName) throws GemException, IOException {
        if (reportType != null) {
            GemJarGlobalVar.report_type = reportType;
        }
        CommonUtils.updateResourceMap();
        initializeGemJARGlobalVariables();
        GemJarGlobalVar.expected_testcases = iTestContext.getSuite().getAllMethods().size();
        if (reportName != null) {
            GemJarGlobalVar.reportName = reportName;
        } else {
            GemJarGlobalVar.reportName = GemJarGlobalVar.reportName != null ? GemJarGlobalVar.reportName : "GemjarTestReport";
        }
        ProjectSampleJson.loadSampleJson();
        GemTestReporter.startSuite(GemJarGlobalVar.projectName, GemJarGlobalVar.environment, GemJarGlobalVar.s_run_id);
    }

    @Parameters("browserName")
    @BeforeTest
    public void beforeTest(@Optional String browserName) {
        if (browserName != null) {
            GemJarGlobalVar.browserInTest = browserName;
        }

    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        String testcaseName = method.getName();
        String testClassName = method.getDeclaringClass().getSimpleName();
        GemTestReporter.startTestCase(testcaseName, testClassName, false);
        CommonExceptionHandler exceptionHandler = new CommonExceptionHandler();
        exceptionHandler.setThreadName(testcaseName);
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler.getUncaughtExceptionHandler());
        TestCaseData.setCurrentTestCaseData(testcaseName);
    }

    @AfterMethod
    public void afterMethod(ITestResult result) throws GemException {
        DriverManager.quitDriver();
        GemTestReporter.endTestCase();

    }

    @AfterSuite
    public void afterSuite() throws GemException {
        try {
            System.out.println("in after suite");
            GemTestReporter.endSuite(GemJarGlobalVar.reportLocation);
        } catch (Exception e) {
            throw new GemException(e.getMessage());
        }
    }
}
