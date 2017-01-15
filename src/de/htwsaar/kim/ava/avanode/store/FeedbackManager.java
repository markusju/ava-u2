package de.htwsaar.kim.ava.avanode.store;

import java.util.LinkedList;

/**
 * Created by markus on 14.01.17.
 */
public class FeedbackManager {

    private int feedbackCounter;
    private int threshold;
    private LinkedList<FeedbackObserver> feedbackObservers = new LinkedList<>();


    public FeedbackManager(int threshold) {
        this.threshold = threshold;
    }


    public void incrementFeedback() {
        feedbackCounter++;
        if ((feedbackCounter % threshold) == 0)
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
