package de.htwsaar.kim.ava.avanode.tests;

/**
 * Created by markus on 26.01.17.
 */


import de.htwsaar.kim.ava.avanode.dot.Edge;
import de.htwsaar.kim.ava.avanode.file.FileConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GraphGenTest {




    @BeforeEach
    void setUp() {

    }

    @Test
    void constructTest() {
        FileConfig.checkParams(100, 6, 3);

    }


}
