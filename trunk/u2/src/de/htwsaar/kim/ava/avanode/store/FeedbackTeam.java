package de.htwsaar.kim.ava.avanode.store;

import de.htwsaar.kim.ava.avanode.application.NodeCore;

import java.util.logging.Level;

/**
 * Created by markus on 15.01.17.
 */
public class FeedbackTeam implements FeedbackObserver {


    private NodeCore nodeCore;

    public FeedbackTeam(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
        installObserver();
    }

    private void installObserver() {
        this.nodeCore.getDataStore().getFeedbackManager().attachObserver(this);
    }


    @Override
    public void feedbackThresholdReached() {
        nodeCore.getLogger().log(Level.INFO, "Feedback Threshold reached.");

    }




}
