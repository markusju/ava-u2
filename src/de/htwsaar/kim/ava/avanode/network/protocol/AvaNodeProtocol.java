package de.htwsaar.kim.ava.avanode.network.protocol;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.exception.ClientErrorException;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.network.protocol.AbstractBaseProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.commands.Command;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply400;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply500;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.Request;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by markus on 25.12.16.
 */
public class AvaNodeProtocol extends AbstractBaseProtocol implements Runnable {


    private NodeCore nodeCore;
    private Request request;
    private int source;

    public AvaNodeProtocol(Socket socket, NodeCore nodeCore) throws IOException {
        super(socket);
        this.nodeCore = nodeCore;
    }

    public NodeCore getNodeCore() {
        return nodeCore;
    }

    public int getSource() {
        return source;
    }

    public Request getRequest() {
        return request;
    }


    private void checkRequest() throws ClientErrorException {
        //Contains SRC Param?
        if (!request.getParameters().containsKey("SRC")) {
            throw new ClientErrorException("SRC Paramter was not supplied!");
        }
    }
    @Override
    public void run() {
        try {
            //Syntaktische Analyse
            request = new AvaNodeProtocolRequest(this);



            //Semantische Analyse
            Command command = Command.interpretRequest(request);

            checkRequest();
            source = Integer.valueOf(request.getParameters().get("SRC"));

            getNodeCore().getLogger().log(Level.INFO, "Request '"+request.getMethod()+"' from "+source);

            //Evaluierung
            Reply reply = command.execute(this);
            reply.putReply(this);

        } catch (ClientErrorException | IOException | CommandExecutionErrorException e) {
            new Reply400(new HashMap<String, String>(){{
                put("ERROR", e.toString());
            }}).putReply(this);
        } catch (Exception e) {
            new Reply500(new HashMap<String, String>(){{
                put("ERROR", e.toString());
            }}).putReply(this);
            e.printStackTrace();
        }



    }
}
