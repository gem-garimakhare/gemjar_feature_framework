package com.gemini.generic.feature.utils;

import com.gemini.generic.utils.GemJarUtils;
import com.google.gson.Gson;
import io.cucumber.docstring.DocString;

import java.util.HashMap;
import java.util.Map;

public class Variables {
    private Map<String, String> variableMap;

    public Variables() {
        variableMap = new HashMap<String, String>();
        loadConfigData();
    }

    public void loadConfigData() {
        Gson gson = new Gson();
        variableMap = gson.fromJson(GemJarUtils.getConfigObject(), HashMap.class);

    }

    public void enterNewDataORUpdate(String key, Object value) {
        if (value != null) {
            if (value.getClass().getSimpleName().equals("DocString")) {
                variableMap.put(key, ((DocString) value).getContent());
            } else if (value.getClass().getSimpleName().equals("String")) {
                variableMap.put(key, (String) value);
            } else {
                try {
                    variableMap.put(key, String.valueOf(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            variableMap.put(key, null);
        }
    }

    public String getVariableData(String key) {
        return variableMap.get(key);
    }

    public Map<String, String> getCurrentMap() {
        return this.variableMap;
    }

}
