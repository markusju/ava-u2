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
import java.util.logging.Level;

/**
 * Created by markus on 23.01.17.
 */
public class TERMINATE implements Command {
    @Override
    public String getMethodName() {
        return "TERMINATE";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        if (protocol.getNodeCore().getNodeType() != NodeType.CANDIDATE && protocol.getNodeCore().getNodeType() != NodeType.VOTER)
            throw new CommandExecutionErrorException("nope");

        int s = Integer.valueOf(protocol.getRequest().getMethodArguments().get(0));
        int myVectime = protocol.getNodeCore().getFileConfig().getOwnEntry().getVectorTime();
        FileEntry observer = protocol.getNodeCore().getFileConfig().getEntryById(protocol.getSource());


        if (s > myVectime) {
            protocol.getNodeCore().getLogger().log(Level.INFO, "Termination time s="+s+" is valid.");
            try {
                protocol.getNodeCore().getTcpClient().sendRequest(
                        observer.getHost(),
                        observer.getPort(),
                        new AvaNodeProtocolRequest(
                                "ACK",
                                new LinkedList<String>(),
                                new HashMap<String, String>()
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
            protocol.getNodeCore().getFileConfig().getOwnEntry().setVectorTimeLimit(s);

        } else {
            protocol.getNodeCore().getLogger().log(Level.INFO, "Termination time s="+s+" is invalid.");
            try {
                protocol.getNodeCore().getTcpClient().sendRequest(
                        observer.getHost(),
                        observer.getPort(),
                        new AvaNodeProtocolRequest(
                                "NACK",
                                new LinkedList<String>(),
                                new HashMap<String, String>()
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }





        return new Reply200(new HashMap<>());
    }
}
