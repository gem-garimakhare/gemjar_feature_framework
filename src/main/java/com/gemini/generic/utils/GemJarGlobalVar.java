package com.gemini.generic.utils;


import com.gemini.generic.api.utils.Request;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.classgraph.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GemJarGlobalVar {

    public static boolean jarexecution = false;
    public static boolean lambdaExecution = false;
    public static boolean createLocalReport = true;

    public static Map<String, Resource> resourcemap;
    public static String projectName;
    public static String environment;
    public static String reportName;
    public static String testCaseFileName;
    public static String testCaseDataJsonPath;
    public static List<String> testCasesToRun;
    public static String reportLocation;

    public static boolean cucumberFlag = false;

    //// Test case variables

    public static String browserInTest;

    public static String report_type;

    public static JsonObject CONFIG_JSON_OBJECT = new JsonObject();


    public static JsonElement suiteDetail;
    public static Map<String, JsonElement> globalResponseHM;


    public static int expected_testcases = 1;

    // public static boolean uploadSS = false;

    public static ArrayList<Request> failedTestcaseData = new ArrayList<Request>();

    public static String s_run_id;

    public static String JEWEL_ENTRY_POINT = "https://apis.gemecosystem.com/enter-point";
    public static String SUITE_API_URL = "https://apis.gemecosystem.com/suiteexe";
    public static String STEP_API_URL = "https://apis.gemecosystem.com/testcase";
    public static String BUCKET_API = "https://apis.gemecosystem.com/v1/upload/file";
    public static String REPORT_LINK = "https://jewel.gemecosystem.com/#/autolytics/execution-report?s_run_id=";

    public static String JIRA_LINK = "https://apis.gemecosystem.com/v1/jira";
    public static String EMAIL_LINK = "https://apis.gemecosystem.com/v1/email";

    public static boolean jewelCredentials = false;
    public static String pageTimeout = "20";
    public static String scriptTimeout = "10";

    public static String implicitTime = "3";
}
