package com.gemini.generic.utils;


import com.gemini.generic.reporting.GemEcoUpload;
import com.gemini.generic.tdd.TestCaseData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class GemJarUtils implements GemJarConstants {


    public static void loadGemJarConfigData() {
        if (CommonUtils.verifyResourceIsPresent(GEMJAR_CONFIG_JSON)) {
            try {
                String configData = CommonUtils.getFilesWithExtensionMap("json").get(GEMJAR_CONFIG_JSON).get(0).getContentAsString();
                JsonObject configJson = CommonUtils.convertStringInToJsonElement(configData).getAsJsonObject();
                String environment = GemJarUtils.getEnvironment(configJson);
                JsonElement envJsonElement = configJson.get(environment);
                if (envJsonElement != null) {
                    JsonObject envJsonObject = envJsonElement.getAsJsonObject();
                    Set<String> envKeys = envJsonObject.keySet();
                    for (String envKey : envKeys) {
                        GemJarGlobalVar.CONFIG_JSON_OBJECT.addProperty(envKey, CommonUtils.convertJsonElementToString(envJsonObject.get(envKey)));
                        configJson.remove(envKey);
                    }
                    configJson.remove(environment);
                    Set<String> parentConfigKeys = configJson.keySet();
                    for (String parentConfigKey : parentConfigKeys) {
                        GemJarGlobalVar.CONFIG_JSON_OBJECT.addProperty(parentConfigKey, CommonUtils.convertJsonElementToString(configJson.get(parentConfigKey)));
                    }

                } else {
                    GemJarGlobalVar.CONFIG_JSON_OBJECT = configJson;
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

            JsonObject gemJarDefaultConfig = new JsonObject();
            gemJarDefaultConfig.addProperty(ENVIRONMENT, "N/A");
            gemJarDefaultConfig.addProperty(PROJECT_NAME, "N/A");
            gemJarDefaultConfig.addProperty(REPORT_NAME, "GemjarReport");
            if (GemJarGlobalVar.lambdaExecution) {
                gemJarDefaultConfig.addProperty(REPORT_LOCATION, "/tmp");
            } else {
                gemJarDefaultConfig.addProperty(REPORT_LOCATION, System.getProperty("user.dir"));
            }
            gemJarDefaultConfig.addProperty(GLUE_CODE, "com.gemini.generic.feature.utils");
            GemJarGlobalVar.CONFIG_JSON_OBJECT = gemJarDefaultConfig;
        }


    }

    private static String getEnvironment(JsonObject configJson) {
        String environment = System.getProperty(ENVIRONMENT) != null ? System.getProperty(ENVIRONMENT) : CommonUtils.convertJsonElementToString(configJson.get(ENVIRONMENT));
        if (environment != null) {
            for (String configKey : configJson.keySet()) {
                if (environment.equalsIgnoreCase(configKey)) {
                    environment = configKey;
                    break;
                }
            }
        }
        return environment;
    }

    public static JsonObject getConfigObject() {
        return GemJarGlobalVar.CONFIG_JSON_OBJECT;
    }


    public static String getGemJarKeyValue(String key) {
        String valueFromSystemProperty = System.getProperty(key);
        String valueFromConfigFile = getGemJarConfigData(key);
        return valueFromSystemProperty != null && !valueFromSystemProperty.isEmpty() ? valueFromSystemProperty : valueFromConfigFile;
    }

    public static boolean isGemjarConfigKeyPresent(String key) {
        String systemKeyVaue = System.getProperty(key);
        if (systemKeyVaue != null) return true;
        String configData = getGemJarConfigData(key);
        return configData != null;
    }

    public static void checkProperJewelCredentialsPresent() {
        boolean gemjarJewelUserName = isGemjarConfigKeyPresent(GemJarConstants.GEMJAR_USER_NAME);
        boolean gemjarJewelToken = isGemjarConfigKeyPresent(GemJarConstants.GEMJAR_REPORTING_TOKEN);
        if (gemjarJewelToken || gemjarJewelUserName) {
            if (!(gemjarJewelToken && gemjarJewelUserName)) {
                System.out.println("EITHER PROVIDE BOTH " + GemJarConstants.GEMJAR_USER_NAME + " AND " + GemJarConstants.GEMJAR_REPORTING_TOKEN + " OR REMOVE BOTH KEYS....");
                System.exit(0);

            }
        }

    }


    public static JsonElement getEnvironmentBasedValue(String key) {
        JsonObject jsonObject = GemJarGlobalVar.CONFIG_JSON_OBJECT;
        JsonElement environmentData = jsonObject.get(GemJarGlobalVar.environment) != null ? jsonObject.get(GemJarGlobalVar.environment) : null;
        return environmentData != null ? environmentData.getAsJsonObject().get(key) : null;
    }

    public static String getGemJarConfigData(String key) {
        return CommonUtils.convertJsonElementToString(GemJarGlobalVar.CONFIG_JSON_OBJECT.get(key));
    }


    public static void initializeGemJARGlobalVariables() throws IOException {

        loadGemJarConfigData();
        GemJarUtils.checkProperJewelCredentialsPresent();
        GemJarGlobalVar.projectName = getGemJarKeyValue(PROJECT_NAME);
        GemJarGlobalVar.environment = getGemJarKeyValue(ENVIRONMENT);
        GemJarGlobalVar.s_run_id = getS_Run_id();
        GemJarGlobalVar.reportName = getGemJarKeyValue(REPORT_NAME);
        GemJarGlobalVar.reportLocation = getReportLocation();
        GemJarGlobalVar.browserInTest = getGemJarKeyValue(BROWSER_NAME);
        GemJarGlobalVar.pageTimeout = getGemJarKeyValue(PAGE_TIMEOUT) != null ? getGemJarKeyValue(PAGE_TIMEOUT) : GemJarGlobalVar.pageTimeout;
        GemJarGlobalVar.scriptTimeout = getGemJarKeyValue(SCRIPT_TIMEOUT) != null ? getGemJarKeyValue(SCRIPT_TIMEOUT) : GemJarGlobalVar.scriptTimeout;
        GemJarGlobalVar.implicitTime = getGemJarKeyValue(IMPLICIT_TIMEOUT) != null ? getGemJarKeyValue(IMPLICIT_TIMEOUT) : GemJarGlobalVar.implicitTime;
        GemJarGlobalVar.testCaseDataJsonPath = System.getProperty(TESTCASE_DATAJSON_PATH);
        GemJarGlobalVar.testCasesToRun = getTestCasesToRunFromSystemProperties();
        GemJarGlobalVar.createLocalReport = getLocalReportCreateStatus();
        if (!GemJarGlobalVar.cucumberFlag) {
            GemJarGlobalVar.testCaseFileName = getTestCAseFileName();
            TestCaseData.setProjectTestCaseData(GemJarGlobalVar.resourcemap.get(GemJarGlobalVar.testCaseFileName).getContentAsString());
        }
        GemEcoUpload.setUpJewelURLs();
    }

    private static boolean getLocalReportCreateStatus() {
        String createLocal = getGemJarKeyValue(GemJarConstants.LOCAL_REPORT);
        if (createLocal != null && (createLocal.equalsIgnoreCase("N") || createLocal.equalsIgnoreCase("false"))) {
            GemJarGlobalVar.createLocalReport = false;
        }
        return GemJarGlobalVar.createLocalReport;
    }

    private static String getS_Run_id() {
        String s_run_id_FromArguments = System.getProperty(GemJarConstants.S_RUN_ID);
        String s_run_id_fromProjectNameEnvironment = GemJarGlobalVar.projectName + "_" + GemJarGlobalVar.environment.toUpperCase() + "_" + UUID.randomUUID();
        return s_run_id_FromArguments != null ? s_run_id_FromArguments : s_run_id_fromProjectNameEnvironment;
    }

    private static String getTestCAseFileName() {
        String testcaseFileName = getGemJarKeyValue(TESTCASE_FILE_NAME);
        return testcaseFileName != null ? testcaseFileName : DEFAULT_TESTCASE_JSONFILE_NAME;
    }

    private static List<String> getTestCasesToRunFromSystemProperties() {
        List<String> testCasesToRun;
        String testCaseString = System.getProperty(TESTCASES_TO_RUN);
        String[] testCaseArray = testCaseString != null ? testCaseString.split(",") : null;
        if (testCaseArray != null) {
            testCasesToRun = new ArrayList<String>();
            for (String testcase : testCaseArray) {
                testCasesToRun.add(testcase.trim());
            }
        } else {
            testCasesToRun = null;
        }
        return testCasesToRun;
    }

    private static String getReportLocation() {
        try {
            String reportLoc = getGemJarKeyValue(REPORT_LOCATION);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(REPORT_FILE_DATE_FORMAT);
            DateTimeFormatter hms = DateTimeFormatter.ofPattern(REPORT_FILE_TIME_FORMAT);
            String dateSepLoc = File.separator + "Report" + File.separator + dtf.format(LocalDateTime.now()) + File.separator + hms.format(LocalDateTime.now());
            String loc = null;
            if (reportLoc != null) {
                File file = new File(reportLoc);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        return file.getAbsolutePath() + dateSepLoc;
                    } else {
                        file.mkdir();
                        return file.getAbsolutePath() + dateSepLoc;
                    }
                } else {
                    try {
                        file.mkdir();
                        loc = file.getAbsolutePath() + dateSepLoc;
                        return loc;
                    } catch (Exception e) {
                        try {
                            File file1 = new File(System.getProperty("user.dir") + reportLoc);
                            if (file1.exists()) {
                                return file1.getAbsolutePath() + dateSepLoc;
                            } else {
                                file1.mkdir();
                                return file.getAbsolutePath() + dateSepLoc;
                            }
                        } catch (Exception e1) {
                            System.out.println("Failed to create rport at location " + reportLoc);
                            File file2 = new File(System.getProperty("user.dir") + dateSepLoc);
                            file2.mkdir();
                            return file.getAbsolutePath();
                        }
                    }
                }
            } else {
                File file3 = new File(System.getProperty("user.dir") + dateSepLoc);
                file3.mkdir();
                return file3.getAbsolutePath();
            }
        } catch (Exception e) {
            System.out.println("Some Error Occur With reportLocation . Default reportLocation Set");
            return "";
        }
    }

    public static int getExpectedNumberOftestCases() {
        String expectedTestcasesFromSystemProperties = getGemJarKeyValue(GemJarConstants.EXPECTED_TESTCASES);
        int expectedTestcases = expectedTestcasesFromSystemProperties != null ? Integer.parseInt(expectedTestcasesFromSystemProperties) : GemJarGlobalVar.expected_testcases;
        return expectedTestcases;
    }

}


