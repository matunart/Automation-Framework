package com.qa.logger;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.Reporter;

/**
 * Created by Maksim_Demidov on 8/3/2017.
 **/
public class TestNGReportAppender extends AppenderSkeleton {

    @Override
    protected void append(LoggingEvent event) {
        Reporter.log(eventToString(event));
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    private String eventToString(LoggingEvent event) {
        StringBuilder result = new StringBuilder(layout.format(event));

        if (layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();
            if (s != null) {
                for (String value : s) {
                    result.append(value).append(Layout.LINE_SEP);
                }
            }
        }
        if (event.getLevel().equals(Level.INFO) && event.getLocationInformation().getClassName()
            .equals("org.slf4j.helpers.MarkerIgnoringBase")) {
            result.insert(0, "<b><font color='blue'>").append("</font></b>");
        } else if (event.getLevel().equals(Level.WARN)) {
            result.insert(0, "<b><font color='orange'>").append("</font></b>");
        } else if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            result.insert(0, "<b><font color='red'>").append("</font></b>");
        }
        return result.append("<br/>").toString();
    }

}

