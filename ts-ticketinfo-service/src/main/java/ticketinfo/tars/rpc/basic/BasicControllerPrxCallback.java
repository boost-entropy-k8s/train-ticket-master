// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 1.0.1.
// **********************************************************************

package ticketinfo.tars.rpc.basic;

import com.qq.tars.rpc.protocol.tars.support.TarsAbstractCallback;

public abstract class BasicControllerPrxCallback extends TarsAbstractCallback {

	public abstract void callback_home(String ret);

	public abstract void callback_queryForTravel(ResponseTravelResult ret);

	public abstract void callback_queryForStationId(ResponseString ret);

}
