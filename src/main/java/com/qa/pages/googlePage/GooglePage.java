package com.qa.pages.googlePage;


import com.qa.environment.Environment;
import com.qa.pages.BasePage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GooglePage extends BasePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(GooglePage.class);
    private static final String DEFAULT_URL_PATH = "";

    @FindBy(css = ".gLFyf.gsfi")
    private WebElement inputField;

    @FindBy(xpath = "//input[@name = 'btnK']")
    private List<WebElement> searchBtn;

    public void open() {
        String url = Environment.getBaseUrl() + DEFAULT_URL_PATH;
        LOGGER.info(String.format("Open %s: %s", getClass().getSimpleName(), url));
        openUrl(url);
    }

    public void searchFor(String query) {
        inputField.clear();
        inputField.sendKeys(query);
        pressEscKey();
        searchBtn.stream().filter(WebElement::isDisplayed).findFirst().get().click();
    }


}
