package com.qa.basetest;


import com.qa.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
    private final static String ENV = System.getProperty("QAENV", "PRODUCTION");


    static {
        try {
            Environment.readDataFromFile(ENV);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
