package com.qa.environment;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


public class Environment {

    private final static Logger LOGGER = LoggerFactory.getLogger(Environment.class);
    private final static File FILE = new File("./src/main/resources/Envorinment.xlsx").getAbsoluteFile();

    private static String baseUrl = "";
    private static String publishInstance = "";
    private static User userAdmin = new User();

    public static void readDataFromSheet(String sheetName) throws IOException{
        LOGGER.debug("Read test environment data from sheet [{}] of file [{}]", sheetName, FILE.getAbsolutePath());

        try {
            InputStream inputStream = new FileInputStream(FILE);
            Workbook book = new XSSFWorkbook(inputStream);
            Sheet sheet = book.getSheet(sheetName);
            if (null == sheet) {
                throw new IOException(
                        String.format("There is no sheet [%s] in the file [%s]", sheetName, FILE.getAbsolutePath()));
            }
            Iterator<Row> rowIterator = sheet.rowIterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String rowName = row.getCell(0).getStringCellValue().trim();
                switch (rowName) {
                    case "PublishInstance":
                        publishInstance = row.getCell(1).getStringCellValue().trim();
                        LOGGER.debug("Publish Instance: {}", baseUrl);
                        break;
                    case "BaseURL":
                        baseUrl = row.getCell(1).getStringCellValue().trim();
                        LOGGER.debug("Base URL: {}", baseUrl);
                        break;
                    case "UserAdmin":
                        String userName = row.getCell(1).getStringCellValue().trim();
                        String userPassword = row.getCell(1).getStringCellValue().trim();
                        userAdmin.setUsername(userName);
                        userAdmin.setPassword(userPassword);
                        LOGGER.debug("Setting username: {} and password: {}", userName, userPassword);
                        break;
                }
                book.close();
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't read data from sheet [{}] of file [{}]:\n{}",
                    sheetName, FILE.getAbsolutePath(), e.getMessage());
            throw e;
        }
    }

    public static String getBaseUrl() { return baseUrl; }

    public static String getPublishInstance() { return publishInstance; }

    public static User getUserAdmin() { return userAdmin; }
}
