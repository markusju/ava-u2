package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.application.NodeType;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.store.TerminationState;

/**
 * Created by markus on 24.01.17.
 */
public class ACK implements Command{
    @Override
    public String getMethodName() {
        return "ACK";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        if (protocol.getNodeCore().getNodeType() != NodeType.OBSERVER)
            throw new CommandExecutionErrorException("Invalid Node Type");


        if (protocol.getNodeCore().getDataStore().getTerminationManager().getTerminationState() != TerminationState.TERMINATESENT)
            throw new CommandExecutionErrorException("");


        protocol.getNodeCore().getDataStore().getTerminationManager().incrementAckCounter();


        return null;
    }
}
