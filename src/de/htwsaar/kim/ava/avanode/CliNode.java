package de.htwsaar.kim.ava.avanode;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.application.NodeType;

import java.io.IOException;

/**
 * Created by markus on 29.01.17.
 */
public class CliNode {


    public static void main(String... args) throws IOException {


        if (args.length != 3) {
            System.out.println("Usage: CliNode <nodeId> <Type> <feedbackThreshold>");
            return;
        }


        int nodeId = Integer.valueOf(args[0]);
        String type = args[1];
        int feedbackThreshold = Integer.valueOf(args[2]);



        NodeCore cand1 = new NodeCore(nodeId, feedbackThreshold);

        switch (type) {
            case "OBSERVER":
                cand1.setNodeType(NodeType.OBSERVER);
                break;
            case "VOTER":
                cand1.setNodeType(NodeType.VOTER);
                break;
            case "CANDIDATE":
                cand1.setNodeType(NodeType.CANDIDATE);
                break;
            default:
                throw new RuntimeException("Invalid Type");
        }

        cand1.startNode();

    }




}
