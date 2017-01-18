package de.htwsaar.kim.ava.avanode.store;

import de.htwsaar.kim.ava.avanode.application.NodeCore;

import java.util.logging.Level;

/**
 * Created by markus on 15.01.17.
 */
public class CampaignManager {
    private CampaignState campaignState = CampaignState.WHITE;
    private int messageCounter = 0;
    private int firstNeighbor;

    private NodeCore nodeCore;

    public CampaignManager(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
    }

    public CampaignState getCampaignState() {
        return campaignState;
    }

    public void setCampaignState(CampaignState campaignState) {
        nodeCore.getLogger().log(Level.INFO, "Campaign state changed to "+campaignState);
        this.campaignState = campaignState;
    }

    public void incrementMessageCounter() {
        messageCounter++;
    }

    public int getMessageCounter() {
        return messageCounter;
    }

    public void resetMessageCounter() {
        messageCounter = 0;
    }

    public int getFirstNeighbor() {
        return firstNeighbor;
    }

    public void setFirstNeighbor(int firstNeighbor) {
        this.firstNeighbor = firstNeighbor;
    }
}
