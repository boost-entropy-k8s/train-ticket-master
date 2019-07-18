package adminorder.service;

import adminorder.entity.*;
import edu.fudan.common.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
@Slf4j
public class AdminOrderServiceImpl implements AdminOrderService {
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Response getAllOrders(HttpHeaders headers) {

        System.out.println("[Admin Order Service][Get All Orders]");
        //Get all of the orders
        ArrayList<Order> orders = new ArrayList<Order>();
        //From ts-order-service1
        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<Response<ArrayList<Order>>> re = restTemplate.exchange(
                "http://ts-order-service1:12031/api/v1/orderservice/order",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<Response<ArrayList<Order>>>() {
                });
        Response<ArrayList<Order>> result = re.getBody();

        if (result.getStatus() == 1) {
            System.out.println("[Admin Order Service][Get Orders From ts-order-service1 successfully!]");
            ArrayList<Order> orders1 = result.getData();
            orders.addAll(orders1);
        } else {
            System.out.println("[Admin Order Service][Get Orders From ts-order-service1 fail!]");
        }
        //From ts-order-other-service1
        HttpEntity requestEntity2 = new HttpEntity(headers);
        ResponseEntity<Response<ArrayList<Order>>> re2 = restTemplate.exchange(
                "http://ts-order-other-service1:12032/api/v1/orderOtherService/orderOther",
                HttpMethod.GET,
                requestEntity2,
                new ParameterizedTypeReference<Response<ArrayList<Order>>>() {
                });
        result = re2.getBody();

        if (result.getStatus() == 1) {
            System.out.println("[Admin Order Service][Get Orders From ts-order-other-service1 successfully!]");
            ArrayList<Order> orders1 = (ArrayList<Order>) result.getData();
            orders.addAll(orders1);
        } else {
            System.out.println("[Admin Order Service][Get Orders From ts-order-other-service1 fail!]");
        }
        //Return orders
        return new Response<>(1, "Get the orders successfully!", orders);

    }

    @Override
    public Response deleteOrder(String orderId, String trainNumber, HttpHeaders headers) {
        Response deleteOrderResult;
        if (trainNumber.startsWith("G") || trainNumber.startsWith("D")) {
            System.out.println("[Admin Order Service][Delete Order]");
            HttpEntity requestEntity = new HttpEntity(headers);
            ResponseEntity<Response> re = restTemplate.exchange(
                    "http://ts-order-service1:12031/api/v1/orderservice/order/" + orderId,
                    HttpMethod.DELETE,
                    requestEntity,
                    Response.class);
            deleteOrderResult = re.getBody();

        } else {
            System.out.println("[Admin Order Service][Delete Order Other]");
            HttpEntity requestEntity = new HttpEntity(headers);
            ResponseEntity<Response> re = restTemplate.exchange(
                    "http://ts-order-other-service1:12032/api/v1/orderOtherService/orderOther/" + orderId,
                    HttpMethod.DELETE,
                    requestEntity,
                    Response.class);
            deleteOrderResult = re.getBody();

        }
        return deleteOrderResult;

    }

    @Override
    public Response updateOrder(Order request, HttpHeaders headers) {

        Response updateOrderResult;
        log.info("UPDATE ORDER INFO : " + request.toString());
        if (request.getTrainNumber().startsWith("G") || request.getTrainNumber().startsWith("D")) {

            System.out.println("[Admin Order Service][Update Order]");
            HttpEntity requestEntity = new HttpEntity(request, headers);
            ResponseEntity<Response> re = restTemplate.exchange(
                    "http://ts-order-service1:12031/api/v1/orderservice/order/admin",
                    HttpMethod.PUT,
                    requestEntity,
                    Response.class);
            updateOrderResult = re.getBody();

        } else {
            System.out.println("[Admin Order Service][Add New Order Other]");
            HttpEntity requestEntity = new HttpEntity(request, headers);
            ResponseEntity<Response> re = restTemplate.exchange(
                    "http://ts-order-other-service1:12032/api/v1/orderOtherService/orderOther/admin",
                    HttpMethod.PUT,
                    requestEntity,
                    Response.class);
            updateOrderResult = re.getBody();

        }
        return updateOrderResult;
    }

    @Override
    public Response addOrder(Order request, HttpHeaders headers) {

        Response addOrderResult;
        if (request.getTrainNumber().startsWith("G") || request.getTrainNumber().startsWith("D")) {
            System.out.println("[Admin Order Service][Add New Order]");
            HttpEntity requestEntity = new HttpEntity(request, headers);
            ResponseEntity<Response> re = restTemplate.exchange(
                    "http://ts-order-service1:12031/api/v1/orderservice/order/admin",
                    HttpMethod.POST,
                    requestEntity,
                    Response.class);
            addOrderResult = re.getBody();

        } else {
            System.out.println("[Admin Order Service][Add New Order Other]");
            HttpEntity requestEntity = new HttpEntity(request, headers);
            ResponseEntity<Response> re = restTemplate.exchange(
                    "http://ts-order-other-service1:12032/api/v1/orderOtherService/orderOther/admin",
                    HttpMethod.POST,
                    requestEntity,
                    Response.class);
            addOrderResult = re.getBody();

        }
        return addOrderResult;

    }


}
