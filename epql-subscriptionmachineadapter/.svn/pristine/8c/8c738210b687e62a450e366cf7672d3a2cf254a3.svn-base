package com.ge.dspmicro.energy.file;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class FileDeleter implements Runnable {

	private static final Logger _logger = LoggerFactory.getLogger(FileDeleter.class);

	private final AtomicBoolean threadRunning = new AtomicBoolean();
	private long deleteIntervalMillis;
	private String archiveDir;

	/**
	 * 
	 * @param updateIntervalMillis
	 * @param archiveDir
	 */
	public FileDeleter(long deleteIntervalMillis, String archiveDir) {

		if (deleteIntervalMillis > 0) {
			this.deleteIntervalMillis = deleteIntervalMillis;
		} else {
			this.deleteIntervalMillis = 60000l * 60 * 24;
		}

		this.archiveDir = archiveDir;

		this.threadRunning.set(false);
		_logger.debug("Object of FileDeleter is created.");

	}

	@Override
	public void run() {

		if (!this.threadRunning.get()) {

			this.threadRunning.set(true);
			while (this.threadRunning.get()) {

				_logger.debug("Files older than one week are going to delete.");
				try {
					if (this.archiveDir != null && !"".equals(this.archiveDir)) {
						UtilFile.deleteOneWeekOldFiles(this.archiveDir);
					} else {
						_logger.error("Archive Directory Path :[" + this.archiveDir + "]");
					}

				} catch (Exception e) {
					_logger.error("Exception :[" + e.getMessage() + "]");
				}

				try {
					// Wait for an updateInterval period before pushing next data update.
					Thread.sleep(this.deleteIntervalMillis);
				} catch (InterruptedException e) {
					_logger.error("InterruptedException :[" + e.getMessage() + "]");
				}
			}
		}
	}

}
