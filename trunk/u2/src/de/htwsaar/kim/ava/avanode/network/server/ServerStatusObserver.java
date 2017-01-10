package de.htwsaar.kim.ava.avanode.network.server;

import de.htwsaar.kim.ava.avanode.network.server.ServerStatus;

/**
 * Created by Felix on 30.09.2015.
 */
public interface ServerStatusObserver {

    void serverStatusChanged(ServerStatus newStatus);

}
