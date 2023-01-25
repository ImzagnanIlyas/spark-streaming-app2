package org.example.entities;

import org.example.services.DataValidationService;

import java.util.Objects;

public class Trio {
    public String nodeId;
    public String productId;
    public DataValidationService.Sensor sensor;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trio trio = (Trio) o;
        return nodeId.equals(trio.nodeId) && productId.equals(trio.productId) && sensor == trio.sensor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, productId, sensor);
    }

    public Trio() {
    }

    public Trio(String nodeId, String productId, DataValidationService.Sensor sensor) {
        this.nodeId = nodeId;
        this.productId = productId;
        this.sensor = sensor;
    }

    @Override
    public String toString() {
        return "Trio{" +
                "nodeId='" + nodeId + '\'' +
                ", productId='" + productId + '\'' +
                ", sensor=" + sensor +
                '}';
    }
}
