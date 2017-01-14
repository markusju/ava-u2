package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.file.FileEntry;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by markus on 29.12.16.
 */
public class VOTEFORME implements Command {
    @Override
    public String getMethodName() {
        return "VOTEFORME";
    }



    private static AvaNodeProtocolRequest genReject() {
        return new AvaNodeProtocolRequest(
                "REJECT",
                new LinkedList<String>(),
                new HashMap<String, String>()
        );
    }

    private static AvaNodeProtocolRequest genApprove() {
        return new AvaNodeProtocolRequest(
                "APPROVE",
                new LinkedList<String>(),
                new HashMap<String, String>()
        );
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        if (protocol.getNodeCore().getNodeType() != NodeType.VOTER) {
            throw new CommandExecutionErrorException("This command is not allowed for this node type.");
        }

        List<String> messageList = protocol.getRequest().getMethodArguments();
        if (messageList.size() != 1)
            throw new CommandExecutionErrorException("No Candidate ID found.");
        int candId = Integer.valueOf(messageList.get(0));
        int source = protocol.getSource();


        //Check confidence...

        protocol.getNodeCore().getDataStore().increaseByFractionOfItself(candId, 10);

        int confidenceCand1 = protocol.getNodeCore().getDataStore().getConfidenceLevel(1);
        int confidenceCand2 = protocol.getNodeCore().getDataStore().getConfidenceLevel(2);

        if (candId == 1) {
            //Ich vertraue dem anderen Kandidaten mehr...
            if (confidenceCand2 > confidenceCand1) {
                try {
                    protocol.getNodeCore().getTcpClient().sendRequest(
                            protocol.getNodeCore().getFileConfig().getEntryById(1).getHost(),
                            protocol.getNodeCore().getFileConfig().getEntryById(1).getPort(),
                            genReject()

                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Reply200(new HashMap<>());
            }
        }

        if (candId == 2) {
            //Ich vertraue dem anderen Kandidaten mehr...
            if (confidenceCand1 > confidenceCand2) {
                try {
                    protocol.getNodeCore().getTcpClient().sendRequest(
                            protocol.getNodeCore().getFileConfig().getEntryById(2).getHost(),
                            protocol.getNodeCore().getFileConfig().getEntryById(2).getPort(),
                            genReject()

                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Reply200(new HashMap<>());
            }

        }


        //Signal Approval

        try {
            protocol.getNodeCore().getTcpClient().sendRequest(
                    protocol.getNodeCore().getFileConfig().getEntryById(candId).getHost(),
                    protocol.getNodeCore().getFileConfig().getEntryById(candId).getPort(),
                    genApprove()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


        Set<FileEntry> neighbors = protocol.getNodeCore().getFileConfig().getNeighbors();

        for (FileEntry entry: neighbors) {

            //Pas de Loops S.V.P
            if (source == entry.getId())
                continue;

            /*(new Thread() {
                    @Override
                    public void run() {*/
                        try {
                            protocol.getNodeCore().getTcpClient().sendRequest(
                                    entry.getHost(),
                                    entry.getPort(),
                                    new AvaNodeProtocolRequest("VOTEFORME", messageList, new HashMap<String, String>() {{
                                        //put("VECTIME", String.valueOf(protocol.getNodeCore().getFileConfig().getOwnEntry().getVectorTime()));
                                    }})
                            );
                        } catch (IOException e) {
                            protocol.getNodeCore().getLogger().log(Level.SEVERE, "Could not send VOTEFORME to "+entry.getId(), e);
                        }
                    }
                /*}).start();

        }*/


        return new Reply200(new HashMap<String, String>() {{

        }});
    }
}
