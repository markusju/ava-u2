package de.htwsaar.kim.ava.avanode;

import de.htwsaar.kim.ava.avanode.election.Election;

/**
 * Created by markus on 08.01.17.
 */
public class MakeAVAGreatAgain {


    public static void main(String... args) throws Exception {

        Election.setupElection(
                20,
                6,
                3,
                true
        );

    }

}
