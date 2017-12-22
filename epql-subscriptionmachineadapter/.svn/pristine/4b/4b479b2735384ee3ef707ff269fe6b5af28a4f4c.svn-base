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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ge.dspmicro.energy.file.FileDeleter;
import com.ge.dspmicro.energy.source.data.parse.OneMinXMLDataParser;
import com.ge.dspmicro.energy.xml.configuration.CustomAdapterConfig;
import com.ge.dspmicro.energy.xml.configuration.Datasubscriptionconfig;
import com.ge.dspmicro.energy.xml.configuration.Datasubscriptionconfigs;
import com.ge.dspmicro.energy.xml.configuration.Node;
import com.ge.dspmicro.machinegateway.api.adapter.AbstractSubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener;
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

/**
 * 
 * @author Predix Machine Sample
 */
@SuppressWarnings({ "javadoc", "deprecation" })
@Component(name = EnergySubscriptionMachineAdapterImpl.SERVICE_PID, provide = { ISubscriptionMachineAdapter.class,
		IMachineAdapter.class }, designate = EnergySubscriptionMachineAdapterImpl.Config.class, configurationPolicy = ConfigurationPolicy.require)
public class EnergySubscriptionMachineAdapterImpl extends AbstractSubscriptionMachineAdapter {

	// Meta mapping for configuration properties
	@Meta.OCD(name = "%component.name", localization = "OSGI-INF/l10n/bundle")
	interface Config {

		@Meta.AD(name = "%updateIntervalOneMin.name", description = "%updateIntervalOneMin.description", id = UPDATE_INTERVAL_OneMin, required = false, deflt = "")
		String updateIntervalOneMin();
		
		@Meta.AD(name = "%oldFilesDeleteInterval.name", description = "%oldFilesDeleteInterval.description", id = OLD_FILES_DELETE_INTERVAL, required = false, deflt = "")
		String oldFilesDeleteInterval();
		
		@Meta.AD(name = "%adapterName.name", description = "%adapterName.description", id = ADAPTER_NAME, required = false, deflt = "")
		String adapterName();

		@Meta.AD(name = "%adapterDescription.name", description = "%adapterDescription.description", id = ADAPTER_DESCRIPTION, required = false, deflt = "")
		String adapterDescription();

		@Meta.AD(name = "%configFile.name", description = "%configfile.description", id = CONFIG_FILE, required = true, deflt = "")
		String configFile();

		@Meta.AD(name = "%dataSourceDirectory.name", description = "%dataSourceDirectory.description", id = DATA_SOURCE_DIR, required = true, deflt = "")
		String dataSourceDirectory();

		@Meta.AD(name = "%dataArchiveDirectory.name", description = "%dataArchiveDirectory.description", id = DATA_ARCHIVE_DIR, required = true, deflt = "")
		String dataArchiveDirectory();
	}

	/** Service PID for Sample Machine Adapter */
	public static final String SERVICE_PID = "com.ge.dspmicro.hubco.subscriptionmachineadapter";
	/** Key for Update Interval */
	public static final String UPDATE_INTERVAL_OneMin = SERVICE_PID + ".UpdateIntervalOneMin";
	public static final String OLD_FILES_DELETE_INTERVAL = SERVICE_PID + ".DeleteOldFilesInterval";
		
	/** key for machine adapter name */
	public static final String ADAPTER_NAME = SERVICE_PID + ".AdapterName";
	/** Key for machine adapter description */
	public static final String ADAPTER_DESCRIPTION = SERVICE_PID + ".Description";
	/** data subscriptions */
	public static final String DATA_SUBSCRIPTIONS = SERVICE_PID + ".DataSubscriptions";
	/** Configuration XML file */
	public static final String CONFIG_FILE = SERVICE_PID + ".configfile";
	public static final String DATA_SOURCE_DIR = SERVICE_PID + ".dataSourceDirectory";
	public static final String DATA_ARCHIVE_DIR = SERVICE_PID + ".dataArchiveDirectory";

