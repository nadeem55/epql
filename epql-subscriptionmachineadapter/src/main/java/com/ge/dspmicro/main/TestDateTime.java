package com.ge.dspmicro.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDateTime {

	public static void main(String[] args) {

		String strDate = "16-JUN-17";
		String strTime = "12:46:00";

		try {
			DateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
			String strDateTime = strDate + " " + strTime;
			Date objDate = dateFormatter.parse(strDateTime);
			long dateTimeMillis = objDate.getTime();

			System.out.println("Time is :[" + dateTimeMillis + "]");

		} catch (Exception e) {
			System.out.println("Exception:[" + e.getMessage() + "]");
		}

	}

}
