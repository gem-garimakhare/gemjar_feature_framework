package com.gemini.generic.api.utils;


import com.gemini.generic.reporting.GemTestReporter;
import com.gemini.generic.reporting.STATUS;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ApiHealthCheckUtils {

    static JsonArray result = new JsonArray();

    public static JsonArray deepSearch(JsonElement request, String key) {
        if (request instanceof JsonArray) {
            deepSearch(request.getAsJsonArray(), key, "");
        } else if (request instanceof JsonObject) {
            deepSearch(request.getAsJsonObject(), key, "");
        }

        return result;
    }

    private static void deepSearch(JsonArray asJsonArray, String key, String path) {
        for (int l = 0; l < asJsonArray.size(); l++) {
            if (asJsonArray.get(l) instanceof JsonObject) {
                deepSearch((JsonObject) asJsonArray.get(l), key, path + "." + l);
            }
            if (asJsonArray.get(l) instanceof JsonArray) {
                deepSearch((JsonArray) asJsonArray.get(l), key, path + "." + l);
            }
        }
    }

    private static void deepSearch(JsonObject obj, String key, String path) {
        Set<?> keyList = obj.keySet();
        Iterator<?> i = keyList.iterator();
        if (keyList.contains(key)) {
            JsonObject tempObj = new JsonObject();
            tempObj.addProperty(obj.get(key).getAsString(), "body" + path + "." + key);
            result.add(tempObj);
        }
        do {
            try {
                String value = i.next().toString();
                if (obj.get(value) instanceof JsonObject) {
                    JsonObject subObj = (JsonObject) obj.get(value);
                    deepSearch(subObj, key, path + "." + value);
                } else if (obj.get(value) instanceof JsonArray) {
                    deepSearch(obj.get(value).getAsJsonArray(), key, path + "." + value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (i.hasNext());
    }

    public static boolean assertionMethods(String key, String actualValue, String expectedValue, String operator, String loc) {
        switch (operator.toUpperCase()) {
            case "TO": {
                if (actualValue != null && actualValue.equalsIgnoreCase(expectedValue)) {
                    GemTestReporter.addTestStep("DeepSearch Successful of key ~ '" + key + "'", "Expected Value of the Key ~ " + expectedValue + "<BR>Actual Value of the Key ~ " + actualValue + "<BR> Location ~ " + loc, STATUS.PASS);
                    return true;
                }
//                else {
////                    GemTestReporter.addTestStep("DeepSearch Successful of key ~ '" + key + "'", "Expected Value of the Key ~ "+target+"<BR>Actual Value of the Key ~ " + temp + "<BR> Location ~ " + loc, STATUS.FAIL);
//                }
                break;
            }

            case "IN": {
                JsonArray targetArray = JsonParser.parseString(expectedValue).getAsJsonArray();
                for (int i = 0; i < targetArray.size(); i++) {
                    if (targetArray.get(i).getAsString().equals(actualValue)) {
                        GemTestReporter.addTestStep("DeepSearch Successful of key ~ '" + key + "'", "Expected Value of the Key must be in Array ~ " + expectedValue + "<BR>Actual Value of the Key ~ " + actualValue + "<BR> Location ~ " + loc, STATUS.PASS);
                        return true;
                    }
                }
                break;
            }

            case "CONTAINS": {
                if (actualValue != null && actualValue.contains(expectedValue)) {
                    GemTestReporter.addTestStep("DeepSearch Successful of key ~ '" + key + "'", "Expected Value of the Key must contains ~ " + expectedValue + "<BR>Actual Value of the Key ~ " + actualValue + "<BR> Location ~ " + loc, STATUS.PASS);
                    return true;
                }
//                else {
////                    GemTestReporter.addTestStep("DeepSearch Successful of key ~ '" + key + "'", "Expected Value of the Key must contains ~ "+target+"<BR>Actual Value of the Key ~ " + temp + "<BR> Location ~ " + loc, STATUS.FAIL);
//                }
                break;
            }
        }
        return false;
    }

    //////GHERKINS

    public static String Replace(String value, Map hashMap) {
        while (value.contains("#")) {
            int startIndex = value.indexOf("#");//akash#garg#
            int endIndex = value.substring(startIndex + 1).indexOf("#") + startIndex + 1;
            String key = value.substring(startIndex + 1, endIndex);
            String target;
            int startIndexBracket = key.indexOf("[");
            int endIndexBacket = key.indexOf("]");
            int bracketValue = Integer.parseInt(key.substring(startIndexBracket + 1, endIndexBacket));
            JsonElement loc = (JsonElement) hashMap.get("test_response_" + bracketValue);
            String query = value.substring(startIndex + endIndexBacket + 2, endIndex);
            target = ApiHealthCheckUtils.postAssertion(loc.getAsJsonObject().get("responseBody"), query);
            value = value.replace("#" + key + "#", target);
        }
        return value;
    }

    ///////////////////POST ASSERTION

    public static void postAssertion(JsonElement responseBody, String query, String operator, String target) {
        boolean flag = true;
        JsonElement tempResponseBody = responseBody;
        String tempQuery = query;

        while (flag) {
            int startIndex = query.indexOf("(");
            int endIndex = query.indexOf(")");
            String key = query.substring(startIndex + 1, endIndex);

            tempResponseBody = ApiHealthCheckUtils.getLeftQuery(tempResponseBody, key);

            query = query.substring(endIndex + 1);

            if (
                    query.length() <= 0) {
                flag = false;
            }
        }

        try {
            switch (operator.toUpperCase()) {
                case "TO": {
                    if (tempResponseBody != null && tempResponseBody.getAsString().equalsIgnoreCase(target)) {

                        GemTestReporter.addTestStep("Post_Validation", "<b>Validation Query: </b>" + tempQuery + "<br>Expected_Value: " + target + "<br>Actual_Value: " + tempResponseBody.getAsString(), STATUS.PASS);
                    } else {
                        assert tempResponseBody != null;
                        GemTestReporter.addTestStep("Post_Validation", "<b>Validation Query: </b>" + tempQuery + "<br>Expected_Value: " + target + "<br>Actual_Value: " + tempResponseBody.getAsString(), STATUS.FAIL);
                    }
                    break;
                }

                case "IN": {
                    JsonArray targetArray = JsonParser.parseString(target).getAsJsonArray();
                    if (targetArray.contains(tempResponseBody)) {
                        assert tempResponseBody != null;
                        GemTestReporter.addTestStep("Post_Validation", "<b>Validation Query: </b>" + tempQuery + "<br>Expected_Value: " + target + "<br>Actual_Value: " + tempResponseBody.getAsString(), STATUS.PASS);
                    } else {
                        assert tempResponseBody != null;
                        GemTestReporter.addTestStep("Post_Validation", "<b>Validation Query: </b>" + tempQuery + "<br>Expected_Value: " + target + "<br>Actual_Value: " + tempResponseBody.getAsString(), STATUS.FAIL);
                    }
                    break;
                }

                case "CONTAINS": {
                    if (tempResponseBody != null && tempResponseBody.getAsString().toUpperCase().contains(target.toUpperCase())) {
                        GemTestReporter.addTestStep("Post_Validation", "<b>Validation Query: </b>" + tempQuery + "<br>Expected_Value: " + target + "<br>Actual_Value: " + tempResponseBody.getAsString(), STATUS.PASS);
                    } else {
                        assert tempResponseBody != null;
                        GemTestReporter.addTestStep("Post_Validation", "<b>Validation Query: </b>" + tempQuery + "<br>Expected_Value: " + target + "<br>Actual_Value: " + tempResponseBody.getAsString(), STATUS.FAIL);
                    }
                    break;
                }
            }
        } catch (NullPointerException e) {
            GemTestReporter.addTestStep("Some error occurred in Post Assertion", "Some error occurred <BR>error message ~ " + e.getMessage(), STATUS.FAIL);
            e.printStackTrace();
        }
    }

    private static JsonElement getLeftQuery(JsonElement request, String key) {
        boolean isNumeric = key.chars().allMatch(Character::isDigit);
        if (request instanceof JsonArray && isNumeric) {
            return request.getAsJsonArray().get(Integer.parseInt(key));
        }
        if (request instanceof JsonObject && !isNumeric) {
            return request.getAsJsonObject().get(key);
        }
        return null;
    }

    public static String postAssertion(JsonElement responseBody, String query) {
        boolean flag = true;
        JsonElement tempResponseBody = responseBody;

        while (flag) {
            int startIndex = query.indexOf("(");
            int endIndex = query.indexOf(")");
            String key = query.substring(startIndex + 1, endIndex);

            tempResponseBody = ApiHealthCheckUtils.getLeftQuery(tempResponseBody, key);

            query = query.substring(endIndex + 1);

            if (
                    query.length() <= 0) {
                flag = false;
            }
        }

        assert tempResponseBody != null;
        return tempResponseBody.getAsString();
    }

    /////////////////////VARIABLE REPLACEMENT

    public static JsonElement result(JsonElement obj) {
        return getResultantJson(obj);
    }

    private static JsonElement getResultantJson(JsonElement requestBody) {
        String requestBodyString = requestBody.toString();
        char search = '#';             // Character to search is '#'.
        int HashCount = 0;
        for (int i = 0; i < requestBodyString.length(); i++) {
            if (requestBodyString.charAt(i) == search)
                HashCount++;
        }
        int number = HashCount / 2;
        for (int k = 0; k < number; k++) {
            requestBodyString = getResultantString(requestBodyString);
        }
        return JsonParser.parseString(requestBodyString);
    }

    private static String getResultantString(String requestBodyString) {
        int first = requestBodyString.indexOf("#");
        int second = requestBodyString.indexOf("#", first + 1);
        String start = requestBodyString.substring(0, first);
        String end = requestBodyString.substring(second + 1);
        String buffer = requestBodyString.substring(first + 1, second);
        if (buffer.contains("test_response")) {
            String t = ApiHealthCheckUtils.Replace("#" + buffer + "#", GemJarGlobalVar.globalResponseHM);
            return start + t + end;
        } else {
            String[] arrays = buffer.split("-", 2);
            String user = arrays[0];
            String functionKey = user.toUpperCase();
            if (functionKey.contains("UNIQUE")) {
                int uniqLen = Integer.parseInt(arrays[1]);
                if (uniqLen < 10) {
                    int number = getRandomNumber(uniqLen);
                    requestBodyString = start + number + end;
                    return requestBodyString;
                } else {
                    long currentTimestamp = System.currentTimeMillis();
                    long number = getLongNumber(currentTimestamp, uniqLen);
                    requestBodyString = start + number + end;
                    return requestBodyString;
                }
            } else if (functionKey.contains("CURR")) {
                String dateFormat = arrays[1];
                return dateTime(dateFormat, start, end);
            } else if (functionKey.contains("ALPHA")) {
                int alphaLen = Integer.parseInt(arrays[1]);
                String randomAlpha = getAlphaNumericString(alphaLen);
                return start + randomAlpha + end;
            } else if (functionKey.contains("EPOCH")) {
                long currentTimestamp = System.currentTimeMillis();
                requestBodyString = start + currentTimestamp + end;
                return requestBodyString;
            } else if ((functionKey.contains("UNQ"))) {
                String rangeString = arrays[1];
                String[] ar = rangeString.split("_", 2);
                int firstpart = Integer.parseInt(ar[0]);
                int secondpart = Integer.parseInt(ar[1]);
                int randomNumber = xToY(firstpart, secondpart);
                return start + randomNumber + end;
            } else if (functionKey.contains("UUID")) {
                UUID uuid = UUID.randomUUID();
                String uuidAsString = uuid.toString();
                return start + uuidAsString + end;
            } else if (functionKey.contains("DATE")) {
                String input = arrays[1];
                char search = '-';             // Character to search is '-'.

                int count = 0;
                for (int i = 0; i < input.length(); i++) {
                    if (input.charAt(i) == search)
                        count++;
                }
                if (count == 3 || count == 4) {
                    String rightstring = arrays[1];
                    String[] arr = rightstring.split("-", 4);
                    String pattern = arr[0];
                    String code = arr[1];
                    String add = arr[2];
                    int value = Integer.parseInt(arr[3]);
                    String resultantdate = date(pattern, code, add, value);
                    return start + resultantdate + end;
                } else if (count == 1) {
                    String rightstring = arrays[1];
                    String[] arr = rightstring.split("-", 2);
                    String pattern = arr[0];
                    String code = arr[1];
                    String resultantdate = date(pattern, code);
                    return start + resultantdate + end;
                }
            } else if (functionKey.contains("RANDOM")) {
                String str = arrays[1];
                String number = numfromlist(str);
                return start + number + end;
            }
        }
        return requestBodyString;
    }

    ////////////////////////////////////////////////////////////////
    public static String date(String pattern, String code, String timeadd, int value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        ZoneId zoneId = ZoneId.of(code);

        ZonedDateTime now = ZonedDateTime.now(zoneId);

        String Timeadd = timeadd.toUpperCase();
        if (Timeadd.equals("H")) {
            ZonedDateTime zonedDateTime1 = now.plusHours(value);

            return zonedDateTime1.format(formatter);
        } else if (Timeadd.equals("MIN")) {
            ZonedDateTime zonedDateTime1 = now.plusMinutes(value);

            return zonedDateTime1.format(formatter);
        } else if (Timeadd.equals("MON")) {
            ZonedDateTime zonedDateTime1 = now.plusMonths(value);

            return zonedDateTime1.format(formatter);
        } else if (Timeadd.contains("DAY")) {
            ZonedDateTime zonedDateTime1 = now.plusDays(value);

            return zonedDateTime1.format(formatter);
        } else if (Timeadd.contains("SEC")) {
            ZonedDateTime zonedDateTime1 = now.plusSeconds(value);

            return zonedDateTime1.format(formatter);
        } else if (Timeadd.contains("WEEK")) {
            ZonedDateTime zonedDateTime1 = now.plusWeeks(value);

            return zonedDateTime1.format(formatter);
        } else if (Timeadd.contains("YEAR")) {
            ZonedDateTime zonedDateTime1 = now.plusYears(value);

            return zonedDateTime1.format(formatter);
        }
        return null;
    }

    public static String date(String hash, String number) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(hash);

        ZoneId zoneId = ZoneId.of(number);

        ZonedDateTime now = ZonedDateTime.now(zoneId);
        return now.format(formatter);
    }

    ////////////////////////////////////////////////////////

    public static String numfromlist(String numbers) {
        numbers.replace("[", "");
        numbers.replace("]", "");
        int length = numbers.length();

        String newone = numbers.substring(1, length - 1);

        String[] getnum = newone.split(",");
        List<?> li = Arrays.asList(getnum);

        Random rand = new Random();

        return (String) li.get(rand.nextInt(li.size()));
    }


    ////////////////////////////////////////////////
    public static int xToY(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static int getRandomNumber(int n) {
        int randomNum = (int) Math.pow(10, n - 1);
        return randomNum + new Random().nextInt(9 * randomNum);
    }

    public static long getLongNumber(long num, int n) {
        return (long) (num / Math.pow(10, Math.floor(Math.log10(num)) - n + 1));
    }

    public static String dateTime(String format, String firstpart, String lastpsrt) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date dates = new Date();
        String s = dateFormat.format(dates);
        return firstpart + s + lastpsrt;
    }

    static String getAlphaNumericString(int n) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz";
        // create StringBuffer size of AlphaNumericString
        StringBuilder randomString = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length() * Math.random());
            // add Character one by one in end of sb
            randomString.append(AlphaNumericString.charAt(index));
        }
        return randomString.toString();
    }
}