	/**
	 * The regular expression used to split property values into String array.
	 */
	public final static String SPLIT_PATTERN = "\\s*\\|\\s*";

	private static final Logger _logger = LoggerFactory.getLogger(EnergySubscriptionMachineAdapterImpl.class);

	private UUID uuid = UUID.randomUUID();
	private Dictionary<String, Object> props;
	private MachineAdapterInfo adapterInfo;
	private MachineAdapterState adapterState;
	private Map<UUID, DataNode> dataNodes = new HashMap<UUID, DataNode>();
	private Map<String, Map<String, DataNode>> mapSubcriptionDataNodes = new HashMap<>();

	private Config config;
	private int updateIntervalOneMin;
	private long deleteIntervalMillis;
	private String configFile;
	private String oneDaySubscription;
	public static String dataSourceDir;
	public static String dataArchiveDir;

	/**
	 * Data cache for holding latest data updates
	 */
	protected Map<UUID, PDataValue> dataValueCache = new ConcurrentHashMap<UUID, PDataValue>();
	public static Map<UUID, DataSubscription> dataSubscriptions = new HashMap<UUID, DataSubscription>();
	public static Map<String, UUID> mapDataSubscriptions = new HashMap<String, UUID>();

	/*
	 * OSGi service lifecycle management
	 */

	/**
	 * OSGi component lifecycle activation method
	 * 
	 * @param ctx
	 *            component context
	 * @throws IOException
	 *             on fail to load/set configuration properties
	 */
	@Activate
	public void activate(ComponentContext ctx) throws IOException {

		try {

			if (_logger.isDebugEnabled()) {
				_logger.debug("Starting bundle :[" + ctx.getBundleContext().getBundle().getSymbolicName() + "]");
			}

			// Get all properties and create nodes.
			this.props = ctx.getProperties();

			this.config = Configurable.createConfigurable(Config.class, ctx.getProperties());

			this.updateIntervalOneMin = Integer.parseInt(this.config.updateIntervalOneMin());
			_logger.debug("updateIntervalOneMin is  :[" + this.updateIntervalOneMin + "]");
			
			this.deleteIntervalMillis = Integer.parseInt(this.config.oldFilesDeleteInterval());
			_logger.debug("deleteIntervalMillis is  :[" + this.deleteIntervalMillis + "]");
			
			this.configFile = this.config.configFile();
			_logger.debug("config file path :[" + this.configFile + "]");

			_logger.debug("Path of Source Dir :[" + this.config.dataSourceDirectory() + "]");
			dataSourceDir = this.config.dataSourceDirectory();

			_logger.debug("Path of Archive Dir :[" + this.config.dataArchiveDirectory() + "]");
			dataArchiveDir = this.config.dataArchiveDirectory();

			// Define list of nodes according to each subscription

			ObjectMapper objectMapper = new XmlMapper();
			CustomAdapterConfig customAdapterConfig = objectMapper.readValue(StringUtils.toEncodedString(Files.readAllBytes(Paths.get(this.configFile)), StandardCharsets.UTF_8),
					CustomAdapterConfig.class);

			if (customAdapterConfig != null) {

				createNodes(customAdapterConfig);

				this.adapterInfo = new MachineAdapterInfo(this.config.adapterName(), EnergySubscriptionMachineAdapterImpl.SERVICE_PID, this.config.adapterDescription(),
						ctx.getBundleContext().getBundle().getVersion().toString());

				Datasubscriptionconfig[] arrDatasubscriptionconfig = parseDataSubscriptions(customAdapterConfig);
				if (arrDatasubscriptionconfig != null && arrDatasubscriptionconfig.length > 0) {
					for (Datasubscriptionconfig datasubscriptionconfig : arrDatasubscriptionconfig) {
						String subscriptionName = datasubscriptionconfig.getSubscription_name();
						_logger.debug("Subscription name going to get nodes :[" + subscriptionName + "]");

						Node[] arrNodes = getNodes(customAdapterConfig, subscriptionName);
						if (arrNodes != null && arrNodes.length > 0) {
							_logger.debug("No. of nodes :[" + arrNodes.length + "] of subscription :[" + subscriptionName + "]");

							HubcoDataSubscription smelterDataSubscription = new HubcoDataSubscription(this, subscriptionName, this.updateIntervalOneMin,
									new ArrayList<DataNode>(this.dataNodes.values()));

							dataSubscriptions.put(smelterDataSubscription.getId(), smelterDataSubscription);
							mapDataSubscriptions.put(subscriptionName, smelterDataSubscription.getId());

						}
					}
				}

				OneMinXMLDataParser oneMinXMLDataParser = new OneMinXMLDataParser(this.updateIntervalOneMin , mapSubcriptionDataNodes);
				new Thread(oneMinXMLDataParser).start();

				//FileDeleter fileDeleter = new FileDeleter(this.deleteIntervalMillis, dataArchiveDir);
				//new Thread(fileDeleter).start();
			
			}

		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		}
	}

