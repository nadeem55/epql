package com.nad.epql.calc.coolingtower;

public class WetBulbCalc {

	public double Wetbulb(double pressure, double temperature, double humidity, String Units) {
		double omega = getOmega(pressure, temperature, humidity, Units);
		return getWetbulbValue(pressure, temperature, omega);

	}

	double getOmega(double P, double T, double phi, String Units) {

		Units = "SI";

		if (Units.equals("ENG")) {
			P = fromENGtoSI_p(P);
			T = fromENGtoSI_T(T);
		}
		double omega_PTphi = (0.622 * phi * psat_T_H2O(T, Units)) / (P - phi * psat_T_H2O(T, Units));
		return omega_PTphi;
	}

	double getWetbulbValue(double P, double T, double omega) {

		return Twb_PTomega(P, T, omega, "SI");
	}

	double fromENGtoSI_p(double P) {
		return P * 6.89465;
	}

	double fromENGtoSI_T(double T) {
		return (T - 32) * 5 / 9;
	}

	double psat_T_H2O(double T, String Units) {
		double value = 0;
		T = toSIunit_T(T);
		if (T <= 647.096 && T > 273.15)
			value = fromSIunit_p(p4_T(T));
		else

			System.out.print("Error in psat_T_H2O");

		value = value * 100;

		if (Units.equals("ENG"))
			value = fromSItoENG_p(value);

		return value;
	}

	double toSIunit_T(double Ins) {
		// Translate degC to Kelvon
		return Ins + 273.15;
	}

	double fromSItoENG_p(double P) {
		return P * 0.14504;
	}

	double p4_T(double T) {
		// 'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		// Properties of Water and Steam, September 1997
		// 'Section 8.1 The Saturation-Pressure Equation
		// 'Eq 30, Page 33
		// Dim teta, a, b, c As Double
		double teta = T - 0.23855557567849 / (T - 650.17534844798);
		double a = Math.pow(teta, 2) + 1167.0521452767 * teta - 724213.16703206;
		double b = -17.073846940092 * Math.pow(teta, 2) + 12020.82470247 * teta - 3232555.0322333;
		double c = 14.91510861353 * Math.pow(teta, 2) - 4823.2657361591 * teta + 405113.40542057;
		double p4_T = Math.pow((2 * c / (-b + Math.pow((Math.pow(b, 2) - 4 * a * c), 0.5))), 4);
		return p4_T;
	}

	double fromSIunit_p(double Ins) {
		// Translate bar to MPa
		return Ins * 10;
	}

	double Twb_PTomega(double P, double T, double omega, String Units) {
		// % Dim Psat As Double
		// % Dim Response, Count, Max As Integer
		// % Dim Tnew, Tguess As Double
		// % Dim cp_a As Double
		// % Dim alpha As Double
		// % Dim h_fg2, denom, omega2 As Double

		if (Units.equals("ENG")) {
			P = fromENGtoSI_p(P);
			T = fromENGtoSI_T(T);
		}

		double Max = 1000;
		double Tguess = T;
		double cp_a = 1.005;
		double alpha = 0.25;

		for (int Count = 1; Count <= Max; Count++) {
			double h_fg2 = hV_T_H2O(Tguess, Units) - hL_T_H2O(Tguess, Units);
			double denom = hV_T_H2O(T, Units) - hL_T_H2O(Tguess, Units);
			double omega2 = 0.622 * psat_T_H2O(Tguess, Units) / (P - psat_T_H2O(Tguess, Units));
			double Tnew = (omega * denom - omega2 * h_fg2) / cp_a + T;
			Tguess = (1 - alpha) * Tguess + alpha * Tnew;
			// % if (abs(Tnew - Tguess) < converge_eps) %To be considered later
			// % break;
			// % end
		}
		// 'Response = MsgBox("count =" + CStr(Count) + " Tguess =" + CStr(Tguess),
		// vbOKCancel)

		double Twb_PTomega = Tguess;

		if (Units.equals("ENG")) {
			Twb_PTomega = fromSItoENG_T(Twb_PTomega);
		}
		return Twb_PTomega;
	}

	double hV_T_H2O(double T, String Units) {

		// %Units = upper(Units);
		if (Units.matches("ENG"))
			T = fromENGtoSI_T(T);

		double value = 0;
		T = toSIunit_T(T);
		if (T <= 273.15 && T > 261.15)
			value = 2500.9 + 1.82 * (T - 273.15);
		else if (T > 273.15 && T < 647.096)
			value = fromSIunit_h(h4V_p(p4_T(T)));
		else
			System.out.print("Warning in hV_T_H2O"); // CVErr(xlErrValue); to be considered later

		if (Units.equals("ENG"))
			value = fromSItoENG_h(value);
		return value;
	}

