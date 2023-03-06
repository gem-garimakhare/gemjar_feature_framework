package com.gemini.generic.api.utils;

import com.gemini.generic.utils.CommonUtils;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;

public class ProjectSampleJson {

    private static final HashMap<String, JsonElement> sampleJsonObjectMap = new HashMap<String, JsonElement>();

    private static final HashMap<String, String> sampleJsonObjectStringMap = new HashMap<String, String>();


    public static void loadSampleJson() {
        try {

            for (String name : GemJarGlobalVar.resourcemap.keySet()) {
                if (name.contains(".")) {
                    String extension = name.substring(name.lastIndexOf("."));
                    if (extension.contains("json")) {
                        String content = GemJarGlobalVar.resourcemap.get(name).getContentAsString();
                        String nameWithoutExtension = name.substring(0, name.lastIndexOf("."));
                        sampleJsonObjectStringMap.put(nameWithoutExtension, content);
                        sampleJsonObjectMap.put(nameWithoutExtension, CommonUtils.convertStringInToJsonElement(content));

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JsonElement getSampleData(String sampleName) {
        if (sampleJsonObjectMap.containsKey(sampleName)) {
            return sampleJsonObjectMap.get(sampleName);
        } else {
            return null;
        }
    }

    public static String getSampleDataString(String sampleName) {
        if (sampleJsonObjectStringMap.containsKey(sampleName)) {
            return sampleJsonObjectStringMap.get(sampleName);
        } else {
            return null;
        }
    }

    public static void addSampleData(String sampleName, String sampleValue) {
        try {
            sampleJsonObjectMap.put(sampleName, JsonParser.parseString(sampleValue));
            sampleJsonObjectStringMap.put(sampleName, sampleValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addSampleData(String sampleName, JsonElement sampleValue) {
        try {
            sampleJsonObjectMap.put(sampleName, sampleValue);
            sampleJsonObjectStringMap.put(sampleName, sampleValue.getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
