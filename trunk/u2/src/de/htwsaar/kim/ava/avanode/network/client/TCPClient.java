package de.htwsaar.kim.ava.avanode.network.client;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.network.protocol.AbstractBaseProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.Request;

import javax.xml.soap.Node;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by markus on 27.12.16.
 */
public class TCPClient {

    private NodeCore nodeCore;

    private AvaNodeClientProtocol avaNodeClientProtocol;


    public TCPClient(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
    }

    private void connect(String host, int port) throws IOException {
        avaNodeClientProtocol = new AvaNodeClientProtocol(new Socket(host, port));
    }

    private void disconnect() throws IOException {
        avaNodeClientProtocol.close();
    }

    public void sendRequest(String host, int port, Request request) throws IOException {
        connect(host, port);
        request.addParameter("SRC", String.valueOf(nodeCore.getNodeId()));
        avaNodeClientProtocol.putLine(request.toProtString());
        //TODO Evaluate Response...
        disconnect();
    }



}