	/**
	 * OSGi component lifecycle deactivation method
	 * 
	 * @param ctx
	 *            component context
	 */
	@Deactivate
	public void deactivate(ComponentContext ctx) {

		// Put your clean up code here when container is shutting down
		if (_logger.isDebugEnabled()) {
			_logger.debug("Stopped sample for " + ctx.getBundleContext().getBundle().getSymbolicName());
		}

		Collection<DataSubscription> values = this.dataSubscriptions.values();
		// Stop random data generation thread.
		for (DataSubscription sub : values) {
			sub.stop();
		}
		this.adapterState = MachineAdapterState.Stopped;
	}

	/**
	 * OSGi component lifecycle modified method. Called when the component
	 * properties are changed.
	 * 
	 * @param ctx
	 *            component context
	 */
	@Modified
	public synchronized void modified(ComponentContext ctx) {

		// Handle run-time changes to properties.

		this.props = ctx.getProperties();
	}

	/*
	 * ####################################### # IMachineAdapter interface
	 * methods # #######################################
	 */

	@Override
	public UUID getId() {
		return this.uuid;
	}

	@Override
	public MachineAdapterInfo getInfo() {
		return this.adapterInfo;
	}

	@Override
	public MachineAdapterState getState() {
		return this.adapterState;
	}

	/*
	 * Returns all data nodes. Data nodes are auto-generated at startup.
	 */
	@Override
	public List<PDataNode> getNodes() {
		return new ArrayList<PDataNode>(this.dataNodes.values());
	}

	/*
	 * Reads data from data cache. Data cache always contains latest values.
	 */
	@Override
	public PDataValue readData(UUID nodeId) throws MachineAdapterException {
		if (this.dataValueCache.containsKey(nodeId)) {
			return this.dataValueCache.get(nodeId);
		}

		// Do not return null.
		return new PDataValue(nodeId);
	}

	/*
	 * Writes data value into data cache.
	 */
	@Override
	public void writeData(UUID nodeId, PDataValue value) throws MachineAdapterException {

		if (this.dataValueCache.containsKey(nodeId)) {
			// Put data into cache. The value typically should be written to a
			// device node.
			this.dataValueCache.put(nodeId, value);
		}
	}

	/*
	 * ################################################### #
	 * ISubscriptionMachineAdapter interface methods #
	 * ###################################################
	 */

	/*
	 * Returns list of all subscriptions.
	 */
	@Override
	public List<IDataSubscription> getSubscriptions() {
		return new ArrayList<IDataSubscription>(this.dataSubscriptions.values());
	}

