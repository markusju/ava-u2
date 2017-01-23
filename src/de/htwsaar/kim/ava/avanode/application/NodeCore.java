package de.htwsaar.kim.ava.avanode.application;

import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.avanode.file.FileConfig;
import de.htwsaar.kim.ava.avanode.logging.SingleLineFormatter;
import de.htwsaar.kim.ava.avanode.network.client.TCPClient;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.avanode.network.protocol.commands.CAMPAIGN;
import de.htwsaar.kim.ava.avanode.network.protocol.commands.STARTCAMPAIGN;
import de.htwsaar.kim.ava.avanode.network.protocol.commands.STARTVOTEFORME;
import de.htwsaar.kim.ava.avanode.network.server.TCPParallelServer;
import de.htwsaar.kim.ava.avanode.store.DataStore;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by markus on 25.12.16.
 */
public class NodeCore {


    private int nodeId;

    private TCPParallelServer tcpParallelServer;
    private TCPClient tcpClient;
    private FileConfig fileConfig;
    private DataStore dataStore;
    private Logger logger;
    private NodeType nodeType = NodeType.UNKNOWN;
    private int feedbackThreshold;

    public NodeCore(int nodeId, int feedbackThreshold) throws IOException {
        this.nodeId = nodeId;
        fileConfig = new FileConfig(this, this.nodeId, "file.txt", "file.dot");
        tcpParallelServer = new TCPParallelServer(this);
        tcpClient = new TCPClient(this);
        dataStore = new DataStore(this);
        this.feedbackThreshold = feedbackThreshold;

        logger = Logger.getLogger(String.valueOf(nodeId));
        Handler handler = new ConsoleHandler();
        handler.setFormatter(new SingleLineFormatter());
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }


    public FileConfig getFileConfig() {
        return fileConfig;
    }

    public TCPParallelServer getTcpParallelServer() {
        return tcpParallelServer;
    }

    public TCPClient getTcpClient() {
        return tcpClient;
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public void stopNode() {
        tcpParallelServer.stopServer();
    }

    public void startNode() {
        tcpParallelServer.start();
    }

    public int getNodeId() {
        return nodeId;
    }

    public Logger getLogger() {
        return logger;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public int getFeedbackThreshold() {
        return feedbackThreshold;
    }

    public void startCampaign() {
        try {
            TCPClient.sendSTARTCAMPAIGN(getTcpClient(),
                    getFileConfig().getOwnEntry().getHost(),
                    getFileConfig().getOwnEntry().getPort()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startVoteforme() {
        try {
            TCPClient.sendSTARTVOTEFORME(getTcpClient(),
                    getFileConfig().getOwnEntry().getHost(),
                    getFileConfig().getOwnEntry().getPort()
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startVoteformeD() {
        try {
            AvaNodeProtocol protDummy = new AvaNodeProtocol(this);
            (new STARTVOTEFORME()).execute(protDummy);
        } catch (CommandExecutionErrorException e) {
            e.printStackTrace();
        }
    }



}
