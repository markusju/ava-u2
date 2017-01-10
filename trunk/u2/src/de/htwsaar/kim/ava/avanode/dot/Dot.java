package de.htwsaar.kim.ava.avanode.dot;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by markus on 26.12.16.
 */
public class Dot {

    private Set<Edge> edges = new HashSet<>();

    public Dot() {

    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("graph G {"+"\n");

        for (Edge edge: edges) {
            sb.append(edge).append(";\n");
        }
        sb.append("}"+"\n");

        return sb.toString();
    }
}
