package com.gemini.generic.feature.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AssertEnum {

    NOT_EQUALS {
        @Override
        public boolean doAssert(String value1, String value2) {
            return !StringUtils.equals(value1, value2);
        }
    },

    EQUALS {
        @Override
        public boolean doAssert(String value1, String value2) {
            return StringUtils.equals(value1, value2);
        }
    },

    NOT_CONTAINS {
        @Override
        public boolean doAssert(String value1, String value2) {
            return !StringUtils.contains(value1, value2);
        }
    },
    CONTAINS {
        @Override
        public boolean doAssert(String value1, String value2) {
            return StringUtils.contains(value1, value2);
        }
    },

    NOT_IN {
        @Override
        public boolean doAssert(String value1, String list){
            List<String> arrayList = new ArrayList<>(Arrays.asList(list.split(",")));
            return !arrayList.contains(value1);
        }
    },

    IN {
        @Override
        public boolean doAssert(String value1, String list){
            List<String> arrayList = new ArrayList<>(Arrays.asList(list.split(",")));
            return arrayList.contains(value1);
        }
    },

    NOT_TO {
        @Override
        public boolean doAssert(String value1, String value2) {
            return !StringUtils.equals(value1, value2);
        }
    },

    TO {
        @Override
        public boolean doAssert(String value1, String value2) {
            return StringUtils.equals(value1, value2);
        }
    };


    public abstract boolean doAssert(String value1, String value2);
}