	/*
	 * Adds new data subscription into the list.
	 */
	@Override
	public synchronized UUID addDataSubscription(IDataSubscription subscription) throws MachineAdapterException {

		if (subscription == null) {
			throw new IllegalArgumentException("Subscription is null");
		}

		List<DataNode> subscriptionNodes = new ArrayList<DataNode>();

		// Add new data subscription.
		if (!this.dataSubscriptions.containsKey(subscription.getId())) {
			// Make sure that new subscription contains valid nodes.
			for (PDataNode sampleNode : subscription.getSubscriptionNodes()) {
				if (!this.dataNodes.containsKey(sampleNode.getNodeId())) {
					throw new MachineAdapterException("Node doesn't exist for this adapter");
				}

				subscriptionNodes.add(this.dataNodes.get(sampleNode.getNodeId()));
			}

			// Create new subscription.
			DataSubscription newSubscription = new DataSubscription(this, subscription.getName(), subscription.getUpdateIntervalMillis(), subscriptionNodes);
			this.dataSubscriptions.put(newSubscription.getId(), newSubscription);
			new Thread(newSubscription).start();
			return newSubscription.getId();
		}

		return null;
	}

	/*
	 * Remove data subscription from the list
	 */
	@Override
	public synchronized void removeDataSubscription(UUID subscriptionId) {

		// Stop subscription, notify all subscribers, and remove subscription
		if (this.dataSubscriptions.containsKey(subscriptionId)) {
			this.dataSubscriptions.get(subscriptionId).stop();
			this.dataSubscriptions.remove(subscriptionId);
		}
	}

	/**
	 * get subscription given subscription id.
	 */
	@Override
	public IDataSubscription getDataSubscription(UUID subscriptionId) {
		if (this.dataSubscriptions.containsKey(subscriptionId)) {
			return this.dataSubscriptions.get(subscriptionId);
		}
		throw new MachineAdapterException("Subscription does not exist");
	}

	@Override
	public synchronized void addDataSubscriptionListener(UUID dataSubscriptionId, IDataSubscriptionListener listener) throws MachineAdapterException {
		if (this.dataSubscriptions.containsKey(dataSubscriptionId)) {
			this.dataSubscriptions.get(dataSubscriptionId).addDataSubscriptionListener(listener);
			return;
		}
		throw new MachineAdapterException("Subscription does not exist");
	}

	@Override
	public synchronized void removeDataSubscriptionListener(UUID dataSubscriptionId, IDataSubscriptionListener listener) {
		if (this.dataSubscriptions.containsKey(dataSubscriptionId)) {
			this.dataSubscriptions.get(dataSubscriptionId).removeDataSubscriptionListener(listener);
		}
	}

	/**
	 * 
	 * @param customAdapterConfig
	 */
	private void createNodes(CustomAdapterConfig customAdapterConfig) {

		try {

			if (customAdapterConfig != null) {

				Datasubscriptionconfigs[] arrDatasubscriptionconfigs = customAdapterConfig.getDatasubscriptionconfigs();
				if (arrDatasubscriptionconfigs != null && arrDatasubscriptionconfigs.length > 0) {
					//_logger.debug("Number of arrDatasubscriptionconfigs :[" + arrDatasubscriptionconfigs.length + "]");
					for (Datasubscriptionconfigs datasubscriptionconfigs : arrDatasubscriptionconfigs) {
						if (datasubscriptionconfigs != null) {
							Datasubscriptionconfig[] arrDatasubscriptionconfig = datasubscriptionconfigs.getDatasubscriptionconfig();
							if (arrDatasubscriptionconfig != null && arrDatasubscriptionconfig.length > 0) {
								//_logger.debug("Number of Datasubscriptionconfig :[" + arrDatasubscriptionconfig.length + "]");

								for (Datasubscriptionconfig datasubscriptionconfig : arrDatasubscriptionconfig) {
									if (datasubscriptionconfig != null) {
										Node[] arrNodes = datasubscriptionconfig.getNode();
										if (arrNodes != null && arrNodes.length > 0) {

											//_logger.debug("Number of nodes :[" + arrNodes.length + "]");
											Map<String, DataNode> dataNodes_2 = new HashMap<String, DataNode>();

											for (Node node : arrNodes) {
												String nodeName = node.getNodeName();
												DataNode dataNode = new DataNode(this.uuid, nodeName);

												// Create a new node and put it
												// in the cache.
												this.dataNodes.put(dataNode.getNodeId(), dataNode);
												//_logger.debug("Node is created :[" + nodeName + "]");

												dataNodes_2.put(dataNode.getName(), dataNode);
											}
											// Keep dataNodes against each
											// subscription
											mapSubcriptionDataNodes.put(datasubscriptionconfig.getSubscription_name(), dataNodes_2);
										}
									}
								}
							}
						}
					}
				} else {
					_logger.error("There is no Datasubscriptionconfigs in given file.");
				}
			} else {
				_logger.info("customAdapterConfig is null!!!!!!!!!");
			}
		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		}
	}

