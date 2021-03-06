// Copyright (c) 2012 Fraunhofer IGD
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to 
// deal in the Software without restriction, including without limitation the 
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or 
// sell copies of the Software, and to permit persons to whom the Software is 
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in 
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING  
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
// DEALINGS IN THE SOFTWARE.

package de.fhg.igd.pcolor.colorspace;

import java.util.Arrays;

import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.Illuminant;
import de.fhg.igd.pcolor.util.MathTools;

/**
 * Represents CIECAM02 Viewing Conditions.
 * 
 * @author Thu Huong Luu
 * @author Simon Thum
 */
public class ViewingConditions {
	
	/**
	 * Viewing conditions modelled after sRGB's "encoding" (would-be ideal)
	 * viewing environment with 64 cd/m2 average luminance and 20 % adaption
	 * luminance. The dim surrounding matches the dim viewing environment
	 * assumed when video standards were crafted in the 70s (see Charles
	 * Pontyon).
	 */
	public static final ViewingConditions sRGB_encoding_envirnonment =
			createAdapted(Illuminant.D50, 64.0, 64/5, Surrounding.dimSurrounding);

	/**
	 * Viewing conditions modelled after sRGB's "typical" viewing environment
	 * with 200 cd/m2.
	 */
	public static final ViewingConditions sRGB_typical_envirnonment = 
			createAdapted(Illuminant.D50, 200, 200/5, Surrounding.averageSurrounding);
	
	/**
	 * Viewing conditions modelled after Adobe RGB (1998) whitepoint luminance.
	 * Adobe specifies the adapted whitepoint to be equal. See
	 * http://www.adobe.com/digitalimag/pdfs/AdobeRGB1998.pdf
	 */
	public static final ViewingConditions AdobeRGB_envirnonment = 
			createAdapted(Illuminant.D65, 160, 160/5, Surrounding.averageSurrounding);


	// environment parameters
	private final double L_A, Y_b;
	private final double[] XYZ_w;
	private final Surrounding surrounding;

	// derived variables
	private final double z, n, N_bb, N_cb, A_w, F_L;
	private final double[] D_RGB;

	/**
	 * Construct a new ViewingConditions instance. This constructor is for internal use.
	 * @param XYZ_w XYZ of adopted whitepoint
	 * @param L_A average luminance of visual surround
	 * @param Y_b adaptation luminance of color background
	 * @param sur the surrounding
	 * @param RGB_w the white point in RGB values (equations 7.4-6)
	 * @param RGB_c the adapted RGB values (equations 7.4-6)
	 */
	private ViewingConditions(double[] XYZ_w, double L_A, double Y_b, Surrounding sur, double[] RGB_w, double[] RGB_c) {
		this.XYZ_w = XYZ_w; // XYZ whitepoint
		this.L_A = L_A; // average luminance of visual surround
		this.Y_b = Y_b; // adaptation luminance of color background
		this.surrounding = sur;

		// calculate increase in brightness and colorfulness caused by brighter viewing environments
		double L_Ax5 = 5.0 * L_A;
		double k = 1.0 / (L_Ax5 + 1.0);
		double kpow4 = Math.pow(k, 4.0);
		F_L = 0.2 * kpow4 * (L_Ax5) + 0.1 * Math.pow(1.0 - kpow4, 2.0) * Math.pow(L_Ax5, 1.0/3.0);

		// calculate response compression on J and C caused by background lightness. 
		n = Y_b / XYZ_w[1]; 
		z = 1.48 + Math.sqrt(n);

		N_bb = 0.725 * Math.pow(1.0 / n, 0.2); 
		N_cb = N_bb; // chromatic contrast factors (calculate increase in J, Q, and C caused by dark backgrounds)

		// calculate achromatic response to white
		double[] RGB_wc = new double[] {RGB_c[0] * RGB_w[0], RGB_c[1] * RGB_w[1], RGB_c[2] * RGB_w[2]};
		double[] RGBPrime_w = CS_CIECAM02.CAT02toHPE(RGB_wc);
		double[] RGBPrime_aw = new double[3];
		for(int channel = 0; channel < RGBPrime_w.length; channel++) {
			if(RGBPrime_w[channel] >= 0) {
				double n = Math.pow(F_L * RGBPrime_w[channel] / 100.0, 0.42);
				RGBPrime_aw[channel] = 400.0 * n / (n + 27.13) + 0.1;
			} else {
				double n = Math.pow(-1.0 * F_L * RGBPrime_w[channel] / 100.0, 0.42);
				RGBPrime_aw[channel] = -400.0 * n / (n + 27.13) + 0.1;
			}
		}
		A_w = (2.0 * RGBPrime_aw[0] + RGBPrime_aw[1] + RGBPrime_aw[2] / 20.0 - 0.305) * N_bb;
		D_RGB = RGB_c;
	}
	