	double fromSItoENG_T(double T) {
		return (9 / 5) * T + 32;
	}

	double fromSItoENG_h(double h) {
		return h * 0.42992;
	}

	double h4V_p(double P) {
		// %Dim Low_Bound, High_Bound, hs, ps, Ts As Double
		double h4V_p = 0;
		if (P > 0.000611657 && P < 22.06395) {
			double Ts = T4_p(P);

			if (P < 16.529)
				h4V_p = h2_pT(P, Ts);
			else {
				// 'Iterate to find the the backward solution of p3sat_h
				double Low_Bound = 2087.23500164864;
				double High_Bound = 2563.592004 + 5;// %'5 added to extrapolate to ensure even the border ==350°C solved.
				double hs = 0;
				double ps = 0;
				while (Math.abs(P - ps) > 0.000001) {
					hs = (Low_Bound + High_Bound) / 2;
					ps = p3sat_h(hs);
					if (ps < P)
						High_Bound = hs;
					else
						Low_Bound = hs;

				}
				h4V_p = hs;
			}
		} else
			System.out.print("Warning in h4V_P"); // CVErr(xlErrValue); to be considered later

		return h4V_p;
	}

	double T4_p(double P) {
		// % 'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		// Properties of Water and Steam, September 1997
		// % 'Section 8.2 The Saturation-Temperature Equation
		// % 'Eq 31, Page 34
		// % Dim beta, e, f, g, d As Double
		double beta = Math.pow(P, 0.25);
		double e = Math.pow(beta, 2) - 17.073846940092 * beta + 14.91510861353;
		double f = 1167.0521452767 * (Math.pow(beta, 2)) + 12020.82470247 * beta - 4823.2657361591;
		double g = -724213.16703206 * (Math.pow(beta, 2)) - 3232555.0322333 * beta + 405113.40542057;
		double d = 2 * g / (-f - Math.pow((Math.pow(f, 2) - 4 * e * g), 0.5));
		double T4_p = (650.17534844798 + d - Math.pow((Math.pow((650.17534844798 + d), 2) - 4 * (-0.23855557567849 + 650.17534844798 * d)), 0.5)) / 2;
		return T4_p;
	}

	double h2_pT(double P, double T) {
		// % 'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		// Properties of Water and Steam, September 1997
		// % '6 Equations for Region 2, Section. 6.1 Basic Equation
		// % 'Table 11 and 12, Page 14 and 15
		// % Dim i As Integer
		// % Dim tau, g0_tau, gr_tau As Double
		// % Dim Ir, Jr, nr, J0, n0 As Variant
		// % Const R As Double = 0.461526 %'kJ/(kg K)

		double h2_pT = 0;
		double R = 0.461526;

		double[] J0 = { 0, 1, -5, -4, -3, -2, -1, 2, 3 };
		double[] n0 = { -9.6927686500217, 10.086655968018, -0.005608791128302, 0.071452738081455, -0.40710498223928, 1.4240819171444, -4.383951131945, -0.28408632460772,
				0.021268463753307 };
		double[] Ir = { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 5, 6, 6, 6, 7, 7, 7, 8, 8, 9, 10, 10, 10, 16, 16, 18, 20, 20, 20, 21, 22, 23, 24, 24, 24 };
		double[] Jr = { 0, 1, 2, 3, 6, 1, 2, 4, 7, 36, 0, 1, 3, 6, 35, 1, 2, 3, 7, 3, 16, 35, 0, 11, 25, 8, 36, 13, 4, 10, 14, 29, 50, 57, 20, 35, 48, 21, 53, 39, 26, 40, 58 };
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
		h2_pT = R * T * tau * (g0_tau + gr_tau);

		return h2_pT;
	}

	double p3sat_h(double h) {
		// % 'Revised Supplementary Release on Backward Equations for the Functions
		// T(p,h), v(p,h) and T(p,s), v(p,s) for Region 3 of the IAPWS Industrial
		// Formulation 1997 for the Thermodynamic Properties of Water and Steam
		// % '2004
		// % 'Section 4 Boundary Equations psat(h) and psat(s) for the Saturation Lines
		// of Region 3
		// % 'Se pictures Page 17, Eq 10, Table 17, Page 18
		// %Dim Ii, Ji, ni As Variant, ps As Double, i As Integer
		double[] Ii = { 0, 1, 1, 1, 1, 5, 7, 8, 14, 20, 22, 24, 28, 36 };
		double[] Ji = { 0, 1, 3, 4, 36, 3, 0, 24, 16, 16, 3, 18, 8, 24 };
		double[] ni = { 0.600073641753024, -9.36203654849857, 24.6590798594147, -107.014222858224, -91582131580576.8, -8623.32011700662, -23.5837344740032, 2.52304969384128E+17,
				-3.89718771997719E+18, -3.33775713645296E+22, 35649946963.6328, -1.48547544720641E+26, 3.30611514838798E+18, 8.13641294467829E+37 };
		h = h / 2600;
		double ps = 0;
		for (int i = 0; i <= 13; i++)
			ps = ps + ni[i] * Math.pow((h - 1.02), Ii[i]) * Math.pow((h - 0.608), Ji[i]);
		// %Next i

		double p3sat_h = ps * 22;
		return p3sat_h;
	}

