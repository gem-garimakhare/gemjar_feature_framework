package com.gemini.generic.feature.utils;

import org.testng.annotations.Test;

public class AssertTest {




    @Test
    public void testAssertClass() {

        Assert anAssert = new Assert("Vivek", "NOT_EQUALS", "Vivek1");
        boolean status = anAssert.doAssert().getStatus();
        System.out.println(status);
        org.testng.Assert.assertEquals(true, status);
    }

}
