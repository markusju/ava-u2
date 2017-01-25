package de.htwsaar.kim.ava.avanode.store;

/**
 * Created by markus on 25.01.17.
 */
public class ElectionManager {
    private int votesCand1 = 0;
    private int votesCand2 = 0;


    public void addVote(int candId) {
        switch(candId) {
            case 1:
                votesCand1++;
                break;
            case 2:
                votesCand2++;
                break;
        }
    }

    public int electPresident() {
        return ((votesCand1 > votesCand2)?1:2);
    }


}
