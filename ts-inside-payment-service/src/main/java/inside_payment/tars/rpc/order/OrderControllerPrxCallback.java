// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 1.0.1.
// **********************************************************************

package inside_payment.tars.rpc.order;

import com.qq.tars.rpc.protocol.tars.support.TarsAbstractCallback;

public abstract class OrderControllerPrxCallback extends TarsAbstractCallback {

	public abstract void callback_home(String ret);

	public abstract void callback_getTicketListByDateAndTripId(Response ret, LeftTicketInfoTars leftTicketInfoTarsOut);

	public abstract void callback_createNewOrder(Response ret, OrderTars orderTarsOut);

	public abstract void callback_addcreateNewOrder(Response ret, OrderTars orderTarsOut);

	public abstract void callback_queryOrders(Response ret, java.util.List<OrderTars> orderTarsListOut);

	public abstract void callback_queryOrdersForRefresh(Response ret, java.util.List<OrderTars> orderTarsListOut);

	public abstract void callback_calculateSoldTicket(Response ret, SoldTicketTars soldTicketTarsOut);

	public abstract void callback_getOrderPrice(Response ret, String priceOut);

	public abstract void callback_payOrder(Response ret, OrderTars orderTarsOut);

	public abstract void callback_getOrderById(Response ret, OrderTars orderTarsOut);

	public abstract void callback_modifyOrder(Response ret, OrderTars orderTarsOut);

	public abstract void callback_securityInfoCheck(Response ret, OrderSecurityTars orderSecurityTarsOut);

	public abstract void callback_saveOrderInfo(Response ret, OrderTars orderTarsOut);

	public abstract void callback_updateOrder(Response ret, OrderTars orderTarsOut);

	public abstract void callback_deleteOrder(Response ret, OrderTars orderTarsOut);

	public abstract void callback_findAllOrder(Response ret, java.util.List<OrderTars> orderTarsListOut);

}
