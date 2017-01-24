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
import java.util.Set;

/**
 * Created by markus on 23.01.17.
 */
public class STARTTERMINATE implements Command {
    @Override
    public String getMethodName() {
        return "STARTTERMINATE";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        if (protocol.getNodeCore().getNodeType() != NodeType.OBSERVER) {
            throw new CommandExecutionErrorException("Invalid NodeType!");
        }

        Set<FileEntry> allNodes = protocol.getNodeCore().getFileConfig().getAllEntries();
        int s = 1234;

        for (FileEntry node: allNodes) {
            if (node.getId() == protocol.getNodeCore().getNodeId())
                continue;

            try {
                protocol.getNodeCore().getTcpClient().sendRequest(
                        node.getHost(),
                        node.getPort(),
                        new AvaNodeProtocolRequest(
                                "TERMINATE",
                                new LinkedList<String>() {{
                                    add(String.valueOf(s));
                                }},
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
