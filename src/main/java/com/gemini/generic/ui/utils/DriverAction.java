package com.gemini.generic.ui.utils;

import com.gemini.generic.reporting.GemTestReporter;
import com.gemini.generic.reporting.STATUS;
import com.gemini.generic.utils.GemJarGlobalVar;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DriverAction {

    private final static Logger logger = LogManager.getLogger(DriverAction.class);

    public static STATUS launchUrl(String url) {
        final String stepTtile = "Launch Url";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().get(url);
            status = STATUS.PASS;
            description = "Url ~" + url;
        } catch (Exception e) {
            description = "Launch Url Failed <BR>Url ~" + url;
            logger.info("Launch Url Failed <BR>Url ~" + url);
            logger.debug(e.getStackTrace());

        }

        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static String getTitle(String url) {
        String title = DriverManager.getWebDriver().getTitle();
        return title;
    }


    public static STATUS maximizeBrowser() {
        final String stepTtile = "Maximize Browser";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().manage().window().maximize();
            status = STATUS.PASS;
            description = "Browser Maximize Successful ";
        } catch (Exception e) {
            description = "Browser Maximize Failed";
            logger.info(description);
            logger.debug(e.getStackTrace());
        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static STATUS maximizeToDefaultBrowserSize() {
        STATUS status = STATUS.FAIL;
        maximizeBrowser();
        Dimension dimension = getBrowserSize();
        Dimension dimension1 = new Dimension(1296, 696);
        if (!dimension1.equals(dimension)) {
            setBrowserSize(1296, 696);
        }

        return status;
    }


    public static STATUS minimizeBrowser() {
        final String stepTtile = "Minimize Browser";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().manage().window().minimize();
            status = STATUS.PASS;
            description = "Browser Minimize Successful ";
        } catch (Exception e) {
            description = "Browser Minimize Failed";
            logger.info(description);
            logger.debug(e.getStackTrace());
        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static Dimension getBrowserSize() {
        try {
            return DriverManager.getWebDriver().manage().window().getSize();
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    public static void setBrowserSize(int width, int height) {
        final String stepTtile = "Set Browser Size";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            Dimension dimension = new Dimension(width, height);
            DriverManager.getWebDriver().manage().window().setSize(dimension);
            status = STATUS.PASS;
            description = "Browser Size Set To <BR> width ~ " + width + "<BR> height ~ " + height;
        } catch (Exception e) {
            description = "Set Browser Size Failed";
            logger.info(description);
            logger.debug(e.getStackTrace());
        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
    }


    public static void setBrowserPosition(int x, int y) {
        final String stepTtile = "Set Browser Position";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            Point point = new Point(x, y);
            DriverManager.getWebDriver().manage().window().setPosition(point);
            status = STATUS.PASS;
            description = "Browser Position Set To <BR> x ~ " + x + "<BR> y ~ " + y;
        } catch (Exception e) {
            description = "Set Browser Position Failed";
            logger.info(description);
            logger.debug(e.getStackTrace());
        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
    }


    public static Point getBrowserLocation() {
        try {
            Point p = DriverManager.getWebDriver().manage().window().getPosition();
            return p;
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }
    ////////// TimeOuts///////////

    public static void waitSec(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setImplicitTimeOut(long seconds) {

        try {
            DriverManager.getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }

    }

    public static void setScriptTimeOut(long seconds) {
        try {
            DriverManager.getWebDriver().manage().timeouts().scriptTimeout(Duration.ofSeconds(seconds));
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
    }

    public static void setPageLoadTimeOut(long seconds) {
        try {
            DriverManager.getWebDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(seconds));
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
    }

    /// Navigation

    public static STATUS navigateBack() {
        final String stepTtile = "Backward Navigation";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().navigate().back();
            status = STATUS.PASS;
            description = "Backward Navigation Successful";
        } catch (Exception e) {
            description = "Backward Navigation Failed";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }

        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static STATUS refresh() {
        final String stepTtile = "Refresh Page";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().navigate().refresh();
            status = STATUS.PASS;
            description = "Page Refresh Successful";
        } catch (Exception e) {
            description = "Page Refresh Failed";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }

        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }


    public static STATUS navigateForward() {
        final String stepTtile = "Forward Navigation";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().navigate().forward();
            status = STATUS.PASS;
            description = "Forward Navigation Successful";
        } catch (Exception e) {
            description = "Forward Navigation Failed";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }

        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());

        return status;
    }


    public static STATUS navigateToUrl(String url) {
        final String stepTtile = "Navigate to Url";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().navigate().forward();
            status = STATUS.PASS;
            description = "Navigated to Url Successful<BR>URL ~ " + url;
        } catch (Exception e) {
            description = "Navigation to Url Failed<BR>URL ~ " + url;
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }

    /// WebElement
    public static WebElement getElement(By locator) {
        try {
            WebElement element = DriverManager.getWebDriver().findElement(locator);
            return element;
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    // return set<String>
    public static String getWindowHandle() {
        try {
            return DriverManager.getWebDriver().getWindowHandle();
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    public static Set<String> getWindowHandles() {
        try {
            return DriverManager.getWebDriver().getWindowHandles();
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    public static String getCurrentURL() {
        try {
            return DriverManager.getWebDriver().getCurrentUrl();
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    public static String getPageSource() {
        try {
            return DriverManager.getWebDriver().getPageSource();
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    /////////// Switch Operations/////////////////////
    public static void switchToWindow(String nameOfHandle) {
        try {
            DriverManager.getWebDriver().switchTo().window(nameOfHandle);
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
    }


    public static STATUS switchToFrame(int index) {
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().switchTo().frame(index);
            status = STATUS.PASS;
        } catch (Exception e) {

        }
        return status;
    }

    public static STATUS switchToFrame(String nameOrId) {
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().switchTo().frame(nameOrId);
            status = STATUS.PASS;
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
        return status;
    }

    public static STATUS switchToFrame(WebElement frameElement) {
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().switchTo().frame(frameElement);
            status = STATUS.PASS;
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
        return status;
    }

    public static STATUS switchToParentFrame() {
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().switchTo().parentFrame();
            status = STATUS.PASS;
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
        return status;
    }


    public static void switchToDefaultContent() {
        try {
            DriverManager.getWebDriver().switchTo().defaultContent();
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
    }


    public static WebElement switchToActiveElement() {
        try {
            return DriverManager.getWebDriver().switchTo().activeElement();
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    public static void switchToAlert() {
        final String stepTtile = "Switch To Alert";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().switchTo().alert();
            status = STATUS.PASS;
            description = "Switch To Alert Successful";
        } catch (Exception e) {
            description = "Switch To Alert Failed";
            logger.info(description);
            logger.debug(e.getStackTrace());


        }

        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
    }


    public static STATUS acceptAlert() {
        final String stepTile = "Accept Alert";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().switchTo().alert().accept();
            status = STATUS.PASS;
            description = "Alert Accepted Successfully";
        } catch (Exception e) {
            description = "Failed to accept alert" + e.getMessage();
            status = STATUS.FAIL;
            logger.info(description);
            logger.debug(e.getStackTrace());

        }

        GemTestReporter.addTestStep(stepTile, description, status, DriverAction.takeSnapShot());
        return status;

    }

    public static STATUS dismissAlert() {
        final String stepTtile = "Dismiss Alert";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().switchTo().alert().dismiss();
            status = STATUS.PASS;
            description = "Alert Dismissed Successfully";
        } catch (Exception e) {
            description = "Failed to Dismiss Alert ";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;

    }


    public static STATUS alertInput(String input) {
        final String stepTitle = "SendKeys To Alert";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            DriverManager.getWebDriver().switchTo().alert().sendKeys(input);
            status = STATUS.PASS;
            description = "SendKeys To Alert Successful <BR> input ~ " + input;
        } catch (Exception e) {
            description = "SendKeys To Alert Failed <BR> input ~ " + input;
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(stepTitle, description, status, DriverAction.takeSnapShot());
        return status;
    }

    ////////////// Web Elements///////////////////


    public static List<WebElement> getElements(By locator) {
        try {
            List<WebElement> elements = DriverManager.getWebDriver().findElements(locator);
            return elements;
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    public static String getElementText(By locator) {
        WebElement element = getElement(locator);
        return DriverAction.getElementText(element);
    }

    public static String getElementText(WebElement element) {
        String elementText = "";
        try {
            elementText = element.getText();
        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
        return elementText;
    }

    public static List<String> getElementsText(By locator) {
        List<String> elementsText = new ArrayList<String>();
        try {
            List<WebElement> elements = getElements(locator);

            for (WebElement element : elements) {
                elementsText.add(getElementText(element));
            }

        } catch (Exception e) {
            logger.debug(e.getStackTrace());

        }
        return elementsText;
    }

    ////////////////// Click Operation/////////////////

    public static STATUS click(By locator, String elementLabel) {
        WebElement element = getElement(locator);
        return click(element, elementLabel);
    }

    public static STATUS click(By locator) {
        String elementLabel = getElementText(locator);
        if (elementLabel.isEmpty())
            elementLabel = "element";
        return click(locator, elementLabel);
    }

    public static STATUS click(By locator, String steps, String description) {
        WebElement element = getElement(locator);
        return click(element, steps, description);
    }

    public static STATUS click(WebElement webElement, String steps, String description) {
        STATUS status = STATUS.FAIL;
        String descriptions = description;
        try {
            webElement.click();
            status = STATUS.PASS;
            descriptions = "Successfully : " + description;
        } catch (Exception e) {
            if (StringUtils.isNotBlank(description)) {
                descriptions = "Failed to: " + description + e.getMessage();
            }
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(steps, descriptions, status, DriverAction.takeSnapShot());
        return status;
    }

    public static STATUS click(WebElement webElement) {
        String elementLabel = getElementText(webElement);
        return click(webElement, elementLabel);
    }

    public static STATUS click(WebElement webElement, String elementLabel) {
        String step = "Click on " + elementLabel;
        String description = "Clicked on " + elementLabel;
        return click(webElement, step, description);
    }

    ///////////////// Type Operation//////////////////////
    public static STATUS typeText(By locator, String textToEnter) {
        boolean elementPresent = isExist(locator);
        if (elementPresent) {
            WebElement element = getElement(locator);
            return DriverAction.typeText(element, textToEnter);
        } else {
            return STATUS.FAIL;
        }
    }

    public static STATUS typeText(By locator, String textToEnter, String elementLabel) {
        boolean elementPresent = isExist(locator);
        if (elementPresent) {
            WebElement element = getElement(locator);
            return DriverAction.typeText(element, textToEnter, elementLabel);
        } else {
            return STATUS.FAIL;
        }
    }

    public static STATUS typeText(By locator, String steps, String description, String textToEnter) {
        boolean elementPresent = isExist(locator);
        return elementPresent ? DriverAction.typeText(getElement(locator), steps, description, textToEnter) : STATUS.FAIL;
    }

    public static STATUS typeText(WebElement webElement, String steps, String description, String textToEnter) {
        STATUS status = STATUS.FAIL;
        try {
            webElement.clear();
            webElement.sendKeys(textToEnter);
            status = STATUS.PASS;
        } catch (Exception e) {
            description = "Failed : " + description;
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(steps, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static STATUS typeText(WebElement element, String textToEnter) {
        String step = "Enter text : " + textToEnter;
        String description = "Enter text : " + textToEnter;
        return typeText(element, step, description, textToEnter);
    }

    public static STATUS typeText(WebElement element, String textToEnter, String elementLabel) {
        String step = "Enter text : " + textToEnter + " into " + elementLabel;
        String description = "Enter text : " + textToEnter + " into " + elementLabel;
        return typeText(element, step, description, textToEnter);
    }


    public static String getAttributeName(WebElement webElement, String name) {
        try {
            return webElement.getAttribute(name);
        } catch (Exception e) {
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    public static String getAttributeName(By locator, String attributeName) {
        if (isExist(locator)) {
            WebElement element = getElement(locator);
            return getAttributeName(element, attributeName);
        } else {
            return null;
        }
    }

    public static String getCSSValue(By locator, String propertyName) {
        WebElement element = getElement(locator);
        return getCSSValue(element, propertyName);
    }

    public static String getCSSValue(WebElement element, String propertyName) {
        try {
            return element.getCssValue(propertyName);
        } catch (Exception e) {
            GemTestReporter.addTestStep("Fetch css value", "Error Occur : Unable to get the CSS value" + e.getMessage(), STATUS.FAIL, DriverAction.takeSnapShot());
            logger.debug(e.getStackTrace());
            return null;
        }
    }

    /////////////////////////////////////////////

    /* public static String takeSnapShot() {
         Timestamp timestamp = new Timestamp(System.currentTimeMillis());
         String fileWithPath = GemJarGlobalVar.reportLocation + "/SS/SS" + timestamp.getTime() + ".png";
         WebDriver webdriver = DriverManager.getWebDriver();
         TakesScreenshot scrShot = ((TakesScreenshot) webdriver);
         File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
         File DestFile = new File(fileWithPath);
         try {
             FileUtils.copyFile(SrcFile, DestFile);

         } catch (IOException e) {
             e.printStackTrace();
         }
         String SSpath = "SS/SS" + timestamp.getTime() + ".png";
         String fullpath = DestFile.getAbsolutePath();

         try {
             if (GemJarGlobalVar.jewelCredentials){
                 return ApiInvocationImpl.fileUpload(fullpath);
             }
         } catch (Exception e) {
             System.out.println("Some error occur while uploading SS to AWS");
             e.printStackTrace();
         }
         return SSpath;
     }
 */
    public static String takeSnapShot() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileWithPath = GemJarGlobalVar.reportLocation + "/SS/SS_" + GemJarGlobalVar.projectName + "_" + timestamp.getTime() + ".png";
        WebDriver webdriver = DriverManager.getWebDriver();
        TakesScreenshot scrShot = ((TakesScreenshot) webdriver);
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File DestFile = new File(fileWithPath);
        try {
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String SSpath = "SS/SS_" + GemJarGlobalVar.projectName + "_" + timestamp.getTime() + ".png";
        String fullpath = DestFile.getAbsolutePath();
        return SSpath;
    }

    public static String takeSnapShotBase64() {
        try {
            TakesScreenshot scrShot = ((TakesScreenshot) DriverManager.getWebDriver());
            return scrShot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    // close Driver or Current tab

    public static void closeCurrentTab() {
        try {
            DriverManager.closeDriver();
        } catch (Exception e) {
            GemTestReporter.addTestStep("Close Current Tab", "Some error occured", STATUS.FAIL);
            logger.debug(e.getStackTrace());

        }
    }


    //Double click mouse event

    public static STATUS doubleClick(WebElement element, String elementLabel) {
        try {
            Actions act = new Actions(DriverManager.getWebDriver());
            act.doubleClick(element).perform();
            GemTestReporter.addTestStep("Double click on " + elementLabel, "Double clicked on " + elementLabel, STATUS.PASS, DriverAction.takeSnapShot());
            return STATUS.PASS;
        } catch (Exception e) {
            e.printStackTrace();
            GemTestReporter.addTestStep("Double click on " + elementLabel, "Failed to double click on " + elementLabel, STATUS.FAIL, DriverAction.takeSnapShot());
            return STATUS.FAIL;
        }
    }

    public static STATUS doubleClick(By locator, String elementLabel) {
        String step = "Double click on " + elementLabel;
        String description = "Double clicked on " + elementLabel;
        return doubleClick(locator, step, description);
    }

    public static STATUS doubleClick(By locator) {
        String elementLabel = getElementText(locator);
        return doubleClick(locator, elementLabel);
    }

    public static STATUS doubleClick(By locator, String steps, String description) {
        WebElement element = getElement(locator);
        return doubleClick(element, steps, description);
    }

    public static STATUS doubleClick(WebElement elementType, String steps, String description) {
        STATUS status = STATUS.FAIL;
        try {
            Actions act = new Actions(DriverManager.getWebDriver());
            act.doubleClick(elementType).perform();
            status = STATUS.PASS;
        } catch (Exception e) {
            if (StringUtils.isNotBlank(description)) {
                description = "Failed to :" + description;
            }
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(steps, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static STATUS doubleClick(WebElement element) {
        String elementLabel = getElementText(element);
        String step = "Double click on " + elementLabel;
        String description = "Double clicked on " + elementLabel;
        return doubleClick(element, step, description);
    }

    // Right Click or Context click

    public static STATUS rightClick(WebElement element, String elementLabel) {
        String step = "Right click on " + elementLabel;
        String description = "Right click on " + elementLabel;
        return rightClick(element, step, description);
    }

    public static STATUS rightClick(By locator, String elementLabel) {
        boolean elementExists = isExist(locator);
        return elementExists ? rightClick(getElement(locator), elementLabel) : STATUS.FAIL;
    }

    public static STATUS rightClick(By locator) {
        boolean elementExists = isExist(locator);
        return elementExists ? rightClick(getElement(locator)) : STATUS.FAIL;
    }

    public static STATUS rightClick(By locator, String steps, String description) {
        WebElement element = getElement(locator);
        return rightClick(element, steps, description);
    }

    public static STATUS rightClick(WebElement elementType, String steps, String description) {
        STATUS status = STATUS.FAIL;
        try {
            Actions act = new Actions(DriverManager.getWebDriver());
            act.contextClick(elementType).perform();
            status = STATUS.PASS;

        } catch (Exception e) {
            description = "Failed :  " + description;
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(steps, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static STATUS rightClick(WebElement element) {
        String elementLabel = getElementText(element);
        String step = "Right click on " + elementLabel;
        String description = "Right click on " + elementLabel;
        return rightClick(element, step, description);
    }

    // drop down


    // upload file
    public static STATUS fileUpload(By locator, String path) {
        WebElement element = getElement(locator);
        return fileUpload(element, path);
    }

    public static STATUS fileUpload(WebElement elementType, String path) {
        final String stepTtile = "File Upload";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            elementType.sendKeys(path);
            status = STATUS.PASS;
            description = "File Upload Successfull";
        } catch (Exception e) {
            description = "File Upload Failure";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }


    // drop down

    public static STATUS dropDown(WebElement element, String name) {
        String description = "";
        STATUS status = STATUS.FAIL;
        final String stepTtile = "Select " + name + " from dropdown";
        try {
            Select drp = new Select(element);
            drp.selectByVisibleText(name);
            description = "Selected " + name + " from dropdown";
            status = STATUS.PASS;
        } catch (Exception e) {
            description = "Failed to select " + name + " from dropdown";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static STATUS dropDown(By locator, int index) {
        WebElement element = getElement(locator);
        return dropDown(element, index);
    }

    public static STATUS dropDown(By locator, String name) {
        WebElement element = getElement(locator);
        return dropDown(element, name);
    }

    public static STATUS dropDown(WebElement element, int index) {
        String description = "";
        STATUS status = STATUS.FAIL;
        String prefix = "";
        if (index == 1) {
            prefix = "1st";
        } else if (index == 2) {
            prefix = "2nd";
        } else if (index == 3) {
            prefix = "3rd";
        } else prefix = index + "th";

        final String stepTtile = "Select " + prefix + " element from dropdown";
        try {
            Select drp = new Select(element);
            drp.selectByIndex(index);
            description = "Selected " + prefix + "element from dropdown";
            status = STATUS.PASS;
        } catch (Exception e) {
            description = "Failed to select " + prefix + " element from dropdown";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }
        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }


    // upload file

    // scroll
    public static STATUS pageScroll(int X, int Y) {
        String description = "";
        STATUS status = STATUS.FAIL;
        final String stepTtile = "Scrolling";
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverManager.getWebDriver();
            js.executeScript("window.scrollBy(+" + X + "," + Y + ")");
            status = STATUS.PASS;
            description = "Successful";
        } catch (Exception e) {
            description = "Error Occur";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }

        GemTestReporter.addTestStep(stepTtile, description, status, DriverAction.takeSnapShot());
        return status;
    }

    public static STATUS hoverOver(By element, String Label) {
        try {
            WebElement mainMenu = DriverManager.getWebDriver().findElement(element);
            Actions actions = new Actions(DriverManager.getWebDriver());
            actions.moveToElement(mainMenu);
            actions.build().perform();
            if (!Label.equals("")) {
                GemTestReporter.addTestStep("Hover on " + Label, " Hovering on " + Label, STATUS.PASS, takeSnapShot());
            }
            return STATUS.PASS;
        } catch (Exception e) {
            if (!Label.equals("")) {
                GemTestReporter.addTestStep("Hover on " + Label, " Hovering on " + Label + " Failed", STATUS.FAIL);
            }
            return STATUS.FAIL;
        }
    }

    public static STATUS hoverOver(By element) {
        return hoverOver(element, "");
    }

    public static STATUS hoverOver(WebElement element, String Label) {
        try {
            Actions actions = new Actions(DriverManager.getWebDriver());
            actions.moveToElement(element);
            actions.build().perform();
            if (!Label.equals("")) {
                GemTestReporter.addTestStep("Hover on " + Label, " Hovering on " + Label, STATUS.PASS, takeSnapShot());
            }
            return STATUS.PASS;
        } catch (Exception e) {
            if (!Label.equals("")) {
                GemTestReporter.addTestStep("Hover on " + Label, " Hovering on " + Label + " Failed", STATUS.FAIL);
            }
            return STATUS.FAIL;
        }
    }

    public static STATUS hoverOver(WebElement element) {
        return hoverOver(element, "");
    }


    // scroll
    public static void scrollAnElementToSpecificPosition(int X, int Y) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverManager.getWebDriver();
            js.executeScript("window.scrollBy(+" + X + "," + Y + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scrollIntoView(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverManager.getWebDriver();
            js.executeScript("arguments[0].scrollIntoView();", element);
        } catch (Exception e) {
            logger.debug(e.getLocalizedMessage());
        }
    }

    public static void scrollIntoView(By locator) {
        scrollIntoView(getElement(locator));
    }

    public static void scrollToBottom() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverManager.getWebDriver();
            js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scrollToTop() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverManager.getWebDriver();
            js.executeScript("window.scrollTo(0, -document.body.scrollHeight);");
        } catch (Exception e) {

        }
    }


    // drag and drop

    public static STATUS dragAndDrop(By from, By To) {
        String fromElementLabel = getElementText(from);
        String toElementLabel = getElementText(To);
        return dragAndDrop(from, fromElementLabel, To, toElementLabel);
    }

    public static STATUS dragAndDrop(By from, String fromElementLabel, By to, String toElementLabel) {
        WebElement elementFrom = getElement(from);
        WebElement elementTo = getElement(to);
        return dragAndDrop(elementFrom, fromElementLabel, elementTo, toElementLabel);
    }

    public static STATUS dragAndDrop(WebElement from, String fromElementLabel, WebElement To, String toElementLabel) {
        STATUS status = STATUS.FAIL;
        String steps = "Drag and drop from " + fromElementLabel + " to " + toElementLabel;
        String description = "";
        try {
            WebElement from1 = DriverManager.getWebDriver().findElement((By) from);
            WebElement To1 = DriverManager.getWebDriver().findElement((By) To);
            Actions act = new Actions(DriverManager.getWebDriver());
            act.dragAndDrop(from1, To1).build().perform();
            description = "Dragged from" + fromElementLabel + " and dropped to " + toElementLabel;
            status = STATUS.PASS;
        } catch (Exception e) {
            description = "Error Occur: Failed to Dragg from" + fromElementLabel + " and drop to " + toElementLabel + e.getMessage();
            logger.info(description);
            logger.debug(e.getStackTrace());
            e.printStackTrace();

        }
        GemTestReporter.addTestStep(steps, description, status, DriverAction.takeSnapShot());
        return status;

    }

    public static STATUS clearText(By locator) {
        return clearText(getElement(locator));
    }

    public static STATUS clearText(WebElement elementType) {
        String steps = "Clear Text";
        String description = "";
        STATUS status = STATUS.FAIL;
        try {
            elementType.clear();
            status = STATUS.PASS;
            description = "Successful";
        } catch (Exception e) {
            description = "Error Occur";
            logger.info(description);
            logger.debug(e.getStackTrace());

        }

        GemTestReporter.addTestStep(steps, description, status, DriverAction.takeSnapShot());

        return status;
    }


    public static boolean isExist(By locator) {
        List<WebElement> elementList = getElements(locator);
        int elementListSize = elementList.size();
        return elementListSize > 0;
    }

    public static void waitUntilElementAppear(By locator, int duration) {
        WebElement webElement = DriverAction.getElement(locator);
        WebDriverWait webDriverWait = new WebDriverWait(DriverManager.getWebDriver(), Duration.ofSeconds(duration));
        webDriverWait.until(ExpectedConditions.visibilityOf(webElement));
    }

    public static void waitUntilElementAppear(WebElement webElement, int duration) {
        WebDriverWait webDriverWait = new WebDriverWait(DriverManager.getWebDriver(), Duration.ofSeconds(duration));
        webDriverWait.until(ExpectedConditions.visibilityOf(webElement));
    }


}