package de.htwsaar.kim.ava.avanode.tests;

import de.htwsaar.kim.ava.avanode.exception.CommandExecutionErrorException;

/**
 * Created by markus on 28.01.17.
 */
public class Pair implements Comparable {
    private Integer a;
    private Integer b;

    public Pair(Integer first, Integer second) {
        a = first;
        b = second;
    }

    public Integer getFirst() {
        return a;
    }

    public Integer getSecond() {
        return b;
    }


    public void setFirst(int first) {
        a = first;
    }

    public void setSecond(int second) {
        b = second;
    }


    public void decrementFirst() {
        a--;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Pair) {
            return getFirst().compareTo(((Pair) o).getFirst());
        } else {
            throw new RuntimeException("Nope");
        }
    }
}
