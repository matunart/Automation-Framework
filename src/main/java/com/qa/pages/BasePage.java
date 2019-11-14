package com.qa.pages;


import com.qa.driverutil.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BasePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);
    private final static int MOBILE_BROWSER_WIDTH = 414;

    protected Actions actions;

    public BasePage() {
        initElements();
        actions = actions();
    }

    public void openUrl(String url) {
        DriverUtil.openUrl(url);
        initElements();
    }

    public static Actions actions() {
        return new Actions(DriverUtil.getWebDriver());
    }

    public void initElements() {
        waitUntilReadyStateComplete();
        PageFactory
            .initElements(new AjaxElementLocatorFactory(DriverUtil.getWebDriver(), DriverUtil.DEFAULT_WAIT), this);
    }

    protected WebElement findElement(By locator) {
        return DriverUtil.getWebDriver().findElement(locator);
    }

    protected List<WebElement> findElements(By locator) {
        return DriverUtil.getWebDriver().findElements(locator);
    }

    protected boolean waitUntilReadyStateComplete() throws TimeoutException {
        return waiting().until(
            (ExpectedCondition<Boolean>) wd -> executeScript("return document.readyState").equals("complete"));
    }

    protected boolean waitUntilJQueryInactive() throws TimeoutException {
        return waiting().until(
            (ExpectedCondition<Boolean>) c -> (Long) executeScript("return jQuery.active") == 0);
    }

    public static Object executeScript(String js, Object... args) {
        JavascriptExecutor executor = (JavascriptExecutor) DriverUtil.getWebDriver();
        try {
            return executor.executeScript(js, args);
        } catch (Exception e) {
            LOGGER.warn("Couldn't execute script:\n{}", e.getMessage());
            return null;
        }
    }

    public static WebDriverWait waiting() throws TimeoutException {
        return waiting(DriverUtil.DEFAULT_WAIT);
    }

    public static WebDriverWait waiting(int seconds) throws TimeoutException {
        return new WebDriverWait(DriverUtil.getWebDriver(), seconds);
    }

    public boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isElementInView(WebElement element) {
        try {
            waiting(0).until(ExpectedConditions.visibilityOf(element));
            int elementY = element.getLocation().getY();
            int browserHeight = DriverUtil.getWebDriver().manage().window().getSize().getHeight();
            LOGGER.debug("Check if Y scroll position [{}] <= element Y location [{}]"
                         + " <= (Y scroll position + browser height) [{}]",
                         getScrollY(), elementY, getScrollY() + browserHeight);
            return (getScrollY() <= elementY && elementY <= getScrollY() + browserHeight);
        } catch (TimeoutException e) {
            return false;
        }
    }

    private Long getScrollY() {
        return (Long) executeScript("return Math.round(window.scrollY);");
    }

    private Long getScrollX() {
        return (Long) executeScript("return Math.round(window.scrollX);");
    }

    protected boolean isElementInXViewOnMobileBrowser(WebElement element) {
        try {
            waiting(0).until(ExpectedConditions.visibilityOf(element));
            int elementX = element.getLocation().getX();
            return (0 <= elementX && elementX <= MOBILE_BROWSER_WIDTH);
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isScrolledIntoView(WebElement element) {
        try {
            waiting(0).until(ExpectedConditions.visibilityOf(element));
            return (boolean) executeScript(
                "var docViewTop = $(window).scrollTop();"
                + "var docViewBottom = docViewTop + $(window).height();"
                + "var elemTop = $(arguments[0]).offset().top;"
                + "var elemBottom = elemTop + $(arguments[0]).height();"
                + "return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));",
                element);
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getPageTitle() {
        return DriverUtil.getWebDriver().getTitle();
    }

    public String getUrl() {
        return DriverUtil.getWebDriver().getCurrentUrl();
    }

    public void pressEscKey() {
        LOGGER.info("Press Esc key.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        actions.sendKeys(Keys.ESCAPE).build().perform();
    }

    public static void scrollTo(WebElement element) {
        if (element.isDisplayed()) {
            executeScript("arguments[0].scrollIntoView(true);", element);
        }
    }

    protected void moveMouseToElement(WebElement webElement) {
        actions.moveToElement(webElement).build().perform();
    }

    public void waitUntilUrlContains(String urlPart) {
        waiting().until(ExpectedConditions.urlContains(urlPart));
    }

    public void waitForElementDisappearance(String css, int seconds) {
        DriverUtil.getWebDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            waiting().withTimeout(seconds, TimeUnit.SECONDS)
                .pollingEvery(500L, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.stalenessOf(DriverUtil.getWebDriver().findElement(By.cssSelector(css))));
        } catch (TimeoutException | NoSuchElementException ignored) {
        } finally {
            DriverUtil.getWebDriver().manage().timeouts().implicitlyWait(DriverUtil.DEFAULT_WAIT, TimeUnit.SECONDS);
        }
    }

    public boolean isThereJSErrorOnThePage(String errorType) {
        LogEntries logEntries = DriverUtil.getWebDriver().manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            if (entry.getMessage().contains(errorType)) {
                LOGGER.error("Java Script [{}] error has been detected:", errorType);
                LOGGER.error(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                return true;
            }
        }
        return false;
    }


}
