package de.htwsaar.kim.ava.avanode.network.protocol.commands;

import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.Request;

public interface Command {


    String getMethodName();
    Reply execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException;


    static Command interpretRequest(Request request) {
        switch (request.getMethod()) {
            case "APPROVE":
                return new APPROVE();
            case "CAMPAIGN":
                return new CAMPAIGN();
            case "REJECT":
                return new REJECT();
            case "RUMOR":
                return new RUMOR();
            case "STATUS":
                return new STATUS();
            case "UNKNOWN":
                return new UNKNOWN();
            case "VOTEFORME":
                return new VOTEFORME();
            case "STARTVOTEFORME":
                return new STARTVOTEFORME();
            case "SHUTDOWN":
                return new SHUTDOWN();
            default:
                return new UNKNOWN();
        }
    }

}