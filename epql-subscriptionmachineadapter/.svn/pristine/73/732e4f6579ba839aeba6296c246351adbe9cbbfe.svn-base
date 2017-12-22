package com.ge.dspmicro.main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ge.dspmicro.energy.source.data.parse.OneMinXMLDataParser;
import com.ge.dspmicro.energy.xml.data.Tag;

public class TestMain {

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {

		try {
			float value = 55.5f;
			int io = (int)value;
			//int a = Integer.parseInt(value);
			System.out.println("Value is :["+ io +"]");
			//new TestMain().moveFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void getFiles() throws IOException {

		DirectoryStream<Path> stream = null;
		List<File> listFiles = new ArrayList<>();
		try {

			String filePath = "D:\\AOGETL";
			Path dir = FileSystems.getDefault().getPath(filePath);
			stream = Files.newDirectoryStream(dir);
			for (Path path : stream) {
				if (path.getFileName().toString().contains(".xml")) {
					listFiles.add(path.getFileName().toFile());
					System.out.println(path.getFileName());
				}

			}

		} catch (NotDirectoryException e) {
			System.out.println("NotDirectoryException :[" + e.getMessage() + "]");
		} catch (IOException e) {
			System.out.println("IOException :[" + e.getMessage() + "]");
		} catch (Exception e) {
			System.out.println("Exception :[" + e.getMessage() + "]");
		} finally {
			stream.close();
		}
	}

	/**
	 * 
	 */
	private void moveFile() {

		try {

			File afile = new File("D:\\predix-machine-containers\\predixmachine-17.1.0\\predix-machine-debugger\\appdata\\nodes_data\\20170505_093751.xml");
			String archiveDir = "D:\\predix-machine-containers\\predixmachine-17.1.0\\predix-machine-debugger\\appdata\\nodes_data\\\\archive\\";

			//Check either the file already exists at destination or not
			File oldFile = new File(archiveDir + "/" + afile.getName());
			if (oldFile.exists()) {
				oldFile.delete();
				System.out.println("File :[" + afile.getName() + "] is deleted successfully!");
			}

			if (afile.renameTo(new File(archiveDir + afile.getName()))) {
				System.out.println("File is moved successful!");
			} else {
				System.out.println("File is failed to move!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void moveFile_2() {

		InputStream inStream = null;
		OutputStream outStream = null;

		try {

			File afile = new File("D:\\predix-machine-containers\\predixmachine-17.1.0\\predix-machine-debugger\\appdata\\nodes_data\\data.csv");
			File bfile = new File("D:\\predix-machine-containers\\predixmachine-17.1.0\\predix-machine-debugger\\appdata\\nodes_data\\ArchiveDir\\");

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {

				outStream.write(buffer, 0, length);

			}

			inStream.close();
			outStream.close();

			//delete the original file
			afile.delete();

			System.out.println("File is copied successful!");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	private float getRandomNumber() {

		float randomNumber = 0.0f;
		try {

			float min = 108.0f;
			float max = 116.60f;
			Random random = new Random();
			randomNumber = random.nextInt((int) (max + 1 - min)) + min;

			return randomNumber;

		} catch (Exception e) {

		}
		return randomNumber;
	}
}
