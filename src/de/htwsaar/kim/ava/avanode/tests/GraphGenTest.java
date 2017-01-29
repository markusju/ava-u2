package de.htwsaar.kim.ava.avanode.tests;

/**
 * Created by markus on 26.01.17.
 */


import de.htwsaar.kim.ava.avanode.dot.Edge;
import de.htwsaar.kim.ava.avanode.file.FileConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class GraphGenTest {




    @BeforeEach
    void setUp() {

    }

    @Test
    void constructTest() throws FileNotFoundException, UnsupportedEncodingException {
        //FileConfig.checkParams(100, 6, 3);
        for (int i = 0; i < 1000 ; i++) {
            FileConfig.genElectionDotFileWrapper(100, 6, 3);
        }
    }


}
