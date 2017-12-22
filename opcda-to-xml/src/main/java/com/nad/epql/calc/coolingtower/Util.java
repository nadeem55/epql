package com.nad.epql.calc.coolingtower;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Administrator
 *
 */
public class Util {

	public static String[] tags = { "TE_WS_UNIT1_NET0", "PT_WS_UNIT1_NET0", "420TE001_UNIT1_NET0", "420PT002_UNIT1_NET0", "420TE002_UNIT1_NET0", "420PT003_UNIT1_NET0",
			"WS_HT_UNIT1_NET0", "420NLS001A_A_UNIT1_NET0", "420NLS001B_A_UNIT1_NET0", "420NLS001C_A_UNIT1_NET0", "420NLS001D_A_UNIT1_NET0", "420NLS001E_A_UNIT1_NET0",
			"203PT026_UNIT1_NET0", "420NLS002A_I_UNIT1_NET0", "420NLS002B_I_UNIT1_NET0", "420NLS002C_I_UNIT1_NET0", "420NLS002D_I_UNIT1_NET0", "AH3_UA_UNIT1_NET0",
			"2G_AI21_UNIT1_NET0", "BLOCK_MW_UNIT1_NET0", "DWATTX_UNIT1_NET0", "DEHAM002_UNIT1_NET0", "202FT005_UNIT1_NET0" };

	public static String[] tagNamesForUI = { "Ambient_Temperature", "Ambient_Pressure_Hpa", "Supply_Temperature", "Supply_Pressure", "Return_Temperature", "Return_Pressure",
			"Humidity", "Pumps_Amperes_A", "Pumps_Amperes_B", "Pumps_Amperes_C", "Pumps_Amperes_D", "Pumps_Amperes_E", "Condenser_Vaccum", "Fan_Amperes_A", "Fan_Amperes_B",
			"Fan_Amperes_C", "Fan_Amperes_D", "Voltage", "Power_Factor", "Gross_Load_MW", "GT_Load_MW", "STG_Load_MW", "Main_Steam_Flow" };

