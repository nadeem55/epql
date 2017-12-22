package com.nad.epql.calc.coolingtower;

public class LiquidDensityCalc {

	public double liquidDensity(double P, double T, String Units) {
		return rho_pT_H2O(P, T, Units);
	}

	double rho_pT_H2O(double P, double T, String Units) {
		if (Units.equals("ENG")) {
			P = fromENGtoSI_p(P);
			T = fromENGtoSI_T(T);
		}

		double rho_pT_H2O = 1 / v_pT_H2O(P, T, Units);

		if (Units.equals("ENG"))
			rho_pT_H2O = fromSItoENG_rho(rho_pT_H2O);
		return rho_pT_H2O;

	}

	double v_pT_H2O(double P, double T, String Units) {
		if (Units.equals("ENG")) {
			P = fromENGtoSI_p(P);
			T = fromENGtoSI_T(T);
		}

		P = P / 100;
		P = toSIunit_p(P);
		T = toSIunit_T(T);
		double v_pT_H2O = 0;
		int tmp = region_pT(P, T);
		switch (tmp) {
		case 1:
			v_pT_H2O = fromSIunit_v(v1_pT(P, T));
			break;
		case 2:
			v_pT_H2O = fromSIunit_v(v2_pT(P, T));
			break;
		case 3:
			v_pT_H2O = fromSIunit_v(v3_ph(P, h3_pT(P, T)));
			break;
		case 4:
			System.out.println("Invalid Number of Arguments");
			break;
		case 5:
			v_pT_H2O = fromSIunit_v(v5_pT(P, T));
			break;
		default:
			System.out.println("Invalid Number of Arguments");
			break;
		}

		if (Units.equals("ENG")) {
			v_pT_H2O = fromSItoENG_v(v_pT_H2O);
		}
		return v_pT_H2O;
	}

	int region_pT(double P, double T) {
		// %Dim ps As Double
		int region_pT;
		double ps;
		if ((T > 1073.15) && (P < 10) && (T < 2273.15) && (P > 0.000611)) {
			region_pT = 5;
		} else if ((T <= 1073.15) && (T > 273.15) && (P <= 100) && (P > 0.000611)) {
			if (T > 623.15) {
				if (P > B23p_T(T)) {
					region_pT = 3;
					if (T < 647.096) {
						ps = p4_T(T);
						if (Math.abs(P - ps) < 0.00001)
							region_pT = 4;
					}
				} else
					region_pT = 2;
			} else {
				ps = p4_T(T);
				if (Math.abs(P - ps) < 0.00001)
					region_pT = 4;
				else if (P > ps)
					region_pT = 1;
				else
					region_pT = 2;
			}
		} else {
			region_pT = 0;
		} // %'**Error, Outside valid area
		return region_pT;
	}

	double B23p_T(double T) {
		/*
		 * %'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		 * Properties of Water and Steam %'1997 %'Section 4 Auxiliary Equation for the
		 * Boundary between Regions 2 and 3 %'Eq 5, Page 5
		 */
		return 348.05185628969 - 1.1671859879975 * T + Math.pow((1.0192970039326E-03 * T), 2);
	}

	double p4_T(double T) {
		/*
		 * % 'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		 * Properties of Water and Steam, September 1997 % 'Section 8.1 The
		 * Saturation-Pressure Equation % 'Eq 30, Page 33 % Dim teta, a, b, c As Double
		 */
		double teta = T - 0.23855557567849 / (T - 650.17534844798);
		double a = Math.pow(teta, 2) + 1167.0521452767 * teta - 724213.16703206;
		double b = -17.073846940092 * Math.pow(teta, 2) + 12020.82470247 * teta - 3232555.0322333;
		double c = 14.91510861353 * Math.pow(teta, 2) - 4823.2657361591 * teta + 405113.40542057;
		return Math.pow((2 * c / (-b + Math.pow((Math.pow(b, 2) - 4 * a * c), 0.5))), 4);
	}

