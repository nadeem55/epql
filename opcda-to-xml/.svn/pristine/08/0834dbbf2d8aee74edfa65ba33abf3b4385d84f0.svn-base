package com.nad.epql.calc.coolingtower;

public class SpecificVolumeCalc {

	public double specificVolume(double P, double T, double Phi, String Units) {
		double y = omega_PTphi(T, P, Phi, Units);
		return v_dry_PTomega(T, P, y, Units);

	}

	double v_dry_PTomega(double T, double P, double omega, String Units) {
		if (Units.equals("ENG")) {
			T = fromENGtoSI_T(T);
			P = fromENGtoSI_p(P);
		}
		double phi = phi_PTomega(P, T, omega, Units);
		double v_dry_PTomega = v_dry_PTphi(P, T, phi, Units);
		if (Units.equals("ENG")) {
			return v_dry_PTomega = fromSItoENG_v(v_dry_PTomega);
		}
		return v_dry_PTomega;
	}

	double omega_PTphi(double T, double P, double phi, String Units) {
		if (Units.equals("ENG")) {
			P = fromENGtoSI_p(P);
			T = fromENGtoSI_T(T);
		}
		return (0.622 * phi * psat_T_H2O(T, Units)) / (P - phi * psat_T_H2O(T, Units));
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
			System.out.println("Error in psat_T_H2O");

		value = value * 100;

		if (Units.equals("ENG"))
			value = (value);
		return value;
	}

	double toSIunit_T(double Ins) {
		double value = Ins + 273.15;
		return value;
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
		return Math.pow((2 * c / (-b + Math.pow((Math.pow(b, 2) - 4 * a * c), 0.5))), 4);
	}

	double fromSIunit_p(double Ins) {
		// Translate bar to MPa
		return Ins * 10;
	}

	double phi_PTomega(double P, double T, double omega, String Units) {

		if (Units.equals("ENG")) {
			P = fromENGtoSI_p(P);
			T = fromENGtoSI_T(T);
		}

		return (omega * P) / ((0.622 + omega) * (psat_T_H2O(T, Units)));
	}

	double v_dry_PTphi(double P, double T, double phi, String Units) {
		if (Units.equals("ENG")) {
			T = fromENGtoSI_T(T);
			P = fromENGtoSI_p(P);
		}
		double R_air = 0.289645; // %' kJ/kg-K
		double P_v = phi * psat_T_H2O(T, Units);
		double P_a = P - P_v;
		double v_dry_PTphi = R_air * (T + 273.15) / P_a;

		if (Units.equals("ENG")) {
			v_dry_PTphi = fromSItoENG_v(v_dry_PTphi);
		}
		return v_dry_PTphi;
	}

	double fromSItoENG_v(double v) {
		return (v * 16.0184833);
	}
}