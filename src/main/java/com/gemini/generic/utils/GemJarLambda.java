package com.gemini.generic.utils;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class GemJarLambda implements RequestHandler<Object, Object> {

    public final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    private String createTestCase(JsonObject testCaseObject) {

        String tag = ifvalueisNullGetEmpty(testCaseObject.get("tags"));
        String type = ifvalueisNullGetEmpty(testCaseObject.get("type"));
        String name = ifvalueisNullGetEmpty(testCaseObject.get("name"));
        String steps = convertStepsandExampleToString(testCaseObject.get("scenario_steps"));
        String examples = convertStepsandExampleToString(testCaseObject.get("examples"));

        String testCaseString = tag + "\n" + type + ": " + name + "\n" + steps + "\n";
        if (!examples.isEmpty())
            testCaseString = testCaseString + "Examples: \n" + examples + "\n";
        return testCaseString;
    }

    private String ifvalueisNullGetEmpty(JsonElement element) {
        String requiredString = CommonUtils.convertJsonElementToString(element);
        return requiredString != null ? requiredString : "";
    }

    private String convertStepsandExampleToString(JsonElement stepOrExampleObject) {
        if (stepOrExampleObject != null) {
            JsonArray objectArray = stepOrExampleObject.getAsJsonArray();
            String requiredString = "";
            for (JsonElement line : objectArray) {
                requiredString = requiredString + CommonUtils.convertJsonElementToString(line) + "\n";
            }
            return requiredString;
        } else {
            return "";
        }
    }

    @Override
    public Object handleRequest(Object o, Context context) {
        GemJarGlobalVar.lambdaExecution = true;
        JsonObject object = gson.toJsonTree(o).getAsJsonObject();
        System.out.println(object.toString());
        for (String key : object.keySet()) {
            System.setProperty(key, CommonUtils.convertJsonElementToString(object.get(key)));
        }
        try {
            FileUtils.cleanDirectory(new File(GemJarConstants.LAMBDA_FOLDER_LOCATION));
        } catch (IOException e) {
            System.out.println("failed cleaning temp folder");
        }
        if (object.has(GemJarConstants.FEATURE_JSON)) {
            try {
                JsonElement featureElement = object.get(GemJarConstants.FEATURE_JSON);
                System.setProperty(GemJarConstants.EXPECTED_TESTCASES, featureElement.getAsJsonArray().size() + "");
                System.setProperty(GemJarConstants.FEATURE_FILE_PATH, convertFeatureJsonIntoFeatureText(featureElement));
                System.out.println("feature file path " + System.getProperty(GemJarConstants.FEATURE_FILE_PATH));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            GemJarExecutions.executeGemJar();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                FileUtils.cleanDirectory(new File(GemJarConstants.LAMBDA_FOLDER_LOCATION));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public String convertFeatureJsonIntoFeatureText(JsonElement featureJson) throws IOException {
        JsonArray testcasesArray = featureJson.getAsJsonArray();
        Map<String, String> featureFileMap = new HashMap<String, String>();
        for (JsonElement testCaseElement : testcasesArray) {
            JsonObject testCaseObject = testCaseElement.getAsJsonObject();
            String testcaseData = createTestCase(testCaseObject);
            String featureFileName = CommonUtils.convertJsonElementToString(testCaseObject.get("feature"));
            if (featureFileMap.containsKey(featureFileName))
                featureFileMap.put(featureFileName, featureFileMap.get(featureFileName) + testcaseData);
            else {
                String feature = "Feature: " + featureFileName + "\n" + testcaseData;
                featureFileMap.put(featureFileName, feature);
            }
        }
        String featureFilePath = "";

        for (String featureFile : featureFileMap.keySet()) {
            File file = new File(GemJarConstants.LAMBDA_FOLDER_LOCATION + "/" + featureFile + ".feature");
            file.createNewFile();
            FileUtils.writeStringToFile(file, featureFileMap.get(featureFile), Charset.defaultCharset());
            featureFilePath = featureFilePath + file.getAbsolutePath() + ",";
        }

        return featureFilePath.substring(0, featureFilePath.length() - 1);
    }


}