	/**
	 * Construct a new ViewingConditions instance. The degree of adaption (D) is derived
	 * from the background and surround. This is the standard case treated in CIE 159:2004.
	 * @param XYZ_w XYZ of adopted whitepoint
	 * @param L_A average luminance of visual surround
	 * @param Y_b adaptation luminance of color background
	 * @param sur the surrounding
	 * @return a ViewingConditions instance
	 */
	public static ViewingConditions createAdapted(CIEXYZ XYZ_w, double L_A, double Y_b, Surrounding sur) {
		double[] xyz_w = MathTools.floatToDoubleArray(XYZ_w.toCIEXYZ100());
		// calculate RGB whitepoint
		double[] RGB_w = CS_CIECAM02.XYZtoCAT02(xyz_w);
		double D = calcD(L_A, sur);
		double[] RGB_c = calcAdaptedRGBc(XYZ_w, RGB_w, D);
		return new ViewingConditions(xyz_w, L_A, Y_b, sur, RGB_w, RGB_c);
	}

	/**
	 * Create viewing conditions assuming full adaption. This is primarily
	 * useful in color management applications.
	 * 
	 * @param XYZ_w XYZ of adopted whitepoint
	 * @param L_A average luminance of visual surround
	 * @param Y_b adaptation luminance of color background
	 * @param sur the surrounding
	 * @return a fully adapted viewing conditions instance
	 */
	public static ViewingConditions createFullyAdapted(CIEXYZ XYZ_w, float L_A, float Y_b, Surrounding sur) {
		double[] xyz_w = MathTools.floatToDoubleArray(XYZ_w.toCIEXYZ100());
		double[] RGB_w = CS_CIECAM02.XYZtoCAT02(xyz_w);
		double[] RGB_c = calcAdaptedRGBc(XYZ_w, RGB_w, 1.0);
		return new ViewingConditions(xyz_w, L_A, Y_b, sur, RGB_w, RGB_c);
	}

	private static double[] calcAdaptedRGBc(CIEXYZ XYZ_w, double[] RGB_w, double D) {
		double[] RGB_c = new double[3];
		double Yw = XYZ_w.get(CIEXYZ.Y) * 100;
		for(int i = 0; i < RGB_c.length; i++) {
			RGB_c[i] = (D * Yw / RGB_w[i]) + (1.0 - D);
		}
		return RGB_c;
	}
	
	private static double calcD(double L_A, Surrounding sur) {
		return Math.max(0.0, Math.min(1.0, sur.getF() * (1.0 - (1.0 / 3.6) * Math.pow(Math.E, (-L_A - 42.0) / 92.0))));
	}
	
	/**
	 * Derive (or better: guess) an (adopted) white point by mixing two white points.
	 * Luminance will also be mixed.
	 * See CIE:159:2004, section 5.
	 * @param background_white the whitepoint of the background
	 * @param surround_white the whitepoint of the surround
	 * @param bgFactor the background weight
	 * @return XYZ mixed from background and surround 
	 */
	public static CIEXYZ mixedWhitepoint(CIEXYZ background_white, CIEXYZ surround_white, float bgFactor) {
		return CIEXYZ.blend(new CIEXYZ[] {background_white, surround_white}, new float[]{bgFactor, 1 - bgFactor});
	}

	@Override
	public boolean equals(Object anObject) {
	    if (this == anObject) return true;
	    if (anObject == null || !(anObject instanceof ViewingConditions)) return false;
	    ViewingConditions object = (ViewingConditions)anObject;
	    return Arrays.equals(this.XYZ_w, object.XYZ_w) &&
	    	this.surrounding.equals(object.surrounding) &&
		    Double.doubleToLongBits(this.L_A) == Double.doubleToLongBits(object.L_A) &&
		    Double.doubleToLongBits(this.Y_b) == Double.doubleToLongBits(object.Y_b);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (int)Double.doubleToLongBits(L_A);
		hash = 31 * hash + (int)Double.doubleToLongBits(Y_b);
		hash = 31 * hash + surrounding.hashCode();
		hash = 31 * hash + Arrays.hashCode(XYZ_w);
		return hash;
	}

	/**
	 * @return a useful value
	 */
	public double getL_A() {
		return L_A;
	}

	/**
	 * @return a useful value
	 */
	public double getY_b() {
		return Y_b;
	}

	/**
	 * @return a useful value
	 */
	public double[] getXYZ_w() {
		return XYZ_w;
	}

	/**
	 * @return a useful value
	 */
	public Surrounding getSurrounding() {
		return surrounding;
	}

	/**
	 * @return a useful value
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return a useful value
	 */
	public double getN() {
		return n;
	}

	/**
	 * @return a useful value
	 */
	public double getN_bb() {
		return N_bb;
	}

	/**
	 * @return a useful value
	 */
	public double getN_cb() {
		return N_cb;
	}

	/**
	 * @return a useful value
	 */
	public double getA_w() {
		return A_w;
	}

	/**
	 * @return a useful value
	 */
	public double getF_L() {
		return F_L;
	}

	/**
	 * @return a useful value
	 */
	public double[] getD_RGB() {
		return D_RGB;
	}
}
