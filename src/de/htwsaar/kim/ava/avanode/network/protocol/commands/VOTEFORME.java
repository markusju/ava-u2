package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.file.FileEntry;
import de.htwsaar.kim.ava.avanode.network.client.TCPClient;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;

import java.io.IOException;
import java.util.HashMap;
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
        int identifier = Integer.valueOf(protocol.getRequest().getParameters().get("ID"));

        if (protocol.getNodeCore().getDataStore().alreadySeenVotForMe(identifier)) {
            protocol.getNodeCore().getLogger().log(Level.INFO, "Not relaying. I have seen this VOTEFORME already.");
            return new Reply200(new HashMap<>());
        }

        protocol.getNodeCore().getDataStore().addVoteForMeId(identifier);

        //Check confidence...
        protocol.getNodeCore().getDataStore().increaseConfidenceByFractionOfItself(candId, 10);

        int confidenceCand1 = protocol.getNodeCore().getDataStore().getConfidenceLevel(1);
        int confidenceCand2 = protocol.getNodeCore().getDataStore().getConfidenceLevel(2);

            //Ich vertraue dem anderen Kandidaten mehr...
            if (candId == 1 && (confidenceCand2 > confidenceCand1) ||
                    candId == 2 && (confidenceCand1 > confidenceCand2)) {

                protocol.getNodeCore().getLogger().log(Level.INFO, "Not relaying. Confidence for "+candId+" to low");
                try {
                    TCPClient.sendREJECT(protocol.getNodeCore().getTcpClient(),
                            protocol.getNodeCore().getFileConfig().getEntryById(candId).getHost(),
                            protocol.getNodeCore().getFileConfig().getEntryById(candId).getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Reply200(new HashMap<>());
            }

        //Signal Approval
        try {
            TCPClient.sendAPPROVE(protocol.getNodeCore().getTcpClient(),
                    protocol.getNodeCore().getFileConfig().getEntryById(candId).getHost(),
                    protocol.getNodeCore().getFileConfig().getEntryById(candId).getPort()
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
                                        put("ID", String.valueOf(identifier));
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
