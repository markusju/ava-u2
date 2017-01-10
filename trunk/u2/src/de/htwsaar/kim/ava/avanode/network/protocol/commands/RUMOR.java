package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.file.FileEntry;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;
import de.htwsaar.kim.ava.avanode.store.Rumor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by markus on 25.12.16.
 */
public class RUMOR implements Command {

    @Override
    public String getMethodName() {
        return "RUMOR";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) {
        List<String> messageList = protocol.getRequest().getMethodArguments();
        String message = String.join(" ", messageList);
        int source = protocol.getSource();
        int vectime = Integer.valueOf(protocol.getRequest().getParameters().get("VECTIME"));

        //Update Vectime for source
        protocol.getNodeCore().getFileConfig().getEntryById(source).updateVectorTime(vectime);

        //Update/Add and retrieve Rumor
        protocol.getNodeCore().getDataStore().addRumor(message, source);
        Rumor rumor = protocol.getNodeCore().getDataStore().getRumor(message);

        //Increment own vector time
        protocol.getNodeCore().getFileConfig().getOwnEntry().incVectorTime();

        //Log
        protocol.getNodeCore().getLogger().log(Level.INFO, "Received RUMOR "+rumor.getRumor()+" from "+source+". My Vectime: "+ protocol.getNodeCore().getFileConfig().getOwnEntry().getVectorTime()+" Far-end Vectime: "+vectime);

        if (rumor.alreadyHeard()) {
            protocol.getNodeCore().getLogger().log(Level.INFO, "Not relaying RUMOR "+ rumor.getRumor()+". Already heard this.");
            return new Reply200(new HashMap<String, String>(){{
                put("MESSAGE", "Not relaying Rumor. Already heard.");
                //put("VECTIME", String.valueOf(protocol.getNodeCore().getFileConfig().getOwnEntry().getVectorTime()));
            }});

        }

        Set<FileEntry> neighbors = protocol.getNodeCore().getFileConfig().getNeighbors();

            for (FileEntry entry: neighbors) {
                try {

                    if (rumor.inReceivedFrom(entry.getId()))
                        continue;

                    //Increment own vector time
                    protocol.getNodeCore().getFileConfig().getOwnEntry().incVectorTime();

                    protocol.getNodeCore().getTcpClient().sendRequest(
                            entry.getHost(),
                            entry.getPort(),
                            new AvaNodeProtocolRequest("RUMOR", messageList, new HashMap<String, String>() {{
                                put("VECTIME", String.valueOf(protocol.getNodeCore().getFileConfig().getOwnEntry().getVectorTime()));
                            }})
                    );

                    rumor.addSentTo(entry.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        return new Reply200(new HashMap<String, String>(){{
            put("NEIGHBORS", neighbors.toString());
        }});
    }
}