	double v1_pT(double P, double T) {
		/*
		 * %'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		 * Properties of Water and Steam, September 1997 %'5 Equations for Region 1,
		 * Section. 5.1 Basic Equation %'Eqution 7, Table 3, Page 6 % Dim i As Integer
		 * %Dim ps, tau, g_p As Double %Dim I1, J1, n1 As Variant
		 */
		double R = 0.461526; // %'kJ/(kg K)
		int[] I1 = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 8, 8, 21, 23, 29, 30, 31, 32 };
		int[] J1 = { -2, -1, 0, 1, 2, 3, 4, 5, -9, -7, -1, 0, 1, 3, -3, 0, 1, 3, 17, -4, 0, 6, -5, -2, 10, -8, -11, -6, -29, -31, -38, -39, -40, -41 };
		double[] n1 = { 0.14632971213167, -0.84548187169114, -3.756360367204, 3.3855169168385, -0.95791963387872, 0.15772038513228, -0.016616417199501, 8.1214629983568E-04,
				2.8319080123804E-04, -6.0706301565874E-04, -0.018990068218419, -0.032529748770505, -0.021841717175414, -5.283835796993E-05, -4.7184321073267E-04,
				-3.0001780793026E-04, 4.7661393906987E-05, -4.4141845330846E-06, -7.2694996297594E-16, -3.1679644845054E-05, -2.8270797985312E-06, -8.5205128120103E-10,
				-2.2425281908E-06, -6.5171222895601E-07, -1.4341729937924E-13, -4.0516996860117E-07, -1.2734301741641E-09, -1.7424871230634E-10, -6.8762131295531E-19,
				1.4478307828521E-20, 2.6335781662795E-23, -1.1947622640071E-23, 1.8228094581404E-24, -9.3537087292458E-26 };
		double ps = P / 16.53;
		double tau = 1386 / T;
		double g_p = 0;
		for (int i = 0; i < 34; i++) {
			g_p = g_p - n1[i] * I1[i] * Math.pow((7.1 - ps), (I1[i] - 1)) * Math.pow((tau - 1.222), J1[i]);
		}
		return R * T / P * ps * g_p / 1000;
	}

	double fromSIunit_v(double Ins) {
		return Ins;
	}

	double v2_pT(double P, double T) {
		/*
		 * %'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		 * Properties of Water and Steam, September 1997 %'6 Equations for Region 2,
		 * Section. 6.1 Basic Equation %'Table 11 and 12, Page 14 and 15 %Dim i As
		 * Integer %Dim tau, g0_pi, gr_pi As Double % Dim Ir, Jr, nr, J0, n0 As Variant
		 */
		double R = 0.461526; // %'kJ/(kg K)
		int[] J0 = { 0, 1, -5, -4, -3, -2, -1, 2, 3 };
		double[] n0 = { -9.6927686500217, 10.086655968018, -0.005608791128302, 0.071452738081455, -0.40710498223928, 1.4240819171444, -4.383951131945, -0.28408632460772,
				0.021268463753307 };
		int[] Ir = { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 5, 6, 6, 6, 7, 7, 7, 8, 8, 9, 10, 10, 10, 16, 16, 18, 20, 20, 20, 21, 22, 23, 24, 24, 24 };
		int[] Jr = { 0, 1, 2, 3, 6, 1, 2, 4, 7, 36, 0, 1, 3, 6, 35, 1, 2, 3, 7, 3, 16, 35, 0, 11, 25, 8, 36, 13, 4, 10, 14, 29, 50, 57, 20, 35, 48, 21, 53, 39, 26, 40, 58 };
		double[] nr = { -1.7731742473213E-03, -0.017834862292358, -0.045996013696365, -0.057581259083432, -0.05032527872793, -3.3032641670203E-05, -1.8948987516315E-04,
				-3.9392777243355E-03, -0.043797295650573, -2.6674547914087E-05, 2.0481737692309E-08, 4.3870667284435E-07, -3.227767723857E-05, -1.5033924542148E-03,
				-0.040668253562649, -7.8847309559367E-10, 1.2790717852285E-08, 4.8225372718507E-07, 2.2922076337661E-06, -1.6714766451061E-11, -2.1171472321355E-03,
				-23.895741934104, -5.905956432427E-18, -1.2621808899101E-06, -0.038946842435739, 1.1256211360459E-11, -8.2311340897998, 1.9809712802088E-08, 1.0406965210174E-19,
				-1.0234747095929E-13, -1.0018179379511E-09, -8.0882908646985E-11, 0.10693031879409, -0.33662250574171, 8.9185845355421E-25, 3.0629316876232E-13,
				-4.2002467698208E-06, -5.9056029685639E-26, 3.7826947613457E-06, -1.2768608934681E-15, 7.3087610595061E-29, 5.5414715350778E-17, -9.436970724121E-07 };
		double tau = 540 / T;
		double g0_pi = 1 / P;
		double gr_pi = 0;
		for (int i = 0; i < 43; i++) {
			gr_pi = gr_pi + nr[i] * Ir[i] * Math.pow(P, (Ir[i] - 1)) * Math.pow((tau - 0.5), Jr[i]);
		}
		return R * T / P * P * (g0_pi + gr_pi) / 1000;
	}

	double h3_pT(double P, double T) {
		/*
		 * % 'Not avalible with IF 97 % 'Solve function T3_ph-T=0 with half interval
		 * method. % Dim Ts, Low_Bound, High_Bound, hs As Double
		 */
		double Ts = T + 1;
		double Low_Bound = h1_pT(P, 623.15);
		double hs = 0;
		double High_Bound = h2_pT(P, B23T_p(P));
		while (Math.abs(T - Ts) > 0.00001) {
			hs = (Low_Bound + High_Bound) / 2;
			Ts = T3_ph(P, hs);
			if (Ts > T)
				High_Bound = hs;
			else
				Low_Bound = hs;
		}
		return hs;
	}

	double h1_pT(double P, double T) {
		/*
		 * %'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		 * Properties of Water and Steam, September 1997 %'5 Equations for Region 1,
		 * Section. 5.1 Basic Equation %'Eqution 7, Table 3, Page 6 %Dim i As Integer
		 * %Dim ps, tau, g_t As Double %Dim I1, J1, n1 As Variant
		 */
		double R = 0.461526;// % 'kJ/(kg K)
		int[] I1 = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 8, 8, 21, 23, 29, 30, 31, 32 };
		int[] J1 = { -2, -1, 0, 1, 2, 3, 4, 5, -9, -7, -1, 0, 1, 3, -3, 0, 1, 3, 17, -4, 0, 6, -5, -2, 10, -8, -11, -6, -29, -31, -38, -39, -40, -41 };
		double[] n1 = { 0.14632971213167, -0.84548187169114, -3.756360367204, 3.3855169168385, -0.95791963387872, 0.15772038513228, -0.016616417199501, 8.1214629983568E-04,
				2.8319080123804E-04, -6.0706301565874E-04, -0.018990068218419, -0.032529748770505, -0.021841717175414, -5.283835796993E-05, -4.7184321073267E-04,
				-3.0001780793026E-04, 4.7661393906987E-05, -4.4141845330846E-06, -7.2694996297594E-16, -3.1679644845054E-05, -2.8270797985312E-06, -8.5205128120103E-10,
				-2.2425281908E-06, -6.5171222895601E-07, -1.4341729937924E-13, -4.0516996860117E-07, -1.2734301741641E-09, -1.7424871230634E-10, -6.8762131295531E-19,
				1.4478307828521E-20, 2.6335781662795E-23, -1.1947622640071E-23, 1.8228094581404E-24, -9.3537087292458E-26 };
		P = P / 16.53;
		double tau = 1386 / T;
		double g_t = 0;
		for (int i = 0; i < 34; i++) {
			g_t = g_t + (n1[i] * Math.pow((7.1 - P), I1[i]) * J1[i] * Math.pow((tau - 1.222), (J1[i] - 1)));
		}
		return R * T * tau * g_t;
	}

	double B23T_p(double P) {
		/*
		 * %'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		 * Properties of Water and Steam %'1997 %'Section 4 Auxiliary Equation for the
		 * Boundary between Regions 2 and 3 %'Eq 6, Page 6
		 */
		return 572.54459862746 + Math.pow(((P - 13.91883977887) / 1.0192970039326E-03), 0.5);
	}

	double h2_pT(double P, double T) {
		/*
		 * %'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		 * Properties of Water and Steam, September 1997 %'6 Equations for Region 2,
		 * Section. 6.1 Basic Equation %'Table 11 and 12, Page 14 and 15 %Dim i As
		 * Integer %Dim tau, g0_tau, gr_tau As Double %Dim Ir, Jr, nr, J0, n0 As Variant
		 */
		double R = 0.461526; // %'kJ/(kg K)
		int[] J0 = { 0, 1, -5, -4, -3, -2, -1, 2, 3 };
		double[] n0 = { -9.6927686500217, 10.086655968018, -0.005608791128302, 0.071452738081455, -0.40710498223928, 1.4240819171444, -4.383951131945, -0.28408632460772,
				0.021268463753307 };
		int[] Ir = { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 5, 6, 6, 6, 7, 7, 7, 8, 8, 9, 10, 10, 10, 16, 16, 18, 20, 20, 20, 21, 22, 23, 24, 24, 24 };
		int[] Jr = { 0, 1, 2, 3, 6, 1, 2, 4, 7, 36, 0, 1, 3, 6, 35, 1, 2, 3, 7, 3, 16, 35, 0, 11, 25, 8, 36, 13, 4, 10, 14, 29, 50, 57, 20, 35, 48, 21, 53, 39, 26, 40, 58 };
		double[] nr = { -1.7731742473213E-03, -0.017834862292358, -0.045996013696365, -0.057581259083432, -0.05032527872793, -3.3032641670203E-05, -1.8948987516315E-04,
				-3.9392777243355E-03, -0.043797295650573, -2.6674547914087E-05, 2.0481737692309E-08, 4.3870667284435E-07, -3.227767723857E-05, -1.5033924542148E-03,
				-0.040668253562649, -7.8847309559367E-10, 1.2790717852285E-08, 4.8225372718507E-07, 2.2922076337661E-06, -1.6714766451061E-11, -2.1171472321355E-03,
				-23.895741934104, -5.905956432427E-18, -1.2621808899101E-06, -0.038946842435739, 1.1256211360459E-11, -8.2311340897998, 1.9809712802088E-08, 1.0406965210174E-19,
				-1.0234747095929E-13, -1.0018179379511E-09, -8.0882908646985E-11, 0.10693031879409, -0.33662250574171, 8.9185845355421E-25, 3.0629316876232E-13,
				-4.2002467698208E-06, -5.9056029685639E-26, 3.7826947613457E-06, -1.2768608934681E-15, 7.3087610595061E-29, 5.5414715350778E-17, -9.436970724121E-07 };
		double tau = 540 / T;
		double g0_tau = 0;
		for (int i = 0; i < 9; i++) {
			g0_tau = g0_tau + n0[i] * J0[i] * Math.pow(tau, (J0[i] - 1));
		}
		double gr_tau = 0;
		for (int i = 0; i < 43; i++) {
			gr_tau = gr_tau + nr[i] * Math.pow(P, Ir[i]) * Jr[i] * Math.pow((tau - 0.5), (Jr[i] - 1));
		}
		return R * T * tau * (g0_tau + gr_tau);
	}

	double T3_ph(double P, double h) {
		/*
		 * %'Revised Supplementary Release on Backward Equations for the Functions
		 * T(p,h), v(p,h) and T(p,s), v(p,s) for Region 3 of the IAPWS Industrial
		 * Formulation 1997 for the Thermodynamic Properties of Water and Steam %'2004
		 * %'Section 3.3 Backward Equations T(p,h) and v(p,h) for Subregions 3a and 3b
		 * %'Boundary equation, Eq 1 Page 5 %Dim i As Integer, Ji, Ii, ni As Variant,
		 * h3ab, ps, hs, Ts As Double
		 */
		double R = 0.461526;
		double Tc = 647.096;
		double pc = 22.064;
		double T3_ph;
		double rhoc = 322;
		double h3ab = (2014.64004206875 + 3.74696550136983 * P - 2.19921901054187E-02 * Math.pow(P, 2) + 8.7513168600995E-05 * Math.pow(P, 3));
		if (h < h3ab) {
			/*
			 * %'Subregion 3a %'Eq 2, Table 3, Page 7
			 */
			int[] Ii = { -12, -12, -12, -12, -12, -12, -12, -12, -10, -10, -10, -8, -8, -8, -8, -5, -3, -2, -2, -2, -1, -1, 0, 0, 1, 3, 3, 4, 4, 10, 12 };
			int[] Ji = { 0, 1, 2, 6, 14, 16, 20, 22, 1, 5, 12, 0, 2, 4, 10, 2, 0, 1, 3, 4, 0, 2, 0, 1, 1, 0, 1, 0, 3, 4, 5 };
			double[] ni = { -1.33645667811215E-07, 4.55912656802978E-06, -1.46294640700979E-05, 6.3934131297008E-03, 372.783927268847, -7186.54377460447, 573494.7521034,
					-2675693.29111439, -3.34066283302614E-05, -2.45479214069597E-02, 47.8087847764996, 7.64664131818904E-06, 1.28350627676972E-03, 1.71219081377331E-02,
					-8.51007304583213, -1.36513461629781E-02, -3.84460997596657E-06, 3.37423807911655E-03, -0.551624873066791, 0.72920227710747, -9.92522757376041E-03,
					-0.119308831407288, 0.793929190615421, 0.454270731799386, 0.20999859125991, -6.42109823904738E-03, -0.023515586860454, 2.52233108341612E-03,
					-7.64885133368119E-03, 1.36176427574291E-02, -1.33027883575669E-02 };
			double ps = P / 100;
			double hs = h / 2300;
			double Ts = 0;
			for (int i = 0; i < 31; i++) {
				Ts = Ts + ni[i] * Math.pow((ps + 0.24), Ii[i]) * Math.pow((hs - 0.615), Ji[i]);
			}
			T3_ph = Ts * 760;
		} else {
			/*
			 * %'Subregion 3b %'Eq 3, Table 4, Page 7,8
			 */
			int[] Ii = { -12, -12, -10, -10, -10, -10, -10, -8, -8, -8, -8, -8, -6, -6, -6, -4, -4, -3, -2, -2, -1, -1, -1, -1, -1, -1, 0, 0, 1, 3, 5, 6, 8 };
			int[] Ji = { 0, 1, 0, 1, 5, 10, 12, 0, 1, 2, 4, 10, 0, 1, 2, 0, 1, 5, 0, 4, 2, 4, 6, 10, 14, 16, 0, 2, 1, 1, 1, 1, 1 };
			double[] ni = { 3.2325457364492E-05, -1.27575556587181E-04, -4.75851877356068E-04, 1.56183014181602E-03, 0.105724860113781, -85.8514221132534, 724.140095480911,
					2.96475810273257E-03, -5.92721983365988E-03, -1.26305422818666E-02, -0.115716196364853, 84.9000969739595, -1.08602260086615E-02, 1.54304475328851E-02,
					7.50455441524466E-02, 2.52520973612982E-02, -6.02507901232996E-02, -3.07622221350501, -5.74011959864879E-02, 5.03471360939849, -0.925081888584834,
					3.91733882917546, -77.314600713019, 9493.08762098587, -1410437.19679409, 8491662.30819026, 0.861095729446704, 0.32334644281172, 0.873281936020439,
					-0.436653048526683, 0.286596714529479, -0.131778331276228, 6.76682064330275E-03 };
			double hs = h / 2800;
			double ps = P / 100;
			double Ts = 0;
			for (int i = 0; i < 33; i++) {
				Ts = Ts + ni[i] * Math.pow((ps + 0.298), Ii[i]) * Math.pow((hs - 0.72), Ji[i]);
			}
			T3_ph = Ts * 860;
		}
		return T3_ph;
	}

	double v3_ph(double P, double h) {
		/*
		 * %'Revised Supplementary Release on Backward Equations for the Functions
		 * T(p,h), v(p,h) and T(p,s), v(p,s) for Region 3 of the IAPWS Industrial
		 * Formulation 1997 for the Thermodynamic Properties of Water and Steam %'2004
		 * %'Section 3.3 Backward Equations T(p,h) and v(p,h) for Subregions 3a and 3b
		 * %'Boundary equation, Eq 1 Page 5 % Dim i As Integer, Ji, Ii, ni As Variant,
		 * h3ab, ps, hs, vs As Double
		 */
		double R = 0.461526;
		double Tc = 647.096;
		double pc = 22.064;
		double rhoc = 322;
		double v3_ph;
		double h3ab = (2014.64004206875 + 3.74696550136983 * P - 2.19921901054187E-02 * Math.pow(P, 2) + 8.7513168600995E-05 * Math.pow(P, 3));
		if (h < h3ab) {
			/*
			 * %'Subregion 3a %'Eq 4, Table 6, Page 9
			 */
			int[] Ii = { -12, -12, -12, -12, -10, -10, -10, -8, -8, -6, -6, -6, -4, -4, -3, -2, -2, -1, -1, -1, -1, 0, 0, 1, 1, 1, 2, 2, 3, 4, 5, 8 };
			int[] Ji = { 6, 8, 12, 18, 4, 7, 10, 5, 12, 3, 4, 22, 2, 3, 7, 3, 16, 0, 1, 2, 3, 0, 1, 0, 1, 2, 0, 2, 0, 2, 2, 2 };
			double[] ni = { 5.29944062966028E-03, -0.170099690234461, 11.1323814312927, -2178.98123145125, -5.06061827980875E-04, 0.556495239685324, -9.43672726094016,
					-0.297856807561527, 93.9353943717186, 1.92944939465981E-02, 0.421740664704763, -3689141.2628233, -7.37566847600639E-03, -0.354753242424366, -1.99768169338727,
					1.15456297059049, 5683.6687581596, 8.08169540124668E-03, 0.172416341519307, 1.04270175292927, -0.297691372792847, 0.560394465163593, 0.275234661176914,
					-0.148347894866012, -6.51142513478515E-02, -2.92468715386302, 6.64876096952665E-02, 3.52335014263844, -1.46340792313332E-02, -2.24503486668184,
					1.10533464706142, -4.08757344495612E-02 };
			double ps = P / 100;
			double hs = h / 2100;
			double vs = 0;
			for (int i = 0; i < 32; i++) {
				vs = vs + ni[i] * Math.pow((ps + 0.128), Ii[i]) * Math.pow((hs - 0.727), Ji[i]);
			}
			v3_ph = vs * 0.0028;
		} else {
			/*
			 * %'Subregion 3b %'Eq 5, Table 7, Page 9
			 */
			int[] Ii = { -12, -12, -8, -8, -8, -8, -8, -8, -6, -6, -6, -6, -6, -6, -4, -4, -4, -3, -3, -2, -2, -1, -1, -1, -1, 0, 1, 1, 2, 2 };
			int[] Ji = { 0, 1, 0, 1, 3, 6, 7, 8, 0, 1, 2, 5, 6, 10, 3, 6, 10, 0, 2, 1, 2, 0, 1, 4, 5, 0, 0, 1, 2, 6 };
			double[] ni = { -2.25196934336318E-09, 1.40674363313486E-08, 2.3378408528056E-06, -3.31833715229001E-05, 1.07956778514318E-03, -0.271382067378863, 1.07202262490333,
					-0.853821329075382, -2.15214194340526E-05, 7.6965608822273E-04, -4.31136580433864E-03, 0.453342167309331, -0.507749535873652, -100.475154528389,
					-0.219201924648793, -3.21087965668917, 607.567815637771, 5.57686450685932E-04, 0.18749904002955, 9.05368030448107E-03, 0.285417173048685, 3.29924030996098E-02,
					0.239897419685483, 4.82754995951394, -11.8035753702231, 0.169490044091791, -1.79967222507787E-02, 3.71810116332674E-02, -5.36288335065096E-02,
					1.6069710109252 };
			double ps = P / 100;
			double hs = h / 2800;
			double vs = 0;
			for (int i = 0; i < 30; i++) {
				vs = vs + ni[i] * Math.pow((ps + 0.0661), Ii[i]) * Math.pow((hs - 0.72), Ji[i]);
			}
			v3_ph = vs * 0.0088;
		}
		return v3_ph;
	}

	double v5_pT(double P, double T) {
		/*
		 * %'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		 * Properties of Water and Steam, September 1997 %'Basic Equation for Region 5
		 * %%'Eq 32,33, Page 36, Tables 37-41 %Dim Iir, Jir, nir, ni0, Ji0 As Variant,
		 * tau, gamma0_pi, gammar_pi As Double, i As Integer
		 */
		double R = 0.461526; // % 'kJ/(kg K)
		int[] Ji0 = { 0, 1, -3, -2, -1, 2 };
		double[] ni0 = { -13.179983674201, 6.8540841634434, -0.024805148933466, 0.36901534980333, -3.1161318213925, -0.32961626538917 };
		int[] Iir = { 1, 1, 1, 2, 3 };
		int[] Jir = { 0, 1, 3, 9, 3 };
		double[] nir = { -1.2563183589592E-04, 2.1774678714571E-03, -0.004594282089991, -3.9724828359569E-06, 1.2919228289784E-07 };
		double tau = 1000 / T;
		double gamma0_pi = 1 / P;
		double gammar_pi = 0;
		for (int i = 0; i < 5; i++) {
			gammar_pi = gammar_pi + nir[i] * Iir[i] * Math.pow(P, (Iir[i] - 1)) * Math.pow(tau, Jir[i]);
		}
		return R * T / P * P * (gamma0_pi + gammar_pi) / 1000;

	}

	double fromSItoENG_v(double v) {
		return v * 16.0184833;
	}

	double fromSItoENG_rho(double rho) {
		return rho * 0.062428;
	}

	double fromENGtoSI_p(double P) {
		return P * 6.89465;
	}

	double fromENGtoSI_T(double T) {
		return (T - 32) * 5 / 9;
	}

	double toSIunit_p(double Ins) {
		// %Translate bar to MPa
		return Ins / 10;
	}

	double toSIunit_T(double Ins) {
		// %Translate degC to Kelvon
		return Ins + 273.15;
	}
}