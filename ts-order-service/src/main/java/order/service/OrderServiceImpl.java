package order.service;

import edu.fudan.common.util.Response;
import lombok.extern.slf4j.Slf4j;
import order.entity.*;
import order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Response getSoldTickets(Seat seatRequest, HttpHeaders headers) {
        ArrayList<Order> list = orderRepository.findByTravelDateAndTrainNumber(seatRequest.getTravelDate(),
                seatRequest.getTrainNumber());
        if (list != null && list.size() > 0) {
            Set ticketSet = new HashSet();
            for (Order tempOrder : list) {
                ticketSet.add(new Ticket(Integer.parseInt(tempOrder.getSeatNumber()),
                        tempOrder.getFrom(), tempOrder.getTo()));
            }
            LeftTicketInfo leftTicketInfo = new LeftTicketInfo();
            leftTicketInfo.setSoldTickets(ticketSet);
            System.out.println("Left ticket info is: " + leftTicketInfo.toString());
            return new Response<>(1, "Success", leftTicketInfo);
        } else {
            System.out.println("Left ticket info is empty");
            return new Response<>(0, "Order is Null.", null);
        }
    }

    @Override
    public Response findOrderById(UUID id, HttpHeaders headers) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            return new Response<>(0, "No Content by this id", id);
        } else {
            return new Response<>(1, "Success", order);
        }
    }

    @Override
    public Response create(Order order, HttpHeaders headers) {
        System.out.println("[Order Service][Create Order] Ready Create Order.");
        ArrayList<Order> accountOrders = orderRepository.findByAccountId(order.getAccountId());
        //CreateOrderResult cor = new CreateOrderResult();
        if (accountOrders.contains(order)) {
            System.out.println("[Order Service][Order Create] Fail.Order already exists.");
            return new Response<>(0, "Order already exist", null);
        } else {
            order.setId(UUID.randomUUID());
            orderRepository.save(order);
            System.out.println("[Order Service][Order Create] Success.");
            System.out.println("[Order Service][Order Create] Price:" + order.getPrice());
            return new Response<>(1, "Success", order);
        }
    }

    @Override
    public Response alterOrder(OrderAlterInfo oai, HttpHeaders headers) {

        UUID oldOrderId = oai.getPreviousOrderId();
        Order oldOrder = orderRepository.findById(oldOrderId);
        if (oldOrder == null) {
            System.out.println("[Order Service][Alter Order] Fail.Order do not exist.");
            return new Response<>(0, "Old Order Does Not Exists", null);
        }
        oldOrder.setStatus(OrderStatus.CANCEL.getCode());
        saveChanges(oldOrder, headers);
        Order newOrder = oai.getNewOrderInfo();
        newOrder.setId(UUID.randomUUID());
        Response cor = create(oai.getNewOrderInfo(), headers);
        if (cor.getStatus() == 1) {
            System.out.println("[Order Service][Alter Order] Success.");
            return new Response<>(1, "Success", newOrder);
        } else {
            return new Response<>(0, cor.getMsg(), null);
        }
    }

    @Override
    public Response<ArrayList<Order>> queryOrders(OrderInfo qi, String accountId, HttpHeaders headers) {
        //1.Get all orders of the user
        ArrayList<Order> list = orderRepository.findByAccountId(UUID.fromString(accountId));
        System.out.println("[Order Service][Query Order][Step 1] Get Orders Number of Account:" + list.size());
        //2.Check is these orders fit the requirement/
        if (qi.isEnableStateQuery() || qi.isEnableBoughtDateQuery() || qi.isEnableTravelDateQuery()) {
            ArrayList<Order> finalList = new ArrayList<>();
            for (Order tempOrder : list) {
                boolean statePassFlag = false;
                boolean boughtDatePassFlag = false;
                boolean travelDatePassFlag = false;
                //3.Check order state requirement.
                if (qi.isEnableStateQuery()) {
                    if (tempOrder.getStatus() != qi.getState()) {
                        statePassFlag = false;
                    } else {
                        statePassFlag = true;
                    }
                } else {
                    statePassFlag = true;
                }
                System.out.println("[Order Service][Query Order][Step 2][Check Status Fits End]");
                //4.Check order travel date requirement.
                if (qi.isEnableTravelDateQuery()) {
                    if (tempOrder.getTravelDate().before(qi.getTravelDateEnd()) &&
                            tempOrder.getTravelDate().after(qi.getBoughtDateStart())) {
                        travelDatePassFlag = true;
                    } else {
                        travelDatePassFlag = false;
                    }
                } else {
                    travelDatePassFlag = true;
                }
                System.out.println("[Order Service][Query Order][Step 2][Check Travel Date End]");
                //5.Check order bought date requirement.
                if (qi.isEnableBoughtDateQuery()) {
                    if (tempOrder.getBoughtDate().before(qi.getBoughtDateEnd()) &&
                            tempOrder.getBoughtDate().after(qi.getBoughtDateStart())) {
                        boughtDatePassFlag = true;
                    } else {
                        boughtDatePassFlag = false;
                    }
                } else {
                    boughtDatePassFlag = true;
                }
                System.out.println("[Order Service][Query Order][Step 2][Check Bought Date End]");
                //6.check if all requirement fits.
                if (statePassFlag && boughtDatePassFlag && travelDatePassFlag) {
                    finalList.add(tempOrder);
                }
                System.out.println("[Order Service][Query Order][Step 2][Check All Requirement End]");
            }
            System.out.println("[Order Service][Query Order] Get order num:" + finalList.size());
            return new Response<>(1, "Get order num", finalList);
        } else {
            System.out.println("[Order Service][Query Order] Get order num:" + list.size());
            return new Response<>(1, "Get order num", list);
        }
    }

    @Override
    public Response queryOrdersForRefresh(OrderInfo qi, String accountId, HttpHeaders headers) {
        ArrayList<Order> orders =   queryOrders(qi, accountId, headers).getData();
        ArrayList<String> stationIds = new ArrayList<>();
        for (Order order : orders) {
            stationIds.add(order.getFrom());
            stationIds.add(order.getTo());
        }

        List<String> names = queryForStationId(stationIds, headers);
        for (int i = 0; i < orders.size(); i++) {
            orders.get(i).setFrom(names.get(i * 2));
            orders.get(i).setTo(names.get(i * 2 + 1));
        }
        return new Response<>(1, "Query Orders For Refresh Success", orders);
    }

    public List<String> queryForStationId(List<String> ids, HttpHeaders headers) {

        HttpEntity requestEntity = new HttpEntity(ids, headers);
        ResponseEntity<Response<List<String>>> re = restTemplate.exchange(
                "http://ts-station-service1:12345/api/v1/stationservice/stations/namelist",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Response<List<String>>>() {
                });
        System.out.println("Name List is: "+ re.getBody().toString());
        List<String> names = re.getBody().getData();
        return names;
    }

    @Override
    public Response saveChanges(Order order, HttpHeaders headers) {

        Order oldOrder = orderRepository.findById(order.getId());
        if (oldOrder == null) {
            System.out.println("[Order Service][Modify Order] Fail.Order not found.");
            return new Response<>(0, "Order Not Found", null);
        } else {
            oldOrder.setAccountId(order.getAccountId());
            oldOrder.setBoughtDate(order.getBoughtDate());
            oldOrder.setTravelDate(order.getTravelDate());
            oldOrder.setTravelTime(order.getTravelTime());
            oldOrder.setCoachNumber(order.getCoachNumber());
            oldOrder.setSeatClass(order.getSeatClass());
            oldOrder.setSeatNumber(order.getSeatNumber());
            oldOrder.setFrom(order.getFrom());
            oldOrder.setTo(order.getTo());
            oldOrder.setStatus(order.getStatus());
            oldOrder.setTrainNumber(order.getTrainNumber());
            oldOrder.setPrice(order.getPrice());
            oldOrder.setContactsName(order.getContactsName());
            oldOrder.setContactsDocumentNumber(order.getContactsDocumentNumber());
            oldOrder.setDocumentType(order.getDocumentType());
            orderRepository.save(oldOrder);
            System.out.println("[Order Service] Success.");
            return new Response<>(1, "Success", oldOrder);
        }
    }

    @Override
    public Response cancelOrder(UUID accountId, UUID orderId, HttpHeaders headers) {
        Order oldOrder = orderRepository.findById(orderId);
        if (oldOrder == null) {
            System.out.println("[Cancel Service][Cancel Order] Fail.Order not found.");
            return new Response<>(0, "Order Not Found", null);
        } else {
            oldOrder.setStatus(OrderStatus.CANCEL.getCode());
            orderRepository.save(oldOrder);
            System.out.println("[Cancel Service][Cancel Order] Success.");
            return new Response<>(1, "Success", oldOrder);
        }
    }

    @Override
    public Response queryAlreadySoldOrders(Date travelDate, String trainNumber, HttpHeaders headers) {
        ArrayList<Order> orders = orderRepository.findByTravelDateAndTrainNumber(travelDate, trainNumber);
        SoldTicket cstr = new SoldTicket();
        cstr.setTravelDate(travelDate);
        cstr.setTrainNumber(trainNumber);
        System.out.println("[Order Service][Calculate Sold Ticket] Get Orders Number:" + orders.size());
        for (Order order : orders) {
            if (order.getStatus() >= OrderStatus.CHANGE.getCode()) {
                continue;
            }
            if (order.getSeatClass() == SeatClass.NONE.getCode()) {
                cstr.setNoSeat(cstr.getNoSeat() + 1);
            } else if (order.getSeatClass() == SeatClass.BUSINESS.getCode()) {
                cstr.setBusinessSeat(cstr.getBusinessSeat() + 1);
            } else if (order.getSeatClass() == SeatClass.FIRSTCLASS.getCode()) {
                cstr.setFirstClassSeat(cstr.getFirstClassSeat() + 1);
            } else if (order.getSeatClass() == SeatClass.SECONDCLASS.getCode()) {
                cstr.setSecondClassSeat(cstr.getSecondClassSeat() + 1);
            } else if (order.getSeatClass() == SeatClass.HARDSEAT.getCode()) {
                cstr.setHardSeat(cstr.getHardSeat() + 1);
            } else if (order.getSeatClass() == SeatClass.SOFTSEAT.getCode()) {
                cstr.setSoftSeat(cstr.getSoftSeat() + 1);
            } else if (order.getSeatClass() == SeatClass.HARDBED.getCode()) {
                cstr.setHardBed(cstr.getHardBed() + 1);
            } else if (order.getSeatClass() == SeatClass.SOFTBED.getCode()) {
                cstr.setSoftBed(cstr.getSoftBed() + 1);
            } else if (order.getSeatClass() == SeatClass.HIGHSOFTBED.getCode()) {
                cstr.setHighSoftBed(cstr.getHighSoftBed() + 1);
            } else {
                System.out.println("[Order Service][Calculate Sold Tickets] Seat class not exists. Order ID:" + order.getId());
            }
        }
        return new Response<>(1, "Success", cstr);
    }

    @Override
    public Response getAllOrders(HttpHeaders headers) {
        ArrayList<Order> orders = orderRepository.findAll();
        if (orders != null && orders.size() > 0) {
            return new Response<>(1, "Success.", orders);
        } else {
            return new Response<>(0, "No Content.", null);
        }
    }

    @Override
    public Response modifyOrder(String orderId, int status, HttpHeaders headers) {
        Order order = orderRepository.findById(UUID.fromString(orderId));
        if (order == null) {
            return new Response<>(0, "Order Not Found", null);
        } else {
            order.setStatus(status);
            orderRepository.save(order);
            return new Response<>(1, "Modify Order Success", order);
        }
    }

    @Override
    public Response getOrderPrice(String orderId, HttpHeaders headers) {
        Order order = orderRepository.findById(UUID.fromString(orderId));
        if (order == null) {
            System.out.println("[Other Service][Get Order Price] Order Not Found.");
            return new Response<>(0, "Order Not Found", "-1.0");
        } else {
            System.out.println("[Order Service][Get Order Price] Price:" + order.getPrice());
            return new Response<>(1, "Success", order.getPrice());
        }
    }

    @Override
    public Response payOrder(String orderId, HttpHeaders headers) {
        Order order = orderRepository.findById(UUID.fromString(orderId));
        //PayOrderResult result = new PayOrderResult();
        if (order == null) {
            return new Response<>(0, "Order Not Found", null);
        } else {
            order.setStatus(OrderStatus.PAID.getCode());
            orderRepository.save(order);
            return new Response<>(1, "Pay Order Success.", order);
        }
    }

    @Override
    public Response getOrderById(String orderId, HttpHeaders headers) {
        Order order = orderRepository.findById(UUID.fromString(orderId));
        if (order == null) {
            return new Response<>(0, "Order Not Found", null);
        } else {
            return new Response<>(1, "Success.", order);
        }
    }

    @Override
    public void initOrder(Order order, HttpHeaders headers) {
        Order orderTemp = orderRepository.findById(order.getId());
        if (orderTemp == null) {
            orderRepository.save(order);
        } else {
            System.out.println("[Order Service][Init Order] Order Already Exists ID:" + order.getId());
        }
    }

    @Override
    public Response checkSecurityAboutOrder(Date dateFrom, String accountId, HttpHeaders headers) {
        OrderSecurity result = new OrderSecurity();
        ArrayList<Order> orders = orderRepository.findByAccountId(UUID.fromString(accountId));
        int countOrderInOneHour = 0;
        int countTotalValidOrder = 0;
        Calendar ca = Calendar.getInstance();
        ca.setTime(dateFrom);
        ca.add(Calendar.HOUR_OF_DAY, -1);
        dateFrom = ca.getTime();
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.NOTPAID.getCode() ||
                    order.getStatus() == OrderStatus.PAID.getCode() ||
                    order.getStatus() == OrderStatus.COLLECTED.getCode()) {
                countTotalValidOrder += 1;
            }
            if (order.getBoughtDate().after(dateFrom)) {
                countOrderInOneHour += 1;
            }
        }
        result.setOrderNumInLastOneHour(countOrderInOneHour);
        result.setOrderNumOfValidOrder(countTotalValidOrder);
        return new Response<>(1, "Check Security Success . ", result);
    }

    @Override
    public Response deleteOrder(String orderId, HttpHeaders headers) {
        UUID orderUuid = UUID.fromString(orderId);
        Order order = orderRepository.findById(orderUuid);

        if (order == null) {
            return new Response<>(0, "Order Not Exist.", null);
        } else {
            orderRepository.deleteById(orderUuid);
            return new Response<>(1, "Delete Order Success", order);
        }
    }

    @Override
    public Response addNewOrder(Order order, HttpHeaders headers) {
        System.out.println("[Order Service][Admin Add Order] Ready Add Order.");
        ArrayList<Order> accountOrders = orderRepository.findByAccountId(order.getAccountId());
        if (accountOrders.contains(order)) {
            System.out.println("[Order Service][Admin Add Order] Fail.Order already exists.");
            return new Response<>(0, "Order already exist", null);
        } else {
            order.setId(UUID.randomUUID());
            orderRepository.save(order);
            System.out.println("[Order Service][Admin Add Order] Success.");
            System.out.println("[Order Service][Admin Add Order] Price:" + order.getPrice());
            return new Response<>(1, "Add new Order Success", order);
        }
    }

    @Override
    public Response updateOrder(Order order, HttpHeaders headers) {
        log.info("UPDATE ORDER INFO: " +order.toString());
        Order oldOrder = orderRepository.findById(order.getId());
        if (oldOrder == null) {
            System.out.println("[Order Service][Admin Update Order] Fail.Order not found.");
            return new Response<>(0, "Order Not Found, Can't update", null);
        } else {
            System.out.println(oldOrder.toString());
            oldOrder.setAccountId(order.getAccountId());
            oldOrder.setBoughtDate(order.getBoughtDate());
            oldOrder.setTravelDate(order.getTravelDate());
            oldOrder.setTravelTime(order.getTravelTime());
            oldOrder.setCoachNumber(order.getCoachNumber());
            oldOrder.setSeatClass(order.getSeatClass());
            oldOrder.setSeatNumber(order.getSeatNumber());
            oldOrder.setFrom(order.getFrom());
            oldOrder.setTo(order.getTo());
            oldOrder.setStatus(order.getStatus());
            oldOrder.setTrainNumber(order.getTrainNumber());
            oldOrder.setPrice(order.getPrice());
            oldOrder.setContactsName(order.getContactsName());
            oldOrder.setContactsDocumentNumber(order.getContactsDocumentNumber());
            oldOrder.setDocumentType(order.getDocumentType());
            orderRepository.save(oldOrder);
            System.out.println("[Order Service] [Admin Update Order] Success.");
            return new Response<>(1, "Admin Update Order Success", oldOrder);
        }
    }
}

