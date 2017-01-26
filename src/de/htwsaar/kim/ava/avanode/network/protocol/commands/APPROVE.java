package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/*
 * WEITER-SO
 */
public class APPROVE implements Command{


    @Override
    public String getMethodName() {
        return "APPROVE";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {

        if (protocol.getNodeCore().getNodeType() != NodeType.CANDIDATE) {
            throw new CommandExecutionErrorException("This command is not allowed for this node type.");
        }

        int source = protocol.getSource();
        int id = Integer.valueOf(protocol.getRequest().getParameters().get("ID"));

        protocol.getNodeCore().getDataStore().getFeedbackManager().receivedApproveOrReject(id);

        return new Reply200(new HashMap<String, String>() {{

        }});
    }
}
