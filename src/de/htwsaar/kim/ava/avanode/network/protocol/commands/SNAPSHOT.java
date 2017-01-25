package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by markus on 24.01.17.
 */
public class SNAPSHOT implements Command {
    @Override
    public String getMethodName() {
        return "SNAPHSHOT";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {

        if (protocol.getNodeCore().getNodeType() != NodeType.VOTER && protocol.getNodeCore().getNodeType() != NodeType.CANDIDATE)
            throw new CommandExecutionErrorException("Invalid Node Type");

        int s = protocol.getNodeCore().getFileConfig().getOwnEntry().getVectorTimeLimit();

        if (s == -1)
            throw new CommandExecutionErrorException("S has not been set yet.");

        protocol.getNodeCore().getFileConfig().getOwnEntry().updateVectorTime(s);
        int voteCandId = protocol.getNodeCore().getDataStore().getVoteCandidateId();

        try {
            protocol.getNodeCore().getTcpClient().sendRequest(
                    protocol.getNodeCore().getFileConfig().getEntryById(0).getHost(),
                    protocol.getNodeCore().getFileConfig().getEntryById(0).getPort(),
                    new AvaNodeProtocolRequest(
                            "VOTE",
                            new LinkedList<String>() {{
                                add(String.valueOf(voteCandId));
                            }},
                            new HashMap<String, String>()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
