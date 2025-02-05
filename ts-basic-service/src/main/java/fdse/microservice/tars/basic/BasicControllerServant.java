// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 1.0.1.
// **********************************************************************

package fdse.microservice.tars.basic;

import com.qq.tars.protocol.annotation.*;
import com.qq.tars.protocol.tars.annotation.*;
import com.qq.tars.common.support.Holder;

@Servant
public interface BasicControllerServant {

	public String home();

	public ResponseTravelResult queryForTravel(TravelTars info);

	public ResponseString queryForStationId(String stationName);
}
