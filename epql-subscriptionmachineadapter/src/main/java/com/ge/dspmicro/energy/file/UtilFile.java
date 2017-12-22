package com.ge.dspmicro.energy.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Nadeem.Ahmed
 *
 */
public class UtilFile {

	private static final Logger _logger = LoggerFactory.getLogger(UtilFile.class);

	/**
	 * Get all the XML files from given source directory.
	 * 
	 * @param sourceDir
	 * @return
	 * @throws IOException
	 */
	public synchronized static List<File> getXMLFiles(String sourceDir) throws IOException {

		DirectoryStream<Path> stream = null;
		List<File> listFiles = new ArrayList<>();
		try {

			_logger.debug("Source dir name :[" + sourceDir + "]");
			Path dir = FileSystems.getDefault().getPath(sourceDir);
			stream = Files.newDirectoryStream(dir);
			for (Path path : stream) {
				if (path.getFileName().toString().contains(".xml")) {
					listFiles.add(path.getFileName().toFile());
					_logger.debug("source file name :[" + path.getFileName() + "]");
				}
			}
		} catch (NotDirectoryException e) {
			_logger.error("NotDirectoryException :[" + e.getMessage() + "]");
		} catch (IOException e) {
			_logger.error("IOException :[" + e.getMessage() + "]");
		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		} finally {
			stream.close();
		}
		return listFiles;
	}

	/**
	 * Get all the files from given source directory containing the keyword
	 * OneMin or OneDay
	 * 
	 * @param sourceDir
	 * @return
	 * @throws IOException
	 */
	public synchronized static List<File> getXMLFiles(String sourceDir, String fileType) throws IOException {

		DirectoryStream<Path> stream = null;
		List<File> listFiles = new ArrayList<>();
		try {

			_logger.debug("Source dir name :[" + sourceDir + "]");
			Path dir = FileSystems.getDefault().getPath(sourceDir);
			stream = Files.newDirectoryStream(dir);
			for (Path path : stream) {
				if (path.getFileName().toString().contains(fileType)) {
					listFiles.add(path.getFileName().toFile());
					_logger.debug("source file name :[" + path.getFileName() + "]");
				}
			}
		} catch (NotDirectoryException e) {
			_logger.error("NotDirectoryException :[" + e.getMessage() + "]");
		} catch (IOException e) {
			_logger.error("IOException :[" + e.getMessage() + "]");
		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		} finally {
			stream.close();
		}
		return listFiles;
	}

	/**
	 * Move the file from source directory to destination directory. If the file
	 * already extists at destination directory then deletes the old file and
	 * place the new file.
	 * 
	 * @param file
	 * @param archiveDir
	 * @return
	 */
	public synchronized static boolean moveFile(String fileName, String destinationDir) {

		try {

			_logger.debug("file to move :[" + fileName + "]");
			File file = new File(fileName);

			//Check either the file already exists at destination or not, if exists then delete it.
			File oldFile = new File(destinationDir + "/" + file.getName());
			if (oldFile.exists()) {
				oldFile.delete();
				_logger.debug("File :[" + file.getName() + "] is deleted successfully!");
			}

			if (file.renameTo(new File(destinationDir + "/" + file.getName()))) {
				_logger.debug("File :[" + file.getName() + "] is moved successfully!");
				return true;
			} else {
				_logger.debug("File :[" + file.getName() + "] is failed to move!");
			}
		} catch (SecurityException e) {
			_logger.error("SecurityException :[" + e.getMessage() + "]");
		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
		}
		return false;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public static synchronized boolean deleteOneWeekOldFiles(String sourceDir) throws IOException {

		DirectoryStream<Path> stream = null;
		File file = null;
		try {

			long currentTime = System.currentTimeMillis();
			_logger.debug("Source dir name :[" + sourceDir + "]");
			Path dir = FileSystems.getDefault().getPath(sourceDir);
			stream = Files.newDirectoryStream(dir);
			for (Path path : stream) {
				if (path.getFileName().toString().contains(".xml")) {
					file = new File(sourceDir + "/" + path.getFileName().toString());
					if (file != null) {
						try {
							long timeDifference = currentTime - file.lastModified();
							if (timeDifference > TimeUnit.DAYS.toMillis(8)) {
								file.delete();
								_logger.debug("File name :[" + sourceDir + "/" + file.getName() + "] is deleted.");
							}
						} catch (Exception e) {
							_logger.error("Exception :[" + e.getMessage() + "]");
						}

					}
				}
			}
		} catch (NotDirectoryException e) {
			_logger.error("NotDirectoryException :[" + e.getMessage() + "]");
			return false;
		} catch (IOException e) {
			_logger.error("IOException :[" + e.getMessage() + "]");
			return false;
		} catch (Exception e) {
			_logger.error("Exception :[" + e.getMessage() + "]");
			return false;
		} finally {
			stream.close();
		}

		return true;
	}

	
}
