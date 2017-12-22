package com.ge.dspmicro.energy.subscriptionmachineadapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.Util;

import com.ge.dspmicro.machinegateway.api.adapter.AbstractSubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.api.adapter.IMachineAdapter;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterException;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterInfo;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterState;
import com.ge.dspmicro.machinegateway.types.PDataNode;
import com.ge.dspmicro.machinegateway.types.PDataValue;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Modified;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscription;
import com.ge.dspmicro.machinegateway.types.PEnvelope;
import com.ge.dspmicro.machinegateway.types.PQuality;
import com.ge.dspmicro.machinegateway.types.PQuality.QualityEnum;
import com.ge.dspmicro.machinegateway.types.PTimestamp;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class SourceDataSubscription extends DataSubscription {

	private static final Logger _logger = LoggerFactory.getLogger(SourceDataSubscription.class);

	/**
	 * 
	 * @param adapter
	 * @param subName
	 * @param updateInterval
	 * @param nodes
	 * @param source
	 */
	public SourceDataSubscription(ISubscriptionMachineAdapter adapter, String subName, int updateInterval, List<DataNode> nodes, String source) {

		super(adapter, subName, updateInterval, nodes);
		this.source = source;
		for (DataNode sampleDataNode : nodes) {
			hashedNodes.put(sampleDataNode.getName(), sampleDataNode);
		}

	}

	@Override
	public void run() {

		if (!this.threadRunning.get() && this.hashedNodes.size() > 0) {
			
			this.threadRunning.set(true);
			while (this.threadRunning.get()) {

				List<PDataValue> data;

				try {

					data = parseCsv();
					
					//parse XML file
					
					//Move the XML file from this folder to archive folder

					// Writing the simulated data into cache
					((EnergySubscriptionMachineAdapterImpl) this.iSubscriptionMachineAdapter).putData(data);

					HashMap<String, String> properties = new HashMap<String, String>();
					properties.put(IDataSubscription.PROPKEY_SUBSCRIPTION, this.subName);

					for (IDataSubscriptionListener listener : this.listeners) {
						listener.onDataUpdate(this.iSubscriptionMachineAdapter, properties, data);
					}

				} catch (Exception e1) {
					_logger.error("[run] '" + source + "'", e1);
				}

				try {
					// Wait for an updateInterval period before pushing next data update.
					Thread.sleep(this.updateIntervalMillis);
				} catch (InterruptedException e) {
					_logger.error("InterruptedException :[" + e.getMessage() + "]");
				}
			}
		}

	}

	/**
	 * 
	 * @return
	 */
	private List<PDataValue> parseCsv() {

		// Generate random data for each node and push data update.
		List<PDataValue> data = new ArrayList<PDataValue>();

		String line = "";
		String csvSplitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(source))) {

			while ((line = br.readLine()) != null) {
				String[] tag = line.split(csvSplitBy);
				if (tag.length < 4)
					continue;

				DataNode node = hashedNodes.get(tag[0]);
				PEnvelope envelope = new PEnvelope(tag[1]);

				if (node == null) {
					_logger.error("skipped " + tag[0]);
				} else {

					try {
						PDataValue value = new PDataValue(node.getNodeId(), envelope);
						value.setNodeName(node.getName());
						value.setAddress(node.getAddress());
						PTimestamp ts = new PTimestamp(Long.parseLong(tag[2]));
						QualityEnum qe = null;
						if (tag[3].equals("GOOD")) {
							qe = (QualityEnum.GOOD);
						} else if (tag[3].equals("BAD")) {
							qe = (QualityEnum.BAD);
						} else if (tag[3].equals("NOT_SUPPORTED")) {
							qe = (QualityEnum.NOT_SUPPORTED);
						} else if (tag[3].equals("UNCERTAIN")) {
							qe = (QualityEnum.UNCERTAIN);
						}
						PQuality pq = new PQuality(qe);
						value.setQuality(pq);
						value.setTimestamp(ts);
						value.setQuality(pq);
						data.add(value);

					} catch (Exception e) {
						_logger.error("[CsvDataSubscription.run] exception: " + e + " skipped " + tag[0]);
					}
				}

			}
		} catch (Exception e) {
			_logger.error("[CsvDataSubscription.run] exception: " + e);
		}

		return data;
	}

}
