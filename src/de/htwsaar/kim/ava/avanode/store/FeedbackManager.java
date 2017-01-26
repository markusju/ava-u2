package de.htwsaar.kim.ava.avanode.store;

import de.htwsaar.kim.ava.avanode.application.NodeCore;

import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;

/**
 * Created by markus on 14.01.17.
 */
public class FeedbackManager {

    private int feedbackCounter;
    private LinkedList<FeedbackObserver> feedbackObservers = new LinkedList<>();
    private HashMap<Integer, Integer> approveRejectIdMap = new HashMap<>();


    private NodeCore nodeCore;

    public FeedbackManager(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
    }


    public void receivedApproveOrReject(int id) {
        int count = approveRejectIdMap.containsKey(id) ?approveRejectIdMap.get(id) : 0;
        approveRejectIdMap.put(id, count + 1);

        if (count >= nodeCore.getFeedbackThreshold())
            return;

        incrementFeedback();
    }

    public void incrementFeedback() {
        feedbackCounter++;
        if ((feedbackCounter % nodeCore.getFeedbackThreshold()) == 0)
            notifyObservers();
    }

    public void incrementToThreshold() {
        feedbackCounter += nodeCore.getFeedbackThreshold();
        if ((feedbackCounter % nodeCore.getFeedbackThreshold()) == 0)
            notifyObservers();
    }



    private void notifyObservers() {
        for (FeedbackObserver feedbackObserver : feedbackObservers) {
                feedbackObserver.feedbackThresholdReached();
        }
    }

    public void attachObserver(FeedbackObserver feedbackObserver) {
        feedbackObservers.add(feedbackObserver);
    }

    public void detachObserver(FeedbackObserver feedbackObserver) {
        feedbackObservers.remove(feedbackObserver);
    }

}
