package de.htwsaar.kim.ava.avanode.dot;

/**
 * Created by markus on 26.12.16.
 */
public class Edge implements Comparable {


    private int a;
    private int b;

    public Edge(int a, int b) {
        if (a == b) throw new IllegalArgumentException("Not allowed. A and B are equal");
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

    @Override
    public int hashCode()
    {
        int hash = 17;
        hash = hash * 31 + a;
        hash = hash * 31 + b;
        return hash;
    }


    @Override
    public int compareTo(Object o) {
        if (o instanceof Edge) {
            if (o.equals(this)) return 0;
            if (((Edge) o).getA() < this.getA()) return 1;
            if (((Edge) o).getA() > this.getA()) return -1;
            if (((Edge) o).getA() == this.getA()) {
                if (((Edge) o).getB() < this.getB()) return 1;
                if (((Edge) o).getB() < this.getB()) return -1;
            }
        }
        throw new RuntimeException("Not good");
    }
}
