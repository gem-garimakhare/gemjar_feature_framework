package com.gemini.generic.feature.utils;

import com.gemini.generic.utils.CommonUtils;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class ValueFixer {
    private static final String varStartChar = "#(";
    private static final String varEndChar = ")";
    private static final String keySperator = ".";

    private static final String functionStartChar = "fn(";

    private static final String functionEndChar = ")";
    private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    private static String fixAndConcatnate(Map<String, String> varMap, String value) {
        String target;
        if (value.contains(varStartChar)) {
            int startIndex = value.indexOf(varStartChar) + 2;
            int endIndex = value.indexOf(varEndChar);
            String totalSubString = value.substring(startIndex, endIndex);
            if (totalSubString.contains(keySperator)) {
                target = checkForNestedKeyInMap(varMap, totalSubString);
            } else {
                target = CommonUtils.convertJsonElementToString(checkForJsonArrayValue(varMap, totalSubString));
            }
        } else {
            target = value;
        }
        return target;
    }

    public static String fixValue(Map<String, String> varmap, String value) {
        String prefix;
        String variableString;
        while (value.contains(varStartChar)) {
            prefix = value.substring(0, value.indexOf(varStartChar));
            variableString = fixAndConcatnate(varmap, value.substring(value.indexOf(varStartChar), value.indexOf(varEndChar) + 1));
            value = prefix + variableString + value.substring(value.indexOf(varEndChar) + 1);
        }
        while (value.contains(functionStartChar)) {
            prefix = value.substring(0, value.indexOf(functionStartChar));
            variableString = checkForOperationFunctions(value.substring(value.indexOf(functionStartChar), value.indexOf(varEndChar) + 1));
            value = prefix + variableString + value.substring(value.indexOf(functionEndChar) + 1);
        }
        return value;
    }

    private static String checkForNestedKeyInMap(Map<String, String> varMap, String totalSubString) {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        String[] dotSepKeys = totalSubString.split("\\.");
        String valuefromMap = CommonUtils.convertJsonElementToString(checkForJsonArrayValue(varMap, dotSepKeys[0]));
        JsonElement valueAsJsonElement = gson.fromJson(valuefromMap, JsonElement.class);
        for (int i = 1; i < dotSepKeys.length; i++) {
            if (dotSepKeys[i].contains("[") && totalSubString.contains("]")) {
                valueAsJsonElement = checkForJsonArrayValue(valueAsJsonElement, dotSepKeys[i]);
            } else {
                valueAsJsonElement = getValueFromJsonElement(valueAsJsonElement, dotSepKeys[i]);
            }
        }
        return CommonUtils.convertJsonElementToString(valueAsJsonElement);
    }

    private static JsonElement checkForJsonArrayValue(JsonElement valueAsJsonElement, String totalSubString) {
        int arrayStartIndex = totalSubString.indexOf('[') + 1;
        int endIndex = totalSubString.indexOf(']');
        int index = Integer.parseInt(totalSubString.substring(arrayStartIndex, endIndex));
        String key = totalSubString.substring(0, arrayStartIndex - 1);
        JsonElement arrayValue = getValueFromJsonElement(valueAsJsonElement, key);
        return arrayValue.isJsonArray() ? arrayValue.getAsJsonArray().size() > index ? arrayValue.getAsJsonArray().get(index) : null : null;
    }

    private static JsonElement checkForJsonArrayValue(Map<String, String> valueAsMap, String totalSubString) {
        if (totalSubString.contains("[") && totalSubString.contains("]")) {
            int startIndex = totalSubString.indexOf('[') + 1;
            int endIndex = totalSubString.indexOf(']');
            int index = Integer.parseInt(totalSubString.substring(startIndex, endIndex));
            String key = totalSubString.substring(0, startIndex - 1);
            String valueOfKey = valueAsMap.get(key);
            if (valueOfKey != null) {
                JsonElement arrayValue = gson.fromJson(valueOfKey, JsonElement.class);
                if (arrayValue.isJsonArray()) {
                    return arrayValue.getAsJsonArray().get(index);
                } else {
                    return null;
                }
            } else return null;
        } else {
            return CommonUtils.convertStringInToJsonElement(valueAsMap.get(totalSubString));
        }
    }

    private static JsonElement getValueFromJsonElement(JsonElement jsonElement, String key) {
        if (jsonElement == null) {
            return null;
        } else {
            if (jsonElement.isJsonNull()) {
                return null;
            } else if (jsonElement.isJsonPrimitive()) {
                return null;
            } else if (jsonElement.isJsonArray()) {
                return null;
            } else if (jsonElement.isJsonObject()) {
                return jsonElement.getAsJsonObject().get(key);
            }
        }
        return null;
    }

    public static JsonElement fixJsonElement(JsonElement jsonElement, Map<String, String> varMap) {
        if (jsonElement != null) {
            if (jsonElement.isJsonPrimitive()) {
                jsonElement = gson.fromJson("\"" + fixValue(varMap, CommonUtils.convertJsonElementToString(jsonElement)) + "\"", JsonElement.class);
            } else if (jsonElement.isJsonArray()) {
                jsonElement = fixJsonArray(jsonElement.getAsJsonArray(), varMap);
            } else if (jsonElement.isJsonObject()) {
                jsonElement = fixJsonObject(jsonElement.getAsJsonObject(), varMap);
            }
        }
        return jsonElement;
    }

    private static JsonElement fixJsonObject(JsonObject asJsonObject, Map<String, String> varMap) {
        Set<String> setOfkeys = asJsonObject.keySet();
        for (String key : setOfkeys) {
            asJsonObject.add(key, fixJsonElement(asJsonObject.get(key), varMap));
        }
        return asJsonObject;
    }

    private static JsonElement fixJsonArray(JsonArray asJsonArray, Map<String, String> varMap) {
        for (JsonElement jsonele : asJsonArray) {
            fixJsonObject(jsonele.getAsJsonObject(), varMap);
        }
        return asJsonArray;
    }

    ///////////////////////////////////////Check for Functions////////////////

    private static String checkForOperationFunctions(String key) {
        if (key.contains(functionStartChar)) {
            int startIndex = key.indexOf(functionStartChar) + 3;
            int endIndex = key.indexOf(functionEndChar);
            String functionWithParam = key.substring(startIndex, endIndex).trim();
            String functionName;
            String functionParam;
            //if (functionWithParam.contains("\\s")) {
            if(StringUtils.containsWhitespace(functionWithParam)){
                //functionName = functionWithParam.trim().substring(0, functionWithParam.indexOf("\\s"));
                functionName = functionWithParam.trim().substring(0, functionWithParam.indexOf(" "));
                //functionParam = functionWithParam.substring(functionWithParam.indexOf("\\s") + 1).trim();
                functionParam = functionWithParam.substring(functionWithParam.indexOf(" ") + 1).trim();

            } else {
                functionName = functionWithParam;
                functionParam = "";
            }
            return callOperationFunction(functionName, functionParam);
        }
        return key;
    }

    private static String callOperationFunction(String functionName, String functionParam) {
        String funName = functionName.toUpperCase();
        String result = "";
        switch (funName) {
            case "UNIQUE":
                result = getUniqueNuber(functionParam);
                break;
            case "CURR":
                result = getCurrDate(functionParam);
                break;
            case "ALPHA":
                result = getRandomAlphabet(functionParam);
                break;
            case "EPOCH":
                result = Instant.now().toEpochMilli() + "";
                break;
            case "UUID":
                result = UUID.randomUUID().toString();
                break;
            case "DATE":
                System.out.println("Yet to be implemented");
                break;
            case "READFILE":
                result = readFile(functionParam);
                break;
            default:
                System.out.println("Method is supported : " + funName);
        }
        return result;
    }

    public static String readFile(String filePath) {
        System.out.println("Inside read file function");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return sb.toString();
    }


    private static String getRandomAlphabet(String functionParam) {
        int n = Integer.parseInt(functionParam);
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder randomString = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            randomString.append(AlphaNumericString.charAt(index));
        }
        return randomString.toString();
    }

    private static String getUniqueNuber(String functionParam) {
        int uniqLen = Integer.parseInt(functionParam);
        if (uniqLen < 10) {
            int number = getRandomNumber(uniqLen);
            return number + "";
        } else {
            long currentTimestamp = System.currentTimeMillis();
            Long number = getLongNumber(currentTimestamp, uniqLen);
            return number + "";
        }
    }

    private static int getRandomNumber(int n) {
        int randomNum = (int) Math.pow(10, n - 1);
        return randomNum + new Random().nextInt(9 * randomNum);
    }

    private static long getLongNumber(long num, int n) {
        return (long) (num / Math.pow(10, Math.floor(Math.log10(num)) - n + 1));
    }

    private static String getCurrDate(String functionParam) {
        DateFormat dateFormat = new SimpleDateFormat(functionParam);
        Date dates = new Date();
        return dateFormat.format(dates);
    }

}
