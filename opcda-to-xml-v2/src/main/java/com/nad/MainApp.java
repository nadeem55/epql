package com.nad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nad.file.xml.SaveData;
import com.nad.opcda.OPCDAClient;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class MainApp {

	private final static Logger logger = LoggerFactory.getLogger(MainApp.class);

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			String domainName = "ENGRO";
			String hostName = "DROP250";
			String progId = "Ovation.OPCDAServer.1";
			String userName = "Administrator";
			String userPassword = "wdpf";

			// Starting the thread to save the data to XML files.
			SaveData saveData = new SaveData();
			saveData.start();

			// Make connection with OPCDA server, get the data of specifed tags
			// and keep in data buffer.
			OPCDAClient opcdaClient = new OPCDAClient(domainName, hostName, progId, userName, userPassword);
			opcdaClient.getData();

		} catch (Exception e) {
			logger.error("Exception :[" + e.getMessage() + "]");
		}
	}

}
