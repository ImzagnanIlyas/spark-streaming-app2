package org.example.entities;

public class AggregationData {
    private double tempSoilAvg;
    private double tempAirAvg;
    private double humidityAvg;
    private double moistureAvg;
    private double phAvg;
    private double nAvg;
    private double pAvg;
    private double kAvg;

    public AggregationData(){}

    public void sumNodeValues(SensorValues values){
        tempSoilAvg += values.getTempSoil();
        tempAirAvg += values.getTempAir();
        humidityAvg += values.getHumidity();
        moistureAvg += values.getMoisture();
        phAvg += values.getPh();
        nAvg += values.getNpk().getN();
        pAvg += values.getNpk().getP();
        kAvg += values.getNpk().getK();
    }

    public void divideByNodesNumber(long n){
        tempSoilAvg /= n ;
        tempAirAvg /= n ;
        humidityAvg /= n ;
        moistureAvg /= n ;
        phAvg /= n ;
        nAvg /= n ;
        pAvg /= n ;
        kAvg /= n ;
    }

    public double getTempSoilAvg() {
        return tempSoilAvg;
    }

    public void setTempSoilAvg(double tempSoilAvg) {
        this.tempSoilAvg = tempSoilAvg;
    }

    public double getTempAirAvg() {
        return tempAirAvg;
    }

    public void setTempAirAvg(double tempAirAvg) {
        this.tempAirAvg = tempAirAvg;
    }

    public double getHumidityAvg() {
        return humidityAvg;
    }

    public void setHumidityAvg(double humidityAvg) {
        this.humidityAvg = humidityAvg;
    }

    public double getMoistureAvg() {
        return moistureAvg;
    }

    public void setMoistureAvg(double moistureAvg) {
        this.moistureAvg = moistureAvg;
    }

    public double getPhAvg() {
        return phAvg;
    }

    public void setPhAvg(double phAvg) {
        this.phAvg = phAvg;
    }

    public double getnAvg() {
        return nAvg;
    }

    public void setnAvg(double nAvg) {
        this.nAvg = nAvg;
    }

    public double getpAvg() {
        return pAvg;
    }

    public void setpAvg(double pAvg) {
        this.pAvg = pAvg;
    }

    public double getkAvg() {
        return kAvg;
    }

    public void setkAvg(double kAvg) {
        this.kAvg = kAvg;
    }
}
