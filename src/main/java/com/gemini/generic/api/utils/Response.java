package com.gemini.generic.api.utils;


import com.gemini.generic.utils.CommonUtils;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Instant;


public class Response {

    private final static Logger logger = LogManager.getLogger(Response.class);
    private int Status;
    private String responseMessage;
    private String errorMessage;
    private String responseBody;
    private JsonElement responseBodyObject;
    private String execTime;

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    private String requestHeaders;
    private final JsonObject jsonObject = new JsonObject();
    private final Gson gson = new GsonBuilder().setLenient().create();

    public Response() {

    }

    public Response(JsonElement responseData) {
        JsonObject response = responseData.getAsJsonObject();
        this.Status = response.get("status").getAsInt();
        this.responseMessage = response.get("responseMessage").getAsString();
        this.responseBody = response.get("responseBody") != null ? response.get("responseBody").toString() : null;
        this.responseBodyObject = response.get("responseBody") != null ? response.get("responseBody") : null;
        this.errorMessage = response.get("responseError") != null ? response.get("responseError").toString() : null;
        this.execTime = response.get("execTime") != null ? response.get("execTime").getAsString() : null;
        setJsonObject();
    }

    public Response(HttpURLConnection httpsCon, long startTime, String requestHeaders) {
        try {
            this.Status = httpsCon.getResponseCode();
            this.responseMessage = httpsCon.getResponseMessage();
            this.errorMessage = CommonUtils.getDataFromBufferedReader(httpsCon.getErrorStream());
            this.responseBody = this.errorMessage == null ? CommonUtils.getDataFromBufferedReader(httpsCon.getInputStream()) : null;
            this.responseBodyObject = this.responseBody != null ? CommonUtils.convertStringInToJsonElement(this.responseBody) : null;
            this.execTime = String.valueOf(Instant.now().toEpochMilli() - startTime);
            this.requestHeaders = requestHeaders;
            setJsonObject();
        } catch (IOException e) {
            logger.info("I/O Exception Occured while creating response Constructor");
            throw new RuntimeException(e);
        }
    }

    public int getStatus() {
        return this.Status;
    }

    public void setStatus(int status) {
        this.Status = status;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public JsonElement getResponseBodyJson() {
        return this.responseBodyObject;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setExecTime(String responseTimeInMilliSec) {
        this.execTime = responseTimeInMilliSec;
    }

    public String getExecTime() {
        return this.execTime;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject() {
        jsonObject.addProperty("status", this.getStatus());
        jsonObject.addProperty("requestHeaders", requestHeaders);
        jsonObject.addProperty("responseMessage", this.getResponseMessage());
        jsonObject.add("responseError", this.getErrorMessage() != null ? JsonParser.parseString(this.getErrorMessage()) : null);
        jsonObject.add("responseBody", this.getResponseBody() != null ? JsonParser.parseString(this.getResponseBody()) : null);
        jsonObject.addProperty("execTime", this.getExecTime() + " ms");
    }

    public String toString() {
        String response = "Response : \n" + gson.toJson(jsonObject);
        return response;

    }
}
