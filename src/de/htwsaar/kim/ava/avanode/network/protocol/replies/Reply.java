package de.htwsaar.kim.ava.avanode.network.protocol.replies;

import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;

import java.util.Map;

/**
 * Created by markus on 26.12.16.
 */
public abstract class Reply {

    protected int replyCode;
    protected String message;
    protected  Map<String, String> parameters;


    public Reply(int replyCode, String message, Map<String, String> parameters) {
        this.replyCode = replyCode;
        this.message = message;
        this.parameters = parameters;
    }


    public void putReply(AvaNodeProtocol protocol) {
        StringBuilder sb = new StringBuilder();

        sb.append(replyCode).append(" ").append(message).append("\n");

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        protocol.putLine(sb.toString());
    }


}
