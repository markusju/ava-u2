package de.htwsaar.kim.ava.avanode.store;

import de.htwsaar.kim.ava.avanode.application.NodeCore;

import javax.xml.soap.Node;
import java.util.logging.Level;

/**
 * Created by markus on 25.01.17.
 */
public class ElectionManager {
    private int votesCand1 = 0;
    private int votesCand2 = 0;


    private NodeCore nodeCore;


    public ElectionManager(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
    }

    public void addVote(int candId) {
        switch(candId) {
            case 1:
                votesCand1++;
                break;
            case 2:
                votesCand2++;
                break;
        }
        checkIfAllCollected();
    }

    private int totalVotes() {
        return votesCand1 + votesCand2;
    }

    public int electPresident() {
        return ((votesCand1 > votesCand2)?1:2);
    }

    private void checkIfAllCollected() {

        if (totalVotes() == nodeCore.getFileConfig().getNumOfVotersAndCandidates()) {
            nodeCore.getLogger().log(Level.INFO, "All Votes have been collected.");
            allVotesCollected();
        }

    }

    private void reset() {
        votesCand1 = 0;
        votesCand2 = 0;
    }

    private void allVotesCollected() {
        nodeCore.getLogger().log(Level.INFO, "Elected President: "+electPresident());
        nodeCore.getLogger().log(Level.INFO, "Votes for 1: "+votesCand1);
        nodeCore.getLogger().log(Level.INFO, "Votes for 2: "+votesCand2);
        reset();
    }


}
