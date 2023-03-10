package com.gemini.generic.reporting;

class Testcase_Info {
    private int FAIL;
    private int PASS;
    private int INFO;
    private int WARN;
    private int ERR;
    private int EXE;
    private int TOTAL;

    // Getter Methods

    public int getFAIL() {
        return FAIL;
    }

    public int getPASS() {
        return PASS;
    }

    public int getINFO() {
        return INFO;
    }

    public int getWARN() {
        return WARN;
    }

    public int getERR() {
        return ERR;
    }

    public int getEXE() {
        return EXE;
    }

    public int getTOTAL() {
        return TOTAL;
    }

    // Setter Methods

    public void setFAIL(int FAIL) {
        this.FAIL = FAIL;
    }

    public void setPASS(int PASS) {
        this.PASS = PASS;
    }

    public void setINFO(int INFO) {
        this.INFO = INFO;
    }

    public void setWARN(int WARN) {
        this.WARN = WARN;
    }

    public void setERR(int ERR) {
        this.ERR = ERR;
    }

    public void setEXE(int EXE) {
        this.EXE = EXE;
    }

    public void setTOTAL(int TOTAL) {
        this.TOTAL = TOTAL;
    }
}
