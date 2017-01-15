package de.htwsaar.kim.ava.avanode.network.server;

import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.network.protocol.AvaNodeProtocol;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

/**
 * Created by markus on 07.06.15.
 */
public class TCPParallelWorker implements Runnable {


    private static final String THREAD_NAME = "WorkerThread";
    private Socket socket;
    private NodeCore nodeCore;

    public TCPParallelWorker(Socket socket, NodeCore nodeCore) {
        //setName(THREAD_NAME);
        this.socket = socket;
        this.nodeCore = nodeCore;
    }


    public void stopWorker() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run - method of the Worker
     */
    public void run() {
        nodeCore.getLogger().log(Level.FINE, "Worker spawned. Client connected: "+socket.getInetAddress().toString());
        try {

            new AvaNodeProtocol(socket, nodeCore).run();
            socket.close();

        } catch (Exception e) {
                nodeCore.getLogger().log(Level.SEVERE, "Worker could not process the request!", e);
        }

        nodeCore.getLogger().log(Level.FINE, "Worker finished.");

    }






}
