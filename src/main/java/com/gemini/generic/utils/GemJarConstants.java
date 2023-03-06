package com.gemini.generic.utils;

public interface GemJarConstants {

    ////////////////////GEMJAR PROJECT DATA//////////////////////

    /**
     * Constant <code>GEMJAR_CONFIG_JSON="GEMJAR_CONFIG_JSON"</code>.
     */
    String GEMJAR_CONFIG_JSON = "gemjar-config.json";

    /**
     * Constant <code> PROJECTNAME = "projectName"</code>
     */
    String PROJECT_NAME = "projectName";

    /**
     * Constant <code> ENVIRONMENT = "environment"</code>
     */
    String ENVIRONMENT = "environment";


    /**
     * Constant <code> REPORT_NAME = "reportName"</code>
     */
    String REPORT_NAME = "reportName";

    /**
     * Constant <code> REPORT_LOCATION = "reportLocation"</code>
     */
    String REPORT_LOCATION = "reportLocation";

    String LOCAL_REPORT = "localReport";


    String GEMJAR_USER_NAME = "gemjarUserName";
    String GEMJAR_REPORTING_TOKEN = "gemjarToken";
    String GEMJAR_SUBSCRIPTION_ID = "gemjarSubID";

    String GEM_REPORT_TEMPLATE = "GemjarReport.html";

    String REPORT_FILE_DATE_FORMAT = "dd-MMM-yyyy";

    String REPORT_FILE_TIME_FORMAT = "HH-mm-ss";

    String EXPECTED_TESTCASES = "expected_testcases";


    /////////////////////Testng Constants//////////////////////

    /**
     * Constant TESTCASE_FILE_NAME = "testCaseFileName"
     */
    String TESTCASE_FILE_NAME = "testCaseFileName";

    String TESTCASES_TO_RUN = "testCasesToRun";

    String DEFAULT_TESTCASE_JSONFILE_NAME = "testcase.json";

    String TESTCASE_DATAJSON_PATH = "testCaseDataJsonPath";

    String TESTCASE_JSON_INPUT_TAG = "inputData";

    //////////////////////// Cucumber Constants /////////////////////

    String GLUE_CODE = "glueCode";

    String CUCUMBER_TAGS_NAME = "tagName";
    String Scenario = "scenario";
    String FEATURE_FILE_PATH = "feature";

    String SAMPLE_JSON_EXTENSION = "_samplejson";
    ////////////////////////Cucumber Property Keys ////////////

    String CUCUMBER_GLUE = "cucumber.glue";

    String CUCUMBER_FEATURE = "cucumber.features";

    String CUCUMBER_TAGS = "cucumber.filter.tags";

    String CUCUMBER_SCENARIO_NAME = "cucumber.filter.name";
    /////////////////////////////SELENIUM CONSTANTS///////////////

    String REMOTE_WEBDRIVER_URL = "remoteDriverUrl";
    /**
     * Constant <code>LAUNCH_URL = launchUrl</code>
     */
    String LAUNCH_URL = "launchUrl";

    /**
     * Constant <code>BROWSER_TIMEOUT = browserTimeout</code>
     */
    String BROWSER_TIMEOUT = "browserTimeout";

    /**
     * Constant <code>BROWSER_NAME = browserName</code>
     */

    String BROWSER_NAME = "browserName";

    ///////////////////API Constants/////////////////////////////

    String BASE_URL = "baseUrl";

    String GET = "GET";
    /**
     * Constant <code>PUT="PUT"</code>.
     */
    String PUT = "PUT";

    String ACCEPT = "accept";

    /**
     * Constant <code>DELETE="DELETE"</code>.
     */
    String DELETE = "DELETE";

    /**
     * Constant <code>POST="POST"</code>.
     */
    String POST = "POST";

    /**
     * Constant <code>PATCH="PATCH"</code>.
     */
    String PATCH = "PATCH";

    int readTimeOut = 100000;
    //////////////////////////////Reporting URL////////////////

    String JEWEL_ENTRY_URL = "jewelEntryUrl";

    String ChromeOptions = "chromeOptions";

    String S_RUN_ID = "s_run_id";

    String STEP_NAME = "step name";
    String STEP_DESCRIPTION = "step description";

    String IMPLICIT_TIMEOUT = "implicitTimeout";
    String SCRIPT_TIMEOUT = "scriptTimeout";
    String PAGE_TIMEOUT = "page_Timeout";

    ///////////////////////////////////////////LAMBDA Constants///////////////////

    String FEATURE_JSON = "feature_Json";
    String LAMBDA_FOLDER_LOCATION = "/tmp";

    String JEWEL_REPORT_EMAIL_FLAG = "emailReport";
    String JEWEL_REPORT_EMAIL_TO = "emailTO";
    String JEWEL_REPORT_EMAIL_CC = "emailCC";

    String JEWEL_REPORT_EMAIL_BCC = "emailBCC";
}
