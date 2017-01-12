package de.htwsaar.kim.ava.avanode.tests;

import de.htwsaar.kim.ava.avanode.dot.Dot;
import de.htwsaar.kim.ava.avanode.dot.Edge;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by markus on 11.01.17.
 */
class DotTest {


    private Dot dot = new Dot();

    @BeforeEach
    void setUp() {
        dot.addEdge(new Edge(1, 2));
        dot.addEdge(new Edge(1, 3));
        dot.addEdge(new Edge(1, 4));
        dot.addEdge(new Edge(1, 5));
        dot.addEdge(new Edge(1, 6));
        dot.addEdge(new Edge(2, 3));
        dot.addEdge(new Edge(3, 6));

    }

    @AfterEach
    void tearDown() {
        dot = new Dot();
    }

    @Test
    void testSelf() {

        Edge e1 = new Edge(1, 296);
        Edge e2 = new Edge(296, 1);

        assertEquals(e1, e2);
        assertEquals(e2, e1);


        dot.addEdge(new Edge(6, 3));
        System.out.println(dot);
    }


    @Test
    void testAdjacentNodes() {
        Set<Integer> adjacent = dot.getAdjacentNodes(3);
        assertEquals(dot.getAdjacentNodes(3), new HashSet<Integer>() {{
            add(3);
            add(6);
        }});
        assertEquals(dot.getAdjacentNodes(1), new HashSet<Integer>() {{
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
        }});
    }

}