package de.htwsaar.kim.ava.avanode.store;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;

/**
 * Created by markus on 24.01.17.
 */
public class TerminationManager {


    private TerminationState terminationState = TerminationState.CLEAR;

    private int ackCounter = 0;
    private int nackCounter = 0;
    private int sentCounter = 0;

    private int s = 0;


    private NodeCore nodeCore;

    public TerminationManager(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
    }


    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }




    public TerminationState getTerminationState() {
        return terminationState;
    }

    public void setTerminationState(TerminationState terminationState) {
        this.terminationState = terminationState;
    }


    public int getAckCounter() {
        return ackCounter;
    }

    public void incrementAckCounter() {
        ackCounter++;
        checkCondition();
    }

    public void resetAckCounter() {
        ackCounter = 0;
    }




    public int getNackCounter() {
        return nackCounter;
    }

    public void incrementNackCounter() {
        nackCounter++;
        checkCondition();
    }

    public void resetNackCounter() {
        nackCounter = 0;
    }




    public int getSentCounter() {
        return sentCounter;
    }

    public void incrementSentCounter() {
        sentCounter++;
    }

    public void resetSentCounter() {
        sentCounter = 0;
    }


    private void reset() {
        resetAckCounter();
        resetNackCounter();
        resetSentCounter();
        setTerminationState(TerminationState.CLEAR);
    }

    private void checkCondition() {
        int total = nackCounter+ackCounter;

        if (total == sentCounter) {
            nodeCore.getLogger().log(Level.INFO, "Received termination feedback from all nodes...");

            if (nackCounter  > 0) {
                reset();
                //(new Thread(() -> {
                    try {
                        nodeCore.getTcpClient().sendRequest(
                                nodeCore.getFileConfig().getEntryById(0).getHost(),
                                nodeCore.getFileConfig().getEntryById(0).getPort(),
                                new AvaNodeProtocolRequest(
                                        "STARTTERMINATE",
                                        new LinkedList<String>() {{
                                            add(String.valueOf(s+10));
                                        }},
                                        new HashMap<String, String>()
                                )
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //})).start();
            } else {
                reset();
                nodeCore.getLogger().log(Level.INFO, "Terminating Vector Time s="+s+" has been accepted by all nodes.");
            }


        }

    }

}
