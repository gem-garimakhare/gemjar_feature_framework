package com.gemini.generic.reporting;

import com.gemini.generic.utils.CommonUtils;
import com.gemini.generic.utils.GemJarConstants;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.google.gson.*;
import org.apache.commons.codec.binary.Base64;

import java.time.Instant;
import java.util.*;

public class GemTestReporter {

    private static final String gemEcoProductName = "GEMJAR";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final ThreadLocal<Testcase_Details> testCase_Details = new ThreadLocal<Testcase_Details>();

    private static final ArrayList<String> failedTestcases = new ArrayList<String>();

    private static final ArrayList<String> suiteReasonOfFailure = new ArrayList<String>();

    private static final ThreadLocal<List<String>> reasonOfFailures = new ThreadLocal<List<String>>();

    private static final ThreadLocal<JsonObject> testCaseMetaData = new ThreadLocal<JsonObject>();

    private static final JsonObject stepJson = new JsonObject();

    private static final ThreadLocal<JsonArray> steps = new ThreadLocal<JsonArray>();

    private static volatile Suits_Details suiteDetails;

    private static final ArrayList<Testcase_Details> TestCase_Details = new ArrayList<Testcase_Details>();

    private static final JsonObject suiteMetaData = new JsonObject();

    private static JsonObject reporting;

    enum Reporting {
        report_product,
        suits_details
    }

    public static void startSuite(String projectName, String env, String s_run_id) {
        suiteDetails = new Suits_Details(s_run_id, projectName, env);
        reporting = new JsonObject();
        reporting.addProperty(Reporting.report_product.name(), gemEcoProductName);
        GemEcoUpload.postNewRecord();
    }

    public static void startTestCase(String testcaseName, String category, boolean ignore) {
        steps.set(new JsonArray());
        reasonOfFailures.set(new ArrayList<String>());
        testCase_Details.set(new Testcase_Details(testcaseName, category, ignore));
        testCaseMetaData.set(new JsonObject());
    }

    public static void addTestStep(String stepTitle, String stepDescription, STATUS status) {
        addTestStep(stepTitle, stepDescription, status, new HashMap<>());
    }

    public static void addTestStep(String stepTitle, String stepDescription, STATUS status, String screenShotPath) {
        Map<String, String> scrnshot = new HashMap<>();
        boolean isBase64 = Base64.isArrayByteBase64(screenShotPath.getBytes());
        if (isBase64) {
            scrnshot.put("ScreenShot", "data:image/gif;base64, " + screenShotPath);
        } else {
            scrnshot.put("ScreenShot", screenShotPath);
        }
        addTestStep(stepTitle, stepDescription, status, scrnshot);
    }

    public static void addTestStep(String stepTitle, String stepDescription, STATUS status,
                                   Map<String, String> extraKeys) {
        JsonObject step = new JsonObject();
        step.addProperty(GemJarConstants.STEP_NAME, stepTitle);
        step.addProperty(GemJarConstants.STEP_DESCRIPTION, stepDescription);

        step.addProperty("status", status.name());
        if (extraKeys != null && !(extraKeys.isEmpty())) {
            Set<String> extraKeySet = extraKeys.keySet();
            for (String key : extraKeySet) {
                step.addProperty(key, extraKeys.get(key));
            }
        }
        String localSnapShotPath = CommonUtils.convertJsonElementToString(step.get("ScreenShot"));
        if (localSnapShotPath != null) {
            step.addProperty("ScreenShot", localSnapShotPath);
        }
        steps.get().add(step);
    }

