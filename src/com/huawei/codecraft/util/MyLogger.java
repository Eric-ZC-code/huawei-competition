package com.huawei.codecraft.util;

import com.huawei.codecraft.Main;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.*;

public class MyLogger {
    private Logger logger;

    public MyLogger(Logger logger) {
        this.logger = logger;
    }

    public static MyLogger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
//        name = name.split(".")[0];
        try {
            logger.setUseParentHandlers(false);
            FileHandler fh = new FileHandler("log_"+name+".txt", true);
            fh.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    String formatStr = MessageFormat.format(record.getMessage(), record.getParameters());



                    return String.format("[%s][%s] %tF %<tT: %s%n",
                                         record.getLevel(),
                                         record.getLoggerName(),
                                         record.getMillis(),
                                         record.getMessage(),
                                         formatStr);
                }
            });
            logger.addHandler(fh);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new MyLogger(logger);
    }
    public void info(String message, Object... args) {
        logger.log(Level.INFO, message, args);
    }
    public void warning(String message, Object... args) {
        logger.log(Level.WARNING, message, args);
    }
    public void severe(String message, Object... args) {
        logger.log(Level.SEVERE, message, args);
    }
    public void fine(String message, Object... args) {
        logger.log(Level.FINE, message, args);
    }
    public void finer(String message, Object... args) {
        logger.log(Level.FINER, message, args);
    }
}
