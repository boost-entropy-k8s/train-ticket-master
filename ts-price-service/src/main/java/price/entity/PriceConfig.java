package price.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import price.tars.priceconfig.PriceConfigTars;

import java.util.UUID;

@Document(collection="price_config")
public class PriceConfig {

    @Id
    private UUID id;

    private String trainType;

    private String routeId;

    private double basicPriceRate;

    private double firstClassPriceRate;

    public PriceConfig() {
        //Empty Constructor
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

    public PriceConfigTars toTars(){
        PriceConfigTars priceConfigTars = new PriceConfigTars();
        priceConfigTars.setBasicPriceRate(this.basicPriceRate);
        priceConfigTars.setFirstClassPriceRate(this.firstClassPriceRate);
        priceConfigTars.setId(this.id.toString());
        priceConfigTars.setRouteId(this.routeId);
        priceConfigTars.setTrainType(this.trainType);
        return priceConfigTars;
    }

}
