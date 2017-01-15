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
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by markus on 16.01.17.
 */
public class STARTCAMPAIGN implements Command {


    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        if (protocol.getNodeCore().getNodeType() != NodeType.CANDIDATE) {
            throw new CommandExecutionErrorException("This command is not allowed for this node type.");
        }

        Set<FileEntry> neighbors = protocol.getNodeCore().getFileConfig().getNeighbors();
        protocol.getNodeCore().getDataStore().incrementCounter();

        for (FileEntry partyFellow: neighbors) {
            try {
                TCPClient.sendCAMPAIGN(protocol.getNodeCore().getTcpClient(),
                        partyFellow.getHost(),
                        partyFellow.getPort()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return new Reply200(new HashMap<>());
    }
}
