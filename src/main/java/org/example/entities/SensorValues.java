package org.example.entities;


import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;

import java.io.Serializable;

public class SensorValues implements Serializable {

    private double tempSoil;
    private double tempAir;
    private double humidity;
    private double moisture;
    private double ph;
    private NpkPayload npk;

    public SensorValues() {}

    public double getTempSoil() {
        return tempSoil;
    }

    public void setTempSoil(double tempSoil) {
        this.tempSoil = tempSoil;
    }

    public double getTempAir() {
        return tempAir;
    }

    public void setTempAir(double tempAir) {
        this.tempAir = tempAir;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getMoisture() {
        return moisture;
    }

    public void setMoisture(double moisture) {
        this.moisture = moisture;
    }

    public double getPh() {
        return ph;
    }

    public void setPh(double ph) {
        this.ph = ph;
    }

    public NpkPayload getNpk() {
        return npk;
    }

    public void setNpk(NpkPayload npk) {
        this.npk = npk;
    }

    @Override
    public String toString() {
        return "SensorValues{" +
                "tempSoil=" + tempSoil +
                ", tempAir=" + tempAir +
                ", humidity=" + humidity +
                ", moisture=" + moisture +
                ", ph=" + ph +
                ", npk=" + npk +
                '}';
    }
}
