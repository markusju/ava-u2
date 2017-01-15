package de.htwsaar.kim.ava.avanode.store;

import de.htwsaar.kim.ava.avanode.application.NodeCore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by markus on 28.12.16.
 */
public class DataStore {

    private HashMap<String, Rumor> rumors = new HashMap<>();
    private List<Integer> voteForMeIds = new LinkedList<>();
    private int counter = 0;
    private int cand1Confidence = 0;
    private int cand2Confidence = 0;

    private CampaignManager campaignManager;
    private FeedbackManager feedbackManager;

    private NodeCore nodeCore;


    public DataStore(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
        feedbackManager = new FeedbackManager(nodeCore);
        campaignManager = new CampaignManager(nodeCore);
    }

    public void addRumor(String rumor, int source) {
        if (rumors.containsKey(rumor)) {
            rumors.get(rumor).addReceivedFrom(source);
        } else {
            rumors.put(rumor, new Rumor(rumor, source));
        }

    }

    public HashMap<String, Rumor> getRumors() {
        return rumors;
    }

    public Rumor getRumor(String rumor) {
        return this.rumors.get(rumor);
    }

    public void addVoteForMeId(int identifier) {
        voteForMeIds.add(identifier);
    }

    public boolean alreadySeenVotForMe(int identifier) {
        return voteForMeIds.contains(identifier);
    }

    public void incrementCounter() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }


    public int getConfidenceLevel(int candId) {
        switch(candId) {
            case 1:
                return cand1Confidence;
            case 2:
                return cand2Confidence;
            default:
                throw new IllegalArgumentException();
        }
    }

    public synchronized void setConfidenceLevel(int candId, int confidenceLevel) {
        if (confidenceLevel > 100)
            confidenceLevel = 100;
        if (confidenceLevel < 0)
            confidenceLevel = 0;

        switch(candId) {
            case 1:
                cand1Confidence = confidenceLevel;
                return;
            case 2:
                cand2Confidence = confidenceLevel;
                return;
        }
    }

    public void increaseConfidenceByFractionOfItself(int candid, int denom) {
        int confidenceLevel = getConfidenceLevel(candid);
        confidenceLevel = (confidenceLevel/denom)+confidenceLevel;

        if (confidenceLevel > 100)
            confidenceLevel = 100;

        if (confidenceLevel < 0)
            confidenceLevel = 0;

        setConfidenceLevel(candid, confidenceLevel);
    }

    public CampaignManager getCampaignManager() {
        return campaignManager;
    }

    public FeedbackManager getFeedbackManager() {
        return feedbackManager;
    }
}
