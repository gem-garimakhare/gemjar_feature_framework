package com.gemini.generic.reporting;


import com.gemini.generic.utils.CommonUtils;
import com.gemini.generic.utils.GemJarConstants;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.gemini.generic.utils.GemJarUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.time.Instant;

public class GemReportingUtility {

    public static void createReport(String suiteDetail, String stepJson, String reportLoc) {
        if (GemJarGlobalVar.createLocalReport) {
            try {
                String htmlTemplate = CommonUtils.getFilesWithExtensionMap("html").get(GemJarConstants.GEM_REPORT_TEMPLATE).get(0).getContentAsString();
                htmlTemplate = htmlTemplate.replace("var obj = '';", "var obj = " + suiteDetail + ";");

                GemJarGlobalVar.reportName = GemJarGlobalVar.reportName != null ? GemJarGlobalVar.reportName : "GemjarTestReport";
                String localReportPath = reportLoc + File.separator + GemJarGlobalVar.reportName + ".html";
                FileUtils.writeStringToFile(new File(localReportPath), htmlTemplate, Charset.defaultCharset());
                System.out.println("LOCAL REPORT : " + localReportPath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void createReport(String suiteDetail, String stepJson) {
        createReport(suiteDetail, stepJson, null);
    }

    public static long getCurrentTimeInSecond() {
        return Instant.now().getEpochSecond();
    }

    public static long getCurrentTimeInMilliSecond() {
        return Instant.now().getEpochSecond() * 1000;
    }

    public static String getMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentUserName() {
        if (GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_USER_NAME) != null) {
            return GemJarUtils.getGemJarKeyValue(GemJarConstants.GEMJAR_USER_NAME);
        }
        return System.getProperty("user.name");
    }

}
