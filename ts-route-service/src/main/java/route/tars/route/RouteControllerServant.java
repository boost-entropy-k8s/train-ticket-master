// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 1.0.1.
// **********************************************************************

package route.tars.route;

import com.qq.tars.protocol.annotation.*;
import com.qq.tars.protocol.tars.annotation.*;
import com.qq.tars.common.support.Holder;

@Servant
public interface RouteControllerServant {

	public String home();

	public ResponseRoute createAndModifyRoute(RouteInfoTars createAndModifyRouteInfo);

	public ResponseString deleteRoute(String routeId);

	public ResponseRoute queryById(String routeId);

	public ResponseRoutes queryAll();

	public ResponseRoutes queryByStartAndTerminal(String startId, String terminalId);
}
