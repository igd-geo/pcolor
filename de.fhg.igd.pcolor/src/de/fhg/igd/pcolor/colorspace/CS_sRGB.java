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

/**
 * This class implements sRGB colorspace, providing forward and backwards
 * transformations from the sRGB colorspace. CS_sRGB color components are
 * normalized to 0.0-1.0.
 */
public class CS_sRGB extends PColorSpace {

	private static final long serialVersionUID = 9219578757932711538L;
	/**
	 * Red
	 */
	public static final int R = 0;
	/**
	 * Green
	 */
	public static final int G = 1;
	/**
	 * Blue
	 */
	public static final int B = 2;

	/**
	 * default constructor
	 * creates sRGB color space with three components
	 */
	public CS_sRGB() {
		super(CS_sRGB, 3);
	}

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		// calculate rgb values
		double[] XYZ = new double[] {colorvalue[0], colorvalue[1], colorvalue[2]};
		double[] rgb = XYZtorgb(XYZ);

		// correct gamma
		for(int i = 0; i < colorvalue.length; i++) {
			if(Math.abs(rgb[i]) <= 0.0031308) {
				rgb[i] *= 12.92;
			} else {
				rgb[i] = Math.signum(rgb[i]) * (1.055 * Math.pow(Math.abs(rgb[i]), 1.0 / 2.4) - 0.055); 
			}
		}
		clipToGamut(rgb);

		float[] result = new float[] {(float)rgb[0], (float)rgb[1], (float)rgb[2]};
		return result;
	}

	/**
	 * converts CIEXYZ to sRGB
	 * @param XYZ CIEXYZ color
	 * @return sRGB color
	 */
	protected static double[] XYZtorgb(double[] XYZ) {
		double[] rgb = new double[3];
		rgb[0] =  3.2404542 * XYZ[0] -  1.5371385 * XYZ[1] - 0.4985314 * XYZ[2];
		rgb[1] = -0.9692660 * XYZ[0] +  1.8760108 * XYZ[1] + 0.0415560 * XYZ[2];
		rgb[2] =  0.0556434 * XYZ[0] -  0.2040259 * XYZ[1] + 1.0572252 * XYZ[2];
		return rgb;
	}

	/**
	 * Modifies an sRGB component array inplace such that all of its coordinates
	 * fall between 0 and 1. This is done by rescaling any OOG points back
	 * towards the center of the gamut (0.5, 0.5, 0.5) such that they fall just
	 * within the gamut. This is a widely used but relatively stupid method of
	 * gamut mapping, meant to be efficient rather than accurate; however, since
	 * we starting from RGB data in the first place we can expect not to have
	 * too many insanely OOG colors to deal with.
	 * 
	 * @param rgb -
	 *            An sRGB component array, to be modified inplace.
	 * @return true if the color was clipped, or false if it wasn't necessary.
	 */
	protected static boolean clipToGamut(double[] rgb) {
		if(rgb[0] < 0d || rgb[0] > 1d || rgb[1] < 0d || rgb[1] > 1d || rgb[2] < 0d || rgb[2] > 1d) {
			// set origin to [0.5 0.5 0.5]
			rgb[0] -= 0.5;
			rgb[1] -= 0.5;
			rgb[2] -= 0.5;
			// determine the furthest coordinate from 0
			double maxMin = 0f;
			for(int i = 0; i < 3; i++) {
				double m = Math.abs(rgb[i]);
				if(m > maxMin) {
					maxMin = m;
				}
			}
			// rescale point such that its coordinates all fall within the gamut, reset origin to [0 0 0]
			double multiplier = 0.5 / maxMin;
			rgb[0] = rgb[0] * multiplier + 0.5f;
			rgb[1] = rgb[1] * multiplier + 0.5f;
			rgb[2] = rgb[2] * multiplier + 0.5f;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		// correct gamma
		double[] rgb = new double[3];
		for(int i = 0; i < rgb.length; i++) {
			if(colorvalue[i] <= 0.04045) {
				rgb[i] = colorvalue[i] / 12.92;
			} else {
				rgb[i] = Math.pow((colorvalue[i] + 0.055) / 1.055, 2.4);
			}
		}

		// calculate XYZ tristimulus values
		double[] XYZ = rgbtoXYZ(rgb);

		float[] result = new float[] {(float)XYZ[0], (float)XYZ[1], (float)XYZ[2]};
		return result;
	}

	/**
	 * converts sRGB to CIEXYZ
	 * @param rgb sRGB color
	 * @return CIEXYZ color
	 */
	protected static double[] rgbtoXYZ(double[] rgb) {
		double[] XYZ = new double[3];
		XYZ[0] = 0.4124564 * rgb[0] +  0.3575761 * rgb[1] + 0.1804375 * rgb[2];
		XYZ[1] = 0.2126729 * rgb[0] +  0.7151522 * rgb[1] + 0.0721750 * rgb[2];
		XYZ[2] = 0.0193339 * rgb[0] +  0.1191920 * rgb[1] + 0.9503041 * rgb[2];
		return XYZ;
	}

	@Override
	public float[] fromRGB(float[] colorvalue) {
		return colorvalue;
	}

	@Override
	public float[] toRGB(float[] colorvalue) {
		return colorvalue;
	}

	@Override
	public float getMinValue(int component) {
		switch (component) {
		case R:
			return 0f;
		case G:
			return 0f;
		case B:
			return 0f;
		default:
			return Float.NaN;
		}
	}

	@Override
	public float getMaxValue(int component) {
		switch (component) {
		case R:
			return 1f;
		case G:
			return 1f;
		case B:
			return 1f;
		default:
			return Float.NaN;
		}
	}

	@Override
	public String getName(int component) {
		switch (component) {
		case R:
			return "R";
		case G:
			return "G";
		case B:
			return "B";
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
		return 8;
	}
}