	/***
	 * Nick name as Key & Tag name as Value
	 * 
	 * @return
	 */
	public static HashMap<String, String> getTagMapUI2Tag() {
		HashMap<String, String> tagMap = new HashMap();
		for (int i = 0; i < tags.length; i++) {
			tagMap.put(tagNamesForUI[i], tags[i]);
		}

		return tagMap;
	}

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getTagValues(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> tagValueMap) {

		double ambientTemp, ambientPressure, humidity, supplyTemp, returnTemp, returnPressure;
		double fanAmpereA, fanAmpereB, fanAmpereC, fanAmpereD;
		double pumpAmpereA, pumpAmpereB, pumpAmpereC, pumpAmpereD, pumpAmpereE;
		double voltage, powerFactor;
		double fanFlowA, fanFlowB, fanFlowC, fanFlowD;
		double pumpFlowA, pumpFlowB, pumpFlowC, pumpFlowD, pumpFlowE;
		double currentEfficiency;

		HashMap<String, String> uiMap = getTagMapUI2Tag();

		/* Start of Calculations */
		ambientTemp = Double.parseDouble(tagValueMap.get(uiMap.get("Ambient_Temperature")).get("value"));

		ambientPressure = Double.parseDouble(tagValueMap.get(uiMap.get("Ambient_Pressure_Hpa")).get("value"));
		returnPressure = Double.parseDouble(tagValueMap.get(uiMap.get("Return_Pressure")).get("value"));
		humidity = Double.parseDouble(tagValueMap.get(uiMap.get("Humidity")).get("value"));
		supplyTemp = Double.parseDouble(tagValueMap.get(uiMap.get("Supply_Temperature")).get("value"));
		returnTemp = Double.parseDouble(tagValueMap.get(uiMap.get("Return_Temperature")).get("value"));

		fanAmpereA = Double.parseDouble(tagValueMap.get(uiMap.get("Fan_Amperes_A")).get("value"));
		fanAmpereB = Double.parseDouble(tagValueMap.get(uiMap.get("Fan_Amperes_B")).get("value"));
		fanAmpereC = Double.parseDouble(tagValueMap.get(uiMap.get("Fan_Amperes_C")).get("value"));
		fanAmpereD = Double.parseDouble(tagValueMap.get(uiMap.get("Fan_Amperes_D")).get("value"));

		pumpAmpereA = Double.parseDouble(tagValueMap.get(uiMap.get("Pumps_Amperes_A")).get("value"));
		pumpAmpereB = Double.parseDouble(tagValueMap.get(uiMap.get("Pumps_Amperes_B")).get("value"));
		pumpAmpereC = Double.parseDouble(tagValueMap.get(uiMap.get("Pumps_Amperes_C")).get("value"));
		pumpAmpereD = Double.parseDouble(tagValueMap.get(uiMap.get("Pumps_Amperes_D")).get("value"));
		pumpAmpereE = Double.parseDouble(tagValueMap.get(uiMap.get("Pumps_Amperes_E")).get("value"));

		voltage = Double.parseDouble(tagValueMap.get(uiMap.get("Voltage")).get("value"));
		powerFactor = Double.parseDouble(tagValueMap.get(uiMap.get("Power_Factor")).get("value"));

		String timeStamp = tagValueMap.get(uiMap.get("Main_Steam_Flow")).get("time");

		double ambientPressureKPA = ambientPressure / 10;
		// its in MPA so converting it HPA
		double returnPressureKPA = returnPressure * 1000;
		double humidity100 = humidity / 100;

		WetBulbCalc wetBulbCal = new WetBulbCalc();

		double wetBulbTemp = wetBulbCal.Wetbulb(ambientPressureKPA, ambientTemp, humidity100, "SI");

		double approach = supplyTemp - wetBulbTemp;

		double range = returnTemp - supplyTemp;

		double energyIndex;

		double fanPowerA = fanAmpereA * voltage * powerFactor;
		double fanPowerB = fanAmpereB * voltage * powerFactor;
		double fanPowerC = fanAmpereC * voltage * powerFactor;
		double fanPowerD = fanAmpereD * voltage * powerFactor;

		// Calculate Individual Fan Flows
		// Apply range for fan flow-- if fan flow is greater than 321 set it to 0
		// fanFlow * 10000
		// new Equation :: Fan_Flow_A =
		// 0.00001*Fan_Power_A^3+0.0011*Fan_Power_A^2+0.7464*Fan_Power_A +84.892
		// old equation:: fanFlowA =
		// 0.1755*Math.pow(fanPowerA,2)-50.423*fanPowerA+3797.8;
		fanFlowA = 0.00001 * Math.pow(fanPowerA, 3) + 0.0011 * Math.pow(fanPowerA, 2) + 0.7464 * fanPowerA + 84.892;
		if (fanFlowA > 321 || fanAmpereA < 5)fanFlowA =0;
//		fanFlowA = (fanFlowA < 321) ? fanFlowA : 0;
		fanFlowA *= 10000;

		fanFlowB = 0.00001 * Math.pow(fanPowerB, 3) + 0.0011 * Math.pow(fanPowerB, 2) + 0.7464 * fanPowerB + 84.892;
		if (fanFlowB > 321 || fanAmpereB < 5)fanFlowB =0;
//		fanFlowB = (fanFlowB < 321) ? fanFlowB : 0;
		fanFlowB *= 10000;

		fanFlowC = 0.00001 * Math.pow(fanPowerC, 3) + 0.0011 * Math.pow(fanPowerC, 2) + 0.7464 * fanPowerC + 84.892;
		if (fanFlowC > 321 || fanAmpereC < 5)fanFlowC =0;
//		fanFlowC = (fanFlowC < 321) ? fanFlowC : 0;
		fanFlowC *= 10000;

		fanFlowD = 0.00001 * Math.pow(fanPowerD, 3) + 0.0011 * Math.pow(fanPowerD, 2) + 0.7464 * fanPowerD + 84.892;
		if (fanFlowD > 321 || fanAmpereD < 5)fanFlowD =0;
//		fanFlowD = (fanFlowD < 321) ? fanFlowD : 0;
		fanFlowD *= 10000;

		double pumpPowerA = pumpAmpereA * voltage * powerFactor;
		double pumpPowerB = pumpAmpereB * voltage * powerFactor;
		double pumpPowerC = pumpAmpereC * voltage * powerFactor;
		double pumpPowerD = pumpAmpereD * voltage * powerFactor;
		double pumpPowerE = pumpAmpereE * voltage * powerFactor;

		// Calculate Individual Pump Flows
		// if pump amperes <5 then pump flow should be 0
		// check which flow is 0 from fan and pump 
		pumpFlowA = 0.0103 * Math.pow(pumpPowerA, 2) + 13.266 * pumpPowerA - 648.04;
		if (pumpFlowA < 0 || pumpAmpereA < 5) pumpFlowA = 0;
//		pumpFlowA = (pumpFlowA > 0 ) ? pumpFlowA : 0;
		
		pumpFlowB = 0.0103 * Math.pow(pumpPowerB, 2) + 13.266 * pumpPowerB - 648.04;
		if (pumpFlowB < 0 || pumpAmpereB < 5) pumpFlowB = 0;
//		pumpFlowB = (pumpFlowB > 0) ? pumpFlowB : 0;

		pumpFlowC = 0.0103 * Math.pow(pumpPowerC, 2) + 13.266 * pumpPowerC - 648.04;
		if (pumpFlowC < 0 || pumpAmpereC < 5) pumpFlowC = 0;
//		pumpFlowC = (pumpFlowC > 0) ? pumpFlowC : 0;

		pumpFlowD = 0.0103 * Math.pow(pumpPowerD, 2) + 13.266 * pumpPowerD - 648.04;
		if (pumpFlowD < 0 || pumpAmpereD < 5) pumpFlowD = 0;
//		pumpFlowD = (pumpFlowD > 0) ? pumpFlowD : 0;

		pumpFlowE = 0.0103 * Math.pow(pumpPowerE, 2) + 13.266 * pumpPowerE - 648.04;
		if (pumpFlowE < 0 || pumpAmpereE < 5) pumpFlowE = 0;
//		pumpFlowE = (pumpFlowE > 0) ? pumpFlowE : 0;

		double totalFanFlow = fanFlowA + fanFlowB + fanFlowC + fanFlowD;
		double totalPumpFlow = pumpFlowA + pumpFlowB + pumpFlowC + pumpFlowD + pumpFlowE;

		double totalFanPower = fanPowerA + fanPowerB + fanPowerC + fanPowerD;
		double totalPumpPower = pumpPowerA + pumpPowerB + pumpPowerC + pumpPowerD + pumpPowerE;

		LiquidDensityCalc liquidDensityCalc = new LiquidDensityCalc();
		double liquidDensity = liquidDensityCalc.liquidDensity(returnPressureKPA, returnTemp, "SI");
		double currentPumpFlowKgPerhr = totalPumpFlow * liquidDensity;
		SpecificVolumeCalc specificVolumeCalc = new SpecificVolumeCalc();
		double specificVolume = specificVolumeCalc.specificVolume(ambientPressureKPA, ambientTemp, humidity100, "SI");
		double airDensity = 1 / specificVolume;

		double currentGasFlowKgPerHr = totalFanFlow * airDensity;

		double lgRatio = currentPumpFlowKgPerhr / currentGasFlowKgPerHr;

		energyIndex = (totalFanPower + totalPumpPower) / totalPumpFlow;

		//2017-12-14 (changed - ahmad asif)
		//currentEfficiency = range * totalPumpFlow;
		currentEfficiency = ( range /( range + approach)) * 100;

		/* End of Calculations */

		// String [] calculatedTags =
		// {"Range","Approach","WetBulbTemperature","LgRatio","PumpFlowA","PumpFlowB","PumpFlowC","PumpFlowD","PumpFlowE","FanFlowA","FanFlowB",
		// "FanFlowC","FanFlowD","EnergyIndex","LiquidDensity","AirDensity","Efficiency","TotalFanPower","TotalPumpPower","TotalPower"};
		//
		HashMap<String, Double> calculatedTagMap = new HashMap<String, Double>();
		calculatedTagMap.put("RANGE", range);
		calculatedTagMap.put("APPROACH", approach);
		calculatedTagMap.put("WET_BULB_TEMPERATURE", wetBulbTemp);
		calculatedTagMap.put("LG_RATIO", lgRatio);
		calculatedTagMap.put("PUMP_FLOW_A", pumpFlowA);
		calculatedTagMap.put("PUMP_FLOW_B", pumpFlowB);
		calculatedTagMap.put("PUMP_FLOW_C", pumpFlowC);
		calculatedTagMap.put("PUMP_FLOW_D", pumpFlowD);
		calculatedTagMap.put("PUMP_FLOW_E", pumpFlowE);
		calculatedTagMap.put("FAN_FLOW_A", fanFlowA);
		calculatedTagMap.put("FAN_FLOW_B", fanFlowB);
		calculatedTagMap.put("FAN_FLOW_C", fanFlowC);
		calculatedTagMap.put("FAN_FLOW_D", fanFlowD);
		calculatedTagMap.put("ENERGY_INDEX", energyIndex);
		calculatedTagMap.put("LIQUID_DENSITY", liquidDensity);
		calculatedTagMap.put("AIR_DENSITY", airDensity);
		calculatedTagMap.put("EFFICIENCY", currentEfficiency);
		calculatedTagMap.put("TOTAL_FAN_POWER", totalFanPower);
		calculatedTagMap.put("TOTAL_PUMP_POWER", totalPumpPower);

		double totalPower = totalFanPower + totalPumpPower;
		calculatedTagMap.put("TOTAL_POWER", totalPower);

		for (String tagName : calculatedTagMap.keySet()) {
			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("time", timeStamp);
			tMap.put("value", calculatedTagMap.get(tagName) + "");
			tagValueMap.put(tagName, tMap);
		}

		return tagValueMap;
	}
}
