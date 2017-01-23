package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.file.FileEntry;
import de.htwsaar.kim.ava.avanode.network.client.TCPClient;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;
import de.htwsaar.kim.ava.avanode.store.CampaignManager;
import de.htwsaar.kim.ava.avanode.store.CampaignState;

import java.io.IOException;
import java.util.HashMap;
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

        CampaignManager manager = protocol.getNodeCore().getDataStore().getCampaignManager();
        int counter = protocol.getNodeCore().getDataStore().getCounter();
        int campaignId = Integer.valueOf(String.valueOf(protocol.getNodeCore().getNodeId())+String.valueOf(counter));
        protocol.getNodeCore().getDataStore().incrementCounter();

        /*try {
            protocol.getNodeCore().getDataStore().getCampaignManager().startCampaignLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        if (manager.getCampaignState(campaignId) != CampaignState.WHITE)
            throw new CommandExecutionErrorException("Wrong State");

        Set<FileEntry> neighbors = protocol.getNodeCore().getFileConfig().getNeighbors();

        manager.setCampaignState(campaignId, CampaignState.RED);

        for (FileEntry partyFellow: neighbors) {
            try {
                TCPClient.sendCAMPAIGN(protocol.getNodeCore().getTcpClient(),
                        partyFellow.getHost(),
                        partyFellow.getPort(),
                        protocol.getNodeCore().getNodeId(),
                        campaignId
                );
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return new Reply200(new HashMap<>());
    }
}
