package com.gemini.generic.api.utils;


import com.gemini.generic.feature.utils.ValueFixer;
import com.gemini.generic.utils.CommonUtils;
import com.google.gson.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.docstring.DocString;
import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Request {
    private String step;
    private String baseUrl;
    private String path;
    private String url;

    private final JsonObject requestObject = new JsonObject();
    private final JsonObject urlObject = new JsonObject();

    JsonObject headersObject = new JsonObject();
    private boolean isURLSet = false;

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    private String method;
    private Map<String, String> params = new HashMap<String, String>();
    private String requestPayload;
    private Map<String, String> headerMap = new HashMap<String, String>();
    private String headerString;
    private String contentType;
    private JsonElement requestBody;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

    public Request() {

    }

    public Request(String step, String method, String url, String requestPayload, String contentType,
                   Map<String, String> headers) {
        this.step = step;
        this.setMethod(method);
        this.setURL(url);
        this.setHeaders(headers);
        this.contentType = contentType;
        this.setRequestPayload(requestPayload);

    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        //String requestPaylodString=Paths.get(requestPayload).toFile().isFile()?CommonUtils.readPayLoad(new File(requestPayload)):CommonUtils.readPayLoad(requestPayload);
        String requestPaylodString = CommonUtils.readPayLoad(requestPayload);
        JsonElement element = ApiHealthCheckUtils.result(CommonUtils.convertStringInToJsonElement(requestPaylodString));
        this.requestPayload = CommonUtils.convertJsonElementToString(element);
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        setURL();
    }

    public void setBaseUrl(String baseUrl,boolean flag){
        this.baseUrl=baseUrl;
    }

    public String getPATH() {
        return this.path;
    }

    public void setpath(String path) {
        this.path = path;
    }

    public void setURL() {

        //if (!isURLSet) {
            try {
                URIBuilder builder = new URIBuilder(this.baseUrl);
                if (this.path != null) {
                    builder.setPath(this.path);
                }
                for (String paramKey : params.keySet()) {
                    builder.setParameter(paramKey, params.get(paramKey));
                }
                this.url = builder.build().toURL().toString();
                updateUrlObject();
            } catch (URISyntaxException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
        //}

    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String url) {
        this.url = url;
        this.isURLSet = true;
        updateUrlObject();
    }

    public void updateUrlObject() {
        try {
            URI uri = new URI(this.url);
            String path = uri.getPath();
            setpath(path);
            String baseUrl = uri.getScheme() + "://" + uri.getHost();
            setBaseUrl(baseUrl,true);
            this.baseUrl = baseUrl;
            String query = uri.getQuery();
            this.urlObject.addProperty("path", path);
            this.urlObject.addProperty("baseUrl", baseUrl);
            this.urlObject.addProperty("parameters", query);
            this.urlObject.addProperty("url", this.url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void setParameter(String key, String value) {
        params.put(key, value);
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String methodType) {
        this.method = methodType.toLowerCase();
    }

    public void createRequest(Object obj, Map<String, String> varmap) {
        String dataType = obj.getClass().getSimpleName();
        System.out.println(dataType);
        switch (dataType) {
            case "DataTable":
                createRequestFromDataTable((DataTable) obj);
                break;

            case "DocString":
                createRequestFromDocString((DocString) obj, varmap);
                break;
        }
    }

    private void createRequestFromDocString(DocString obj, Map<String, String> varmap) {
        String data = obj.getContent();
        JsonObject requestData = JsonParser.parseString(data).getAsJsonObject();
        requestData = ValueFixer.fixJsonElement(requestData, varmap).getAsJsonObject();
        Set<String> providedKeys = requestData.keySet();
        for (String key : providedKeys) {
            switch (key) {
                case "baseUrl":
                    setBaseUrl((CommonUtils.convertJsonElementToString(requestData.get(key))));
                    break;
                case "path":
                    setpath(CommonUtils.convertJsonElementToString(requestData.get(key)));
                    break;
                case "url":
                    setURL(CommonUtils.convertJsonElementToString(requestData.get(key)));
                    break;
                case "headers":
                    setHeaders(CommonUtils.convertJsonElementToString(requestData.get(key)));
                    break;
                case "params":
                    setParameter(CommonUtils.convertJsonElementToString(requestData.get(key)));
                    break;
                case "method":
                    setMethod(CommonUtils.convertJsonElementToString(requestData.get(key)));
                    break;
                case "requestBody":
                    setRequestBody(requestData.get(key));
                    break;
                case "expectedStatus":
                    break;
                default:
                    System.out.print("UNSUPPORTED KEY FOR CREATING REQUEST : " + key);
            }
        }
        setURL();
    }

    private void setRequestBody(JsonElement requestBody) {
        this.requestBody = requestBody;
        this.setRequestPayload(CommonUtils.convertJsonElementToString(requestBody));
    }

    /*
     * Parameter
     */
    public void setParameter(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            setParameter(jsonElement.getAsString());
        } else if (jsonElement.isJsonObject()) {
            Gson gson = new Gson();
            Map<String, String> parameterMap = gson.fromJson(jsonElement, Map.class);
            setParameter(parameterMap);
        }
    }

    public void setParameter(Map<String, String> parameterAsMap) {
        this.params = parameterAsMap;
    }

    public void setParameter(String paramsAsString) {
        JsonElement pramJson = CommonUtils.convertStringInToJsonElement(paramsAsString);
        if (pramJson.isJsonPrimitive()) {
            String[] paramArray = paramsAsString.split("&");
            for (String paramString : paramArray) {
                String[] paramKeyValue = paramString.split("=");
                setParameter(paramKeyValue[0], paramKeyValue[1]);
            }
        } else {
            setParameter(pramJson);
        }
    }

    /*
     * Headers
     */
    public void setHeaders(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            setHeaders(jsonElement.getAsString());
        } else if (jsonElement.isJsonObject()) {
            Gson gson = new Gson();
            Map<String, String> headerMap = gson.fromJson(jsonElement, Map.class);
            setHeaders(headerMap);
        }
    }

    public void setHeaders(String headerString) {
        JsonElement headerElement = CommonUtils.convertStringInToJsonElement(headerString);
        setHeaders(headerElement);
    }

    public void setHeaders(Map<String, String> headerKeyValueMap) {
        this.headerMap = headerKeyValueMap;
        this.headersObject = gson.toJsonTree(this.headerMap).getAsJsonObject();

    }

    public void setHeader(String key, String value) {
        this.headerMap.put(key, value);
        this.headersObject = gson.toJsonTree(this.headerMap).getAsJsonObject();
    }

    private void createRequestFromDataTable(DataTable obj) {
    }


    public String toString() {
        this.requestObject.add("URL", urlObject);
        this.requestObject.add("Header", headersObject);
        this.requestObject.addProperty("Method", this.getMethod());
        if (getRequestPayload() != null)
            this.requestObject.add("Request PayLoad", CommonUtils.convertStringInToJsonElement(getRequestPayload()));
        return "Request : \n" + CommonUtils.convertJsonElementToString(this.requestObject);
    }


}

