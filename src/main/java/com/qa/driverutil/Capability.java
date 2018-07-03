package com.qa.driverutil;


import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Capability {

    private static final Map<String, Object> CHROME_PREFS = new HashMap<>();
    private static final LoggingPreferences LOGGING_PREFS = new LoggingPreferences();

    static {
        CHROME_PREFS.put("credentials_enable_service", false);
        CHROME_PREFS.put("enableNetwork", true);
        CHROME_PREFS.put("enablePage", false);
        CHROME_PREFS.put("enableTimeline", false);
        CHROME_PREFS.put("profile.password_manager_enabled", false);

        LOGGING_PREFS.enable(LogType.PERFORMANCE, Level.INFO);
    }

    static DesiredCapabilities chrome() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("disable-extensions");
        chromeOptions.addArguments("disable-infobars");
        chromeOptions.addArguments("disable-plugins");
        chromeOptions.addArguments("disable-print-preview");
        chromeOptions.addArguments("silent");
        chromeOptions.addArguments("start-maximized");
        chromeOptions.addArguments("test-type");
        chromeOptions.setExperimentalOption("prefs", CHROME_PREFS);

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        capabilities.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, LOGGING_PREFS);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        return capabilities;
    }

    static DesiredCapabilities chrome(String device) {
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", device);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("disable-extensions");
        chromeOptions.addArguments("disable-infobars");
        chromeOptions.addArguments("disable-plugins");
        chromeOptions.addArguments("disable-print-preview");
        chromeOptions.addArguments("silent");
        chromeOptions.addArguments("start-maximized");
        chromeOptions.addArguments("test-type");
        chromeOptions.addArguments("--lang=en-GB");
        chromeOptions.setExperimentalOption("prefs", CHROME_PREFS);
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        capabilities.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, LOGGING_PREFS);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        return capabilities;
    }

    static DesiredCapabilities firefox() {
        FirefoxProfile profile = new ProfilesIni().getProfile("default");
        profile.setPreference("geo.prompt.testing", true);
        profile.setPreference("geo.prompt.testing.allow", true);
        profile.setPreference("browser.download.useDownloadDir", false);

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        capabilities.setCapability("marionette", true);
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        capabilities.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);

        return capabilities;
    }

    static DesiredCapabilities internetExplorer() {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
        capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability("ignoreZoomSetting", true);
        capabilities.setCapability("nativeEvents", false);

        return capabilities;
    }

    static DesiredCapabilities edge() {
        DesiredCapabilities capabilities = DesiredCapabilities.edge();
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
        capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability("ignoreZoomSetting", true);
        capabilities.setCapability("nativeEvents", false);
        return capabilities;
    }
}
