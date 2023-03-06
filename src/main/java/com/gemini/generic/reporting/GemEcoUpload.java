package com.gemini.generic.reporting;


import com.gemini.generic.api.utils.ApiInvocation;
import com.gemini.generic.api.utils.Request;
import com.gemini.generic.api.utils.Response;
import com.gemini.generic.utils.CommonUtils;
import com.gemini.generic.utils.GemJarConstants;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.gemini.generic.utils.GemJarUtils;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GemEcoUpload {

    private final static Logger logger = LogManager.getLogger(GemEcoUpload.class);

    private final static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

    public static void postNewRecord() {
        if (GemJarGlobalVar.jewelCredentials) {
            logger.info("Start uploading Suite data on MotherShip Server");
            Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
            Suits_Details suitsdetails2 = GemTestReporter.getSuiteDetails();
            JsonObject suitDetailJson = gson.toJsonTree(suitsdetails2).getAsJsonObject();

            suitDetailJson.addProperty("expected_testcases", GemJarUtils.getExpectedNumberOftestCases());
            suitDetailJson.addProperty("report_name", GemJarGlobalVar.reportName);
            suitDetailJson.addProperty("s_id", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_SUBSCRIPTION_ID));
            String OS = suitDetailJson.get("os").getAsString();
            suitDetailJson.addProperty("os", OS);
            insertSuiteData("post", String.valueOf(suitDetailJson));
        }
    }

    public static void putNewRecord() {
        if (GemJarGlobalVar.jewelCredentials) {
            Suits_Details suitsdetails2 = GemTestReporter.getSuiteDetails();
            JsonObject suiteObject = gson.toJsonTree(suitsdetails2).getAsJsonObject();
            suiteObject.remove("testcase_details");
            suiteObject.addProperty("s_id", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_SUBSCRIPTION_ID));
            String s_report_type = suiteObject.get("os").getAsString();
            suiteObject.addProperty("os", s_report_type);
            suiteObject.addProperty("base_user", GemReportingUtility.getCurrentUserName());
            suiteObject.addProperty("invoke_user", GemReportingUtility.getCurrentUserName());
            suiteObject.remove("os");
            suiteObject.add("meta_data", new JsonArray());
            insertSuiteData("put", gson.toJson(suiteObject));

            updateJira();
            sendJewelEmailReport();

        }
    }

    private static void sendJewelEmailReport() {
        String sendEmailFlag = GemJarUtils.getGemJarKeyValue(GemJarConstants.JEWEL_REPORT_EMAIL_FLAG);
        if (sendEmailFlag == null || (sendEmailFlag.equalsIgnoreCase("false") || sendEmailFlag.equalsIgnoreCase("n"))) {
            Request emailRequest = new Request();
            emailRequest.setMethod("post");
            emailRequest.setHeader("username", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_USER_NAME));
            emailRequest.setHeader("bridgeToken", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_REPORTING_TOKEN));
            emailRequest.setURL(GemJarGlobalVar.EMAIL_LINK);
            JsonObject emailRequestBody = new JsonObject();
            if (GemJarUtils.getGemJarKeyValue(GemJarConstants.JEWEL_REPORT_EMAIL_TO) != null) {
                emailRequestBody.add("to", getEmailRecipient(GemJarConstants.JEWEL_REPORT_EMAIL_TO));
            }
            if (GemJarUtils.getGemJarKeyValue(GemJarConstants.JEWEL_REPORT_EMAIL_CC) != null) {
                emailRequestBody.add("cc", getEmailRecipient(GemJarConstants.JEWEL_REPORT_EMAIL_CC));
            }
            if (GemJarUtils.getGemJarKeyValue(GemJarConstants.JEWEL_REPORT_EMAIL_BCC) != null) {
                emailRequestBody.add("bcc", getEmailRecipient(GemJarConstants.JEWEL_REPORT_EMAIL_BCC));
            }
            emailRequestBody.addProperty("s_run_id", GemJarGlobalVar.s_run_id);
            try {
                if (emailRequestBody.keySet().size() > 1) {
                    emailRequest.setRequestPayload(CommonUtils.convertJsonElementToString(emailRequestBody));
                    Response emailResponse = ApiInvocation.handleRequest(emailRequest);
                    if (emailResponse.getErrorMessage() == null) {
                        System.out.println("JEWEL REPORT SENT");
                    }
                } else {
                    System.out.println("NO EMAIL RECEIPT ADDED");
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
                e.printStackTrace();
            }

        }


    }

    private static JsonArray getEmailRecipient(String recipientKey){
        String recipients = GemJarUtils.getGemJarKeyValue(recipientKey);
        JsonArray recipientsArray = CommonUtils.convertStringInToJsonElement(recipients).isJsonArray() ?
                CommonUtils.convertStringInToJsonElement(recipients).getAsJsonArray():
                CommonUtils.convertStringArrayToJsonArray(recipients.split(","));
        return recipientsArray;

    }

    public static void insertReportData(String method, String insertUrl, String jsonPayload) {
        Request request = new Request();
        request.setMethod(method);
        request.setHeader("username", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_USER_NAME));
        request.setHeader("bridgeToken", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_REPORTING_TOKEN));
        request.setURL(insertUrl);
        request.setRequestPayload(jsonPayload);
        logger.info("testcase payload = " + jsonPayload);
        RunnableClass runnableClass = new RunnableClass(request);
        executor.submit(runnableClass);
    }

    public static void insertSuiteData(String method, String suitePayload) {
        String suiteInsertUrl = GemJarGlobalVar.SUITE_API_URL;
        try {
            Request request = new Request();
            request.setMethod(method);
            request.setURL(suiteInsertUrl);
            request.setRequestPayload(suitePayload);
            request.setHeader("username", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_USER_NAME));
            request.setHeader("bridgeToken", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_REPORTING_TOKEN));
            Response testcase = ApiInvocation.handleRequest(request);
            logger.info("testcase response code = " + testcase.getStatus());
            System.out.println("method " + request.getMethod());
            System.out.println("body " + request.getRequestPayload());
            System.out.println("url " + request.getURL());
            System.out.println("status code " + testcase.getStatus());
            System.out.println("response " + testcase.getResponseBody());
            System.out.println("error message " + testcase.getErrorMessage());


            if (testcase.getErrorMessage() == null) {
                String p_id = CommonUtils.convertJsonElementToString(testcase.getResponseBodyJson().getAsJsonObject().get("data").getAsJsonObject().get("p_id"));
                System.out.println("Jewel Report Link : " + GemJarGlobalVar.REPORT_LINK + GemTestReporter.getSuiteDetails().getS_run_id() + "&p_id=" + p_id);
                logger.info("testcase data upload successful");
            } else if (testcase.getStatus() == 400 && testcase.getErrorMessage().contains("s_run_id already present")) {
                System.out.println("s_run_id already present, sending put request");
                insertSuiteData("put", String.valueOf(suitePayload));
            } else {
                GemJarGlobalVar.failedTestcaseData.add(request);
                logger.info("testcase data upload failed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void postStepRecord(JsonObject testCaseData) {
        testCaseData.addProperty("s_run_id", GemJarGlobalVar.s_run_id);
        testCaseData.addProperty("product_type", "GEMJAR");
        insertTestCaseData(testCaseData.toString());
    }

    public static void insertTestCaseData(String testcaseData) {
        String testCaseInsertUrl = GemJarGlobalVar.STEP_API_URL;
        insertReportData("post", testCaseInsertUrl, testcaseData);
    }

    public static void retryTestcaseUpload() throws Exception {
        logger.info("Retry testcase data upload ");
        for (Request curr : GemJarGlobalVar.failedTestcaseData) {
            logger.info("URL => " + curr.getBaseUrl());
            logger.info("Method => " + curr.getMethod());
            logger.info("payload => " + curr.getRequestPayload());
            int statusCode = 0;
            int retryCount = 0;
            while (retryCount < 5 && statusCode != 201) {
                Response testcase = ApiInvocation.handleRequest(curr);
                logger.info("Response => " + testcase.getResponseBody());
                logger.info("retry count " + (retryCount + 1) + ", Status Code =>" + testcase.getStatus());
                statusCode = testcase.getStatus();
                retryCount += 1;
            }

        }
    }

    public static void setUpJewelURLs() {

        if (GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_REPORTING_TOKEN) != null && GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_USER_NAME) != null) {
            String entryPointURL = GemJarUtils.getGemJarKeyValue(GemJarConstants.JEWEL_ENTRY_URL);
            entryPointURL = entryPointURL == null ? GemJarGlobalVar.JEWEL_ENTRY_POINT : entryPointURL;
            Request request = new Request();
            request.setURL(entryPointURL);
            request.setMethod("get");
            request.setHeader("username", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_USER_NAME));
            request.setHeader("bridgeToken", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_REPORTING_TOKEN));
            try {
                Response response = ApiInvocation.handleRequest(request);
                if (response.getStatus() == 200) {
                    GemJarGlobalVar.jewelCredentials = true;
                    GemJarGlobalVar.createLocalReport = false;
                    JsonObject jsonObject = response.getResponseBodyJson().getAsJsonObject();
                    JsonObject dataJsonObject = jsonObject.get("data").getAsJsonObject();
                    GemJarGlobalVar.SUITE_API_URL = dataJsonObject.get("suite-exe-api").getAsString();
                    GemJarGlobalVar.STEP_API_URL = dataJsonObject.get("test-exe-api").getAsString();
                    GemJarGlobalVar.BUCKET_API = dataJsonObject.get("bucket-file-upload-api").getAsString();
                    GemJarGlobalVar.REPORT_LINK = dataJsonObject.get("jewel-url").getAsString() + "/#/autolytics/execution-report?s_run_id=";
                    GemJarGlobalVar.JIRA_LINK = dataJsonObject.get("jira-api").getAsString();
                    GemJarGlobalVar.EMAIL_LINK = dataJsonObject.get("email-api").getAsString();
                } else {
                    System.out.println("Check Jewel Credentials");
                    System.out.println("Jewel enter-point GET Message = " + response.getErrorMessage());
                    System.out.println("Jewel enter-point GET Status : " + response.getStatus());
                    logger.warn("INVALID JEWEL Credentials. Either remove the jewel Keys or provide correct credentials");
                    System.out.println("INVALID JEWEL Credentials. Either remove the jewel Keys or provide correct credentials");
                    System.exit(0);
                }
            } catch (Exception e) {
                System.out.println("exception occur while fetching jewel url");
                throw new RuntimeException(e);
            }


        }
    }

    public static void closeAllTheThread() {
        executor.shutdown();
        try {
            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) ;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateJira() {
        JsonObject jiraObj = (JsonObject) (GemJarUtils.getGemJarKeyValue("jira") != null ? GemJarUtils.getGemJarKeyValue("jira") : GemJarUtils.getGemJarConfigData("jira") != null ? JsonParser.parseString(GemJarUtils.getGemJarConfigData("jira")) : null);
        if (jiraObj != null) {
            Request jiraRequest = new Request();
            jiraRequest.setURL(GemJarGlobalVar.JIRA_LINK);
            jiraRequest.setMethod("POST");
            jiraObj.addProperty("s_run_id", GemJarGlobalVar.s_run_id);
            jiraObj.addProperty("suiteName", GemJarGlobalVar.reportName);
            jiraObj.addProperty("env", GemJarGlobalVar.environment);
            jiraRequest.setRequestPayload(jiraObj.toString());
            try {
                Response jiraResponse = ApiInvocation.handleRequest(jiraRequest);
                if (jiraResponse.getErrorMessage() == null) {
                    System.out.println("Jira update Successful ");
                    if (!jiraResponse.getResponseBodyJson().isJsonNull() && jiraResponse.getResponseBodyJson().getAsJsonObject().has("data")) {
                        System.out.println("Jira no. => " + jiraResponse.getResponseBodyJson().getAsJsonObject().get("data").getAsJsonObject().get("key").getAsString());
                    }
                } else {
                    System.out.println("Some Error occur while creating JIRA");
                    System.out.println("jira status " + jiraResponse.getStatus());
                    System.out.println("jira body" + jiraResponse.getResponseBody());
                    System.out.println("jira error message " + jiraResponse.getErrorMessage());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}
