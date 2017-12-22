package com.ge.dspmicro.energy.subscriptionmachineadapter;

import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.types.PDataValue;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class HubcoDataSubscription extends DataSubscription {

	private static final Logger _logger = LoggerFactory.getLogger(HubcoDataSubscription.class);

	public HubcoDataSubscription(ISubscriptionMachineAdapter adapter, String subName) {

		super(adapter, subName, (Long) null, null);
	}

	/**
	 * 
	 * @param adapter
	 * @param subName
	 * @param updateInterval
	 * @param nodes
	 * @param source
	 */
	public HubcoDataSubscription(ISubscriptionMachineAdapter adapter, String subName, int updateInterval, List<DataNode> nodes) {

		super(adapter, subName, updateInterval, nodes);
		
	}

	/**
	 * 
	 */
	public void sendData(List<PDataValue> listPDataValue, String subscription) {

		try {

			if (listPDataValue != null && listPDataValue.size() > 0) {

				_logger.debug(" Sending data of subc:[" + subscription + "]");

				HashMap<String, String> properties = new HashMap<String, String>();
				properties.put(IDataSubscription.PROPKEY_SUBSCRIPTION, subscription);

				for (IDataSubscriptionListener listener : this.listeners) {
					_logger.info("Subcription :[" + subscription + "] listener id :[" + listener.getId() + "]");
					listener.onDataUpdate(this.iSubscriptionMachineAdapter, properties, listPDataValue);
				}
			}
		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		}

	}

}