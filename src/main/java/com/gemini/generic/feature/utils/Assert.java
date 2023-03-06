package com.gemini.generic.feature.utils;

import com.gemini.generic.reporting.GemTestReporter;
import com.gemini.generic.reporting.STATUS;
import com.gemini.generic.utils.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Assert {

    private final Logger logger = LogManager.getLogger(Assert.class);

    private String step;

    private String expectedValue;

    private String actualValue;
    private String assertOperation;
    private boolean status;

    public boolean getStatus() {
        return this.status;
    }

    public Assert(String expectedValue, String assertOperation, String actualValue) {
        this.expectedValue = expectedValue;
        this.assertOperation = assertOperation;
        this.actualValue = actualValue;
    }

    public String getExpectedValue() {
        return this.expectedValue;
    }

    public String getActualValue() {
        return this.actualValue;
    }

    public String getAssertOperation() {
        return this.assertOperation;
    }

    public Assert setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
        return this;
    }

    public Assert setActualValue(String actualValue) {
        this.actualValue = actualValue;
        return this;
    }


    public Assert() {

    }

    public Assert setStep(String step) {
        this.step = step;
        return this;
    }

    public Assert splitAssertStatement(String assertStatement) {
        try {


            for (AssertEnum assertKey : AssertEnum.values()) {
                String operationKey1 = assertKey.name();
                operationKey1 = operationKey1.contains("_") ? operationKey1.replace("_", " ") : operationKey1;
                if (assertStatement.toLowerCase().contains(operationKey1.toLowerCase())) {
                    this.expectedValue = assertStatement.split(operationKey1)[0];
                    this.actualValue = assertStatement.split(operationKey1)[1];
                    //this.assertOperation = operationKey1;
                    this.assertOperation = operationKey1.contains(" ") ? operationKey1.replace(" ", "_") : operationKey1;
                    break;
                }
            }
            return this;
        } catch (Exception e) {
            logger.error("Unable to split the assert statements : " + assertStatement + ", Only supported operation are " + Arrays.asList(AssertEnum.values()));
            GemTestReporter.addTestStep("Unable to split the assert statements : " + assertStatement, " Only supported operation are " + Arrays.asList(AssertEnum.values()), STATUS.FAIL);
            return null;
        }
    }

    public Assert doAssert() {
        boolean status = false;
        AssertEnum command = AssertEnum.valueOf(EnumUtils.toEnumLookupValue(this.assertOperation));
        status = command.doAssert(this.expectedValue.trim(), this.actualValue.trim());
        this.status = status;
        return this;
    }

    public void report() {

        if (step != null) {
            this.assertOperation = this.assertOperation.contains("_")?this.assertOperation.replace("_"," "):this.assertOperation;
            GemTestReporter.addTestStep(step,
                    "verify : " + this.expectedValue + " " + this.assertOperation + " " + this.actualValue,
                    status ? STATUS.PASS : STATUS.FAIL);
        }
        else
            GemTestReporter.addTestStep("Assert",
                    "verify : " + this.expectedValue + " " + this.assertOperation + " " + this.actualValue,
                    status ? STATUS.PASS : STATUS.FAIL);
    }
}
