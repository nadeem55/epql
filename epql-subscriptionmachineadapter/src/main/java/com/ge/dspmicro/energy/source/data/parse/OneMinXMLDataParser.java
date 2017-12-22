package com.ge.dspmicro.energy.source.data.parse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.dspmicro.energy.file.UtilFile;
import com.ge.dspmicro.energy.subscriptionmachineadapter.DataNode;
import com.ge.dspmicro.energy.subscriptionmachineadapter.HubcoDataSubscription;
import com.ge.dspmicro.energy.subscriptionmachineadapter.EnergySubscriptionMachineAdapterImpl;
import com.ge.dspmicro.energy.xml.data.Tag;
import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.dspmicro.machinegateway.types.PEnvelope;
import com.ge.dspmicro.machinegateway.types.PQuality;
import com.ge.dspmicro.machinegateway.types.PTimestamp;
import com.ge.dspmicro.machinegateway.types.PQuality.QualityEnum;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class OneMinXMLDataParser implements Runnable {

	private static final Logger _logger = LoggerFactory.getLogger(OneMinXMLDataParser.class);

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
	private Map<String, Map<String, DataNode>> mapSubcriptionDataNodes = null;
	private String dataSourceDirectory = EnergySubscriptionMachineAdapterImpl.dataSourceDir;
	private String dataArchiveDirectory = EnergySubscriptionMachineAdapterImpl.dataArchiveDir;
	private long updateIntervalMillis;

	private final AtomicBoolean threadRunning = new AtomicBoolean();

	/**
	 * 
	 * @param updateIntervalMillis
	 * @param mapSubcriptionDataNodes
	 */
	public OneMinXMLDataParser(long updateIntervalMillis, Map<String, Map<String, DataNode>> mapSubcriptionDataNodes) {

		if (updateIntervalMillis > 0) {
			this.updateIntervalMillis = updateIntervalMillis;
		} else {
			this.updateIntervalMillis = 60000l;
		}
		this.mapSubcriptionDataNodes = mapSubcriptionDataNodes;
		this.threadRunning.set(false);
		_logger.debug("Object of XMLDataParser is created.");

	}

	@Override
	public void run() {

		if (!this.threadRunning.get()) {

			this.threadRunning.set(true);
			while (this.threadRunning.get()) {

				List<PDataValue> listPDataValue = null;

				try {

					_logger.debug("Path of source dir :[" + this.dataSourceDirectory + "]");
					//Parse each file
					if (this.dataSourceDirectory != null && !"".equals(this.dataSourceDirectory)) {
						int fileCount = 0;
						List<File> listFiles = UtilFile.getXMLFiles(this.dataSourceDirectory, "OneMin");
						if (listFiles != null && !listFiles.isEmpty()) {

							_logger.debug("No. of files in dir :[" + listFiles.size() + "]");
							for (File file : listFiles) {

								try {
									_logger.debug("Path of file :[" + this.dataSourceDirectory + "/" + file.getName() + "]");
									//byte[] byteData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
									byte[] byteData = Files.readAllBytes(Paths.get(this.dataSourceDirectory + "/" + file.getName()));
									if (byteData != null && byteData.length > 0) {
										InputStream inputStream = new ByteArrayInputStream(byteData);
										List<Tag> listSensorData = parseData(inputStream);

										if (listSensorData != null) {

											if (mapSubcriptionDataNodes != null && mapSubcriptionDataNodes.size() > 0) {
												for (String subscription : mapSubcriptionDataNodes.keySet()) {

													_logger.debug("Getting datanodes of subscription :[" + subscription + "]");
													Map<String, DataNode> map_potname_datanode = mapSubcriptionDataNodes.get(subscription);
													if (map_potname_datanode != null && map_potname_datanode.size() > 0) {

														listPDataValue = getTagsData(listSensorData, map_potname_datanode, subscription);

														if (listPDataValue != null && listPDataValue.size() > 0) {

															_logger.debug("Getting subc:[" + subscription + "]");

															if (EnergySubscriptionMachineAdapterImpl.mapDataSubscriptions != null
																	&& !EnergySubscriptionMachineAdapterImpl.mapDataSubscriptions.isEmpty()) {

																UUID uuid = EnergySubscriptionMachineAdapterImpl.mapDataSubscriptions.get(subscription);
																HubcoDataSubscription sourceDataSubscription_2 = (HubcoDataSubscription) EnergySubscriptionMachineAdapterImpl.dataSubscriptions
																		.get(uuid);
																if (sourceDataSubscription_2 != null) {
																	sourceDataSubscription_2.sendData(listPDataValue, subscription);
																}
															}
														}
													}
												}
											}
										}
									} else {
										_logger.debug(file.getName() + " file size is zero.");
										continue;
									}

								} catch (Exception e) {
									_logger.error("Exception :[" + e.getMessage() + "]");
								}

								try {
									// Move the XML file from this folder to archive folder
									String filePathName = this.dataSourceDirectory + "/" + file.getName();
									UtilFile.moveFile(filePathName, this.dataArchiveDirectory);
									fileCount++;

									if (fileCount >= 10) {
										break;
									}
								} catch (Exception e) {
									_logger.error("Exception :[" + e.getMessage() + "]");
								}
							}
						}
					}
				} catch (Exception e) {
					_logger.error("Exception :[" + e.getMessage() + "]");
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
	 * @param inputStream
	 * @return
	 */
	public List<Tag> parseData(InputStream inputStream) {

		try {

			List<Tag> listSensorData = new ArrayList<>();

			Tag currSensor = null;
			String tagContent = null;
			XMLInputFactory factory = XMLInputFactory.newInstance();

			if (inputStream != null) {
				XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
				if (reader != null) {
					while (reader.hasNext()) {
						int event = reader.next();

						switch (event) {
						case XMLStreamConstants.START_ELEMENT:
							if ("tag".equalsIgnoreCase(reader.getLocalName())) {
								currSensor = new Tag();
								currSensor.setNAME(reader.getAttributeValue(0));
							}
							break;

						case XMLStreamConstants.CHARACTERS:
							tagContent = reader.getText().trim();
							break;

						case XMLStreamConstants.END_ELEMENT:

							switch (reader.getLocalName()) {

							case "tag":
								//mapSensorData.put(currSensor, currMap);
								listSensorData.add(currSensor);
								break;
							//Element values	
							case "time":
								currSensor.setTIME(tagContent);
								break;
							case "value":
								currSensor.setVALUE(tagContent);
								break;
							default:
								///currMap.put(reader.getLocalName(), tagContent);
							}
							break;

						case XMLStreamConstants.START_DOCUMENT:
							//mapSensorData = new HashMap<>();
							break;
						}
					}
					return listSensorData;
				}
			}
		} catch (XMLStreamException e) {
			_logger.error("XMLStreamException :[" + e.getMessage() + "]");
		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		}
		return null;
	}

	/**
	 * 
	 * @param root
	 * @param map_potname_datanode
	 * @param subscription
	 * @return
	 */

	public synchronized List<PDataValue> getTagsData(List<Tag> listSensorData, Map<String, DataNode> map_potname_datanode, String subscription) {

		// dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		List<PDataValue> listPDataValue = new ArrayList<PDataValue>();
		int numberOfTags = 0;

		try {

			String tagName = "";
			if (listSensorData != null && listSensorData.size() > 0) {

				for (Tag tag : listSensorData) {

					tagName = tag.getNAME();
					_logger.debug("tagName :[" + tagName + "]");

					if (tagName != null && !"".equals(tagName)) {

						tagName = tagName.trim();
						try {

							PTimestamp ts=null;
							try {
								ts = new PTimestamp(Long.parseLong(tag.getTIME()));
							} catch (Exception e) {
								ts = new PTimestamp(System.currentTimeMillis());
								_logger.error("Exception, sensorName:[" + tagName + "], :[" + e.getMessage() + "], data:[" + tag.getTIME() + "]");
							}
							QualityEnum qe = QualityEnum.GOOD;

							DataNode dataNode = map_potname_datanode.get(tagName);

							if (dataNode == null) {
								_logger.debug("Skipped tag is from uploading to TS:[" + tagName + "]");
								continue;
							}

							if (tag.getVALUE() != null && !"".equals(tag.getVALUE())) {

								_logger.debug("Getting data of tag [" + tagName + "]to send to spillway.");
								PEnvelope envelope = new PEnvelope(tag.getVALUE());

								try {
									PDataValue pdataValue = new PDataValue(dataNode.getNodeId(), envelope);
									pdataValue.setNodeName(dataNode.getName());
									pdataValue.setAddress(dataNode.getAddress());
									pdataValue.setTimestamp(ts);

									PQuality pq = new PQuality(qe);
									pdataValue.setQuality(pq);

									listPDataValue.add(pdataValue);

									numberOfTags += 1;

									_logger.debug("Getting Tag name :[" + pdataValue.getNodeName() + "], time :[" + pdataValue.getTimestamp() + "], value :[" + pdataValue.getValue()
											+ "], Subscription:[" + subscription + "]");

								} catch (Exception e) {
									_logger.error("[getTagsData()] exception: " + e + " skipped " + tagName);
								}
							}

						} catch (Exception e) {
							_logger.error("Exception : [" + e.getMessage() + "]");
						}
					}
				}

				//To calculate one pot data.
				double dataSize = 0l;
				for (PDataValue pdataValue : listPDataValue) {
					byte[] arrByte = pdataValue.toBytes();
					dataSize += arrByte.length;
				}
				_logger.debug("Size of data:[" + (dataSize / 1024) + " kb] of subc :[" + subscription + "], no. of pots:[" + numberOfTags / 54 + "]");
			}

		} catch (Exception e) {
			_logger.error("Exception : [" + e.getMessage() + "]");
		}
		return listPDataValue;
	}

}
