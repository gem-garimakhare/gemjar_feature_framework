package com.gemini.generic.feature.utils;

public enum PayloadOperationEnum {

    UNIQUE {
        @Override
        public String getData(String... reqparameter) {
            return null;
        }
    },
    CURR {
        @Override
        public String getData(String... reqparameter) {
            return null;
        }
    },
    ALPHA {
        @Override
        public String getData(String... reqparameter) {
            return null;
        }
    },
    EPOCH {
        @Override
        public String getData(String... reqparameter) {
            return null;
        }
    },
    UUID {
        @Override
        public String getData(String... reqparameter) {
            return null;
        }
    },
    DATE {
        @Override
        public String getData(String... reqparameter) {
            return null;
        }
    };

    public abstract String getData(String... reqparameter);

}
