package de.htwsaar.kim.ava.avanode.dot;

/**
 * Created by markus on 26.12.16.
 */
public class Edge {


    private int a;
    private int b;

    public Edge(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Edge) {
            boolean cond1 = (this.a == ((Edge) obj).getA()) && (this.b == ((Edge) obj).getB());
            boolean cond2 = (this.a == ((Edge) obj).getB()) && (this.b == ((Edge) obj).getA());
            return cond1 || cond2;
        }

        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(a)+" -- "+Integer.toString(b);
    }
}
