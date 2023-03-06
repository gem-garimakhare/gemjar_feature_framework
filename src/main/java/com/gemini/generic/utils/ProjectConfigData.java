package com.gemini.generic.utils;

import java.util.Set;

public class ProjectConfigData {

    public static String getProperty(String key) {
        try {
            return GemJarUtils.getGemJarConfigData(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setProperty(String key, String value) {
        try {
            GemJarUtils.getConfigObject().addProperty(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean containsKey(String key) {
        try {
            Set<String> keySet = GemJarUtils.getConfigObject().keySet();
            return keySet.contains(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean isEmpty() {
        try {
            return GemJarUtils.getConfigObject().isJsonNull() || GemJarUtils.getConfigObject().keySet().isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Set<String> getStringPropertyNames() {
        try {
            return GemJarUtils.getConfigObject().keySet();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getSize() {
        try {
            return GemJarUtils.getConfigObject().keySet().size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
