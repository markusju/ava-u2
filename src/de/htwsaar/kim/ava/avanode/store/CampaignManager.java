package de.htwsaar.kim.ava.avanode.store;

import de.htwsaar.kim.ava.avanode.application.NodeCore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

/**
 * Created by markus on 15.01.17.
 */
public class CampaignManager {
    private Map<Integer, CampaignState> states = new HashMap<>();
    private Map<Integer, Integer> messageCounter = new HashMap<>();
    private int firstNeighbor;

    public Semaphore startCampaignLock = new Semaphore(1, true);

    private NodeCore nodeCore;

    public CampaignManager(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
    }

    public CampaignState getCampaignState(int campaignId) {
        if (!states.containsKey(campaignId)) {
            states.put(campaignId, CampaignState.WHITE);
        }
        return states.get(campaignId);
    }

    public void setCampaignState(int campaignId, CampaignState campaignState) {
        nodeCore.getLogger().log(Level.INFO, "Campaign state changed for campaign "+ campaignId+ " to "+campaignState);
        states.put(campaignId, campaignState);
    }

    public void incrementMessageCounter(int campaignId) {
        int count = messageCounter.containsKey(campaignId) ? messageCounter.get(campaignId) : 0;
        messageCounter.put(campaignId, count + 1);
    }

    public int getMessageCounter(int campaignId) {
        return messageCounter.get(campaignId);
    }

    public void resetMessageCounter(int campaignId) {
        messageCounter.put(campaignId, 0);
    }

    public int getFirstNeighbor() {
        return firstNeighbor;
    }

    public void setFirstNeighbor(int firstNeighbor) {
        this.firstNeighbor = firstNeighbor;
    }

}
