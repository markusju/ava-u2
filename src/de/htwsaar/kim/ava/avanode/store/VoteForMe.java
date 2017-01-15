package de.htwsaar.kim.ava.avanode.store;

/**
 * Created by markus on 15.01.17.
 */
public class VoteForMe {
    private int candId;
    private int identifier;


    public VoteForMe(int candId, int identifier) {
        this.candId = candId;
        this.identifier = identifier;
    }

    public int getCandId() {
        return candId;
    }

    public int getIdentifier() {
        return identifier;
    }
}
