package de.htwsaar.kim.ava.avanode.network.protocol;

import com.google.common.base.Splitter;
import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.exception.ClientErrorException;
import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.network.protocol.AbstractBaseProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.commands.Command;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply200;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply400;
import de.htwsaar.kim.ava.avanode.network.protocol.replies.Reply500;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.Request;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by markus on 25.12.16.
 */
public class AvaNodeProtocol extends AbstractBaseProtocol implements Runnable {


    private NodeCore nodeCore;
    private Request request;
    private int source;
    private Map<Integer, Integer> vectimes = new HashMap<>();

    public static String[] ALLOWED_AFTER_TERMINATION = {"STATUS", "ACK", "NACK", "TERMINATE", "STARTTERMINATE"};

    public AvaNodeProtocol(Socket socket, NodeCore nodeCore) throws IOException {
        super(socket);
        this.nodeCore = nodeCore;
    }

    public AvaNodeProtocol(NodeCore nodeCore) {
        super();
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


    private void readVectime() {
        String vectime = request.getParameters().get("VECTIME");

        if (vectime != null) {
            Map<String, String> tempVectimes = Splitter
                    .on(",").trimResults()
                    .withKeyValueSeparator("=")
                    .split(
                            vectime
                                    .replace("{", "")
                                    .replace("}", "")
                    );


            for(Map.Entry<String, String> entry: tempVectimes.entrySet()) {
                vectimes.put(Integer.valueOf(entry.getKey()), Integer.valueOf(entry.getValue()));
            }
        }
        nodeCore.getFileConfig().processIncomingVectorTimes(vectimes);
    }


    private void checkRequest() throws ClientErrorException {
        //Contains SRC Param?
        if (!request.getParameters().containsKey("SRC")) {
            throw new ClientErrorException("SRC Paramter was not supplied!");
        }
    }

    private void checkForTermination() throws ClientErrorException {
        boolean terminated = nodeCore.getFileConfig().getOwnEntry().isTerminated();
        boolean methodAllowed = Arrays.asList(ALLOWED_AFTER_TERMINATION).contains(request.getMethod());

        if (terminated && !methodAllowed) {
            throw new ClientErrorException("Client is terminated.");
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
            readVectime();

            nodeCore.getFileConfig().getOwnEntry().incVectorTime();

            getNodeCore().getLogger().log(Level.INFO, "Request '"+request.getMethod()+"' from "+source);
            checkForTermination();
            close();
            //Evaluierung
            //nodeCore.getTcpParallelServer().mutex.acquire();
            Reply reply = command.execute(this);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nodeCore.getTcpParallelServer().mutex.release();
        }



    }
}
