package org.example.entities;


import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;

import java.io.Serializable;


public class NodePayload implements Serializable {
    private SensorValues values;
    private String timestamp;
    private int nodeId;
    private int productId;

    @Override
    public String toString() {
        return "NodePayload{" +
                "values=" + values +
                ", timestamp='" + timestamp + '\'' +
                ", nodeId=" + nodeId +
                '}';
    }

    public SensorValues getValues() {
        return values;
    }

    public void setValues(SensorValues values) {
        this.values = values;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
