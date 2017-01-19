package de.htwsaar.kim.ava.avanode.network.client;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.network.protocol.AbstractBaseProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.AvaNodeProtocolRequest;
import de.htwsaar.kim.ava.avanode.network.protocol.requests.Request;

import javax.xml.soap.Node;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

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


    public static void sendAPPROVE(TCPClient tcpClient, String host, int port) throws IOException {
        tcpClient.sendRequest(
                host,
                port,
                genApprove()
        );
    }


    public static void sendREJECT(TCPClient tcpClient, String host, int port) throws IOException {
        tcpClient.sendRequest(
                host,
                port,
                genReject()
        );
    }

    public static void sendCAMPAIGN(TCPClient tcpClient, String host, int port, int candId) throws IOException {
        tcpClient.sendRequest(
                host,
                port,
                genCampaign(candId)
        );
    }

    public static void sendSTARTCAMPAIGN(TCPClient tcpClient, String host, int port) throws IOException {
        tcpClient.sendRequest(
                host,
                port,
                new AvaNodeProtocolRequest(
                        "STARTCAMPAIGN",
                        new LinkedList<String>(),
                        new HashMap<String, String>()
                )
        );
    }


    public static void sendSTARTVOTEFORME(TCPClient tcpClient, String host, int port) throws IOException {
        tcpClient.sendRequest(
                host,
                port,
                new AvaNodeProtocolRequest(
                        "STARTVOTEFORME",
                        new LinkedList<String>(),
                        new HashMap<String, String>()
                )
        );
    }

    private static AvaNodeProtocolRequest genReject() {
        return new AvaNodeProtocolRequest(
                "REJECT",
                new LinkedList<String>(),
                new HashMap<String, String>()
        );
    }

    private static AvaNodeProtocolRequest genApprove() {
        return new AvaNodeProtocolRequest(
                "APPROVE",
                new LinkedList<String>(),
                new HashMap<String, String>()
        );
    }

    private static AvaNodeProtocolRequest genCampaign(int candId) {
        return new AvaNodeProtocolRequest(
                "CAMPAIGN",
                new LinkedList<String>() {{
                    add(String.valueOf(candId));
                }},
                new HashMap<>()
        );
    }

}
