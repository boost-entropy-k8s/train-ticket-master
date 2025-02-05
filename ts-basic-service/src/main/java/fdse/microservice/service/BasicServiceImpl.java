package fdse.microservice.service;

import edu.fudan.common.util.JsonUtils;
import edu.fudan.common.util.Response;
import fdse.microservice.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
@Slf4j
public class BasicServiceImpl implements BasicService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Response queryForTravel(Travel info, HttpHeaders headers) {

        Response response = new Response<>();
        TravelResult result = new TravelResult();
        result.setStatus(true);
        response.setStatus(1);
        response.setMsg("Success");
        boolean startingPlaceExist = checkStationExists(info.getStartingPlace(), headers);
        boolean endPlaceExist = checkStationExists(info.getEndPlace(), headers);
        if (!startingPlaceExist || !endPlaceExist) {
            result.setStatus(false);
            response.setStatus(0);
            response.setMsg("Start place or end place not exist!");
        }

        TrainType trainType = queryTrainType(info.getTrip().getTrainTypeId(), headers);
        if (trainType == null) {
            System.out.println("traintype doesn't exist");
            result.setStatus(false);
            response.setStatus(0);
            response.setMsg("Train type doesn't exist");
        } else {
            result.setTrainType(trainType);
        }

        String routeId = info.getTrip().getRouteId();
        String trainTypeString = trainType.getId();
        Route route = getRouteByRouteId(routeId, headers);
        PriceConfig priceConfig = queryPriceConfigByRouteIdAndTrainType(routeId, trainTypeString, headers);

        String startingPlaceId = (String) queryForStationId(info.getStartingPlace(), headers).getData();
        String endPlaceId = (String) queryForStationId(info.getEndPlace(), headers).getData();

        log.info("startingPlaceId : " + startingPlaceId + "endPlaceId : " + endPlaceId);

        int indexStart = route.getStations().indexOf(startingPlaceId);
        int indexEnd = route.getStations().indexOf(endPlaceId);

        log.info("indexStart : " + indexStart + " __ " + "indexEnd : " + indexEnd);
        log.info("route.getDistances().size : " + route.getDistances().size());
        HashMap<String, String> prices = new HashMap<String, String>();
        try {
            int distance = route.getDistances().get(indexEnd) - route.getDistances().get(indexStart);

            double priceForEconomyClass = distance * priceConfig.getBasicPriceRate();//需要price Rate和距离（起始站）
            double priceForConfortClass = distance * priceConfig.getFirstClassPriceRate();
            prices.put("economyClass", "" + priceForEconomyClass);
            prices.put("confortClass", "" + priceForConfortClass);
        }catch (Exception e){
            prices.put("economyClass", "95.0");
            prices.put("confortClass", "120.0");
        }
        result.setPrices(prices);
        result.setPercent(1.0);
        response.setData(result);
        return response;
    }


    @Override
    public Response queryForStationId(String stationName, HttpHeaders headers) {
        System.out.println("[Basic Information Service][Query For Station Id] Station Id:" + stationName);
        HttpEntity requestEntity = new HttpEntity( headers);
        ResponseEntity<Response> re = restTemplate.exchange(
                "http://ts-station-service1:12345/api/v1/stationservice/stations/id/" + stationName,
                HttpMethod.GET,
                requestEntity,
                Response.class);
        Response id = re.getBody();
        return  id;
    }

    public boolean checkStationExists(String stationName, HttpHeaders headers) {
        System.out.println("[Basic Information Service][Check Station Exists] Station Name:" + stationName);
        HttpEntity requestEntity = new HttpEntity( headers);
        ResponseEntity<Response> re = restTemplate.exchange(
                "http://ts-station-service1:12345/api/v1/stationservice/stations/id/" + stationName,
                HttpMethod.GET,
                requestEntity,
                Response.class);
        Response exist = re.getBody();
       if (exist.getStatus() ==1)
            return true;
        return false;
    }

    public TrainType queryTrainType(String trainTypeId, HttpHeaders headers) {
        System.out.println("[Basic Information Service][Query Train Type] Train Type:" + trainTypeId);
        HttpEntity requestEntity = new HttpEntity( headers);
        ResponseEntity<Response> re = restTemplate.exchange(
                "http://ts-train-service1:14567/api/v1/trainservice/trains/" + trainTypeId,
                HttpMethod.GET,
                requestEntity,
                Response.class);
        Response  response = re.getBody();

        return JsonUtils.conveterObject(response.getData(), TrainType.class);
    }

    private Route getRouteByRouteId(String routeId, HttpHeaders headers) {
        System.out.println("[Basic Information Service][Get Route By Id] Route ID：" + routeId);
        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<Response> re = restTemplate.exchange(
                "http://ts-route-service1:11178/api/v1/routeservice/routes/" + routeId,
                HttpMethod.GET,
                requestEntity,
                Response.class);
        Response result = re.getBody();
        if ( result.getStatus() == 0) {
            System.out.println("[Basic Information Service][Get Route By Id] Fail." + result.getMsg());
            return null;
        } else {
            System.out.println("[Basic Information Service][Get Route By Id] Success.");
            return JsonUtils.conveterObject(result.getData(), Route.class);
        }
    }

    private PriceConfig queryPriceConfigByRouteIdAndTrainType(String routeId, String trainType, HttpHeaders headers) {
        System.out.println("[Basic Information Service][Query For Price Config] RouteId:"
                + routeId + "TrainType:" + trainType);

        HttpEntity requestEntity = new HttpEntity(null, headers);
        ResponseEntity<Response> re = restTemplate.exchange(
                "http://ts-price-service1:16579/api/v1/priceservice/prices/" + routeId + "/" + trainType,
                HttpMethod.GET,
                requestEntity,
                Response.class);
        Response result = re.getBody();

        System.out.println("Response Resutl to String " + result.toString());
        return  JsonUtils.conveterObject(result.getData(), PriceConfig.class);
    }

}
