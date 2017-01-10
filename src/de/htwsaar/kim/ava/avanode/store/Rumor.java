package de.htwsaar.kim.ava.avanode.store;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by markus on 28.12.16.
 */
public class Rumor {

    private final String rumor;
    private Set<Integer> sentTo = new HashSet<>();
    private Set<Integer> receivedFrom = new HashSet<>();


    public Rumor(String rumor, int source) {
        this.rumor = rumor;
        addReceivedFrom(source);
    }


    public String getRumor() {
        return rumor;
    }

    public void addSentTo(Integer recipient) {
        sentTo.add(recipient);
    }

    public void addReceivedFrom(Integer sender) {
        receivedFrom.add(sender);
    }


    public boolean inSentTo(Integer recipient) {
        return sentTo.contains(recipient);
    }

    public boolean inReceivedFrom(Integer sender) {
        return receivedFrom.contains(sender);
    }

    public boolean alreadyHeard() {
        return receivedFrom.size() > 1;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Rumor) obj).getRumor().equals(this.getRumor());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Rumor").append("[").append(rumor).append("]").append(" {");
        sb.append(" sentTo: ").append(sentTo).append(" receivedFrom: ").append(receivedFrom).append("}");

        return sb.toString();
    }
}
