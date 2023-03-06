package com.gemini.generic.api.utils;


import com.gemini.generic.exception.GemException;
import com.gemini.generic.reporting.GemTestReporter;
import com.gemini.generic.reporting.STATUS;
import com.gemini.generic.utils.CommonUtils;
import com.gemini.generic.utils.GemJarConstants;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.gemini.generic.utils.GemJarUtils;
import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApiInvocationImpl extends ApiInvocation {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // healthCheck function for JSON file
    @SuppressWarnings("unchecked")


    public static String getParameterizedUrl(String url, Map<String, String> params) throws GemException {
        try {
            URIBuilder ub = new URIBuilder(url);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                ub.addParameter(key, value);
            }
            return ub.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static JsonArray healthCheckJson(JsonArray req) throws GemException {
        GemJarGlobalVar.expected_testcases += req.size();
        JsonArray responseJson = new JsonArray();
        Map<String, JsonElement> responseHashMap = new HashMap<String, JsonElement>();
        for (int i = 0; i < req.size(); i++) {
            JsonObject test = (JsonObject) req.get(i);
            String step = test.get("test_name").getAsString();
            // Start Report
            GemTestReporter.startTestCase(step, "Health Check", false);
            String method = test.get("method").getAsString();
            String url = test.get("endpoint").getAsString();
            if (url.contains("test_response")) {
                url = ApiHealthCheckUtils.Replace(url, responseHashMap);
            }
            int expectedStatus = test.get("expected_status").getAsInt();
            String payload = null;
            Map<String, String> headers = new HashMap<String, String>();
            Map<String, String> parameters = new HashMap<String, String>();
            boolean isValidationRequired = false;
            JsonObject validationQueries = null;
            if (test.has("request_body")) {
                payload = String.valueOf(test.get("request_body").getAsJsonObject());
                payload = String.valueOf(ApiHealthCheckUtils.result(JsonParser.parseString(payload)));
            }
            if (test.has("headers")) {
                if (test.get("headers").toString().contains("test_response")) {
                    headers = (Map<String, String>) gson.fromJson(ApiHealthCheckUtils.Replace(test.get("headers").toString(), responseHashMap), headers.getClass());
                } else {
                    headers = (Map<String, String>) gson.fromJson(test.get("headers").toString(), headers.getClass());
                }
            }
            if (test.has("parameters")) {
                parameters = (Map<String, String>) gson.fromJson(test.get("parameters").toString(), parameters.getClass());
                if (test.get("parameters").toString().contains("test_response")) {
                    parameters = (Map<String, String>) gson.fromJson(ApiHealthCheckUtils.Replace(test.get("parameters").toString(), responseHashMap), parameters.getClass());
                }
                url = getParameterizedUrl(url, parameters);
            }
            if (test.has("post_validation")) {
                validationQueries = test.get("post_validation").getAsJsonObject();
                if (test.get("post_validation").getAsJsonObject().toString().contains("test_response")) {
                    validationQueries = JsonParser.parseString(ApiHealthCheckUtils.Replace(test.get("post_validation").getAsJsonObject().toString(), responseHashMap)).getAsJsonObject();
                }
                isValidationRequired = true;
            }
            GemTestReporter.addTestStep("<b>FeatureRequest: " + step + "</b>",
                    "<b>FeatureRequest Url :</b>" + url + "<br> <b>RequestHeaders :</b>" + headers, STATUS.INFO);
            if (!(payload == null)) {
                GemTestReporter.addTestStep("Payload", payload, STATUS.INFO);
            }
            try {
                Response response = invokeRequestMethod(new Request(step, method, url, payload, null, headers));
                assert response != null;
                responseJson.add(response.getJsonObject());
                responseHashMap.put("test_response_" + i, response.getJsonObject());
                GemJarGlobalVar.globalResponseHM = responseHashMap;
                String executionTime = response.getExecTime();
                String requestHeaders = response.getRequestHeaders();
                String responseMessage = null;
                responseMessage = response.getResponseMessage();
                if (!(response.getStatus() >= 200 && response.getStatus() < 300)) {
                    responseMessage = response.getErrorMessage();
                }
                int actualStatus = response.getStatus();
                if (expectedStatus != 0) {
                    String description = "<b>Actual Status: </b>" + actualStatus + "<br> <b>Expected Status: </b>"
                            + expectedStatus + "<br> <b>ResponseMessage : </b>" + responseMessage
                            + "<br> <b>ExecutionTime: </b>"
                            + executionTime + "<br> <b>ResponseBody: </b>" + response.getResponseBody();
                    if (expectedStatus == actualStatus) {
                        GemTestReporter.addTestStep("<b>Response: " + step + "</b>", description, STATUS.PASS);
                    } else {
                        GemTestReporter.addTestStep("<b>Response: " + step + "</b>", description, STATUS.FAIL);
                    }
                } else {
                    if (actualStatus >= 200 && actualStatus < 300) {
                        GemTestReporter.addTestStep("<b>Response: " + step + "</b>", "Status Code: " + actualStatus,
                                STATUS.PASS);
                    } else {
                        GemTestReporter.addTestStep("<b>Response: " + step + "</b>", "Status Code: " + actualStatus,
                                STATUS.FAIL);
                    }
                }
                if (isValidationRequired) {
                    Set<String> keySet = validationQueries.keySet();
                    for (String query : keySet) {
                        String targetQuery = validationQueries.get(query).getAsString();
                        int index = targetQuery.indexOf(" ");
                        String operator = targetQuery.substring(0, index);
                        String target = targetQuery.substring(index + 1);
                        if (query.toUpperCase().contains("DEEPSEARCH")) {
                            String deepSearchQuery = query.substring(query.indexOf("(") + 1, query.indexOf(")"));
                            // Call the deepSearch function here with keyname as "deepSearchQuery"
                            JsonArray result = ApiHealthCheckUtils.deepSearch(JsonParser.parseString(response.getResponseBody()), deepSearchQuery);
                            if (result.size() == 0) {
                                GemTestReporter.addTestStep("DeepSearch of key ~ " + deepSearchQuery, "DeepSearch Failed <BR> No Such Key Exist in Response", STATUS.FAIL);
                            } else {
                                boolean f = false;
                                for (int j = 0; j < result.size(); j++) {
                                    String value = result.get(j).getAsJsonObject().keySet().iterator().next();
                                    String loc = result.get(j).getAsJsonObject().get(value).getAsString();
                                    boolean temp = ApiHealthCheckUtils.assertionMethods(deepSearchQuery, value, target, operator, loc);
                                    if (temp) {
                                        f = temp;
                                    }
                                }
                                if (!f) {
                                    GemTestReporter.addTestStep("DeepSearch of key ~ " + deepSearchQuery, "DeepSearch Failed <BR> Expected value does not match actual value <BR> Expected value ~ " + target, STATUS.FAIL);
                                }
                            }
                        } else {
                            ApiHealthCheckUtils.postAssertion(JsonParser.parseString(response.getResponseBody()), query, operator, target);
                        }
                    }
                }
            } catch (Exception e) {
                GemTestReporter.addTestStep("Some error occurred", "Some error occurred", STATUS.FAIL);
                throw new GemException(e.getMessage());
            }
            //end Report
            GemTestReporter.endTestCase();
        }
        return responseJson;
    }

    private static JsonArray healthCheck(JsonArray req) throws GemException {
        return healthCheckJson(req);
    }

    public static Response invokeRequestMethod(Request request) {
        Object obj = CommonUtils.genericInvokeMethod(new ApiInvocation(), "executeRequest", request);
        if (null != obj) {
            return (Response) obj;
        }
        return null;
    }

    private static JsonArray healthCheckJsonWithoutNewTC(JsonArray req) throws GemException {
        JsonArray responseJson = new JsonArray();
        Map<String, JsonElement> responseHashMap = new HashMap<String, JsonElement>();
        for (int i = 0; i < req.size(); i++) {
            JsonObject test = (JsonObject) req.get(i);
            String step = test.get("test_name").getAsString();
            String method = test.get("method").getAsString();
            String url = test.get("endpoint").getAsString();
            if (url.contains("test_response")) {
                url = ApiHealthCheckUtils.Replace(url, responseHashMap);
            }
            int expectedStatus = test.get("expected_status").getAsInt();
            String payload = null;
            Map<String, String> headers = new HashMap<String, String>();
            Map<String, String> parameters = new HashMap<String, String>();
            boolean isValidationRequired = false;
            JsonObject validationQueries = null;
            if (test.has("request_body")) {
                payload = String.valueOf(test.get("request_body").getAsJsonObject());
                payload = String.valueOf(ApiHealthCheckUtils.result(JsonParser.parseString(payload)));
            }
            if (test.has("headers")) {
                if (test.get("headers").toString().contains("test_response")) {
                    headers = (Map<String, String>) gson.fromJson(ApiHealthCheckUtils.Replace(test.get("headers").toString(), responseHashMap), headers.getClass());
                } else {
                    headers = (Map<String, String>) gson.fromJson(test.get("headers").toString(), headers.getClass());
                }
            }
            if (test.has("parameters")) {
                parameters = (Map<String, String>) gson.fromJson(test.get("parameters").toString(), parameters.getClass());
                if (test.get("parameters").toString().contains("test_response")) {
                    parameters = (Map<String, String>) gson.fromJson(ApiHealthCheckUtils.Replace(test.get("parameters").toString(), responseHashMap), parameters.getClass());
                }
                url = getParameterizedUrl(url, parameters);
            }
            if (test.has("post_validation")) {
                validationQueries = test.get("post_validation").getAsJsonObject();
                if (test.get("post_validation").getAsJsonObject().toString().contains("test_response")) {
                    validationQueries = JsonParser.parseString(ApiHealthCheckUtils.Replace(test.get("post_validation").getAsJsonObject().toString(), responseHashMap)).getAsJsonObject();
                }
                isValidationRequired = true;
            }
            GemTestReporter.addTestStep("<b>FeatureRequest: " + step + "</b>",
                    "<b>FeatureRequest Url :</b>" + url + "<br> <b>RequestHeaders :</b>" + headers, STATUS.INFO);
            if (!(payload == null)) {
                GemTestReporter.addTestStep("Payload", payload, STATUS.INFO);
            }
            try {
                Response response = invokeRequestMethod(new Request(step, method, url, payload, null, headers));
                assert response != null;
                responseJson.add(response.getJsonObject());
                responseHashMap.put("test_response_" + i, response.getJsonObject());
                GemJarGlobalVar.globalResponseHM = responseHashMap;
                String executionTime = response.getExecTime();
                String responseMessage;
                responseMessage = response.getResponseMessage();
                if (!(response.getStatus() >= 200 && response.getStatus() < 300)) {
                    responseMessage = response.getErrorMessage();
                }
                int actualStatus = response.getStatus();
                if (expectedStatus != 0) {
                    String description = "<b>Actual Status: </b>" + actualStatus + "<br> <b>Expected Status: </b>"
                            + expectedStatus + "<br> <b>ResponseMessage : </b>" + responseMessage
                            + "<br> <b>ExecutionTime: </b>"
                            + executionTime + "<br> <b>ResponseBody: </b>" + response.getResponseBody();
                    if (expectedStatus == actualStatus) {
                        GemTestReporter.addTestStep("<b>Response: " + step + "</b>", description, STATUS.PASS);
                    } else {
                        GemTestReporter.addTestStep("<b>Response: " + step + "</b>", description, STATUS.FAIL);
                    }
                } else {
                    if (actualStatus >= 200 && actualStatus < 300) {
                        GemTestReporter.addTestStep("<b>Response: " + step + "</b>", "Status Code: " + actualStatus,
                                STATUS.PASS);
                    } else {
                        GemTestReporter.addTestStep("<b>Response: " + step + "</b>", "Status Code: " + actualStatus,
                                STATUS.FAIL);
                    }
                }
                if (isValidationRequired) {
                    Set<String> keySet = validationQueries.keySet();
                    for (String s : keySet) {
                        String targetQuery = validationQueries.get(s).getAsString();
                        int index = targetQuery.indexOf(" ");
                        String operator = targetQuery.substring(0, index);
                        String target = targetQuery.substring(index + 1);
                        if (s.toUpperCase().contains("DEEPSEARCH")) {
                            String deepSearchQuery = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
                            // Call the deepSearch function here with keyname as "deepSearchQuery"
                            JsonArray result = ApiHealthCheckUtils.deepSearch(JsonParser.parseString(response.getResponseBody()), deepSearchQuery);
                            if (result.size() == 0) {
                                GemTestReporter.addTestStep("DeepSearch of key ~ " + deepSearchQuery, "DeepSearch Failed <BR> No Such Key Exist in Response", STATUS.FAIL);
                            } else {
                                boolean f = false;
                                for (int j = 0; j < result.size(); j++) {
                                    String value = result.get(j).getAsJsonObject().keySet().iterator().next();
                                    String loc = result.get(j).getAsJsonObject().get(value).getAsString();
                                    boolean temp = ApiHealthCheckUtils.assertionMethods(deepSearchQuery, value, target, operator, loc);
                                    if (temp) {
                                        f = true;
                                    }
                                }
                                if (!f) {
                                    GemTestReporter.addTestStep("DeepSearch of key ~ " + deepSearchQuery, "DeepSearch Failed <BR> Expected value does not match actual value <BR> Expected value ~ " + target, STATUS.FAIL);
                                }
                            }
                        } else {
                            ApiHealthCheckUtils.postAssertion(JsonParser.parseString(response.getResponseBody()), s, operator, target);
                        }
                    }

                }
            } catch (Exception e) {
                GemTestReporter.addTestStep("Some error occurred", "Some error occurred", STATUS.FAIL);
                throw new GemException(e.getMessage());
            }
            //end Report
//            GemTestReporter.endTestCase();
        }
        return responseJson;
    }

    public static JsonArray healthCheck(File requestPayload) throws GemException {
        StringBuilder payload = new StringBuilder();
        try {
            FileReader fr = new FileReader(requestPayload);
            int i;
            // Holds true till there is nothing to read
            while ((i = fr.read()) != -1) {
                payload.append((char) i);
            }
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        JsonArray req = JsonParser.parseString(payload.toString()).getAsJsonArray();
        return healthCheckJson(req);
    }

    public static JsonArray healthCheck(String filePath) throws GemException {
        File fr = new File(filePath);
        return healthCheck(fr);
    }

    //Method to Do Reporting
    public static void doReporting(Response responseJSON, Request request) {
        int statusCode = responseJSON.getStatus();
        String step = request.getStep();
        String requestHeaders = responseJSON.getRequestHeaders();
        String url = request.getURL();
        String responseMessage = responseJSON.getResponseMessage();
        String responseBody = responseJSON.getResponseBody();
        String executionTime = responseJSON.getExecTime();
        if (!(responseJSON.getStatus() >= 200 && responseJSON.getStatus() < 300)) {
            responseMessage = responseJSON.getErrorMessage();
        }
        GemTestReporter.addTestStep("<b>Request: " + step + "</b>", "<b>Request Url :</b>" + url + "<br> <b>Request Headers :</b>" + requestHeaders, STATUS.INFO);
        String description = "<b>Status : </b>" + statusCode + "<br> <b>Response Message : </b>" + responseMessage + "<br> <b>ExecutionTime : </b>" + executionTime + " ms <br>" + " <b>ResponseBody : </b>" + responseBody;
        if (statusCode >= 200 && statusCode < 300) {
            GemTestReporter.addTestStep("<b>Response: " + step + "</b>", description, STATUS.PASS);
        } else {
            GemTestReporter.addTestStep("<b>Response: " + step + "</b>", description, STATUS.FAIL);
        }
    }

    public static String fileUpload(String filePath) throws IOException {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            MultipartEntityBuilder entitybuilder = MultipartEntityBuilder.create();
            entitybuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entitybuilder.addBinaryBody("file", new File(filePath));
            HttpEntity mutiPartHttpEntity = entitybuilder.build();
            org.apache.http.client.methods.RequestBuilder reqbuilder = org.apache.http.client.methods.RequestBuilder.post(GemJarGlobalVar.BUCKET_API);
            reqbuilder.addParameter("tag", "Protected");
            reqbuilder.addParameter("s_run_id", GemJarGlobalVar.s_run_id);
            reqbuilder.setEntity(mutiPartHttpEntity);
            HttpUriRequest multipartRequest = reqbuilder.build();
            multipartRequest.setHeader(new BasicHeader("username", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_USER_NAME)));
            multipartRequest.setHeader(new BasicHeader("bridgeToken", GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_REPORTING_TOKEN)));
            HttpResponse httpresponse = httpclient.execute(multipartRequest);
            JsonObject js = (JsonObject) JsonParser.parseString(EntityUtils.toString(httpresponse.getEntity()));
            JsonElement jsonElement = js.getAsJsonObject().get("data");
            if (jsonElement instanceof JsonArray) {
                return js.get("data").getAsJsonArray().get(0).getAsJsonObject().get("Url").getAsString();
            } else {
                return js.getAsJsonObject().get("data").getAsJsonObject().get("Url").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