	/**
	 * 
	 * @return
	 */
	private Datasubscriptionconfig[] parseDataSubscriptions(CustomAdapterConfig customAdapterConfig) {

		try {

			if (customAdapterConfig != null) {

				Datasubscriptionconfigs[] arrDatasubscriptionconfigs = customAdapterConfig.getDatasubscriptionconfigs();
				if (arrDatasubscriptionconfigs != null && arrDatasubscriptionconfigs.length > 0) {
					_logger.debug("Number of arrDatasubscriptionconfigs :[" + arrDatasubscriptionconfigs.length + "]");
					for (Datasubscriptionconfigs datasubscriptionconfigs : arrDatasubscriptionconfigs) {
						if (datasubscriptionconfigs != null) {
							Datasubscriptionconfig[] arrDatasubscriptionconfig = datasubscriptionconfigs.getDatasubscriptionconfig();
							if (arrDatasubscriptionconfig != null && arrDatasubscriptionconfig.length > 0) {
								_logger.debug("Number of Datasubscriptionconfig :[" + arrDatasubscriptionconfig.length + "]");
								return arrDatasubscriptionconfig;
							}
						}
					}
				} else {
					_logger.error("There is no Datasubscriptionconfigs in given file.");
				}
			} else {
				_logger.info("customAdapterConfig is null!!!!!!!!!");
			}

		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		}
		return null;
	}

	/**
	 * Get nodes against given subscription
	 */
	private Node[] getNodes(CustomAdapterConfig customAdapterConfig, String subScriptionName) {

		try {

			if (customAdapterConfig != null) {

				Datasubscriptionconfigs[] arrDatasubscriptionconfigs = customAdapterConfig.getDatasubscriptionconfigs();
				if (arrDatasubscriptionconfigs != null && arrDatasubscriptionconfigs.length > 0) {
					_logger.debug("Number of arrDatasubscriptionconfigs :[" + arrDatasubscriptionconfigs.length + "]");
					for (Datasubscriptionconfigs datasubscriptionconfigs : arrDatasubscriptionconfigs) {
						if (datasubscriptionconfigs != null) {
							Datasubscriptionconfig[] arrDatasubscriptionconfig = datasubscriptionconfigs.getDatasubscriptionconfig();
							if (arrDatasubscriptionconfig != null && arrDatasubscriptionconfig.length > 0) {
								_logger.debug("Number of Datasubscriptionconfig :[" + arrDatasubscriptionconfig.length + "]");
								for (Datasubscriptionconfig datasubscriptionconfig : arrDatasubscriptionconfig) {
									if (datasubscriptionconfig != null) {
										if (datasubscriptionconfig.getSubscription_name().equalsIgnoreCase(subScriptionName)) {
											return datasubscriptionconfig.getNode();
										}
									}
								}
							}
						}
					}
				} else {
					_logger.error("There is no Datasubscriptionconfigs in given file.");
				}
			} else {
				_logger.info("customAdapterConfig is null!!!!!!!!!");
			}

		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		}
		return null;
	}

	// Put data into data cache.
	/**
	 * @param values
	 *            list of values
	 */
	protected void putData(List<PDataValue> values) {
		for (PDataValue value : values) {
			this.dataValueCache.put(value.getNodeId(), value);
		}
	}
}
