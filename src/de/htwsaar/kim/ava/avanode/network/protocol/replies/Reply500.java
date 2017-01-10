package de.htwsaar.kim.ava.avanode.network.protocol.replies;

import java.util.Map;

/**
 * Created by markus on 26.12.16.
 */
public class Reply500 extends Reply {

    public Reply500(Map<String, String> parameters) {
        super(500, "SERVER ERROR", parameters);
    }
}
