package fdse.microservice.entity;

import fdse.microservice.tars.rpc.priceconfig.PriceConfigTars;

import java.util.UUID;

public class PriceConfig {

    private UUID id;

    private String trainType;

    private String routeId;

    private double basicPriceRate;

    private double firstClassPriceRate;

    public PriceConfig() {
        //Empty Constructor
    }

    public PriceConfig(PriceConfigTars priceConfigTars) {
        this.id = UUID.fromString(priceConfigTars.getId());
        this.trainType = priceConfigTars.getTrainType();
        this.routeId = priceConfigTars.getRouteId();
        this.basicPriceRate = priceConfigTars.getBasicPriceRate();
        this.firstClassPriceRate = priceConfigTars.getFirstClassPriceRate();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public double getBasicPriceRate() {
        return basicPriceRate;
    }

    public void setBasicPriceRate(double basicPriceRate) {
        this.basicPriceRate = basicPriceRate;
    }

    public double getFirstClassPriceRate() {
        return firstClassPriceRate;
    }

    public void setFirstClassPriceRate(double firstClassPriceRate) {
        this.firstClassPriceRate = firstClassPriceRate;
    }
}
