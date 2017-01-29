package de.htwsaar.kim.ava.avanode;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.election.Election;
import de.htwsaar.kim.ava.avanode.file.FileConfig;
import de.htwsaar.kim.ava.avanode.network.server.TCPParallelServer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by markus on 25.12.16.
 */
public class CliElection {


public static void main(String... args) throws Throwable {
    Election.setupElection(
            Integer.valueOf(args[0]),
            Integer.valueOf(args[1]),
            Integer.valueOf(args[2]),
            Boolean.valueOf(args[3])
    );

}



}

