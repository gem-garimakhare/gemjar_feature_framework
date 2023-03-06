package com.gemini.generic.tdd;

import com.gemini.generic.utils.GemJarConstants;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.google.gson.*;
import org.testng.ITestNGMethod;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GemJarDataProvider {
    @DataProvider(name = "GemJarDataProvider")
    public static Object[][] GemJarTestDataProvider(ITestNGMethod testNGMethod) {
        try {
            String methodName = testNGMethod.getMethodName();
            String data;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (GemJarGlobalVar.testCaseDataJsonPath != null) {
                data = new String(Files.readAllBytes(new File(GemJarGlobalVar.testCaseDataJsonPath).toPath()));
            } else {
                /*data = IOUtils.toString(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(GemJarGlobalVar.testCaseFileName)),
                        StandardCharsets.UTF_8);*/
                data = GemJarGlobalVar.resourcemap.get(GemJarGlobalVar.testCaseFileName).getContentAsString();
            }
            JsonElement jsonElement = gson.fromJson(data, JsonElement.class);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement methodData = jsonObject.get(methodName).getAsJsonObject().get(GemJarConstants.TESTCASE_JSON_INPUT_TAG);
            if (methodData.isJsonArray()) {
                JsonArray methodDataArray = methodData.getAsJsonArray();
                int methodDataSize = methodDataArray.size();
                Object[][] obj = new Object[methodDataSize][1];
                for (int i = 0; i < methodDataSize; i++) {
                    obj[i][0] = methodDataArray.get(i).getAsJsonObject();
                }
                return obj;
            } else {
                return new Object[][]{{methodData}};
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }

    }
}
