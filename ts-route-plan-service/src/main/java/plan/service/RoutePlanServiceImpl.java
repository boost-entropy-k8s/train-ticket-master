package plan.service;

import edu.fudan.common.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import plan.entity.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoutePlanServiceImpl implements RoutePlanService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Response searchCheapestResult(RoutePlanInfo info, HttpHeaders headers) {

        //1.暴力取出travel-service和travle2-service的所有结果
        TripInfo queryInfo = new TripInfo();
        queryInfo.setStartingPlace(info.getFormStationName());
        queryInfo.setEndPlace(info.getToStationName());
        queryInfo.setDepartureTime(info.getTravelDate());

        ArrayList<TripResponse> highSpeed = getTripFromHighSpeedTravelServive(queryInfo, headers);
        ArrayList<TripResponse> normalTrain = getTripFromNormalTrainTravelService(queryInfo, headers);

        //2.按照二等座位结果排序
        ArrayList<TripResponse> finalResult = new ArrayList<>();
        finalResult.addAll(highSpeed);
        finalResult.addAll(normalTrain);

        float minPrice;
        int minIndex = -1;
        int size = Math.min(5, finalResult.size());
        ArrayList<TripResponse> returnResult = new ArrayList<>();
        for (int i = 0; i < size; i++) {

            minPrice = Float.MAX_VALUE;
            for (int j = 0; j < finalResult.size(); j++) {
                TripResponse thisRes = finalResult.get(j);
                if (Float.parseFloat(thisRes.getPriceForEconomyClass()) < minPrice) {
                    minPrice = Float.parseFloat(finalResult.get(j).getPriceForEconomyClass());
                    minIndex = j;
                }
            }
            returnResult.add(finalResult.get(minIndex));
            finalResult.remove(minIndex);
        }


        ArrayList<RoutePlanResultUnit> units = new ArrayList<>();
        for (int i = 0; i < returnResult.size(); i++) {
            TripResponse tempResponse = returnResult.get(i);

            RoutePlanResultUnit tempUnit = new RoutePlanResultUnit();
            tempUnit.setTripId(tempResponse.getTripId().toString());
            tempUnit.setTrainTypeId(tempResponse.getTrainTypeId());
            tempUnit.setFromStationName(tempResponse.getStartingStation());
            tempUnit.setToStationName(tempResponse.getTerminalStation());
            tempUnit.setStopStations(getStationList(tempResponse.getTripId().toString(), headers));
            tempUnit.setPriceForSecondClassSeat(tempResponse.getPriceForEconomyClass());
            tempUnit.setPriceForFirstClassSeat(tempResponse.getPriceForConfortClass());
            tempUnit.setEndTime(tempResponse.getEndTime());
            tempUnit.setStartingTime(tempResponse.getStartingTime());

            units.add(tempUnit);
        }

        return new Response<>(1, "Success", units);
    }

    @Override
    public Response searchQuickestResult(RoutePlanInfo info, HttpHeaders headers) {

        //1.暴力取出travel-service和travel2-service的所有结果
        TripInfo queryInfo = new TripInfo();
        queryInfo.setStartingPlace(info.getFormStationName());
        queryInfo.setEndPlace(info.getToStationName());
        queryInfo.setDepartureTime(info.getTravelDate());

        ArrayList<TripResponse> highSpeed = getTripFromHighSpeedTravelServive(queryInfo, headers);
        ArrayList<TripResponse> normalTrain = getTripFromNormalTrainTravelService(queryInfo, headers);

        //2.按照时间排序
        ArrayList<TripResponse> finalResult = new ArrayList<>();

        for (TripResponse tr : highSpeed) {
            finalResult.add(tr);
        }
        for (TripResponse tr : normalTrain) {
            finalResult.add(tr);
        }

        long minTime;
        int minIndex = -1;
        int size = Math.min(finalResult.size(), 5);
        ArrayList<TripResponse> returnResult = new ArrayList<>();
        for (int i = 0; i < size; i++) {

            minTime = Long.MAX_VALUE;
            for (int j = 0; j < finalResult.size(); j++) {
                TripResponse thisRes = finalResult.get(j);
                if (thisRes.getEndTime().getTime() - thisRes.getStartingTime().getTime() < minTime) {
                    minTime = thisRes.getEndTime().getTime() - thisRes.getStartingTime().getTime();
                    minIndex = j;
                }
            }
            returnResult.add(finalResult.get(minIndex));
            finalResult.remove(minIndex);

        }


        ArrayList<RoutePlanResultUnit> units = new ArrayList<>();
        for (int i = 0; i < returnResult.size(); i++) {
            TripResponse tempResponse = returnResult.get(i);

            RoutePlanResultUnit tempUnit = new RoutePlanResultUnit();
            tempUnit.setTripId(tempResponse.getTripId().toString());
            tempUnit.setTrainTypeId(tempResponse.getTrainTypeId());
            tempUnit.setFromStationName(tempResponse.getStartingStation());
            tempUnit.setToStationName(tempResponse.getTerminalStation());

            tempUnit.setStopStations(getStationList(tempResponse.getTripId().toString(), headers));

            tempUnit.setPriceForSecondClassSeat(tempResponse.getPriceForEconomyClass());
            tempUnit.setPriceForFirstClassSeat(tempResponse.getPriceForConfortClass());
            tempUnit.setStartingTime(tempResponse.getStartingTime());
            tempUnit.setEndTime(tempResponse.getEndTime());
            units.add(tempUnit);
        }
        return new Response<>(1, "Success", units);
    }

    @Override
    public Response searchMinStopStations(RoutePlanInfo info, HttpHeaders headers) {
        String fromStationId = queryForStationId(info.getFormStationName(), headers);
        String toStationId = queryForStationId(info.getToStationName(), headers);
        System.out.println("From Id:" + fromStationId + " To:" + toStationId);
        //1.获取这个经过这两个车站的路线

        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<Response<ArrayList<Route>>> re = restTemplate.exchange(
                "http://ts-route-service1:11178/api/v1/routeservice/routes/" + fromStationId + "/" + toStationId,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<Response<ArrayList<Route>>>() {
                });


        ArrayList<Route> routeList = re.getBody().getData();
        System.out.println("[Route Plan Service] Candidate Route Number:" + routeList.size());
        //2.计算这两个车站之间有多少停靠站
        ArrayList<Integer> gapList = new ArrayList<>();
        for (int i = 0; i < routeList.size(); i++) {
            int indexStart = routeList.get(i).getStations().indexOf(fromStationId);
            int indexEnd = routeList.get(i).getStations().indexOf(toStationId);
            gapList.add(indexEnd - indexStart);
        }
        //3.挑选出最少停靠站的几条路线
        ArrayList<String> resultRoutes = new ArrayList<>();
        int size = Math.min(5, routeList.size());
        for (int i = 0; i < size; i++) {
            int minIndex = 0;
            int tempMinGap = Integer.MAX_VALUE;
            for (int j = 0; j < gapList.size(); j++) {
                if (gapList.get(j) < tempMinGap) {
                    tempMinGap = gapList.get(j);
                    minIndex = j;
                }
            }
            resultRoutes.add(routeList.get(minIndex).getId());
            routeList.remove(minIndex);
            gapList.remove(minIndex);
        }
        //4.根据路线，去travel或者travel2获取这些车次信息
        requestEntity = new HttpEntity(resultRoutes, headers);
        ResponseEntity<Response<ArrayList<ArrayList<Trip>>>> re2 = restTemplate.exchange(
                "http://ts-travel-service1:12346/api/v1/travelservice/trips/routes",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Response<ArrayList<ArrayList<Trip>>>>() {
                });

        ArrayList<ArrayList<Trip>> travelTrips = re2.getBody().getData();


        re2 = restTemplate.exchange(
                "http://ts-travel2-service1:16346/api/v1/travel2service/trips/routes",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Response<ArrayList<ArrayList<Trip>>>>() {
                });
        ArrayList<ArrayList<Trip>> travel2Trips = re2.getBody().getData();

        //合并查询结果
        ArrayList<ArrayList<Trip>> finalTripResult = new ArrayList<>();
        for (int i = 0; i < travel2Trips.size(); i++) {
            ArrayList<Trip> tempList = travel2Trips.get(i);
            tempList.addAll(travelTrips.get(i));
            finalTripResult.add(tempList);
        }
        System.out.println("[Route Plan Service] Trips Num:" + finalTripResult.size());
        //5.再根据这些车次信息获取其价格和停靠站信息
        ArrayList<Trip> trips = new ArrayList<>();
        for (ArrayList<Trip> tempTrips : finalTripResult) {
            trips.addAll(tempTrips);
        }
        ArrayList<RoutePlanResultUnit> tripResponses = new ArrayList<>();

        ResponseEntity<Response<TripAllDetail>> re3;
        for (Trip trip : trips) {
            TripResponse tripResponse;
            TripAllDetailInfo allDetailInfo = new TripAllDetailInfo();
            allDetailInfo.setTripId(trip.getTripId().toString());
            allDetailInfo.setTravelDate(info.getTravelDate());
            allDetailInfo.setFrom(info.getFormStationName());
            allDetailInfo.setTo(info.getToStationName());
            requestEntity = new HttpEntity(allDetailInfo, headers);
            String requestUrl = "";
            if (trip.getTripId().toString().charAt(0) == 'D' || trip.getTripId().toString().charAt(0) == 'G') {
                requestUrl = "http://ts-travel-service1:12346/api/v1/travelservice/trip_detail";
            } else {
                requestUrl = "http://ts-travel2-service1:16346/api/v1/travel2service/trip_detail";
            }
            re3 = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Response<TripAllDetail>>() {
                    });

            TripAllDetail tripAllDetail = re3.getBody().getData();
            tripResponse = tripAllDetail.getTripResponse();


            RoutePlanResultUnit unit = new RoutePlanResultUnit();
            unit.setTripId(trip.getTripId().toString());
            unit.setTrainTypeId(tripResponse.getTrainTypeId());
            unit.setFromStationName(tripResponse.getStartingStation());
            unit.setToStationName(tripResponse.getTerminalStation());
            unit.setStartingTime(tripResponse.getStartingTime());
            unit.setEndTime(tripResponse.getEndTime());
            unit.setPriceForFirstClassSeat(tripResponse.getPriceForConfortClass());
            unit.setPriceForSecondClassSeat(tripResponse.getPriceForEconomyClass());
            //根据routeid去拿路线图
            String routeId = trip.getRouteId();
            Route tripRoute = getRouteByRouteId(routeId, headers);
            unit.setStopStations(tripRoute.getStations());

            tripResponses.add(unit);
        }
        System.out.println("[Route Plan Service] Trips Response Unit Num:" + tripResponses.size());
        return new Response<>(1, "Success.", tripResponses);
    }

    private String queryForStationId(String stationName, HttpHeaders headers) {
        System.out.println("[Preserve Service][Get Station Name]");

        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<Response<String>> re = restTemplate.exchange(
                "http://ts-station-service1:12345/api/v1/stationservice/stations/id/" + stationName,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<Response<String>>() {
                });
        return re.getBody().getData();
    }

    private Route getRouteByRouteId(String routeId, HttpHeaders headers) {
        System.out.println("[Route Plan Service][Get Route By Id] Route ID：" + routeId);
        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<Response<Route>> re = restTemplate.exchange(
                "http://ts-route-service1:11178/api/v1/routeservice/routes/" + routeId,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<Response<Route>>() {
                });
        Response<Route> result = re.getBody();

        if (result.getStatus() == 0) {
            System.out.println("[Travel Service][Get Route By Id] Fail." + result.getMsg());
            return null;
        } else {
            System.out.println("[Travel Service][Get Route By Id] Success.");
            return result.getData();
        }
    }

    private ArrayList<TripResponse> getTripFromHighSpeedTravelServive(TripInfo info, HttpHeaders headers) {
        HttpEntity requestEntity = new HttpEntity(info, headers);

        ResponseEntity<Response<ArrayList<TripResponse>>> re = restTemplate.exchange(
                "http://ts-travel-service1:12346/api/v1/travelservice/trips/left",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Response<ArrayList<TripResponse>>>() {
                });

        ArrayList<TripResponse> tripResponses = re.getBody().getData();
        System.out.println("[Route Plan Get Trip][Size]" + tripResponses.size());
        return tripResponses;
    }

    private ArrayList<TripResponse> getTripFromNormalTrainTravelService(TripInfo info, HttpHeaders headers) {
        HttpEntity requestEntity = new HttpEntity(info, headers);

        ResponseEntity<Response<ArrayList<TripResponse>>> re = restTemplate.exchange(
                "http://ts-travel2-service1:16346/api/v1/travel2service/trips/left",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Response<ArrayList<TripResponse>>>() {
                });
        ArrayList<TripResponse> list = re.getBody().getData();
        System.out.println("[Route Plan Get TripOther][Size]" + list.size());
        return list;
    }

    private List<String> getStationList(String tripId, HttpHeaders headers) {

        String path;
        if (tripId.charAt(0) == 'G' || tripId.charAt(0) == 'D') {
            path = "http://ts-travel-service1:12346/api/v1/travelservice/routes/" + tripId;
        } else {
            path = "http://ts-travel2-service1:16346/api/v1/travel2service/routes/" + tripId;
        }
        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<Response<Route>> re = restTemplate.exchange(
                path,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<Response<Route>>() {
                });
        Route route = re.getBody().getData();
        return route.getStations();
    }
}
