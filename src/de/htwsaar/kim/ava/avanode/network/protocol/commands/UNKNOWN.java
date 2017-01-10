package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply400;

import java.util.HashMap;

/**
 * Created by markus on 26.12.16.
 */
public class UNKNOWN implements Command {
    @Override
    public String getMethodName() {
        return "UNKNOWN";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) {
        return new Reply400(new HashMap<String, String>(){{
            put("ERROR", "UNKNOWN METHOD");
        }});
    }
}
