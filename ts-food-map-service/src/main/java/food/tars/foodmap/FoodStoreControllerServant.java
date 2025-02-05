// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 1.0.1.
// **********************************************************************

package food.tars.foodmap;

import com.qq.tars.protocol.annotation.*;
import com.qq.tars.protocol.tars.annotation.*;
import com.qq.tars.common.support.Holder;

@Servant
public interface FoodStoreControllerServant {

	public String home();

	public ResponseFoodStores getAllFoodStores();

	public ResponseFoodStores getFoodStoresOfStation(String stationId);

	public ResponseFoodStores getFoodStoresByStationIds(java.util.List<String> stationIdList);
}
