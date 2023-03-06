package com.gemini.generic.reporting;

import com.gemini.generic.api.utils.ApiInvocation;
import com.gemini.generic.api.utils.ApiInvocationImpl;
import com.gemini.generic.api.utils.Request;
import com.gemini.generic.api.utils.Response;
import com.gemini.generic.utils.CommonUtils;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RunnableClass implements Runnable {

    private final Request request;

    private final static Logger logger = LogManager.getLogger(RunnableClass.class);

    public RunnableClass(Request req) {
        this.request = req;
    }

    @Override
    public void run() {
        try {
            if (request.getRequestPayload() != null && request.getRequestPayload().contains("ScreenShot")) {
                updateRequestPayloadForScreenShot();
            }
            Response testcase = ApiInvocation.handleRequest(request);
            if (testcase.getStatus() == 201) {
                logger.info("testcase data upload successful");
            } else {
                GemJarGlobalVar.failedTestcaseData.add(request);
                logger.info("testcase data upload failed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRequestPayloadForScreenShot() throws ExecutionException, InterruptedException {
        JsonObject requestBodyJsonObject = CommonUtils.convertStringInToJsonElement(request.getRequestPayload()).getAsJsonObject();
        JsonArray stepsArray = requestBodyJsonObject.get("steps").getAsJsonArray();
        ArrayList<CompletableFuture<String>> remoteScrnShotList = new ArrayList<CompletableFuture<String>>();
        for (int i = 0; i < stepsArray.size(); i++) {
            JsonObject stepObject = stepsArray.get(i).getAsJsonObject();
            if (stepObject.has("ScreenShot")) {
                String screenShotLocalPath = GemJarGlobalVar.reportLocation + "/" + stepObject.get("ScreenShot").getAsString();
                CompletableFuture<String> futureRemotePath = CompletableFuture.supplyAsync(() -> {
                    String remotePath = null;
                    try {
                        remotePath = ApiInvocationImpl.fileUpload(screenShotLocalPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return remotePath;
                });
                remoteScrnShotList.add(futureRemotePath);
            }
        }
        CompletableFuture<String>[] remotePathArray = (CompletableFuture<String>[]) Array.newInstance(CompletableFuture.class, remoteScrnShotList.size());
        remotePathArray = remoteScrnShotList.toArray(remotePathArray);
        CompletableFuture.allOf(remotePathArray);
        for (int i = 0, j = 0; i < stepsArray.size(); i++) {
            JsonObject stepObject = stepsArray.get(i).getAsJsonObject();
            if (stepObject.has("ScreenShot")) {
                stepObject.addProperty("ScreenShot", remoteScrnShotList.get(j).get());
                j++;
            }
        }
        request.setRequestPayload(requestBodyJsonObject.toString());
    }

}
