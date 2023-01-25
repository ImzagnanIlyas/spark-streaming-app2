package org.example.entities;


import java.io.Serializable;

public class NpkPayload implements Serializable {
    private double n;
    private double p;
    private double k;

    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    @Override
    public String toString() {
        return "NpkPayload{" +
                "n=" + n +
                ", p=" + p +
                ", k=" + k +
                '}';
    }
}
