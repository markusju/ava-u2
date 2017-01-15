package de.htwsaar.kim.ava.avanode;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.file.FileConfig;
import de.htwsaar.kim.ava.avanode.network.server.TCPParallelServer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by markus on 25.12.16.
 */
public class Main {


public static void main(String... args) throws Throwable {

    int max = 10;

    FileConfig.genConfigFile(max);
    FileConfig.genDotFile(max, max+max, "file.dot");

    List<NodeCore> nodes = new LinkedList<NodeCore>();

    for (int i = 1; i <= max ; i++) {
        NodeCore node = new NodeCore(i, 100);
        node.startNode();
        nodes.add(node);
    }





}



}
