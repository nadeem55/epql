package com.nad.opcda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nad.data.buffer.TagsDataBuffer;

/**
 * Make connection with OPCDA server, get the data of specified tags and keep in
 * data buffer.
 * 
 * @author Nadeem.Ahmed
 *
 */
public class OPCDAClient {

	private final static Logger logger = LoggerFactory.getLogger(OPCDAClient.class);

	private String domainName;
	private String hostName;
	private String progId;
	private String userName;
	private String userPassword;

	/**
	 * 
	 * @param domainName
	 * @param hostName
	 * @param progId
	 * @param userName
	 * @param userPassword
	 */
	public OPCDAClient(String domainName, String hostName, String progId, String userName, String userPassword) {
		super();
		this.domainName = domainName;
		this.hostName = hostName;
		this.progId = progId;
		this.userName = userName;
		this.userPassword = userPassword;
	}

	/**
	 * Connects with OPCDA server with provided credentials, read the data of
	 * specific TagIds (provided in properties file). After getting the data put
	 * the data into data buffer.
	 */
	public void getData() {

		try {

			// create connection information
			final ConnectionInformation ci = new ConnectionInformation();
			ci.setDomain(this.domainName);
			ci.setHost(this.hostName);
			ci.setProgId(this.progId);
			ci.setUser(this.userName);
			ci.setPassword(this.userPassword);

			// create a new server
			final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

			try {
				final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

				// connect to server
				server.connect();

				// add sync access, poll every minute
				final AccessBase access = new SyncAccess(server, 1000 * 60);

				for (String tagId : TagsDataBuffer.mapMachineTagToCloudTag.keySet()) {

					access.addItem(tagId, new DataCallback() {

						// @Override
						public void changed(Item item, ItemState state) {
							try {

								logger.debug(dateFormat.format(new Date()) + " = " + item.getId() + " - " + state.getTimestamp().getTimeInMillis() + " - " + state.getValue().getObjectAsFloat());

								String plantTagId = item.getId();
								if (TagsDataBuffer.mapMachineTagToCloudTag.containsKey(plantTagId)) {
									String predixCloudId = TagsDataBuffer.mapMachineTagToCloudTag.get(plantTagId);
									if (predixCloudId != null && !"".equals(predixCloudId)) {
										ConcurrentHashMap<String, String> mapTemp = TagsDataBuffer.mapTagIdValues.get(predixCloudId);

										if (mapTemp != null) {
											mapTemp.put("time", "" + state.getTimestamp().getTimeInMillis());
											mapTemp.put("value", "" + state.getValue().getObjectAsFloat());

										} else {
											mapTemp = new ConcurrentHashMap<String, String>();
											mapTemp.put("time", "" + state.getTimestamp().getTimeInMillis());
											mapTemp.put("value", "" + state.getValue().getObjectAsFloat());
											TagsDataBuffer.mapTagIdValues.put(predixCloudId, mapTemp);
										}
									}
								}
							} catch (Exception e) {
								logger.error("Exception :[" + e.getMessage() + "]");
							}
						}
					});
				}

				// start reading
				access.bind();

				// wait a little bit
				// 60,000 (1min) 36,00,000 (1h) 36,00,000 * 24 (24h)
				while (true) {
					Thread.sleep(1000 * 60 * 60);
				}

				// stop reading
				// access.unbind();

			} catch (JIException e) {
				logger.error(String.format("JIException %08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
			}
		} catch (Exception e) {
			logger.error("Exception :[" + e.getMessage() + "]");
		}

	}

}
