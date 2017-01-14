package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;

import java.util.HashMap;

/**
 * Created by markus on 14.01.17.
 */
public class SHUTDOWN implements Command {
    @Override
    public String getMethodName() {
        return "SHUTDOWN";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        protocol.getNodeCore().stopNode();
        return new Reply200(new HashMap<>());
    }
}
