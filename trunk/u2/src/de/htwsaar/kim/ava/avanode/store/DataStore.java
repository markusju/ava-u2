package de.htwsaar.kim.ava.avanode.store;

import java.util.HashMap;

/**
 * Created by markus on 28.12.16.
 */
public class DataStore {

    private HashMap<String, Rumor> rumors = new HashMap<>();
    private HashMap<Integer, Integer> confidenceLevel = new HashMap<>();

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

    public int getConfidenceLevel(int candId) {
        return confidenceLevel.get(candId);
    }

    public void setConfidenceLevel(int candId, int confidenceLevel) {
        if (confidenceLevel > 100)
            confidenceLevel = 100;
        if (confidenceLevel < 0)
            confidenceLevel = 0;
        this.confidenceLevel.put(candId, confidenceLevel);
    }

    public synchronized void increaseByFractionOfItself(int candid, int denom) {
        int confidenceLevel = this.confidenceLevel.get(candid);
        confidenceLevel = (confidenceLevel/denom)+confidenceLevel;

        if (confidenceLevel > 100)
            confidenceLevel = 100;

        if (confidenceLevel < 0)
            confidenceLevel = 0;

        this.confidenceLevel.put(candid, confidenceLevel);
    }
}
