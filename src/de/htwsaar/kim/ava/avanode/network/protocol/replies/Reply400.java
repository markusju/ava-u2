package de.htwsaar.kim.ava.avanode.network.protocol.replies;

import java.util.Map;

/**
 * Created by markus on 26.12.16.
 */
public class Reply400 extends Reply {
    public Reply400(Map<String, String> parameters) {
        super(400, "BAD REQUEST", parameters);
    }
}