	// TODO: this method is doing nothing
	double fromSIunit_h(double Ins) {
		return Ins;
	}

	double hL_T_H2O(double T, String Units) {

		// %Units = UCase(Units);
		double hL_T_H2O = 0;
		if (Units.equals("ENG"))
			T = fromENGtoSI_T(T);

		T = toSIunit_T(T);
		if (T > 273.15 && T < 647.096)
			hL_T_H2O = fromSIunit_h(h4L_p(p4_T(T)));
		else
			System.out.print("hL_T_H2O = Value of Temprature is not Between T > 273.15 && T < 647.096 "); // % CVErr(xlErrValue); to be considered later

		if (Units.equals("ENG"))
			hL_T_H2O = fromSItoENG_h(hL_T_H2O);

		return hL_T_H2O;
	}

	double h4L_p(double P) {
		// %Dim Low_Bound, High_Bound, hs, ps, Ts As Double
		double Ts = 0;
		double h4L_p = 0;
		if (P > 0.000611657 && P < 22.06395) {
			Ts = T4_p(P);
			if (P < 16.529) {
				h4L_p = h1_pT(P, Ts);
			} else {
				// %'Iterate to find the the backward solution of p3sat_h
				double Low_Bound = 1670.858218;
				double High_Bound = 2087.23500164864;
				double ps = 0;
				double hs = 0;
				while (Math.abs(P - ps) > 0.00001) {
					hs = (Low_Bound + High_Bound) / 2;
					ps = p3sat_h(hs);
					if (ps > P)
						High_Bound = hs;
					else
						Low_Bound = hs;

				}

				h4L_p = hs;
			}
		} else
			System.out.print("Warning/Error occurred in h4L_p..!");

		return h4L_p;
	}

	double h1_pT(double P, double T) {
		// % 'Release on the IAPWS Industrial Formulation 1997 for the Thermodynamic
		// Properties of Water and Steam, September 1997
		// % '5 Equations for Region 1, Section. 5.1 Basic Equation
		// % 'Eqution 7, Table 3, Page 6
		// % Dim i As Integer
		// % Dim ps, tau, g_t As Double
		// % Dim I1, J1, n1 As Variant
		// % Const R As Double = 0.461526 'kJ/(kg K)
		double R = 0.461526;
		double[] I1 = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 8, 8, 21, 23, 29, 30, 31, 32 };
		double[] J1 = { -2, -1, 0, 1, 2, 3, 4, 5, -9, -7, -1, 0, 1, 3, -3, 0, 1, 3, 17, -4, 0, 6, -5, -2, 10, -8, -11, -6, -29, -31, -38, -39, -40, -41 };
		double[] n1 = { 0.14632971213167, -0.84548187169114, -3.756360367204, 3.3855169168385, -0.95791963387872, 0.15772038513228, -0.016616417199501, 8.1214629983568E-04,
				2.8319080123804E-04, -6.0706301565874E-04, -0.018990068218419, -0.032529748770505, -0.021841717175414, -5.283835796993E-05, -4.7184321073267E-04,
				-3.0001780793026E-04, 4.7661393906987E-05, -4.4141845330846E-06, -7.2694996297594E-16, -3.1679644845054E-05, -2.8270797985312E-06, -8.5205128120103E-10,
				-2.2425281908E-06, -6.5171222895601E-07, -1.4341729937924E-13, -4.0516996860117E-07, -1.2734301741641E-09, -1.7424871230634E-10, -6.8762131295531E-19,
				1.4478307828521E-20, 2.6335781662795E-23, -1.1947622640071E-23, 1.8228094581404E-24, -9.3537087292458E-26 };
		P = P / 16.53;
		double tau = 1386 / T;
		double g_t = 0;
		for (int i = 0; i < 34; i++) {
			g_t = g_t + ((n1[i] * Math.pow((7.1 - P), I1[i])) * J1[i] * Math.pow((tau - 1.222), (J1[i] - 1)));
		}
		double h1_pT = R * T * tau * g_t;
		return h1_pT;
	}

}
