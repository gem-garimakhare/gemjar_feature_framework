package com.gemini.generic.utils;

import com.gemini.generic.api.utils.ApiInvocation;
import com.gemini.generic.api.utils.Request;
import com.gemini.generic.api.utils.Response;
import com.gemini.generic.exception.GemException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommonUtils {

    private final static Logger logger = LogManager.getLogger(CommonUtils.class);

    public static String convertJsonElementToString(JsonElement jsonElement) {
        return jsonElement != null ? (jsonElement.isJsonNull() ? null : jsonElement.isJsonPrimitive() ? jsonElement.getAsJsonPrimitive().getAsString() : jsonElement.toString()) : null;

    }

    public static String getDecryptedPwd(String encryptedPwd) {
        String decryptedPwd = "";
        for (int i = encryptedPwd.length() - 1; i >= 0; i--) {
            decryptedPwd += (char) ((int) encryptedPwd.charAt(i) - 1);
        }
        return decryptedPwd;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static void writeDataToOutputStream(final OutputStream outputStream, final String jsonStringPayload) {
        OutputStream os = null;
        try {
            os = outputStream;
            os.write(jsonStringPayload.getBytes());

        } catch (Exception e) {
            logger.info("Exception Occured while Writing Data to Stream");
        } finally {
            try {
                os.close();
                os.flush();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                logger.info("Exception Occured while closing Stream");
                throw new RuntimeException(e);
            }
        }
    }

    public static String readPayLoad(Object requestPayload) {
        StringBuilder payload = null;
        if (requestPayload instanceof File) {
            File requestPayLoadFile = (File) requestPayload;
            FileReader fr = null;
            payload = new StringBuilder();
            try {
                fr = new FileReader(requestPayLoadFile);
                int i;
                // Holds true till there is nothing to read
                while ((i = fr.read()) != -1) {
                    payload.append((char) i);
                }
            } catch (Exception e) {
                logger.info("Exception Occured while Reading Payload");
                return null;
            } finally {
                try {
                    fr.close();
                } catch (IOException e) {
                    logger.info("Exception Occured while closing Stream");
                    throw new RuntimeException(e);
                }
            }
        } else {
            payload = new StringBuilder((String) requestPayload);
        }
        return payload.toString();
    }

    public static String getDataFromBufferedReader(final InputStream inputStream) {
        StringBuilder builder = new StringBuilder();
        String output;
        if (inputStream != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            try {
                while ((output = br.readLine()) != null) {
                    builder.append(output);
                }
            } catch (IOException e) {
                logger.info("Exception Occured while getting Data From Buffered Reader");
                builder = new StringBuilder(e.getMessage());
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.info("I/O Exception Occured while getting Data From Buffered Reader");
                    throw new RuntimeException(e);
                }
            }
            return builder.toString();
        } else {
            return null;
        }

    }

    public static Object genericInvokeMethod(Object obj, String methodName, Object... params) {
        int paramCount = params.length;
        Method method;
        Object requiredObj = null;
        Class<?>[] classArray = new Class<?>[paramCount];
        for (int i = 0; i < paramCount; i++) {
            classArray[i] = params[i].getClass();
        }
        try {
            method = obj.getClass().getDeclaredMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        } catch (NoSuchMethodException e) {
            logger.info("No Such Method Exception Occured invoking Method");
        } catch (IllegalArgumentException e) {
            logger.info("Exception Occured in Arguments Provided Reader");
        } catch (IllegalAccessException e) {
            logger.info("Illegal Access Exception");
        } catch (InvocationTargetException e) {
            logger.info("Inovation Target Exception");
        }

        return requiredObj;
    }

    public static Response invokeRequestMethod(Request request) {
        Object obj = genericInvokeMethod(new ApiInvocation(), "executeRequest", request);
        if (null != obj) {
            return (Response) obj;
        }
        return null;
    }


    private static List<String> getResourceFolderFiles(String name, String extension) throws GemException {
        ScanResult result = null;
        List<String> contentList = new ArrayList<String>();
        String content = null;
        if (StringUtils.contains(name, "\\.")) {
            result = (new ClassGraph()).acceptPackages(name).scan();
        } else {
            result = (new ClassGraph()).acceptPaths(name).scan();
        }
        ResourceList resources = result.getResourcesWithExtension(extension);
        if (resources.size() > 0) {
            logger.debug(resources);
            for (int i = 0; i < resources.size(); i++) {
                try {
                    content = resources.get(i).getContentAsString();
                    contentList.add(content);
                } catch (IOException e) {
                    throw new GemException(e.getMessage());
                }

            }
        }
        return contentList;
    }


    public static JsonElement convertStringInToJsonElement(String value) {
        if (value == null) {
            return null;
        }

        try {
            return JsonParser.parseString(value);
        } catch (JsonSyntaxException syntaxException) {
            return JsonParser.parseString("\"" + value + "\"");
        }
    }


    public static void updateResourceMap() {
        ScanResult sr = null;
        if (GemJarGlobalVar.jarexecution) {
            ClassGraph jarcg = new ClassGraph();
            sr = jarcg.disableModuleScanning().scan(5);

        } else {
            ClassGraph cg = new ClassGraph();
            sr = cg.disableJarScanning().disableModuleScanning().scan(5);
        }
        ResourceList rl = sr.getAllResources().nonClassFilesOnly();
        Map<String, ResourceList> resourceListMap = rl.asMap();
        Map<String, Resource> resourcemap = new HashMap<String, Resource>();
        for (String name : resourceListMap.keySet()) {
            resourcemap.put(name, resourceListMap.get(name).get(0));
        }
        GemJarGlobalVar.resourcemap = resourcemap;
    }

    public static ArrayList<String> getFilesWithExtension(String extension) {
        ArrayList<String> filePaths = new ArrayList<String>();
        ScanResult sr = null;
        if (GemJarGlobalVar.jarexecution) {
            ClassGraph jarcg = new ClassGraph();
            sr = jarcg.disableModuleScanning().scan(5);

        } else {
            ClassGraph cg = new ClassGraph();
            sr = cg.disableJarScanning().disableModuleScanning().scan(5);
        }
        ResourceList rl = sr.getResourcesWithExtension(extension);
        if (rl.size() > 0) {
            for (int i = 0; i < rl.size(); i++) {
                String path = rl.get(i).getURL().toString();
                filePaths.add(path);
            }
            return filePaths;
        } else {
            return null;
        }
    }

    public static Map<String, ResourceList> getFilesWithExtensionMap(String extension) {
        ClassGraph jarcg = new ClassGraph();
        ScanResult sr = jarcg.disableModuleScanning().scan(5);
        ResourceList rl = sr.getResourcesWithExtension(extension);
        return rl.asMap();
    }

    public static boolean verifyResourceIsPresent(String nameWithExtension) {
        boolean status = false;
        String extension = nameWithExtension.substring(nameWithExtension.lastIndexOf(".") + 1);
        ResourceList rl = getFilesWithExtensionMap(extension).get(nameWithExtension);
        status = rl != null;
        return status;
    }


    public static JsonArray convertStringArrayToJsonArray(String[] stringArray) {
        JsonArray jsonArray = new JsonArray();
        for (String string : stringArray) {
            jsonArray.add(string);
        }
        return jsonArray;
    }


}
