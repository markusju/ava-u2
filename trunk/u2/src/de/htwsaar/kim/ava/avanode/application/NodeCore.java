package de.htwsaar.kim.ava.avanode.application;

import de.htwsaar.kim.ava.avanode.file.FileConfig;
import de.htwsaar.kim.ava.avanode.logging.SingleLineFormatter;
import de.htwsaar.kim.ava.avanode.network.client.TCPClient;
import de.htwsaar.kim.ava.avanode.network.server.TCPParallelServer;
import de.htwsaar.kim.ava.avanode.store.CampaignTeam;
import de.htwsaar.kim.ava.avanode.store.DataStore;

import java.io.IOException;
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

    public NodeCore(int nodeId, int feedbackThreshold) throws IOException {
        this.nodeId = nodeId;
        fileConfig = new FileConfig(this.nodeId, "file.txt", "file.dot");
        tcpParallelServer = new TCPParallelServer(this);
        tcpClient = new TCPClient(this);
        dataStore = new DataStore(feedbackThreshold);

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
}
