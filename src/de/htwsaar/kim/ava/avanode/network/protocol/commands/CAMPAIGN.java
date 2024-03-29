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

        if (protocol.getNodeCore().getNodeType() != NodeType.CANDIDATE && protocol.getNodeCore().getNodeType() != NodeType.VOTER)
            throw new CommandExecutionErrorException("Command not allowed for this NodeType");

        int candId = Integer.valueOf(protocol.getRequest().getMethodArguments().get(0));
        Set<FileEntry> neighbors = protocol.getNodeCore().getFileConfig().getNeighbors();
        CampaignManager manager = protocol.getNodeCore().getDataStore().getCampaignManager();

        int source = protocol.getSource();
        int campaignId = Integer.valueOf(protocol.getRequest().getParameters().get("ID"));

        //EXPLORER
        if (manager.getCampaignState(campaignId) == CampaignState.WHITE && protocol.getNodeCore().getNodeId() != candId) {
            protocol.getNodeCore().getLogger().log(Level.INFO, "Interpreting request from "+source+" as EXPLORER");
            manager.setFirstNeighbor(source);
            manager.setCampaignState(campaignId, CampaignState.RED);

            //Process further actions...
            //...
            int confidenceCand1 = protocol.getNodeCore().getDataStore().getConfidenceLevel(1);
            int confidenceCand2 = protocol.getNodeCore().getDataStore().getConfidenceLevel(2);

            //Meh. Can't decide what to do?!
            if (confidenceCand1 != confidenceCand2) {
                //You are annoying me.
                if (candId == 1 && (confidenceCand2 > confidenceCand1) ||
                        candId == 2 && (confidenceCand1 > confidenceCand2)) {
                    protocol.getNodeCore().getLogger().log(Level.INFO, protocol.getNodeCore().getNodeId() + " is annoyed by Candidate " + candId + "'s campaign.");
                    protocol.getNodeCore().getDataStore().incrementConfidence((candId == 1 ? 2 : 1));
                    protocol.getNodeCore().getDataStore().decrementConfidence(candId);
                    //I appreciate you.
                } else {
                    protocol.getNodeCore().getLogger().log(Level.INFO, protocol.getNodeCore().getNodeId() + " appreciates Candidate " + candId + "'s campaign.");
                    protocol.getNodeCore().getDataStore().decrementConfidence((candId == 1 ? 2 : 1));
                    protocol.getNodeCore().getDataStore().incrementConfidence(candId);
                }
            }


            //Relay campaign
            for (FileEntry entry: neighbors) {
                if (entry.getId() == source)
                    continue;

                try {
                    TCPClient.sendCAMPAIGN(protocol.getNodeCore().getTcpClient(),
                            entry.getHost(),
                            entry.getPort(),
                            candId,
                            campaignId);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        manager.incrementMessageCounter(campaignId);

        //ECHO
        if (manager.getMessageCounter(campaignId) == neighbors.size()) {
            protocol.getNodeCore().getLogger().log(Level.INFO, "Interpreting request from "+source+" as ECHO");
            manager.setCampaignState(campaignId, CampaignState.GREEN);

            //Initiator?
            if (protocol.getNodeCore().getNodeId() == candId) {
                //Done
                manager.resetMessageCounter(campaignId);
                manager.setCampaignState(campaignId, CampaignState.WHITE);
                //protocol.getNodeCore().getDataStore().getCampaignManager().startCampaignLock.release();
                protocol.getNodeCore().getDataStore().getFeedbackManager().incrementToThreshold();

                //protocol.getNodeCore().getDataStore().getFeedbackManager().incrementFeedback();
                return new Reply200(new HashMap<String, String>() {{

                }});
            }


            //Burn cycles until there is a state change... otherwise this turns into a race condition
            //TODO: Fix Busy waiting. Make sure no one sees this.
            //TODO: Introduce Semaphore.


            FileEntry firstNeighbor = protocol.getNodeCore().getFileConfig()
                    .getEntryById(manager.getFirstNeighbor());
            try {
                TCPClient.sendCAMPAIGN(protocol.getNodeCore().getTcpClient(),
                        firstNeighbor.getHost(),
                        firstNeighbor.getPort(),
                        candId,
                        campaignId);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Reset
            manager.resetMessageCounter(campaignId);
            manager.setCampaignState(campaignId, CampaignState.WHITE);
        }



        


        return new Reply200(new HashMap<String, String>() {{

        }});
    }
}
