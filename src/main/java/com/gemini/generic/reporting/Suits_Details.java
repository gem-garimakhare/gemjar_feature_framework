package com.gemini.generic.reporting;

import com.gemini.generic.utils.GemJarGlobalVar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Suits_Details {
    private static final String gemEcoProductName = "GEMJAR";
    private String os;
    private String s_run_id;
    private long s_start_time;
    private long s_end_time;
    private String status;
    private String project_name;
    private String user;
    private String env;
    private String machine;
    private final String report_name;
    private final ArrayList<Map<String, Object>> testcase_details = new ArrayList<Map<String, Object>>();
    private Testcase_Info testcase_info;

    private final String framework_name;

    public Suits_Details(String s_run_id, String projectName, String env) {
        this.s_run_id = s_run_id;
        this.project_name = projectName;
        this.env = env;
        this.s_start_time = GemReportingUtility.getCurrentTimeInMilliSecond();
        this.user = GemReportingUtility.getCurrentUserName();
        this.machine = GemReportingUtility.getMachineName();
        this.os = System.getProperty("os.name");
        this.status = "EXE";
        this.report_name = GemJarGlobalVar.reportName;
        this.framework_name = gemEcoProductName;
    }

    public void addTestCaseDetail(Map<String, Object> testCase_Details) {
        this.testcase_details.add(testCase_Details);
    }

    public void addTestCaseInfo(ArrayList<Testcase_Details> TestCase_Details) {
        int exe = 0;
        int fail = 0;
        int info = 0;
        int pass = 0;
        int total = testcase_details.size();
        int warn = 0;
        int err = 0;
        for (Testcase_Details testCaseDetail : TestCase_Details) {
            switch (testCaseDetail.getStatus().toLowerCase()) {
                case "exe":
                    exe += 1;
                    break;
                case "fail":
                    fail += 1;
                    break;
                case "err":
                    err += 1;
                    break;
                case "info":
                    info += 1;
                    break;
                case "pass":
                    pass += 1;
                    break;
                case "warn":
                    warn += 1;
                    break;
                default:
                    break;
            }
        }
        this.testcase_info = new Testcase_Info();
        if (exe > 0) {
            this.testcase_info.setEXE(exe);
        }
        this.testcase_info.setFAIL(fail);
        if (err > 0) {
            this.testcase_info.setERR(err);
        }
        if (info > 0) {
            this.testcase_info.setINFO(info);
        }
        this.testcase_info.setPASS(pass);
        this.testcase_info.setTOTAL(total);
        if (warn > 0) {
            this.testcase_info.setWARN(warn);
        }
    }

    public void endSuite(ArrayList<Testcase_Details> TestCase_Details) {
        addTestCaseInfo(TestCase_Details);
        setSuiteStatus(TestCase_Details);
        this.s_end_time = GemReportingUtility.getCurrentTimeInMilliSecond();
    }

    private void setSuiteStatus(ArrayList<Testcase_Details> TestCase_Details) {
        Set<String> testCaseStatSet = new HashSet<String>();
        for (Testcase_Details testcase : TestCase_Details) {
            testCaseStatSet.add(testcase.getStatus());
        }
        if (testCaseStatSet.contains(STATUS.FAIL.name())) {
            this.status = STATUS.FAIL.name();
        } else if (testCaseStatSet.contains(STATUS.WARN.name())) {
            this.status = STATUS.WARN.name();
        } else if (testCaseStatSet.contains(STATUS.ERR.name())) {
            this.status = STATUS.ERR.name();
        } else {
            this.status = STATUS.PASS.name();
        }
    }

    public String getS_run_id() {
        return s_run_id;
    }

    public long getS_start_time() {
        return s_start_time;
    }

    public float getS_end_time() {
        return s_end_time;
    }

    public String getStatus() {
        return status;
    }

    public String getProject_name() {
        return project_name;
    }

    public String getOs() {
        return os;
    }

    public String getUser() {
        return user;
    }

    public String getEnv() {
        return env;
    }

    public String getMachine() {
        return machine;
    }

    public Testcase_Info getTestcase_info() {
        return testcase_info;
    }

    // Setter Methods

    public void setS_run_id(String s_run_id) {
        this.s_run_id = s_run_id;
    }

    public void setS_start_time(long s_start_time) {
        this.s_start_time = s_start_time;
    }

    public void setS_end_time(long s_end_time) {
        this.s_end_time = s_end_time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public void setTestcase_info(Testcase_Info Testcase_Info) {
        this.testcase_info = Testcase_Info;
    }
}
