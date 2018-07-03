package com.qa.driverutil;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DriverUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(DriverUtil.class);
    private final static DateFormat SCREENSHOT_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss.SSS");
    public final static int DEFAULT_WAIT = 10;
    private static File screenshotDir;
    private static URL hubUrl;

    private static ThreadLocal<WebDriver> threadLocalWebDriver = new ThreadLocal<>();
    private static ThreadLocal<String> threadLocalName = new ThreadLocal<>();
    private static ThreadLocal<String> threadLocalDevice = new ThreadLocal<>();
    private static ThreadLocal<Boolean> threadLocalIsMobile = new ThreadLocal<>();

    static {
        threadLocalName.set("UNDEFINED BROWSER");
        threadLocalDevice.set("");
        threadLocalIsMobile.set(false);
    }

    public DriverUtil(String browserName, String deviceName) throws Exception{
        start(browserName, deviceName);
    }

    public static void start(String browserName, String deviceName) throws Exception {
        threadLocalName.set(browserName);
        threadLocalDevice.set(deviceName);
        threadLocalIsMobile.set(!deviceName.isEmpty());

        String sHubUrl = System.getProperty("HUB_URL", "");
        if (!sHubUrl.isEmpty()) {
            hubUrl = new URL(sHubUrl);
        }

        DesiredCapabilities capabilities = new DesiredCapabilities();
        LOGGER.info("Open browser: {} device: {}", browserName, deviceName.isEmpty() ? "none" : " (" + deviceName + ")");
        switch (browserName.toUpperCase()) {
            case "CHROME":
                capabilities = deviceName.isEmpty() ? Capability.chrome() : Capability.chrome(deviceName);
                threadLocalWebDriver.set((null == hubUrl) ? new ChromeDriver(capabilities)
                        : new RemoteWebDriver(hubUrl, capabilities));
                break;
            case "EDGE":
                capabilities = Capability.edge();
                threadLocalWebDriver.set((null == hubUrl) ? new EdgeDriver(capabilities)
                        : new RemoteWebDriver(hubUrl, capabilities));
                threadLocalWebDriver.get().manage().deleteAllCookies();
                break;
            case "FF":
                capabilities = Capability.firefox();
                threadLocalWebDriver.set((null == hubUrl) ? new FirefoxDriver(capabilities)
                        : new RemoteWebDriver(hubUrl, capabilities));
                break;
            case "IE":
                capabilities = Capability.internetExplorer();
                threadLocalWebDriver.set((null == hubUrl) ? new InternetExplorerDriver(capabilities)
                        : new RemoteWebDriver(hubUrl, capabilities));
                threadLocalWebDriver.get().manage().deleteAllCookies();
                break;
            default:
                throw new IllegalArgumentException(String.format("Wrong BROWSER parameter: %s.", browserName));
        }

        if (!"FF".equalsIgnoreCase(browserName)) {
            threadLocalWebDriver.get().manage().window().maximize();
        }
        threadLocalWebDriver.get().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        if (!"IE".equalsIgnoreCase(browserName)) {
            threadLocalWebDriver.get().manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        }
        threadLocalWebDriver.get().manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
    }

    public static WebDriver getWebDriver() {
        return threadLocalWebDriver.get();
    }

    public static String getName() {
        return threadLocalName.get();
    }

    public static String getDevice() {
        return threadLocalDevice.get();
    }

    public static boolean isMobile() {
        return threadLocalIsMobile.get();
    }

    public static void openUrl(String url) {
        LOGGER.info("Open URL: {}", url);
        threadLocalWebDriver.get().get(url);
    }

    public static void quit() {
        if (null == threadLocalWebDriver.get()) {
            return;
        }
        try {
            LOGGER.info("Close browser {}{}", threadLocalName.get(),
                    !isMobile() ? "" : " (" + threadLocalDevice.get() + ")");
            threadLocalWebDriver.get().quit();
            threadLocalWebDriver.set(null);
        } catch (Exception e) {
            LOGGER.error("Couldn't close browser:\n{}", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void takeScreenshot() {
        LOGGER.debug("Take screenshot");
        Date date = Calendar.getInstance().getTime();
        String timestamp = SCREENSHOT_DATE_FORMAT.format(date);
        try {
            File tmpFile = ((TakesScreenshot) threadLocalWebDriver.get()).getScreenshotAs(OutputType.FILE);
            File screenshotFile = new File(screenshotDir + File.separator + timestamp + ".png");
            LOGGER.debug("Screenshot path: {}", screenshotFile.getAbsolutePath());
            FileUtils.copyFile(tmpFile, screenshotFile);
            String screenshotFileName = screenshotFile.getName();
            LOGGER.info("<a href='./screenshots/{}'><img src='./screenshots/{}' height='100%' width='100%'/></a>",
                    screenshotFileName, screenshotFileName);
        } catch (Exception e) {
            LOGGER.warn("Unable to capture screenshot: \n" + e.getMessage());
        }
    }

    public static void takeFullScreenshot() {
        LOGGER.debug("Take full screenshot");
        java.util.logging.Logger.getLogger("org.openqa.selenium.remote.Augmenter").setLevel(Level.OFF);
        Date date = Calendar.getInstance().getTime();
        String timestamp = SCREENSHOT_DATE_FORMAT.format(date);
        try {
            File file = new File(screenshotDir + File.separator + timestamp + ".png");
            BufferedImage image = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(threadLocalWebDriver.get()).getImage();
            ImageIO.write(image, "png", file);
            String fileName = file.getName();
            LOGGER.info("<a href='./screenshots/{}'><img src='./screenshots/{}' height='100%' width='100%'/></a>",
                    fileName, fileName);
        } catch (Exception e) {
            LOGGER.warn("Unable to capture full screenshot: \n" + e.getMessage());
        }
    }

    public static void setScreenshotDir(File screenshotDir) {
        DriverUtil.screenshotDir = screenshotDir;
    }

    public static File getScreenshotDir() {
        return screenshotDir;
    }

    public static String getNodeIpAddress() {
        if (null == hubUrl) {
            return null;
        }
        String hostName = hubUrl.getHost();
        int port = hubUrl.getPort();

        try {
            HttpHost host = new HttpHost(hostName, port);
            HttpClient client = HttpClients.createDefault();
            SessionId sessionId = ((RemoteWebDriver) threadLocalWebDriver.get()).getSessionId();
            String sUrl = String.format("http://%s:%s/grid/api/testsession?session=%s", hostName, port, sessionId);
            URL sessionUrl = new URL(sUrl);
            LOGGER.debug("Session URL: {}", sessionUrl);
            BasicHttpEntityEnclosingRequest request =
                    new BasicHttpEntityEnclosingRequest("POST", sessionUrl.toExternalForm());
            HttpResponse response = client.execute(host, request);
            JsonObject jsonResponse = extractObject(response);
            JsonElement proxyId = jsonResponse.get("proxyId");
            URL nodeURL = new URL(proxyId.getAsString());
            return nodeURL.getHost();
        } catch (Exception e) {
            LOGGER.debug("Couldn't get Selenium node IP address:\n{}", e.getMessage());
        }
        return null;
    }

    private static JsonObject extractObject(HttpResponse resp) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            stringBuilder.append(line);
        }
        rd.close();
        JsonParser parser = new JsonParser();
        JsonObject objToReturn = (JsonObject) parser.parse(stringBuilder.toString());
        System.out.println(objToReturn.toString());
        System.out.println(objToReturn.get("proxyId"));
        return objToReturn;
    }

    public static String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }

    public static String getPageSource() {
        return getWebDriver().getPageSource();
    }

    public static void back() {
        LOGGER.info("Navigate back");
        getWebDriver().navigate().back();
    }

    public static void forward() {
        LOGGER.info("Navigate forward");
        getWebDriver().navigate().forward();
    }

    public static void refresh() {
        LOGGER.info("Refresh page");
        getWebDriver().navigate().refresh();
    }


    //MOVE TO THE PAGE CLASS:


    public static void refreshPage() {
        String url = threadLocalWebDriver.get().getCurrentUrl();
        LOGGER.info("Refresh page: {}", url);
        threadLocalWebDriver.get().navigate().refresh();
    }

}
