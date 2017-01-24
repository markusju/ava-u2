package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.file.FileEntry;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;
import de.htwsaar.kim.ava.avanode.store.TerminationManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by markus on 24.01.17.
 */
public class STARTSNAPSHOT implements Command {
    @Override
    public String getMethodName() {
        return "STARTSNAPSHOT";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {


        TerminationManager terminationManager = protocol.getNodeCore().getDataStore().getTerminationManager();
        Set<FileEntry> allNodes = protocol.getNodeCore().getFileConfig().getAllEntries();

        for (FileEntry node: allNodes) {
            if (node.getId() == protocol.getNodeCore().getNodeId())
                continue;

            try {
                protocol.getNodeCore().getTcpClient().sendRequest(
                        node.getHost(),
                        node.getPort(),
                        new AvaNodeProtocolRequest(
                                "SNAPSHOT",
                                new LinkedList<String>() {{
                                }},
                                new HashMap<String, String>()
                        )
                );

            } catch (IOException e) {
                e.printStackTrace();
            }



        }


        return null;
    }
}
