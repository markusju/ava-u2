package de.htwsaar.kim.ava.avanode.store;

/**
 * Created by markus on 14.01.17.
 */
interface FeedbackObserver {

    /**
     * Called when a candidate has reached the threshold.
     */
    void feedbackThresholdReached();

}
