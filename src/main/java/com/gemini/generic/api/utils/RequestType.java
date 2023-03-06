package com.gemini.generic.api.utils;

import com.gemini.generic.utils.CommonUtils;

public enum RequestType {
    GET {
        @Override
        public Response executeHttpRequest(Request request) {
            return requestReponseManipulation(request);
        }
    },
    PUT {
        @Override
        public Response executeHttpRequest(Request request) {
            return requestReponseManipulation(request);
        }
    },
    POST {
        @Override
        public Response executeHttpRequest(Request request) {
            return requestReponseManipulation(request);
        }
    },
    DELETE {
        @Override
        public Response executeHttpRequest(Request request) {
            return requestReponseManipulation(request);
        }
    },
    PATCH {
        @Override
        public Response executeHttpRequest(Request request) {
            return requestReponseManipulation(request);
        }
    },
    CREATE {
        @Override
        public Response executeHttpRequest(Request request) {
            return requestReponseManipulation(request);
        }
    };

    public abstract Response executeHttpRequest(Request request);

    // Method to manipulate FeatureRequest and Response
    Response requestReponseManipulation(Request request) {
        return CommonUtils.invokeRequestMethod(request);
    }
}
