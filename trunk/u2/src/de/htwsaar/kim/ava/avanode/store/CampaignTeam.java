package de.htwsaar.kim.ava.avanode.store;

import de.htwsaar.kim.ava.avanode.application.NodeCore;

import java.util.logging.Level;

/**
 * Created by markus on 15.01.17.
 */
public class CampaignTeam implements FeedbackObserver {


    private NodeCore nodeCore;

    public CampaignTeam(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
        installObserver();
    }

    private void installObserver() {
        this.nodeCore.getDataStore().getFeedbackManager().attachObserver(this);
    }




    @Override
    public void feedbackThresholdReached() {
        nodeCore.getLogger().log(Level.INFO, "Feedback Threshold reached.");
        //TODO:
        //Either start new campaign or vote-for-me thingy..
    }




}
