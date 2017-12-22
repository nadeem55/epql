/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.dspmicro.energy.subscriptionmachineadapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscription;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.types.PDataNode;
import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.dspmicro.machinegateway.types.PEnvelope;
import com.ge.dspmicro.machinegateway.types.PQuality;
import com.ge.dspmicro.machinegateway.types.PTimestamp;
import com.ge.dspmicro.machinegateway.types.PQuality.QualityEnum;

/**
 * 
 * @author Predix Machine Sample
 */
@SuppressWarnings("deprecation")
public class DataSubscription implements Runnable, IDataSubscription {

	private static final Logger _logger = LoggerFactory.getLogger(DataSubscription.class);

	protected String source;
	protected Map<String, DataNode> hashedNodes = new HashMap<>();

	private UUID uuid;
	public String subName;
	protected long updateIntervalMillis;
	protected ISubscriptionMachineAdapter iSubscriptionMachineAdapter;
	private Map<UUID, DataNode> nodes = new HashMap<UUID, DataNode>();
	protected List<IDataSubscriptionListener> listeners = new ArrayList<IDataSubscriptionListener>();
	private Random dataGenerator = new Random();
	protected final AtomicBoolean threadRunning = new AtomicBoolean();

	/**
	 * Constructor
	 * 
	 * @param adapter
	 *            machine adapter
	 * @param subName
	 *            Name of this subscription
	 * @param timeoutMillis
	 *            in milliseconds
	 * @param nodes
	 *            list of nodes for this subscription
	 */
	public DataSubscription(ISubscriptionMachineAdapter adapter, String subName, long timeoutMillis, List<DataNode> nodes) {

		if (timeoutMillis > 0) {
			this.updateIntervalMillis = timeoutMillis;
		} else {
			throw new IllegalArgumentException("updataInterval must be greater than zero."); //$NON-NLS-1$
		}

		if (nodes != null && nodes.size() > 0) {
			for (DataNode node : nodes) {
				this.nodes.put(node.getNodeId(), node);
			}
		} else {
			throw new IllegalArgumentException("nodes must have values."); //$NON-NLS-1$
		}

		this.iSubscriptionMachineAdapter = adapter;

		// Generate unique id.
		this.uuid = UUID.randomUUID();
		this.subName = subName;

		// Initialize random data generator.
		this.dataGenerator.setSeed(Calendar.getInstance().getTimeInMillis());
		this.threadRunning.set(false);
	}

	@Override
	public UUID getId() {
		return this.uuid;
	}

	@Override
	public String getName() {
		return this.subName;
	}

	@Override
	@Deprecated
	public int getUpdateInterval() {
		return (int) (this.updateIntervalMillis / 1000L);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.dspmicro.machinegateway.api.adapter.ISubscription#
	 * getUpdateIntervalMillis()
	 */
	@Override
	public long getUpdateIntervalMillis() {
		return this.updateIntervalMillis;
	}

	@Override
	public List<PDataNode> getSubscriptionNodes() {
		return new ArrayList<PDataNode>(this.nodes.values());
	}

	/**
	 * @param listener
	 *            callback listener
	 */
	@Override
	public synchronized void addDataSubscriptionListener(IDataSubscriptionListener listener) {
		if (!this.listeners.contains(listener)) {
			_logger.info("Going to add listener :[" + listener.getId() + "]");
			this.listeners.add(listener);
		}
	}

	/**
	 * @param listener
	 *            callback listener
	 */
	@Override
	public synchronized void removeDataSubscriptionListener(IDataSubscriptionListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * get all listeners
	 * 
	 * @return a list of listeners.
	 */
	@Override
	public synchronized List<IDataSubscriptionListener> getDataSubscriptionListeners() {
		return this.listeners;
	}

	/**
	 * Thread to generate random data for the nodes in this subscription.
	 */
	@Override
	public void run() {

		if (!this.threadRunning.get() && this.nodes.size() > 0) {

			this.threadRunning.set(true);

			while (this.threadRunning.get()) {

				// Generate random data for each node and push data update.
				List<PDataValue> data = new ArrayList<PDataValue>();

				for (DataNode node : this.nodes.values()) {

					// Simulating the data.
					float nodeValue = this.dataGenerator.nextFloat();
					_logger.debug("New simulated value [:  " + nodeValue + "]");

					PEnvelope envelope = new PEnvelope(nodeValue);
					PDataValue pDataValue = new PDataValue(node.getNodeId(), envelope);
					pDataValue.setNodeName(node.getName());
					pDataValue.setAddress(node.getAddress());

					data.add(pDataValue);
				}

				// Writing the simulated data into cache
				((EnergySubscriptionMachineAdapterImpl) this.iSubscriptionMachineAdapter).putData(data);

				// Provide the subscription name as a property of the data. If
				// the data is being
				// published on the databus river, the subscription name will be
				// used as the publish topic

				HashMap<String, String> properties = new HashMap<String, String>();
				properties.put(ISubscription.PROPKEY_SUBSCRIPTION, this.subName);

				for (IDataSubscriptionListener listener : this.listeners) {
					listener.onDataUpdate(this.iSubscriptionMachineAdapter, properties, data);
				}

				try {
					// Wait for an updateInterval period before pushing next
					// data update.
					Thread.sleep(getUpdateIntervalMillis());
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Stops generating random data.
	 */
	public void stop() {

		if (this.threadRunning.get()) {
			this.threadRunning.set(false);

			// Send notification to all listeners.
			for (IDataSubscriptionListener listener : this.listeners) {
				listener.onSubscriptionDelete(this.iSubscriptionMachineAdapter, this.uuid);
			}

			// Do other clean up if needed...
		}
	}

}
