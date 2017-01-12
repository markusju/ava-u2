package de.htwsaar.kim.ava.avanode;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.file.FileConfig;

import javax.xml.soap.Node;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by markus on 08.01.17.
 */
public class MakeAVAGreatAgain {

    public static Random rand = new Random();


    public static void main(String... args) throws Exception {

        System.out.println("Starting Political System. Please stand by.");
        System.out.println("Initiating Candidates.");

        FileConfig.genConfigFile(100);
        FileConfig.genElectionDotFile(
                100,
                6,
                3
        );

        NodeCore cand1 = new NodeCore(1);
        NodeCore cand2 = new NodeCore(2);

        cand1.setNodeType(NodeType.CANDIDATE);
        cand2.setNodeType(NodeType.CANDIDATE);

        cand1.startNode();
        cand2.startNode();

        System.out.println("Initiating Constituents.");

        for (int i = 3; i <= 100; i++) {
            NodeCore node = new NodeCore(i);
            node.setNodeType(NodeType.VOTER);
            node.getDataStore().setConfidenceLevel(1, rand.nextInt(100));
            node.getDataStore().setConfidenceLevel(2, rand.nextInt(100));
            node.startNode();
        }



        System.out.println("Political System started.");
    }

}
