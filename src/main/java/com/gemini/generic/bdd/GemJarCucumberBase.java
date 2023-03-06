package com.gemini.generic.bdd;


import com.gemini.generic.api.utils.ProjectSampleJson;
import com.gemini.generic.exception.GemException;
import com.gemini.generic.reporting.GemTestReporter;
import com.gemini.generic.ui.utils.DriverManager;
import com.gemini.generic.utils.*;
import io.cucumber.java.*;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.github.classgraph.Resource;
import org.apache.commons.io.FileUtils;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;


public class GemJarCucumberBase extends AbstractTestNGCucumberTests {

    @BeforeSuite

    public void beforeSuite(ITestContext iTestContext) {
        GemJarGlobalVar.cucumberFlag = true;
        CommonUtils.updateResourceMap();
        GemJarGlobalVar.expected_testcases = iTestContext.getSuite().getAllMethods().size();
        GemJarUtils.loadGemJarConfigData();
        setCucumberProperties();
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    private void setCucumberProperties() {
        String stepDefinitionPackages = GemJarUtils.getGemJarKeyValue(GemJarConstants.GLUE_CODE);
        System.setProperty(GemJarConstants.CUCUMBER_GLUE, "com.gemini.generic.bdd," + stepDefinitionPackages);
        setFeatureFilePath();
        String tags = GemJarUtils.getGemJarKeyValue(GemJarConstants.CUCUMBER_TAGS_NAME);
        if (tags != null) {
            System.setProperty(GemJarConstants.CUCUMBER_TAGS, tags);
        }
        String scenario = GemJarUtils.getGemJarKeyValue(GemJarConstants.Scenario);
        if (scenario != null) {
            System.setProperty(GemJarConstants.CUCUMBER_SCENARIO_NAME, scenario);
        }
    }

    private void setFeatureFilePath() {
        String featureFilePath = GemJarUtils.getGemJarKeyValue(GemJarConstants.FEATURE_FILE_PATH);
        if (featureFilePath != null) {
            System.setProperty(GemJarConstants.CUCUMBER_FEATURE, featureFilePath);
        } else {

          /*  if(GemJarGlobalVar.lambdaExecution){
                setFeatureForLambda();
            }else{*/
            String features = "";
            for (String feature : CommonUtils.getFilesWithExtension("feature")) {
                features = features + feature + ",";
            }
            features = features.substring(0, features.lastIndexOf(","));
            System.setProperty(GemJarConstants.CUCUMBER_FEATURE, features);
            System.out.println(System.getProperty(GemJarConstants.CUCUMBER_FEATURE));
            //}
        }

    }

    private void setFeatureForLambda() {
        String systemFeatures = "";
        for (String featureFileName : CommonUtils.getFilesWithExtensionMap("feature").keySet()) {
            try {
                String featureFilePath = "/tmp/" + featureFileName;
                File featureFile = new File(featureFilePath);
                String fileName = featureFile.getName();
                File newFeatureFile = new File("/tmp/" + fileName);
                Resource featureResource = CommonUtils.getFilesWithExtensionMap("feature").get(featureFileName).get(0);
                String dataFromFeatureJson = featureResource.getContentAsString();
                newFeatureFile.createNewFile();
                FileUtils.writeStringToFile(newFeatureFile, dataFromFeatureJson, Charset.defaultCharset());
                systemFeatures = systemFeatures + newFeatureFile.getAbsolutePath() + ",";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.setProperty(GemJarConstants.CUCUMBER_FEATURE, systemFeatures.substring(0, systemFeatures.lastIndexOf(",")));
    }

    @BeforeAll(order = 1)
    public static void before_all() throws IOException {
        CommonUtils.updateResourceMap();
        GemJarGlobalVar.cucumberFlag = true;
        GemJarUtils.initializeGemJARGlobalVariables();
        GemJarGlobalVar.reportName = GemJarGlobalVar.reportName != null ? GemJarGlobalVar.reportName : "GemjarTestReport";
        ProjectSampleJson.loadSampleJson();
        GemTestReporter.startSuite(GemJarGlobalVar.projectName, GemJarGlobalVar.environment, GemJarGlobalVar.s_run_id);
    }

    @Before(order = 1)
    public void before(Scenario scenario) {
        String testcaseName = scenario.getName();
        String featureFileName = getScenarioFeatureFileName(scenario);
        GemTestReporter.startTestCase(testcaseName, featureFileName.substring(0, featureFileName.lastIndexOf('.')),
                false);
        CommonExceptionHandler exceptionHandler = new CommonExceptionHandler();
        exceptionHandler.setThreadName(testcaseName);
        Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler.getUncaughtExceptionHandler());

    }

    public String getScenarioFeatureFileName(Scenario scenario) {
        String featureFileName = null;
        try {
            featureFileName = new File(scenario.getUri().toURL().getFile()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return featureFileName;
    }

    @AfterStep
    public void afterStep() {

    }

    @After
    public void after(Scenario scenario) throws GemException {
        DriverManager.quitDriver();
        GemTestReporter.endTestCase();
    }

    @AfterAll
    public static void after_all() {
        try {
            GemTestReporter.endSuite(GemJarGlobalVar.reportLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
