package de.htwsaar.kim.ava.avanode.network.client;

import de.htwsaar.kim.ava.avanode.network.protocol.AbstractBaseProtocol;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by markus on 27.12.16.
 */
public class AvaNodeClientProtocol extends AbstractBaseProtocol {

    public AvaNodeClientProtocol(Socket socket) throws IOException {
        super(socket);
    }


}
