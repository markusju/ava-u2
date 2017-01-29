package de.htwsaar.kim.ava.avanode.election;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.file.FileConfig;
import de.htwsaar.kim.ava.avanode.store.FeedbackTeam;

import java.io.IOException;
import java.util.Random;

/**
 * Created by markus on 29.01.17.
 */
public abstract class Election {

    private static Random rand = new Random();

    public static void setupElection(int numNodes, int fellows, int friends, boolean autoStart) throws IOException {
        System.out.println("Starting Political System. Please stand by.");
        System.out.println("Making AVA Great Again!");
        System.out.println("Initiating Candidates.");

        FileConfig.genConfigFile(numNodes);
        FileConfig.genElectionDotFileWrapper(
                numNodes,
                fellows,
                friends
        );

        int feedbackThreshold = 6;

        NodeCore[] nodes = new NodeCore[1000];

        //Observer
        NodeCore observer = new NodeCore(0, feedbackThreshold);
        observer.setNodeType(NodeType.OBSERVER);
        observer.startNode();

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

        //Install FeedbackTeam
        FeedbackTeam teamCand1 = new FeedbackTeam(cand1);
        FeedbackTeam teamCand2 = new FeedbackTeam(cand2);

        cand1.startNode();
        cand2.startNode();

        System.out.println("Initiating Constituents.");

        //Constituency...
        for (int i = 3; i <= numNodes; i++) {
            NodeCore node = new NodeCore(i, feedbackThreshold);
            nodes[i] = node;
            node.setNodeType(NodeType.VOTER);
            node.getDataStore().setConfidenceLevel(1, rand.nextInt(100));
            node.getDataStore().setConfidenceLevel(2, rand.nextInt(100));
            node.startNode();
        }

        //Establishing Confidence for Party Fellows of Candidate 1
        for (int i = 3; i<3+fellows; i++) {
            nodes[i].getDataStore().setConfidenceLevel(1, 100);
            nodes[i].getDataStore().setConfidenceLevel(2, 0);
        }
        //Establishing Confidence for Party Fellows of Candidate 1
        for (int i =3+fellows; i<3+2*fellows; i++) {
            nodes[i].getDataStore().setConfidenceLevel(2, 100);
            nodes[i].getDataStore().setConfidenceLevel(1, 0);
        }

        System.out.println("Political System successfully started.");

        if (autoStart) {
            cand1.startVoteforme();
            cand2.startVoteforme();
        }


    }

}




