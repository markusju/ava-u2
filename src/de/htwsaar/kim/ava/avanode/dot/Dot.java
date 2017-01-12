package de.htwsaar.kim.ava.avanode.dot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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


    public int getNumOfAdjacentNodes(int node) {
        return getAdjacentNodes(node).size();
    }

    public Set<Integer> getAdjacentNodes(int node) {
        Set<Integer> adjacent = new HashSet<>();
        for (Edge edge : edges) {
            if (edge.getA() == node)
                adjacent.add(edge.getB());
            if (edge.getB() == node)
                adjacent.add(edge.getA());
        }
        return adjacent;
    }

    public void dumpNodeDeg() {
        for (Integer node : getNodes()) {
            System.out.println(node + ": " +getNumOfAdjacentNodes(node));
        }
    }

    public Set<Integer> getNodes() {
        Set<Integer> set = new HashSet<>();
        for (Edge edge : edges) {
                set.add(edge.getA());
                set.add(edge.getB());
        }
        return set;
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
