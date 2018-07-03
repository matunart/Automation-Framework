package com.qa.basetest;


import com.qa.driverutil.DriverUtil;
import com.qa.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    private final static String ENV = System.getProperty("QAENV", "PRODUCTION");
    private static final DateFormat FAILURE_DATE_FORMAT = new SimpleDateFormat("'on' d MMMMM 'at' h:mm:ss a");
    private static final String SCREENSHOT = System.getProperty("SCREENSHOT", "ON").toUpperCase();
    private static ThreadLocal<DriverUtil> threadLocalDriver = new ThreadLocal<>();


    static {
        try {
            Environment.readDataFromSheet(ENV);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"BROWSER", "DEVICE"})
    public void beforeMethod(ITestResult testResult, @Optional("") String browserName, @Optional("") String deviceName, Method method) throws Exception {
        browserName = browserName.isEmpty() ? System.getProperty("BROWSER", "CHROME") : browserName;
        threadLocalDriver.set(new DriverUtil(browserName, deviceName));


        Reporter.setCurrentTestResult(testResult); //Try to use without it
        String testDescription = method.getAnnotation(Test.class).description();
        if (!testDescription.isEmpty()) {
            LOGGER.info(MarkerFactory.getMarker("header"), testDescription);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult testResult) {
        Reporter.setCurrentTestResult(testResult); //Try to use without it

        if (testResult.getStatus() == ITestResult.FAILURE) {
            Date date = Calendar.getInstance().getTime();
            String timeStamp = FAILURE_DATE_FORMAT.format(date);
            LOGGER.error("Test failed {}.", timeStamp);

            String nodeIpAddress = DriverUtil.getNodeIpAddress();
            if (null != nodeIpAddress) {
                LOGGER.info("Selenium node: {}", nodeIpAddress);
            }

            LOGGER.info("Current URL: {}", DriverUtil.getWebDriver().getCurrentUrl());
            if (SCREENSHOT.equals("FULL")) {
                try {
                    DriverUtil.takeFullScreenshot();
                } catch (Exception e) {
                    LOGGER.warn("Unable to take a screenshot:\n" + e.getMessage());
                }
            } else if (!SCREENSHOT.equals("OFF")) {
                DriverUtil.takeScreenshot();
            }

        }
        DriverUtil.quit();
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext context) throws IOException {
        if (DriverUtil.getScreenshotDir() == null) {
            File outputDir = new File(context.getSuite().getOutputDirectory());
            //sout an output directory here
            System.out.println("=======OUTPUT DIR IS: " + outputDir);
            File screenshotDir =
                    new File(outputDir.getParent() + File.separator + "html" + File.separator + "screenshots");
            screenshotDir.mkdirs();
            if (screenshotDir.exists()) {
                DriverUtil.setScreenshotDir(screenshotDir);
            }
        }
        if (Environment.getBaseUrl().isEmpty()) {
            Environment.readDataFromSheet(ENV);
        }
    }


}
