package com.ge.dspmicro.main;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.ge.dspmicro.energy.source.data.parse.OneMinXMLDataParser;
import com.ge.dspmicro.energy.xml.data.Tag;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class MainAppOneMin {
	
	public static void main(String[] args) {

		try {
			byte[] byteData = Files.readAllBytes(Paths.get("OneMin-2017-10-11-15-10-08.xml"));
			InputStream inputStream = new ByteArrayInputStream(byteData);
			List<Tag> listSensorData = new OneMinXMLDataParser(0,null).parseData(inputStream);
			//new OneMinXMLDataParser(0,null).getTagsData(mapSensorData,null, null);
			System.out.println("Done");
		} catch (Exception e) {
			System.out.println("Exception : [" + e.getMessage() + "]");
		}
	}

}
