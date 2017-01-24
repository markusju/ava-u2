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
            case "ACK":
                return new ACK();
            case "APPROVE":
                return new APPROVE();
            case "CAMPAIGN":
                return new CAMPAIGN();
            case "NACK":
                return new NACK();
            case "REJECT":
                return new REJECT();
            case "RUMOR":
                return new RUMOR();
            case "STATUS":
                return new STATUS();
            case "TERMINATE":
                return new TERMINATE();
            case "UNKNOWN":
                return new UNKNOWN();
            case "VOTEFORME":
                return new VOTEFORME();
            case "STARTVOTEFORME":
                return new STARTVOTEFORME();
            case "STARTCAMPAIGN":
                return new STARTCAMPAIGN();
            case "STARTTERMINATE":
                return new STARTTERMINATE();
            case "SHUTDOWN":
                return new SHUTDOWN();
            default:
                return new UNKNOWN();
        }
    }

}