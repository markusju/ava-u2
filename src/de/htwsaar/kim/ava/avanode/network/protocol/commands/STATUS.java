package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;

import java.util.HashMap;

/**
 * Created by markus on 31.12.16.
 */
public class STATUS implements Command {
    @Override
    public String getMethodName() {
        return "STATUS";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) {
        return new Reply200(new HashMap<String, String>() {{
            put("VECTIME", String.valueOf(protocol.getNodeCore().getFileConfig().getOwnEntry().getVectorTime()));
            put("RUMORS", protocol.getNodeCore().getDataStore().getRumors().toString());
            put("CONFIDENCE 1", String.valueOf(protocol.getNodeCore().getDataStore().getConfidenceLevel(1)));
            put("CONFIDENCE 2", String.valueOf(protocol.getNodeCore().getDataStore().getConfidenceLevel(2)));
        }});
    }
}
