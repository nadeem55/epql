package com.nad.file.xml;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nad.data.buffer.TagsDataBuffer;
import com.nad.epql.calc.auxiliary.AuxilliaryData;
import com.nad.epql.calc.coolingtower.Util;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class SaveData extends Thread {

	private final static Logger log = LoggerFactory.getLogger(SaveData.class);

	/**
	 * 
	 */
	public void run() {
		try {
			while (true) {

				sleep(1000 * 60);

				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues = getValueFromProxyTags(TagsDataBuffer.mapTagIdValues);

				// ----------------To perform calculations and to get calculated tags--------------------------------------------------------------
				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapCWArea = AuxilliaryData.getCWArea(mapTempTagIdValues);
				if (mapCWArea != null) {
					mapTempTagIdValues.putAll(mapCWArea);
				}

				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapGTArea = AuxilliaryData.getGTArea(mapTempTagIdValues);
				if (mapGTArea != null) {
					mapTempTagIdValues.putAll(mapGTArea);
				}

				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapHRSGArea = AuxilliaryData.getHRSGArea(mapTempTagIdValues);
				if (mapHRSGArea != null) {
					mapTempTagIdValues.putAll(mapHRSGArea);
				}

				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapIAPAArea = AuxilliaryData.getIAPAArea(mapTempTagIdValues);
				if (mapIAPAArea != null) {
					mapTempTagIdValues.putAll(mapIAPAArea);
				}

				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapRawWaterArea = AuxilliaryData.getRawWaterArea(mapTempTagIdValues);
				if (mapRawWaterArea != null) {
					mapTempTagIdValues.putAll(mapRawWaterArea);
				}

				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapSTArea = AuxilliaryData.getSTArea(mapTempTagIdValues);
				if (mapSTArea != null) {
					mapTempTagIdValues.putAll(mapSTArea);
				}

				ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTotalAUXLoad = AuxilliaryData.getTotalAUXLoad(mapTempTagIdValues);
				if (mapTotalAUXLoad != null) {
					mapTempTagIdValues.putAll(mapTotalAUXLoad);
				}
				// ----------------------------------------------------------------------------------------------------------------------------------

				mapTempTagIdValues = Util.getTagValues(mapTempTagIdValues);

				if (mapTempTagIdValues != null && mapTempTagIdValues.size() > 0) {
					saveData(mapTempTagIdValues);
				} else {
					log.debug("Getting no data from calculations.");
				}
			}
		} catch (Exception e) {
			log.error("Exception :[" + e.getMessage() + "]");
		}
	}

	/**
	 * Get the data from ConcurrentHashMap against each Tag Id and save into xml
	 * file.
	 */
	private void saveData(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTagIdValues) {

		// System.out.println("Going to save data....");
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("tags");
			doc.appendChild(rootElement);

			for (String tagId : mapTagIdValues.keySet()) {

				// staff elements
				Element staff = doc.createElement("tag");
				rootElement.appendChild(staff);

				// set attribute to staff element
				Attr attr = doc.createAttribute("id");

				tagId = tagId.trim();
				attr.setValue(tagId);
				staff.setAttributeNode(attr);
				ConcurrentHashMap<String, String> mapTemp = TagsDataBuffer.mapTagIdValues.get(tagId);
				if (mapTemp != null) {
					for (String key : mapTemp.keySet()) {
						// firstname elements
						Element firstname = doc.createElement(key);
						firstname.appendChild(doc.createTextNode(mapTemp.get(key)));
						staff.appendChild(firstname);
					}
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();
			// System.out.println(dateFormat.format(date));

			// File file = new File("E:\\xml-data\\OneMin-" +
			// dateFormat.format(date) + ".xml");
			File file = new File("E:\\engro-digital\\energy-predix-machine\\appdata\\nodes_data\\OneMin-" + dateFormat.format(date) + ".xml");

			StreamResult result = new StreamResult(file);

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			log.info(dateFormat.format(date) + " = " + file.getName() + " file is saved!");

		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException :[" + e.getMessage() + "]");
		} catch (TransformerException e) {
			log.error("TransformerException :[" + e.getMessage() + "]");
		} catch (Exception e) {
			log.error("Exception :[" + e.getMessage() + "]");
		}
	}

	/**
	 * Get values of 203TE071.UNIT1@NET0, 203TE072.UNIT1@NET0 tags and get
	 * average of these store the value into 420TE001_UNIT1_NET0 (2017-10-24)
	 * 
	 * @param mapTempTagIdValues
	 * @return
	 */
	private ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getValueFromProxyTags(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues) {

		try {

			double value_203TE071 = Double.parseDouble(mapTempTagIdValues.get("203TE071_UNIT1_NET0").get("value"));
			double value_203TE072 = Double.parseDouble(mapTempTagIdValues.get("203TE072_UNIT1_NET0").get("value"));
			
			double value_420TE001 = (value_203TE071 + value_203TE072) / 2;
			
			ConcurrentHashMap<String, String> mapTemp = mapTempTagIdValues.get("420TE001_UNIT1_NET0");
			if (mapTemp != null) {
				mapTemp.put("value", "" + value_420TE001);
			}
		} catch (Exception e) {
			log.error("Exception :[" + e.getMessage() + "]");
		}
		return mapTempTagIdValues;
	}

}
