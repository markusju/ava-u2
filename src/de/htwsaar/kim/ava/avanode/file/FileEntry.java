package de.htwsaar.kim.ava.avanode.file;

/**
 * Created by markus on 25.12.16.
 */
public class FileEntry {

    private int id;
    private String host;
    private int port;

    private int vectorTime = 0;
    private int vectorTimeLimit = -1;

    public FileEntry(int id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public int getVectorTime() {
        return vectorTime;
    }

    public void incVectorTime() {
        vectorTime++;
    }

    public synchronized void updateVectorTime(int vectorTime) {
        this.vectorTime = vectorTime;
    }



    public int getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


    @Override
    public String toString() {
        return "("+id+" "+host+" "+port+")";
    }
}
