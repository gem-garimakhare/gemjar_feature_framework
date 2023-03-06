package com.gemini.generic.ui.utils;


import com.gemini.generic.exception.GemException;
import com.gemini.generic.utils.GemJarConstants;
import com.gemini.generic.utils.GemJarGlobalVar;
import com.gemini.generic.utils.GemJarUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriverManager {


    private final static Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();


    public static void setWebDriver(WebDriver webDriver) {
        driver.set(webDriver);
    }

    public static WebDriver getWebDriver() {
        return driver.get();
    }

    public static void quitDriver() throws GemException {
        try {
            if (getWebDriver() != null)
                getWebDriver().quit();
        } catch (Exception e) {
            logger.info("Failed to quit the browser instance ");
            logger.debug(e.getMessage());

            throw new GemException(e.getMessage());
        }
    }

    public static void closeDriver() throws GemException {
        try {
            if (getWebDriver() != null)
                getWebDriver().close();
        } catch (Exception e) {
            logger.info("Failed to close the browser instance ");
            logger.debug(e.getMessage());

            throw new GemException(e.getMessage());
        }
    }

    public static void initializeBrowser() throws GemException {
        initializeBrowser("Chrome");
    }

    public static void initializeBrowser(String browserName) throws GemException {

        try {
            if (browserName == null) {
                WebDriverManager.chromedriver().setup();
                setWebDriver(new ChromeDriver());
                logger.info("No browser Provided stared default browser : chrome ");
            } else {
                String browser = browserName.trim().toLowerCase();
                switch (browser) {
                    case "chrome":
                        initializeChrome();
                        logger.info("stared browser : chrome ");
                        break;
                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        setWebDriver(new FirefoxDriver());
                        logger.info("stared browser : firefox ");
                        break;
                    case "grid":
                        String remoteURL = GemJarUtils.getGemJarKeyValue(GemJarConstants.REMOTE_WEBDRIVER_URL);
                        ChromeOptions options = new ChromeOptions();
                        options.setHeadless(true);
                        options.setAcceptInsecureCerts(true);
                        WebDriver driver = remoteURL != null ? new RemoteWebDriver(new URL(remoteURL), options) : new RemoteWebDriver(options);
                        setWebDriver(driver);
                        break;
                    default:
                        WebDriverManager.chromedriver().setup();
                        setWebDriver(new ChromeDriver());
                        logger.info("browser provided is not supported stared default browser : chrome ");
                        break;
                }
            }
        } catch (Exception e) {
            logger.debug("failed to initialize browser : " + e.getMessage());

            throw new GemException(e.getMessage());

        }
    }

    public static void initializeChrome(ChromeOptions chromeOptions) {
        WebDriverManager.chromedriver().setup();
        setWebDriver(new ChromeDriver(chromeOptions));
        logger.info("initialize chrome with chromeoptions : " + chromeOptions.asMap());
    }

    public static void initializeChrome(String chromeOptions) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(chromeOptions);
        initializeChrome(options);
    }

    public static void initializeChrome() {
        WebDriverManager.chromedriver().setup();
        setWebDriver(new ChromeDriver());
    }


    public static void setUpBrowser() throws GemException {
        if (GemJarUtils.getGemJarConfigData(GemJarConstants.ChromeOptions) == null || GemJarUtils.getGemJarConfigData(GemJarConstants.ChromeOptions).isEmpty()) {
            DriverManager.initializeBrowser(GemJarGlobalVar.browserInTest);
        } else {
            DriverManager.initializeChrome("--" + GemJarUtils.getGemJarConfigData(GemJarConstants.ChromeOptions));
        }
        DriverAction.launchUrl(GemJarUtils.getGemJarConfigData(GemJarConstants.LAUNCH_URL));
        DriverAction.maximizeToDefaultBrowserSize();
        DriverAction.setImplicitTimeOut(Long.parseLong(GemJarGlobalVar.implicitTime));
        DriverAction.setPageLoadTimeOut(Long.parseLong(GemJarGlobalVar.pageTimeout));
        DriverAction.setScriptTimeOut(Long.parseLong(GemJarGlobalVar.scriptTimeout));


    }

}
