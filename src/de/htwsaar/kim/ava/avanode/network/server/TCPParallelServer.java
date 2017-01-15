package de.htwsaar.kim.ava.avanode.network.server;

import de.htwsaar.kim.ava.avanode.application.NodeCore;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Created by markus on 05.06.15.
 */
public class TCPParallelServer extends Thread {

    public static final int MAX_NUM_THREADS = 20;
    public static final int DEFAULT_PORT = 4322;
    private static final int DEFAULT_TIMEOUT = 10000;
    private static final String THREAD_NAME = "ServerThread";

    private int port = DEFAULT_PORT;
    private int timeout = DEFAULT_TIMEOUT;

    private NodeCore nodeCore;
    private ServerSocket socket;
    private ServerStatus serverStatus = ServerStatus.STOPPED;
    private List<ServerStatusObserver> serverStatusObservers = new LinkedList<>();

    //All Workers are pooled to avoid resource exhaustion
    private /*static*/ ExecutorService workerPool = Executors.newFixedThreadPool(MAX_NUM_THREADS);
    protected List<TCPParallelWorker> workerList = new LinkedList<>();


    public TCPParallelServer(NodeCore nodeCore) {
        this.nodeCore = nodeCore;
        this.port = this.nodeCore.getFileConfig().getPort();
        setName(THREAD_NAME+"-"+ Integer.toString(port));
    }

    /**
     * Start the parallel Server.
     */
    public void run() {
        try
        {

            // Erzeugen der Socket/binden an Port/Wartestellung
            socket = new ServerSocket(port);
            nodeCore.getLogger().log(Level.INFO, String.format("Warten auf Verbindungen (IP: %s, Port: %s)",
                    InetAddress.getLocalHost().getHostAddress(),
                    String.valueOf(port))
            );

            changeServerStatus(ServerStatus.RUNNING);

            while (!isInterrupted())
            {
                try {
                    Socket client = socket.accept();
                    //Connection Timeout
                    client.setSoTimeout(timeout);
                    nodeCore.getLogger().log(Level.FINE, "Neuer Client verbunden: " + client.getInetAddress().toString());
                    TCPParallelWorker worker = new TCPParallelWorker(client, nodeCore);
                    workerList.add(worker);
                    workerPool.execute(worker);
                } catch (IOException e) {
                        e.printStackTrace();
                }

            }

        }
        catch (Exception e)
        {
            nodeCore.getLogger().log(Level.SEVERE, "Server crashed.", e);
            changeServerStatus(ServerStatus.ERROR);
        }

        changeServerStatus(ServerStatus.STOPPED);
    }

    public boolean isRunning() {
        return !isInterrupted();
    }

    public void stopServer() {
        interrupt();
        //workerPool.shutdown();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
        }

        for(TCPParallelWorker worker : workerList) {
            worker.stopWorker();
        }


    }


    /**
     * Gets the port the server is
     * listening on.
     *
     * @return
     */
    public int getPort() {
        return this.port;
    }

    public void attachServerStatusObserver(ServerStatusObserver o) {
        serverStatusObservers.add(o);
    }

    public void detachServerStatusObserver(ServerStatusObserver o) {
        serverStatusObservers.remove(o);
    }

    private void changeServerStatus(ServerStatus newStatus) {
        this.serverStatus = newStatus;
        for (ServerStatusObserver observer : serverStatusObservers) {
            observer.serverStatusChanged(newStatus);
        }
    }


    public ServerStatus getServerStatus() {
        return serverStatus;
    }
}
