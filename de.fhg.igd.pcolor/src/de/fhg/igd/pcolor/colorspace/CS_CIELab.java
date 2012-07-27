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

import java.awt.color.ColorSpace;

/**
 * This class implements the CIE L*a*b* color space (often used for
 * "delta E" difference estimation), providing
 * forward and backwards transformations from the CIE XYZ colorspace.
 */
public class CS_CIELab extends ColorSpace {

	private static final long serialVersionUID = -3816629468334699096L;
	/**
	 * Lightness
	 */
	public static final int L = 0;
	/**
	 * Red-Green
	 */
	public static final int a = 1;
	/**
	 * Yellow-Blue
	 */
	public static final int b = 2;

	// conversion function constants as suggested by Lindbloom and adopted by CIE
	private static final double EPSILON = 216d / 24389;
	private static final double KAPPA = 24389d / 27;

	/**
	 * default constructor
	 * creates CIELab color space with three components
	 */
	public CS_CIELab() {
		super(TYPE_Lab, 3);
	}

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		float[] Lab = new float[3];

		Lab[L] = (float)(116 * fromxyz(colorvalue[1]) - 16);
		Lab[a] = (float)(500 * (fromxyz(colorvalue[0]) - fromxyz(colorvalue[1])));
		Lab[b] = (float)(200 * (fromxyz(colorvalue[1]) - fromxyz(colorvalue[2])));

		return Lab;
	}

	private static double fromxyz(float value) {
		if(value > EPSILON) {
			return Math.cbrt(value);
		} else {
			return (KAPPA * value + 16 / 116); 
		}
	}

	@Override
	public float[] fromRGB(float[] colorvalue) {
		// convert RGB to CIEXYZ, then CIEXYZ to CIELab
		ColorSpace sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		float[] XYZComponents = sRGB.toCIEXYZ(colorvalue);
		return fromCIEXYZ(XYZComponents);
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		float[] XYZ = new float[3];

		double fy = (colorvalue[L] + 16) / 116d;
		double fz = fy - colorvalue[b] / 200d;
		double fx = colorvalue[a] / 500d + fy;

		XYZ[0] = (float)tox(fx);
		XYZ[1] = (float)toy(fy, colorvalue[L]);
		XYZ[2] = (float)toz(fz);

		return XYZ;
	}

	private static double tox(double fx) {
		if(Math.pow(fx, 3) > EPSILON) {
			return Math.pow(fx, 3);
		} else {
			return (116 * fx - 16) / KAPPA; 
		}
	}

	private static double toy(double fy, float L) {
		if(L > KAPPA * EPSILON) {
			return Math.pow((L + 16) / 116d, 3);
		} else {
			return L / KAPPA;
		}
	}

	private static double toz(double fz) {
		if(Math.pow(fz, 3) > EPSILON) {
			return Math.pow(fz, 3);
		} else {
			return (116 * fz - 16) / KAPPA; 
		}
	}

	@Override
	public float[] toRGB(float[] colorvalue) {
		// convert CIELab to CIEXYZ, then CIEXYZ to sRGB
		float[] XYZComponents = toCIEXYZ(colorvalue);
		ColorSpace sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		return sRGB.fromCIEXYZ(XYZComponents);
	}

	@Override
	public float getMinValue(int component) {
		switch (component) {
		case L:
			return 0f;
		case a:
			return -1f;
		case b:
			return -1f;
		default:
			return Float.NaN;
		}
	}

	@Override
	public float getMaxValue(int component) {
		switch (component) {
		case L:
			return 100f;
		case a:
			return 1f;
		case b:
			return 1f;
		default:
			return Float.NaN;
		}
	}

	@Override
	public String getName(int component) {
		switch (component) {
		case L:
			return "L*";
		case a:
			return "a*";
		case b:
			return "b*";
		default:
			return null;
		}
	}

	@Override
	public boolean equals(Object anObject) {
	    if (this == anObject) return true;
	    if (anObject == null || anObject.getClass() != this.getClass()) return false;
	    return true;
	}

	@Override
	public int hashCode() {
		return 18;
	}
}