package com.nad.epql.calc.auxiliary;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuxilliaryData {

	private final static Logger logger = LoggerFactory.getLogger(AuxilliaryData.class);

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getGTArea(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues) {

		// GT Area
		// Aux Load ((H39+H40+H41+H42)*H20*H18)/1000+(H49*H21*H18)
		// Optimum Load 1*Aux Load

		// H18 = ST Power Factor 2G-AI21.UNIT1@NET0
		// H20 = 415V Level ATS2-UA.UNIT1@NET0
		// H21 = 11kV level AH3-UA.UNIT1@NET0

		// H39 = GT CCW PUMP A = 201NLS058A-I.UNIT1@NET0
		// H40 = GT CCW PUMP B 201NLS058B-I.UNIT1@NET0
		// H41 = GT CW BOOSTER PUMP A 201NLS057A-I.UNIT1@NET0
		// H42 = GT CW BOOSTER PUMP B 201NLS057B-I.UNIT1@NET0

		// H49 = GT Starting Motor AH29-IA.UNIT1@NET0

		String auxLoadName = "GT_AREA_AUX_LOAD";
		String optimumLoadName = "GT_AREA_OPTIMUM_LOAD";
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapGTArea = new ConcurrentHashMap<>();

		try {
			String timeStamp = mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("time");

			double ST_Power_Factor = Double.parseDouble(mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("value"));
			double Level_415V = Double.parseDouble(mapTempTagIdValues.get("ATS2_UA_UNIT1_NET0").get("value"));
			double Level_11KV = Double.parseDouble(mapTempTagIdValues.get("AH3_UA_UNIT1_NET0").get("value"));

			double GT_CCW_PUMP_A = Double.parseDouble(mapTempTagIdValues.get("201NLS058A_I_UNIT1_NET0").get("value"));
			double GT_CCW_PUMP_B = Double.parseDouble(mapTempTagIdValues.get("201NLS058B_I_UNIT1_NET0").get("value"));
			double GT_CW_BOOSTER_PUMP_A = Double.parseDouble(mapTempTagIdValues.get("201NLS057A_I_UNIT1_NET0").get("value"));
			double GT_CW_BOOSTER_PUMP_B = Double.parseDouble(mapTempTagIdValues.get("201NLS057B_I_UNIT1_NET0").get("value"));

			double GT_Starting_Motor = Double.parseDouble(mapTempTagIdValues.get("AH29_IA_UNIT1_NET0").get("value"));

			double AuxLoad = ((GT_CCW_PUMP_A + GT_CCW_PUMP_B + GT_CW_BOOSTER_PUMP_A + GT_CW_BOOSTER_PUMP_B) * Level_415V * ST_Power_Factor) / 1000 + (GT_Starting_Motor * Level_11KV * ST_Power_Factor);

			double OptimumLoad = 1 * AuxLoad;

			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", AuxLoad + "");
			tMap.put("time", timeStamp);
			mapGTArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", OptimumLoad + "");
			tMap.put("time", timeStamp);
			mapGTArea.put(optimumLoadName, tMap);

		} catch (Exception e) {

			logger.error("Exception :[" + e.getMessage() + "]");

			// TODO: which time stamp to be used
			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();

			long timeInMillis = System.currentTimeMillis();
			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapGTArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapGTArea.put(optimumLoadName, tMap);
		}
		return mapGTArea;
	}

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getSTArea(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues) {
		// ST Area
		// Part1 ((H29+H30+H31+H32+H33+H34+H35+H36+H37+H38)*H20*H18)/1000
		// Part2 (H23+H24)*H21*H18
		// Aux Load Part1 + Part2
		// Optimum Load 0.995*Aux Load

		// H18 = ST Power Factor 2G-AI21.UNIT1@NET0
		// H20 = 415V Level ATS2-UA.UNIT1@NET0
		// H21 = 11kV level AH3-UA.UNIT1@NET0

		// H23 = CONDENSATE PUMP A 203NLS021A-I.UNIT1@NET0
		// H24 = CONDENSATE PUMP B 203NLS021B-I.UNIT1@NET0

		// H29 = VACCUM PUMP A 203NLS061A-I.UNIT1@NET0
		// H30 = VACCUM PUMP B 203NLS061B-I.UNIT1@NET0
		// H31 = SHAFT SEALING FAN A 203NLS051-I.UNIT1@NET0
		// H32 = SHAFT SEALING FAN B 203NLS052-I.UNIT1@NET0
		// H33 = ST LUBE OIL AC PUMP 203NLS115-I.UNIT1@NET0
		// H34 = ST LUBE OIL DC PUMP 203NLS114-I.UNIT1@NET0
		// H35 = ST HP STARTUP OIL PUMP 203NLS116-I.UNIT1@NET0
		// H36 = ST TURNING GEAR MOTOR PANCHE-20.UNIT1@NET0
		// H37 = EH OIL PUMP A EHOASUR.UNIT1@NET0
		// H38 = EH OIL PUMP B EHOBSUR.UNIT1@NET0

		String auxLoadName = "ST_AREA_AUX_LOAD";
		String optimumLoadName = "ST_AREA_OPTIMUM_LOAD";
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapSTArea = new ConcurrentHashMap<>();
		try {

			String timeStamp = mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("time");

			double ST_Power_Factor = Double.parseDouble(mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("value"));
			double Level_415V = Double.parseDouble(mapTempTagIdValues.get("ATS2_UA_UNIT1_NET0").get("value"));
			double Level_11KV = Double.parseDouble(mapTempTagIdValues.get("AH3_UA_UNIT1_NET0").get("value"));

			double CONDENSATE_PUMP_A = Double.parseDouble(mapTempTagIdValues.get("203NLS021A_I_UNIT1_NET0").get("value"));
			double CONDENSATE_PUMP_B = Double.parseDouble(mapTempTagIdValues.get("203NLS021B_I_UNIT1_NET0").get("value"));

			double VACCUM_PUMP_A = Double.parseDouble(mapTempTagIdValues.get("203NLS061A_I_UNIT1_NET0").get("value"));
			double VACCUM_PUMP_B = Double.parseDouble(mapTempTagIdValues.get("203NLS061B_I_UNIT1_NET0").get("value"));
			double SHAFT_SEALING_FAN_A = Double.parseDouble(mapTempTagIdValues.get("203NLS051_I_UNIT1_NET0").get("value"));
			double SHAFT_SEALING_FAN_B = Double.parseDouble(mapTempTagIdValues.get("203NLS052_I_UNIT1_NET0").get("value"));

			double ST_LUBE_OIL_AC_PUMP = Double.parseDouble(mapTempTagIdValues.get("203NLS115_I_UNIT1_NET0").get("value"));
			double ST_LUBE_OIL_DC_PUMP = Double.parseDouble(mapTempTagIdValues.get("203NLS114_I_UNIT1_NET0").get("value"));
			double ST_HP_STARTUP_OIL_PUMP = Double.parseDouble(mapTempTagIdValues.get("203NLS116_I_UNIT1_NET0").get("value"));

			double ST_TURNING_GEAR_MOTOR = Double.parseDouble(mapTempTagIdValues.get("PANCHE_20_UNIT1_NET0").get("value"));
			double EH_OIL_PUMP_A = Double.parseDouble(mapTempTagIdValues.get("EHOASUR_UNIT1_NET0").get("value"));
			double EH_OIL_PUMP_B = Double.parseDouble(mapTempTagIdValues.get("EHOBSUR_UNIT1_NET0").get("value"));

			double PART1 = ((VACCUM_PUMP_A + VACCUM_PUMP_B + SHAFT_SEALING_FAN_A + SHAFT_SEALING_FAN_B + ST_LUBE_OIL_AC_PUMP + ST_LUBE_OIL_DC_PUMP + ST_HP_STARTUP_OIL_PUMP + ST_TURNING_GEAR_MOTOR
					+ EH_OIL_PUMP_A + EH_OIL_PUMP_B) * (Level_415V * ST_Power_Factor)) / 1000;

			double PART2 = (CONDENSATE_PUMP_A + CONDENSATE_PUMP_B) * (Level_11KV * ST_Power_Factor);

			double AuxLoad = PART1 + PART2;

			double OptimumLoad = 0.995 * AuxLoad;

			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", AuxLoad + "");
			tMap.put("time", timeStamp);
			mapSTArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", OptimumLoad + "");
			tMap.put("time", timeStamp);
			mapSTArea.put(optimumLoadName, tMap);

		} catch (Exception e) {

			logger.error("Exception :[" + e.getMessage() + "]");
			// TODO: which time stamp to be used
			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			long timeInMillis = System.currentTimeMillis();
			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapSTArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapSTArea.put(optimumLoadName, tMap);
		}
		return mapSTArea;
	}

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getIAPAArea(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues) {

		// IAPA Area
		// Aux Load ((H47+H48)*H20*H18)/1000
		// optimum Load 1*Aux Load

		// H18 = ST Power Factor 2G-AI21.UNIT1@NET0
		// H20 = 415V Level ATS2-UA.UNIT1@NET0

		// H47 = INST. AIR COMPRESSOR A 240HSC01A-I.UNIT1@NET0
		// H48 = INST. AIR COMPRESSOR B 240HSC01B-I.UNIT1@NET0

		String auxLoadName = "IAPA_AREA_AUX_LOAD";
		String optimumLoadName = "IAPA_AREA_OPTIMUM_LOAD";
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapIAPAArea = new ConcurrentHashMap<>();
		try {

			String timeStamp = mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("time");

			double ST_Power_Factor = Double.parseDouble(mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("value"));
			double Level_415V = Double.parseDouble(mapTempTagIdValues.get("ATS2_UA_UNIT1_NET0").get("value"));

			double INST_AIR_COMPRESSOR_A = Double.parseDouble(mapTempTagIdValues.get("240HSC01A_I_UNIT1_NET0").get("value"));
			double INST_AIR_COMPRESSOR_B = Double.parseDouble(mapTempTagIdValues.get("240HSC01B_I_UNIT1_NET0").get("value"));

			double AuxLoad = ((INST_AIR_COMPRESSOR_A + INST_AIR_COMPRESSOR_B) + (Level_415V * ST_Power_Factor));

			double OptimumLoad = 1 * AuxLoad;

			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", AuxLoad + "");
			tMap.put("time", timeStamp);
			mapIAPAArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", OptimumLoad + "");
			tMap.put("time", timeStamp);
			mapIAPAArea.put(optimumLoadName, tMap);

		} catch (Exception e) {

			logger.error("Exception :[" + e.getMessage() + "]");
			// TODO: which time stamp to be used
			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();

			long timeInMillis = System.currentTimeMillis();

			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapIAPAArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapIAPAArea.put(optimumLoadName, tMap);
		}
		return mapIAPAArea;
	}

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getRawWaterArea(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues) {

		// Raw Water Area
		// Aux Load ((H45+H46)*H20*H18)/1000+(H59*H21*H18)
		// optimum Load 1*Aux Load

		// H18 = ST Power Factor 2G-AI21.UNIT1@NET0
		// H20 = 415V Level ATS2-UA.UNIT1@NET0
		// H21 = 11kV level AH3-UA.UNIT1@NET0

		// H45 = RAW WATER PUMP B 410HS02-I.UNIT1@NET0
		// H46 = RAW WATER PUMP A 410HS001-I.UNIT1@NET0
		// H59 = Canal Feeder AH8-IA.UNIT1@NET0

		String auxLoadName = "RAW_WATER_AREA_AUX_LOAD";
		String optimumLoadName = "RAW_WATER_AREA_OPTIMUM_LOAD";
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapRawWaterArea = new ConcurrentHashMap<>();
		try {
			String timeStamp = mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("time");

			double ST_Power_Factor = Double.parseDouble(mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("value"));
//			double Level_415V = Double.parseDouble(mapTempTagIdValues.get("ATS2_UA_UNIT1_NET0").get("value"));
			double Level_11KV = Double.parseDouble(mapTempTagIdValues.get("AH3_UA_UNIT1_NET0").get("value"));
//
//			double RAW_WATER_PUMP_B = Double.parseDouble(mapTempTagIdValues.get("410HS02_I_UNIT1_NET0").get("value"));
//			double RAW_WATER_PUMP_A = Double.parseDouble(mapTempTagIdValues.get("410HS001_I_UNIT1_NET0").get("value"));

			double Canal_Feeder = Double.parseDouble(mapTempTagIdValues.get("AH8_IA_UNIT1_NET0").get("value"));

			// double AuxLoad = ((((RAW_WATER_PUMP_B + RAW_WATER_PUMP_A) *
			// Level_415V * ST_Power_Factor) / 1000) + (Canal_Feeder *
			// Level_11KV * ST_Power_Factor));
			// H59*H21*H18 formula (2017-10-20)
			double AuxLoad = Canal_Feeder * Level_11KV * ST_Power_Factor;

			double OptimumLoad = 1 * AuxLoad;

			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", AuxLoad + "");
			tMap.put("time", timeStamp);
			mapRawWaterArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", OptimumLoad + "");
			tMap.put("time", timeStamp);
			mapRawWaterArea.put(optimumLoadName, tMap);

		} catch (Exception e) {

			logger.error("Exception :[" + e.getMessage() + "]");
			// TODO: which time stamp to be used
			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();

			long timeInMillis = System.currentTimeMillis();

			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapRawWaterArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapRawWaterArea.put(optimumLoadName, tMap);
		}
		return mapRawWaterArea;
	}

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getHRSGArea(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues) {

		// HRSG Area
		// Aux Load ((H27+H28)*H20*H18)/1000+((H25+H26)*H21*H18)
		// optimum Load 1*Aux Load

		// H18 = ST Power Factor 2G-AI21.UNIT1@NET0
		// H20 = 415V Level ATS2-UA.UNIT1@NET0
		// H21 = 11kV level AH3-UA.UNIT1@NET0

		// H25 = HPFWP A 202NLS204A-I.UNIT1@NET0
		// H26 = HPFWP B 202NLS204B-I.UNIT1@NET0

		// H27 = LPFWP A 202NLS205A-I.UNIT1@NET0
		// H28 = LPFWP B 202NLS205B-I.UNIT1@NET0

		String auxLoadName = "HRSG_AREA_AUX_LOAD";
		String optimumLoadName = "HRSG_AREA_OPTIMUM_LOAD";
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapHRSGArea = new ConcurrentHashMap<>();
		try {

			String timeStamp = mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("time");

			double ST_Power_Factor = Double.parseDouble(mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("value"));
			double Level_415V = Double.parseDouble(mapTempTagIdValues.get("ATS2_UA_UNIT1_NET0").get("value"));
			double Level_11KV = Double.parseDouble(mapTempTagIdValues.get("AH3_UA_UNIT1_NET0").get("value"));

			double HPFWP_A = Double.parseDouble(mapTempTagIdValues.get("202NLS204A_I_UNIT1_NET0").get("value"));
			double HPFWP_B = Double.parseDouble(mapTempTagIdValues.get("202NLS204B_I_UNIT1_NET0").get("value"));

			double LPFWP_A = Double.parseDouble(mapTempTagIdValues.get("202NLS205A_I_UNIT1_NET0").get("value"));
			double LPFWP_B = Double.parseDouble(mapTempTagIdValues.get("202NLS205B_I_UNIT1_NET0").get("value"));

			double AuxLoad = ((((LPFWP_A + LPFWP_B) * Level_415V * ST_Power_Factor) / 1000) + ((HPFWP_A + HPFWP_B) * Level_11KV * ST_Power_Factor));

			double OptimumLoad = 1 * AuxLoad;

			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", AuxLoad + "");
			tMap.put("time", timeStamp);
			mapHRSGArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", OptimumLoad + "");
			tMap.put("time", timeStamp);
			mapHRSGArea.put(optimumLoadName, tMap);

		} catch (Exception e) {

			logger.error("Exception :[" + e.getMessage() + "]");
			// TODO: which time stamp to be used
			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();

			long timeInMillis = System.currentTimeMillis();

			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapHRSGArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapHRSGArea.put(optimumLoadName, tMap);
		}
		return mapHRSGArea;
	}

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getCWArea(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues) {

		// CW Area
		// Aux Load
		// ((H43+H44)*H20*H18)/1000+((H50+H51+H52+H53+H54+H55+H56+H57+H58)*H21*H18)
		// optimum Load 0.994*Aux Load

		// H18 = ST Power Factor 2G-AI21.UNIT1@NET0
		// H20 = 415V Level ATS2-UA.UNIT1@NET0
		// H21 = 11kV level AH3-UA.UNIT1@NET0

		// H43 = CW SIDE STREAM PUMP A 420NLS003A-A.UNIT1@NET0
		// H44 = CW SIDE STREAM PUMP B 420NLS003B-A.UNIT1@NET0
		//
		// H50 = CT FAN - A 420NLS002A-I.UNIT1@NET0
		// H51 = CT FAN - B 420NLS002B-I.UNIT1@NET0
		// H52 = CT FAN - C 420NLS002C-I.UNIT1@NET0
		// H53 = CT FAN - D 420NLS002D-I.UNIT1@NET0
		// H54 = CW PUMP - A 420NLS001A-A.UNIT1@NET0
		// H55 = CW PUMP - B 420NLS001B-A.UNIT1@NET0
		// H56 = CW PUMP - C 420NLS001C-A.UNIT1@NET0
		// H57 = CW PUMP - D 420NLS001D-A.UNIT1@NET0
		// H58 = CW PUMP - E 420NLS001E-A.UNIT1@NET0

		String auxLoadName = "CW_AREA_AUX_LOAD";
		String optimumLoadName = "CW_AREA_OPTIMUM_LOAD";
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapCWArea = new ConcurrentHashMap<>();
		try {

			String timeStamp = mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("time");

			double ST_Power_Factor = Double.parseDouble(mapTempTagIdValues.get("2G_AI21_UNIT1_NET0").get("value"));
			double Level_415V = Double.parseDouble(mapTempTagIdValues.get("ATS2_UA_UNIT1_NET0").get("value"));
			double Level_11KV = Double.parseDouble(mapTempTagIdValues.get("AH3_UA_UNIT1_NET0").get("value"));

			double CW_SIDE_STREAM_PUMP_A = Double.parseDouble(mapTempTagIdValues.get("420NLS003A_A_UNIT1_NET0").get("value"));
			double CW_SIDE_STREAM_PUMP_B = Double.parseDouble(mapTempTagIdValues.get("420NLS003B_A_UNIT1_NET0").get("value"));
			double CT_FAN_A = Double.parseDouble(mapTempTagIdValues.get("420NLS002A_I_UNIT1_NET0").get("value"));
			double CT_FAN_B = Double.parseDouble(mapTempTagIdValues.get("420NLS002B_I_UNIT1_NET0").get("value"));
			double CT_FAN_C = Double.parseDouble(mapTempTagIdValues.get("420NLS002C_I_UNIT1_NET0").get("value"));
			double CT_FAN_D = Double.parseDouble(mapTempTagIdValues.get("420NLS002D_I_UNIT1_NET0").get("value"));
			double CT_PUMP_A = Double.parseDouble(mapTempTagIdValues.get("420NLS001A_A_UNIT1_NET0").get("value"));
			double CT_PUMP_B = Double.parseDouble(mapTempTagIdValues.get("420NLS001B_A_UNIT1_NET0").get("value"));
			double CT_PUMP_C = Double.parseDouble(mapTempTagIdValues.get("420NLS001C_A_UNIT1_NET0").get("value"));
			double CT_PUMP_D = Double.parseDouble(mapTempTagIdValues.get("420NLS001D_A_UNIT1_NET0").get("value"));
			double CT_PUMP_E = Double.parseDouble(mapTempTagIdValues.get("420NLS001E_A_UNIT1_NET0").get("value"));

			double AuxLoad = ((CW_SIDE_STREAM_PUMP_A + CW_SIDE_STREAM_PUMP_B) * Level_415V * ST_Power_Factor) / 1000
					+ ((CT_FAN_A + CT_FAN_B + CT_FAN_C + CT_FAN_D + CT_PUMP_A + CT_PUMP_B + CT_PUMP_C + CT_PUMP_D + CT_PUMP_E) * Level_11KV * ST_Power_Factor);

			double OptimumLoad = 0.994 * AuxLoad;

			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", AuxLoad + "");
			tMap.put("time", timeStamp);
			mapCWArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", OptimumLoad + "");
			tMap.put("time", timeStamp);
			mapCWArea.put(optimumLoadName, tMap);

		} catch (Exception e) {

			logger.error("Exception :[" + e.getMessage() + "]");
			// TODO: which time stamp to be used
			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();

			long timeInMillis = System.currentTimeMillis();

			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapCWArea.put(auxLoadName, tMap);

			tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", "NA");
			tMap.put("time", "" + timeInMillis);
			mapCWArea.put(optimumLoadName, tMap);
		}
		return mapCWArea;
	}

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getTotalAUXLoad(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapTempTagIdValues) {

		// Auxiliriaries Load Total
		// I15 2G-AI23.UNIT1@NET0
		// H15 AH05-P.UNIT1@NET0

		String auxLoadName = "AUX_LOAD_TOTAL";
		ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapAuxLoad = new ConcurrentHashMap<>();
		try {
			double auxLoad1 = Double.parseDouble(mapTempTagIdValues.get("AH05_P_UNIT1_NET0").get("value"));
			double auxLoad2 = Double.parseDouble(mapTempTagIdValues.get("AH06_P_UNIT1_NET0").get("value"));

			double AuxLoad = auxLoad1 + auxLoad2;

			String timeStamp = mapTempTagIdValues.get("AH05_P_UNIT1_NET0").get("time");

			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", AuxLoad + "");
			tMap.put("time", timeStamp);
			mapAuxLoad.put(auxLoadName, tMap);

		} catch (Exception e) {
			System.out.println("Exception :[" + e.getMessage() + "]");
			long timeInMillis = System.currentTimeMillis();
			ConcurrentHashMap<String, String> tMap = new ConcurrentHashMap<String, String>();
			tMap.put("value", "NA");
			tMap.put("time", timeInMillis + "");
			mapAuxLoad.put(auxLoadName, tMap);

		}
		return mapAuxLoad;
	}

}
