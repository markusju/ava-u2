package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;

import java.util.logging.Level;

/**
 * Created by markus on 24.01.17.
 */
public class VOTE implements Command {
    @Override
    public String getMethodName() {
        return "VOTE";
    }

    @Override
    public Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {

        int voteCandId = Integer.valueOf(protocol.getRequest().getMethodArguments().get(0));

        protocol.getNodeCore().getLogger().log(Level.INFO, "Vote for "+voteCandId);
        protocol.getNodeCore().getDataStore().getElectionManager().addVote(voteCandId);

        return null;
    }
}
