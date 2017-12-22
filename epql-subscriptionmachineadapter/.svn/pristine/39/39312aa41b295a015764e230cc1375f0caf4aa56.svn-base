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
import java.util.Locale;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.dspmicro.energy.subscriptionmachineadapter.DataNode;
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
public class UtilityParser {

	private static final Logger _logger = LoggerFactory.getLogger(UtilityParser.class);

	/**
	 * 
	 * @return
	 */
	public synchronized static List<Map<String, String>> parseXMLFile(File file, String dataSourceDirectory) {

		try {
			if (file != null) {

				_logger.debug("Going to parse file :[" + dataSourceDirectory + "/" + file.getName() + "]");
				byte[] byteData = Files.readAllBytes(Paths.get(dataSourceDirectory + "/" + file.getName()));
				if (byteData != null && byteData.length > 0) {
					_logger.debug("byteData length :[" + byteData.length + "]");
					InputStream inputStream = new ByteArrayInputStream(byteData);
					if (inputStream != null) {
						List<Map<String, String>> listSensorData = parseXMLStream(inputStream);
						return listSensorData;
					}
				}

			}
		} catch (Exception e) {
			_logger.error("Exception : [" + e.getMessage() + "]");
		}

		return null;
	}

	/**
	 * Takes the XML data as Stream and returns the list of
	 * 
	 * @param inputStream
	 * @return
	 */
	public synchronized static List<Map<String, String>> parseXMLStream(InputStream inputStream) {

		try {

			List<Map<String, String>> listSensorData = null;
			Map<String, String> currMap = null;

			String tagContent = null;
			XMLInputFactory factory = XMLInputFactory.newInstance();

			if (inputStream != null) {
				XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
				if (reader != null) {
					while (reader.hasNext()) {

						try {
							int event = reader.next();

							switch (event) {
							case XMLStreamConstants.START_ELEMENT:
								if ("pot".equalsIgnoreCase(reader.getLocalName())) {
									//currSensor = new Tags();
									currMap = new HashMap<>();
								}
								if ("root".equalsIgnoreCase(reader.getLocalName())) {
									listSensorData = new ArrayList<>();
								}
								break;

							case XMLStreamConstants.CHARACTERS:
								tagContent = reader.getText().trim();
								break;

							case XMLStreamConstants.END_ELEMENT:

								switch (reader.getLocalName().toLowerCase()) {

								case "pot":
									listSensorData.add(currMap);
									break;

								default:
									currMap.put(reader.getLocalName(), tagContent);
								}
								break;

							case XMLStreamConstants.START_DOCUMENT:
								listSensorData = new ArrayList<>();
								break;
							}
						} catch (Exception e) {
							_logger.error("Exception :[" + e.getMessage() + "]");
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
	 * @param mapOfTag
	 * @param map_potname_datanode
	 * @param subscription
	 * @return
	 */
	public synchronized static List<PDataValue> getTagsData(Map<String, String> mapOfTag, Map<String, DataNode> map_potname_datanode, String subscription, List<String> listDateFields) {

		// dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String potId = "";
		List<PDataValue> listPDataValue = new ArrayList<PDataValue>();
		int numberOfTags = 0;

		try {

			long dateTimeMillis = System.currentTimeMillis();

			PTimestamp ts = new PTimestamp(dateTimeMillis);
			QualityEnum qe = QualityEnum.GOOD;

			if (mapOfTag != null && mapOfTag.size() > 0) {

				String tagValue = "";
				for (String tagName : mapOfTag.keySet()) {

					tagValue = mapOfTag.get(tagName);
					if (tagValue != null) {
						tagValue = tagValue.trim();
					}

					if (listDateFields.contains(tagName)) {

						try {
							if (tagValue != null) {
								if (!"".equals(tagValue)) {
									DateFormat originalFormat = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH);
									Date originalDate = originalFormat.parse(tagValue);

									DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
									String formattedDate = targetFormat.format(originalDate);

									tagValue = formattedDate;

									_logger.debug("Sending data of Tag date :[" + tagName + "], value :[" + tagValue + "]");
								}
							}
						} catch (Exception e) {
							_logger.error("Exception : [" + e.getMessage() + "]");
							continue;
						}
					}

					DataNode dataNode = map_potname_datanode.get(tagName);

					if (dataNode == null) {
						//_logger.debug("Skipped tag from uploading to Rel DB :[" + tagName + "]");
						continue;
					}

					_logger.debug("Sending data of Tag:[" + tagName + "], value :[" + tagValue + "]");
					PEnvelope envelope = new PEnvelope(tagValue);

					try {
						PDataValue pdataValue = new PDataValue(dataNode.getNodeId(), envelope);
						pdataValue.setNodeName(dataNode.getName());
						pdataValue.setAddress(dataNode.getAddress());
						pdataValue.setTimestamp(ts);

						PQuality pq = new PQuality(qe);
						pdataValue.setQuality(pq);

						listPDataValue.add(pdataValue);

						numberOfTags += 1;

						_logger.debug("Getting Tag name :[" + pdataValue.getNodeName() + "], time :[" + pdataValue.getTimestamp() + "], value :[" + pdataValue.getValue() + "], Subscription:["
								+ subscription + "]");

					} catch (Exception e) {
						_logger.error("[getTagsData()] exception: " + e + " skipped, tagName:[" + tagName + "], value:[" + tagValue + "]");
					}
				}

				double dataSize = 0l;
				for (PDataValue pdataValue : listPDataValue) {
					byte[] arrByte = pdataValue.toBytes();
					dataSize += arrByte.length;
				}
				_logger.debug("Size of data:[" + (dataSize / 1024) + " kb] of subc :[" + subscription + "], no. of pots:[" + numberOfTags / 56 + "]");
			}

		} catch (Exception e) {
			_logger.error("Exception : [" + e.getMessage() + "]");
		}
		return listPDataValue;
	}

	/**
	 * 
	 * @param mapOfTag
	 * @param map_potname_datanode
	 * @param subscription
	 * @param listDateFields
	 * @return
	 */
	public synchronized static List<PDataValue> getTagsData_2(Map<String, String> mapOfTag, Map<String, DataNode> map_potname_datanode, String subscription, List<String> listDateFields) {

		List<PDataValue> listPDataValue = new ArrayList<PDataValue>();

		try {

			long dateTimeMillis = System.currentTimeMillis();

			PTimestamp ts = new PTimestamp(dateTimeMillis);
			QualityEnum qe = QualityEnum.GOOD;

			if (mapOfTag != null && mapOfTag.size() > 0) {

				String tagValue = "";
				for (String tagName : mapOfTag.keySet()) {

					//System.out.println("tagName is :[" + tagName + "]");
					tagValue = mapOfTag.get(tagName);
					if (tagValue != null) {
						tagValue = tagValue.trim();
					}

					if (listDateFields.contains(tagName)) {

						try {
							if (tagValue != null) {
								if (!"".equals(tagValue)) {
									DateFormat originalFormat = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH);
									Date originalDate = originalFormat.parse(tagValue);

									DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
									String formattedDate = targetFormat.format(originalDate);

									tagValue = formattedDate;

									//System.out.println("Sending data of Tag date :[" + tagName + "], value :[" + tagValue + "]");
								}
							}
						} catch (Exception e) {
							System.out.println("Exception : [" + e.getMessage() + "]");
							continue;
						}

					}
					System.out.println("Sending data of Tag:[" + tagName + "], value :[" + tagValue + "]");
				}
			}

		} catch (Exception e) {
			System.out.println("Exception : [" + e.getMessage() + "]");
		}
		return listPDataValue;
	}

}