    public static void addTestStep(String stepTitle, String stepDescription, STATUS status, String newKey, String newKeyValue) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(newKey, newKeyValue);
        addTestStep(stepTitle, stepDescription, status, map);

    }

    public static void addTestCaseMetaData(String key, String value) {
        testCaseMetaData.get().addProperty(key, value);
    }

    public static void addSuiteMetaData(String key, String value) {
        suiteMetaData.addProperty(key, value);
    }

    public static void addReasonOfFailure(String reasonOfFailure) {
        String reasonOfFailureKey = "Reason of failure";
        reasonOfFailures.get().add(reasonOfFailure);
        testCaseMetaData.get().addProperty(reasonOfFailureKey, reasonOfFailures.get().toString());
    }

    public synchronized static void endTestCase() {
        testCase_Details.get().setStatus(steps.get());
        testCase_Details.get().setEnd_time(Instant.now().getEpochSecond() * 1000);
        JsonObject testcaseDetail = gson.toJsonTree(testCase_Details.get()).getAsJsonObject();
        testcaseDetail = mergeTwoJson(testcaseDetail, testCaseMetaData.get()).getAsJsonObject();
        testcaseDetail.remove("meta_data");
        TestCase_Details.add(testCase_Details.get());
        suiteDetails.addTestCaseDetail(gson.fromJson(testcaseDetail, Map.class));
        String testCaseRunID = testCase_Details.get().getTc_run_id();
        JsonObject testCaseStep = new JsonObject();
        testCaseStep.add("steps", steps.get());
        JsonArray meta_data = createTestCaseMetaData();
        testCaseStep.add("meta_data", meta_data);
        stepJson.add(testCaseRunID, testCaseStep);
        if (GemJarGlobalVar.jewelCredentials) {
            testcaseDetail.add("meta_data", createTestCaseMetaData());
            testcaseDetail.add("steps", steps.get());
            testcaseDetail.add("user_defined_data", testCaseMetaData.get());
            GemEcoUpload.postStepRecord(testcaseDetail);
        }
        JsonElement suiteDetail = gson.toJsonTree(reporting);
        suiteDetail.getAsJsonObject().add("teststep_details", stepJson);
        GemJarGlobalVar.suiteDetail = suiteDetail;
    }

    private static JsonArray createTestCaseMetaData() {
        JsonArray metaData = new JsonArray();
        JsonObject testcaseName = new JsonObject();
        testcaseName.addProperty("TESTCASE NAME", testCase_Details.get().getName());
        testcaseName.addProperty("SERVICE PROJECT", "NONE");
        JsonObject dateOfExecution = new JsonObject();
        dateOfExecution.addProperty("value", testCase_Details.get().getStart_time());
        dateOfExecution.addProperty("type", "date");
        testcaseName.add("DATE OF EXECUTION", dateOfExecution);
        metaData.add(testcaseName);

        JsonObject executionTimeDetail = new JsonObject();
        JsonObject startTimeDetail = new JsonObject();
        startTimeDetail.addProperty("value", testCase_Details.get().getStart_time());
        startTimeDetail.addProperty("type", "datetime");
        executionTimeDetail.add("EXECUTION STARTED ON", startTimeDetail);

        JsonObject endTimeDetail = new JsonObject();
        endTimeDetail.addProperty("value", testCase_Details.get().getEnd_time());
        endTimeDetail.addProperty("type", "datetime");
        executionTimeDetail.add("EXECUTION ENDED ON", endTimeDetail);

        long ex_dur = testCase_Details.get().getEnd_time() - testCase_Details.get().getStart_time();
        executionTimeDetail.addProperty("EXECUTION DURATION", ((float) ex_dur / 1000) + " seconds");
        metaData.add(executionTimeDetail);
        metaData.add(getStepStats());

        return metaData;
    }

    private static JsonObject getStepStats() {
        JsonObject stepStats = new JsonObject();
        stepStats.addProperty("TOTAL", steps.get().size());
        Map<String, Integer> statMap = new HashMap<String, Integer>();

        for (JsonElement step : steps.get()) {
            String statusName = step.getAsJsonObject().get("status").getAsString();
            statMap.merge(statusName, 1, Integer::sum);
        }
        for (String statusKey : statMap.keySet()) {
            stepStats.addProperty(statusKey, statMap.get(statusKey));
        }

        return stepStats;

    }

    public static void endSuite() {
        endSuite(null);

    }

    public static void endSuite(String reportLoc) {
        GemEcoUpload.closeAllTheThread();
        if (failedTestcases.size() > 0) {
            suiteMetaData.addProperty("Failed Testcases", failedTestcases.toString());
        }
        suiteDetails.endSuite(TestCase_Details);
        JsonObject suiteObject = gson.toJsonTree(suiteDetails).getAsJsonObject();
        suiteObject = mergeTwoJson(suiteObject, suiteMetaData).getAsJsonObject();
        reporting.add(Reporting.suits_details.name(), suiteObject);
        createReport(reportLoc);
        GemEcoUpload.putNewRecord();
    }

    private static void createReport(String reportLoc) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement suiteDetail = gson.toJsonTree(reporting);
        suiteDetail = mergeTwoJson(suiteDetail.getAsJsonObject(), suiteMetaData);
        suiteDetail.getAsJsonObject().add("teststep_details", stepJson);
        String[] status = new String[]{"INFO", "WARN", "ERR", "EXE"};
        for (String s : status) {
            if (suiteDetail.getAsJsonObject().get("suits_details").getAsJsonObject().get("testcase_info").getAsJsonObject().get(s).getAsInt() == 0) {
                suiteDetail.getAsJsonObject().get("suits_details").getAsJsonObject().get("testcase_info").getAsJsonObject().remove(s);
            }
        }
        System.out.println("suits_details " + suiteDetail);

        GemJarGlobalVar.suiteDetail = suiteDetail;
        GemReportingUtility.createReport(GemJarGlobalVar.suiteDetail.toString(), stepJson.toString(), reportLoc);
    }

    private static JsonElement mergeTwoJson(JsonObject targetElement, JsonObject elementToAdd) {
        for (String MetaKey : elementToAdd.keySet()) {
            targetElement.add(MetaKey, elementToAdd.get(MetaKey));
        }
        return targetElement;
    }

    private static JsonElement removeMetaKeyFromJson(JsonElement element) {
        String MetaData = "meta_data";
        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject.has(MetaData)) {
            JsonObject MetaDataJsonObject = jsonObject.get(MetaData).getAsJsonObject();
            jsonObject.remove(MetaData);
            for (String key : MetaDataJsonObject.keySet()) {
                jsonObject.add(key, MetaDataJsonObject);
            }
        }
        return jsonObject;
    }

    public static Suits_Details getSuiteDetails() {
        return suiteDetails;
    }

    public static String getTestStepdetails() {
        return stepJson.toString();
    }

    public static void removeAKeyAndSpreadKey(JsonElement jsonElement, String keyToSpread) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<String> jsonKeys = jsonObject.keySet();
        for (String key : jsonKeys) {
            JsonElement jsonElement1 = jsonObject.get(key);
            if (jsonElement1.isJsonPrimitive()) {

            } else if (jsonElement1.isJsonArray()) {

            } else if (jsonElement1.isJsonObject()) {
                JsonObject jsonObject1 = jsonElement1.getAsJsonObject();
                if (jsonObject1.has(keyToSpread)) {
                    jsonElement1 = mergeTwoJson(jsonObject1, jsonObject1.get(keyToSpread).getAsJsonObject());
                    jsonObject1.remove(keyToSpread);
                }
            } else if (jsonElement1.isJsonNull()) {

            }
        }
    }
}