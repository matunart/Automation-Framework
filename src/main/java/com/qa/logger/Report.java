package com.qa.logger;

import org.testng.Reporter;

public class Report {

    private static final String SPACE = "		";

    public static void logStep(String sDetails) {
        Reporter.log("<b>" + sDetails + "</b><br>", true);
        Reporter.log(" =======================<br>", true);
    }

    public static void logAction(String sAction) {
        Reporter.log(sAction + "<br>", true);
    }

    public static void debugLog(String sAction) {
        System.out.println(SPACE + sAction);
    }

    public static void logError(String sAction) {
        System.out.println(SPACE + sAction);
        Reporter.log("<b> <font color='red'>" + SPACE + sAction + "</font></b><br>", true);
    }

    public static void logPass(String sAction) {
        System.out.println(SPACE + sAction);
        Reporter.log("<b> <font color='green'>" + SPACE + sAction + "</font></b><br>", true);
    }

    public static void logHeading(String sAction) {
        Reporter.log("<b> <font color='blue'>" + SPACE + sAction + "</font></b><br>", true);
    }

    public static void logWarning(String sAction) {
        System.out.println(SPACE + sAction);
        Reporter.log("<b> <font color='orange'>" + SPACE + sAction + "</font></b><br>", true);
    }

}
