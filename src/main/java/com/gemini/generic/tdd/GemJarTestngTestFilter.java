package com.gemini.generic.tdd;

import com.gemini.generic.utils.GemJarGlobalVar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GemJarTestngTestFilter implements IMethodInterceptor {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        List<IMethodInstance> testCasesToRun = new ArrayList<IMethodInstance>();
        String data = null;
        if (GemJarGlobalVar.testCaseDataJsonPath != null) {
            try {
                data = new String(Files.readAllBytes(new File(GemJarGlobalVar.testCaseDataJsonPath).toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                data = GemJarGlobalVar.resourcemap.get(GemJarGlobalVar.testCaseFileName).getContentAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JsonElement jsonElement = gson.fromJson(data, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (GemJarGlobalVar.testCasesToRun != null) {

            for (IMethodInstance iMethodInstance : methods) {
                String methodName = iMethodInstance.getMethod().getConstructorOrMethod().getMethod().getName();
                JsonObject methodJson = jsonObject.get(methodName) != null
                        ? jsonObject.get(methodName).getAsJsonObject()
                        : null;
                if (GemJarGlobalVar.testCasesToRun.contains(methodName) && (methodJson != null)) {
                    testCasesToRun.add(iMethodInstance);
                }

            }

        } else {
            for (IMethodInstance iMethodInstance : methods) {
                String methodName = iMethodInstance.getMethod().getConstructorOrMethod().getMethod().getName();
                JsonObject methodJson = jsonObject.get(methodName) != null
                        ? jsonObject.get(methodName).getAsJsonObject()
                        : null;
                if ((methodJson != null) && (methodJson.get("runFlag") != null)) {
                    if (methodJson.get("runFlag").getAsString().equalsIgnoreCase("Y")) {
                        testCasesToRun.add(iMethodInstance);
                    }
                }
            }
        }
        return testCasesToRun;
    }
}
