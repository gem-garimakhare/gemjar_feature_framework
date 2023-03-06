package com.gemini.generic.feature.utils;

import com.gemini.generic.api.utils.ApiInvocation;
import com.gemini.generic.api.utils.Request;
import com.gemini.generic.api.utils.Response;
import com.gemini.generic.reporting.GemTestReporter;
import com.gemini.generic.reporting.STATUS;
import com.gemini.generic.ui.utils.DriverAction;
import io.cucumber.docstring.DocString;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScenarioSteps implements FeatureFrameWorkConstants {

    private final static Logger logger = LogManager.getLogger(DriverAction.class);
    Request request = new Request();
    Variables variables = new Variables();

    Assert anAssert = new Assert();

    public Variables getVariables() {
        return this.variables;
    }

    @Before
    public void featureBeforeHooks() {
        this.variables = new Variables();
        this.request = new Request();
        this.anAssert = new Assert();
    }

    private void updateApiResponseInVariable(Response response) {
        if (response != null) {
            variables.enterNewDataORUpdate(RESPONSE, response.getResponseBody());
            variables.enterNewDataORUpdate(RESPONSE_STATUS, response.getStatus());
            variables.enterNewDataORUpdate(RESPONSE_MESSAGE, response.getResponseMessage());
            variables.enterNewDataORUpdate(ERROR_MESSAGE, response.getErrorMessage());
            variables.enterNewDataORUpdate(EXEC_TIME, response.getExecTime());
        } else {
            variables.enterNewDataORUpdate(RESPONSE, response);
            variables.enterNewDataORUpdate(RESPONSE_STATUS, response);
            variables.enterNewDataORUpdate(RESPONSE_MESSAGE, response);
            variables.enterNewDataORUpdate(ERROR_MESSAGE, response);
            variables.enterNewDataORUpdate(EXEC_TIME, response);
        }
    }

    //@Given("^baseUrl\\h(.+)")
    @Given("^baseUrl\\h(https?:\\/\\/.+)")
    public void setBaseUrl(String baseUrl) {
        try {
            request.setBaseUrl(baseUrl);
            GemTestReporter.addTestStep("Update baseUrl", "baseUrl : " + baseUrl, STATUS.INFO);
        } catch (Exception e) {
            GemTestReporter.addTestStep("Update baseUrl", "Failed to update baseUrl : " + baseUrl, STATUS.FAIL);
            logger.info("Failed to update baseUrl : " + baseUrl);
            logger.fatal(e.getMessage());
        }
    }

   // @Given("^url\\h(.+)")
   @Given("^url\\h(https?:\\/\\/.+)")
    public void setUrl(String url) {
        try {
            request.setURL(url);
            GemTestReporter.addTestStep("Update baseUrl", "baseUrl : " + url, STATUS.INFO);
        } catch (Exception e) {
            GemTestReporter.addTestStep("Update baseUrl", "Failed to update baseUrl : " + url, STATUS.FAIL);
            logger.info("Failed to update baseUrl : " + url);
            logger.fatal(e.getMessage());
        }
    }

    @Given("^path\\h(.+)")
    public void setPath(String path) {
        try {
            request.setpath(path);
            request.setURL();
            GemTestReporter.addTestStep("Update request path", "path : " + path, STATUS.INFO);
        } catch (Exception e) {
            GemTestReporter.addTestStep("Failed to update request path", "path : " + path, STATUS.INFO);
            logger.info("Failed to update the path : " + path);
            logger.fatal(e.getMessage());
        }
    }

    @Given("^params\\h([\\w]+)\\h=\\h(.+)$")
    public void setParameters(String key, String value) {
        try {
            request.setParameter(key, value);
            request.setURL();
            GemTestReporter.addTestStep("Update parameter", "key : " + key + " \n value : " + value + " \n updated url : " + request.getURL(), STATUS.INFO);
        } catch (Exception e) {
            GemTestReporter.addTestStep("Failed to update parameter", "key : " + key + " \n value : " + value, STATUS.INFO);
            logger.info("Failed to update parameter", "key : " + key + " \n value : " + value);
            logger.info(e.getMessage());
        }

    }

    @Given("^headers\\h([\\w]+)\\h=\\h(.+)$")
    public void setHeaders(String headersName, String headerValue) {
        try {
            request.setHeader(headersName, headerValue);
            GemTestReporter.addTestStep("Update header", "Header name : " + headersName + "\n Header Value : " + headerValue, STATUS.INFO);
        } catch (Exception e) {
            GemTestReporter.addTestStep("Failed to update header", "Header name : " + headersName + "\n Header Value : " + headerValue, STATUS.FAIL);
            logger.info("Failed to update header", "Header name : " + headersName + "\n Header Value : " + headerValue);
            logger.info(e.getMessage());
        }
    }

    @Given("^requestBody\\h(.+)")
    public void setRequestBody(String requestBody) {
        try {
            if(requestBody.startsWith("#(") && requestBody.endsWith(")"))
                requestBody=ValueFixer.fixValue(this.variables.getCurrentMap(), requestBody);
            if(requestBody.contains("readFile")){
                String filePath = requestBody.substring(requestBody.indexOf("(") + 1, requestBody.lastIndexOf(")"));
                String payload = ValueFixer.readFile(filePath);
                request.setRequestPayload(payload);
                GemTestReporter.addTestStep("Set request body", "Request Body : " + payload + "\n Header Value : " +request.getHeaderMap(), STATUS.INFO);
            }
            else{
            request.setRequestPayload(requestBody);
                GemTestReporter.addTestStep("Set request body", "Request Body : " + requestBody + "\n Header Value : " + request.getHeaderMap(), STATUS.INFO);
            }
        } catch (Exception e) {
            GemTestReporter.addTestStep("Failed to Set request body", "Header name : " + requestBody + "\n Header Value : " + request.getHeaderMap(), STATUS.FAIL);
            logger.info("Failed to update header", "Header name : " + requestBody + "\n Header Value : " + requestBody);
            logger.info(e.getMessage());
        }
    }

    @Given("^method\\h(.+)")
    public void setMethodType(String methodType) {
        try {
            request.setMethod(methodType);
            GemTestReporter.addTestStep("Set method type ", "Method : " + methodType, STATUS.INFO);
            Response response = ApiInvocation.handleRequest(request);
            GemTestReporter.addTestStep("Request", request.toString(), STATUS.INFO);
            GemTestReporter.addTestStep("Response ", response.toString(), STATUS.INFO);
            updateApiResponseInVariable(response);
        } catch (Exception e) {
            GemTestReporter.addTestStep("Failed to Set method type ", "Method : " + methodType, STATUS.INFO);
        } finally {
            request = new Request();
        }

    }

    @Given("^Request\\h:\\h(.+)\\h:$")
    public void requestDocStringDataTable(String step, Object obj) throws Exception {
        try {
            request.createRequest(obj, variables.getCurrentMap());
            request.setStep(step);
            //  GemTestReporter.addTestStep("Request : " + request.getStep(), request.toString(), STATUS.INFO);
        } catch (Exception e) {
            GemTestReporter.addTestStep("Failed to create request : " + step, "Please check the data" + ((DocString) obj).getContent(), STATUS.FAIL);
            logger.info(e.getMessage());
        } finally {
            if (request.getMethod() != null) {
                Response response = ApiInvocation.handleRequest(request);
                //    GemTestReporter.addTestStep("Response : " + request.getStep(), response.toString(), STATUS.INFO);
                updateApiResponseInVariable(response);
                request = new Request();
            }
        }

    }

    @Given("^Print\\h(.+)")
    public void printAnything(String value) {
        String printData = ValueFixer.fixValue(variables.getCurrentMap(), value);
        System.out.println(printData);
        GemTestReporter.addTestStep("Print Statement", printData, STATUS.INFO);
    }

    /*@Given("^FeatureRequest\\h:\\h(.+)\\h:\\hreadfile\\((.+)\\)$")
    public void requestReadDataFromFile(String step,String filepath) {
        System.out.println(step);
        System.out.println(filepath);
    }

    @Given("^FeatureRequest\\h:\\h(.+)\\h:\\h(\\{+.+\\})$")
    public void requestReadDataInline(String step,String requiredData ) {
        System.out.println(step);
        System.out.println(requiredData);
    }*/
    @Given("^Assert\\h:\\h(.+)\\h:$")
    public void assertUsingDocString(String assertStatement, String object) {
        System.out.println(assertStatement);
        System.out.println(object);
    }

    /*@Given("^Assert\\h:\\h(.+)\\h:\\h(.+)(?:\\hNOT)?\\h(IN|CONTAINS|EQUALS)\\h(.+)")
    public void inlineAssertion(String assertStep, String a,String b, String c) {
        String inlineAssertStatement = a+b+c;
        Assert assertA = new Assert().setStep(assertStep).splitAssertStatement(inlineAssertStatement);
        assertA.setActualValue(ValueFixer.fixValue(variables.getCurrentMap(), assertA.getActualValue())).
                setExpectedValue(ValueFixer.fixValue(variables.getCurrentMap(), assertA.getExpectedValue())).
                doAssert().report();
    }*/

    @Given("^Assert\\h: (.+): (.+(?:NOT)?\\h(?:IN|CONTAINS|EQUALS|TO)\\h.+)$")
    public void inlineAssertion(String assertStep, String inlineAssertStatement) {
        Assert assertA = new Assert().setStep(assertStep).splitAssertStatement(inlineAssertStatement);
        assertA.setActualValue(ValueFixer.fixValue(variables.getCurrentMap(), assertA.getActualValue())).
                setExpectedValue(ValueFixer.fixValue(variables.getCurrentMap(), assertA.getExpectedValue())).
                doAssert().report();
    }

    /*@Given("^Assert\\h:\\h(.+)\\h:\\h(.+)")
    public void inlineAssertion(String assertStep, String inlineAssertStatement) {
        Assert assertA = new Assert().setStep(assertStep).splitAssertStatement(inlineAssertStatement);
        assertA.setActualValue(ValueFixer.fixValue(variables.getCurrentMap(), assertA.getActualValue())).
                setExpectedValue(ValueFixer.fixValue(variables.getCurrentMap(), assertA.getExpectedValue())).
                doAssert().report();
    }*/

    @Given("^Set\\h(.+)\\h=\\h(.+)")
    public void setKeyValueInline(String key, String value) {
        variables.enterNewDataORUpdate(key, ValueFixer.fixValue(variables.getCurrentMap(), value));
        GemTestReporter.addTestStep("Update variable ", "Key : " + key + "\n Value : " + value, STATUS.INFO);
    }

    @Given("^Set\\h(.+)\\h=$")
    public void setKeyValueFromObject(String key, Object object) {
        variables.enterNewDataORUpdate(key, ValueFixer.fixValue(variables.getCurrentMap(), (String) object));
    }

    @Given("^headers\\hreadFile[(](.*)[)]$")
    public void setHeaders(String filePath){
        String headerString = ValueFixer.readFile(filePath);
        request.setHeaders(headerString);
        GemTestReporter.addTestStep("Update header", "Headers : " + headerString, STATUS.INFO);

    }


}
