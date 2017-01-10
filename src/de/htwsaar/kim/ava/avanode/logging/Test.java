package de.htwsaar.kim.ava.avanode.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by markus on 08.01.17.
 */
public class Test {

    public static Logger logger = Logger.getGlobal();

    public static void main(String... args) {

        logger.log(Level.INFO, "Yolo");

    }
}
