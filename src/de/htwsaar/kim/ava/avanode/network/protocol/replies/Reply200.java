package de.htwsaar.kim.ava.avanode.network.protocol.replies;

import java.util.Map;

/**
 * Created by markus on 26.12.16.
 */
public class Reply200 extends Reply {
    public Reply200(Map<String, String> parameters) {
        super(200, "OKAY", parameters);
    }
}
