package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.file.FileConfig;
import de.htwsaar.kim.ava.avanode.file.FileEntry;
import de.htwsaar.kim.ava.avanode.network.client.TCPClient;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;
import de.htwsaar.kim.ava.avanode.store.CampaignManager;
import de.htwsaar.kim.ava.avanode.store.CampaignState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by markus on 29.12.16.
 */

public class CAMPAIGN implements Command{
    @Override
    public String getMethodName() {
        return "CAMPAIGN";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {


        Set<FileEntry> neighbors = protocol.getNodeCore().getFileConfig().getNeighbors();
        CampaignManager manager = protocol.getNodeCore().getDataStore().getCampaignManager();

        int source = protocol.getSource();

        manager.incrementMessageCounter();


        if (manager.getCampaignState() == CampaignState.WHITE) {
            manager.setCampaignState(CampaignState.RED);

            for (FileEntry entry: neighbors) {
                if (entry.getId() == source)
                    continue;

                try {
                    TCPClient.sendCAMPAIGN(protocol.getNodeCore().getTcpClient(),
                            entry.getHost(),
                            entry.getPort());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                manager.setFirstNeighbor(source);

            }
        }

        if (manager.getMessageCounter() == neighbors.size()) {
            manager.setCampaignState(CampaignState.GREEN);

            if (protocol.getNodeCore().getNodeType() != NodeType.VOTER) {
                //Done
                return new Reply200(new HashMap<String, String>() {{

                }});
            }

            FileEntry firstNeighbor = protocol.getNodeCore().getFileConfig()
                    .getEntryById(manager.getFirstNeighbor());
            try {
                TCPClient.sendCAMPAIGN(protocol.getNodeCore().getTcpClient(),
                        firstNeighbor.getHost(),
                        firstNeighbor.getPort());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        


        return new Reply200(new HashMap<String, String>() {{

        }});
    }
}
