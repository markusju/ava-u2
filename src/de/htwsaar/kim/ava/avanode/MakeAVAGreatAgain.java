package de.htwsaar.kim.ava.avanode;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.file.FileConfig;
import de.htwsaar.kim.ava.avanode.store.CampaignTeam;

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

        //FileConfig.genConfigFile(100);
        /*FileConfig.genElectionDotFile(
                100,
                6,
                3
        );*/

        int feedbackThreshold = 10;

        NodeCore[] nodes = new NodeCore[1000];


        //Candidates

        NodeCore cand1 = new NodeCore(1, feedbackThreshold);
        NodeCore cand2 = new NodeCore(2, feedbackThreshold);

        //Set types
        cand1.setNodeType(NodeType.CANDIDATE);
        cand2.setNodeType(NodeType.CANDIDATE);

        //Set confidence levels
        cand1.getDataStore().setConfidenceLevel(1, 100);
        cand1.getDataStore().setConfidenceLevel(2, 0);
        cand2.getDataStore().setConfidenceLevel(1, 0);
        cand2.getDataStore().setConfidenceLevel(2, 100);

        //Install CampaignTeam
        CampaignTeam teamCand1 = new CampaignTeam(cand1);
        CampaignTeam teamCand2 = new CampaignTeam(cand2);

        cand1.startNode();
        cand2.startNode();

        System.out.println("Initiating Constituents.");

        for (int i = 3; i <= 100; i++) {
            NodeCore node = new NodeCore(i, feedbackThreshold);
            nodes[i] = node;
            node.setNodeType(NodeType.VOTER);
            node.getDataStore().setConfidenceLevel(1, rand.nextInt(100));
            node.getDataStore().setConfidenceLevel(2, rand.nextInt(100));
            node.startNode();
        }

        for (int i = 3; i<=8; i++) {
            nodes[i].getDataStore().setConfidenceLevel(1, 100);
            nodes[i].getDataStore().setConfidenceLevel(2, 0);
        }

        for (int i = 9; i<=14; i++) {
            nodes[i].getDataStore().setConfidenceLevel(2, 100);
            nodes[i].getDataStore().setConfidenceLevel(1, 0);
        }


        System.out.println("Political System started.");
    }

}
