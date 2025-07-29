package com.plato.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {

    public static final Logger logger = Logger.getLogger("courtLogger");

    static {
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            LogManager.getLogManager().reset();

            FileHandler fileHandler = new FileHandler("logs/info.log", 500000, 1, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);
            logger.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.INFO);
            logger.addHandler(consoleHandler);

            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error in setting up logger configuration", e);
        }
    }
}
