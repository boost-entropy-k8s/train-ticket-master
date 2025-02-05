// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 1.0.1.
// **********************************************************************

package fdse.microservice.tars.basic;

import com.qq.tars.protocol.util.*;
import com.qq.tars.protocol.annotation.*;
import com.qq.tars.protocol.tars.*;
import com.qq.tars.protocol.tars.annotation.*;

@TarsStruct
public class TravelResultTars {

	@TarsStructProperty(order = 0, isRequire = false)
	public boolean status = false;
	@TarsStructProperty(order = 1, isRequire = false)
	public double percent = 0;
	@TarsStructProperty(order = 2, isRequire = false)
	public TrainTypeTars trainType = null;
	@TarsStructProperty(order = 3, isRequire = false)
	public java.util.Map<String, String> prices = null;

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public TrainTypeTars getTrainType() {
		return trainType;
	}

	public void setTrainType(TrainTypeTars trainType) {
		this.trainType = trainType;
	}

	public java.util.Map<String, String> getPrices() {
		return prices;
	}

	public void setPrices(java.util.Map<String, String> prices) {
		this.prices = prices;
	}

	public TravelResultTars() {
	}

	public TravelResultTars(boolean status, double percent, TrainTypeTars trainType, java.util.Map<String, String> prices) {
		this.status = status;
		this.percent = percent;
		this.trainType = trainType;
		this.prices = prices;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + TarsUtil.hashCode(status);
		result = prime * result + TarsUtil.hashCode(percent);
		result = prime * result + TarsUtil.hashCode(trainType);
		result = prime * result + TarsUtil.hashCode(prices);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TravelResultTars)) {
			return false;
		}
		TravelResultTars other = (TravelResultTars) obj;
		return (
			TarsUtil.equals(status, other.status) &&
			TarsUtil.equals(percent, other.percent) &&
			TarsUtil.equals(trainType, other.trainType) &&
			TarsUtil.equals(prices, other.prices) 
		);
	}

	public void writeTo(TarsOutputStream _os) {
		_os.write(status, 0);
		_os.write(percent, 1);
		if (null != trainType) {
			_os.write(trainType, 2);
		}
		if (null != prices) {
			_os.write(prices, 3);
		}
	}

	static TrainTypeTars cache_trainType;
	static { 
		cache_trainType = new TrainTypeTars();
	}
	static java.util.Map<String, String> cache_prices;
	static { 
		cache_prices = new java.util.HashMap<String, String>();
		String var_1 = "";
		String var_2 = "";
		cache_prices.put(var_1 ,var_2);
	}

	public void readFrom(TarsInputStream _is) {
		this.status = _is.read(status, 0, false);
		this.percent = _is.read(percent, 1, false);
		this.trainType = (TrainTypeTars) _is.read(cache_trainType, 2, false);
		this.prices = (java.util.Map<String, String>) _is.read(cache_prices, 3, false);
	}

}
