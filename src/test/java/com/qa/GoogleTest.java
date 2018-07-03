package com.qa;


import static org.testng.Assert.*;

import com.qa.basetest.BaseTest;
import com.qa.driverutil.DriverUtil;
import com.qa.pages.googlePage.GooglePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class GoogleTest extends BaseTest{

    private final static Logger LOGGER = LoggerFactory.getLogger(GoogleTest.class);

    @Test(description = "Google search test")
    public void searchFieldTest() {
        GooglePage googlePage = new GooglePage();
        googlePage.open();

        googlePage.searchFor("Selenium");
        assertTrue(DriverUtil.getPageSource().contains("Selenium"), "There is no Selenium on the page");
    }
}
