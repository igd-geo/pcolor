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

package de.fhg.igd.pcolor;

import java.util.Comparator;

import de.fhg.igd.pcolor.colorspace.CS_JCh;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * JCh is a three-component space derived from the CIECAM02 color appearance
 * model, where J represents lightness, C represents chroma, and h represents
 * hue angle. It is thought to be the best currently-implemented means of
 * representing value, and so it is employed wherever value is used as a metric.
 * <p>
 * Note the the conversion to and from CIECAM02 is somewhat expensive; it is
 * best to code such that it need be performed as infrequently as possible.
 */
public class JCh extends PColor {
	/**
	 * Lightness
	 */
	public static final int J = 0;

	/**
	 * Chroma
	 */
	public static final int C = 1;

	/**
	 * Hue
	 */
	public static final int h = 2;

	/**
	 * 
	 * @param color color
	 * @param cspace JCh color space
	 */
	public JCh(PColor color, CS_JCh cspace) {
		super(cspace, color);
	}

	/**
	 * with 1 as alpha
	 * @param J Lightness
	 * @param C Chroma
	 * @param h Hue
	 */
	public JCh(float J, float C, float h) {
		this(J, C, h, 1f);
	}

	/**
	 * 
	 * 
	 * @param J Lightness
	 * @param C Chroma
	 * @param h Hue
	 * @param alpha alpha value
	 */
	public JCh(float J, float C, float h, float alpha) {
		super(CS_JCh.defaultInstance, new float[] {J, C, h}, alpha);
	}

	/**
	 * Full ctor. Interprets correlates in the given space.
	 * @param J Lightness
	 * @param C Chroma
	 * @param h Hue
	 * @param alpha alpha value
	 * @param cspace JCh color space
	 */
	public JCh(float J, float C, float h, float alpha, CS_JCh cspace) {
		super(cspace, new float[] {J, C, h}, alpha);
	}

	/**
	 * Calculates the mean JCh color in an array of JCh colors.
	 * 
	 * @param jchColors -
	 *            An array of JCh colors
	 * @return A new average JCh color in the default colorspace.
	 */
    public static JCh average(JCh[] jchColors) {
    	double J = 0, a = 0, b = 0, alpha = 0;

    	// perform average in Jab space
    	for(int i = 0; i < jchColors.length; i++) {
    		// handle lightness
    		J += jchColors[i].get(0);

    		// handle hue and chroma
    		double hRad = Math.toRadians(jchColors[i].get(2));
    		a += jchColors[i].get(1) * Math.cos(hRad);
    		b += jchColors[i].get(1) * Math.sin(hRad);

    		// handle alpha
			alpha += jchColors[i].getAlpha();
    	}
    	J /= jchColors.length;
    	a /= jchColors.length;
    	b /= jchColors.length;
    	alpha /= jchColors.length;

    	// convert back to JCh
    	double h = ColorTools.calculateAtan(a, b);
    	float C = (float)(a / Math.cos(Math.toRadians(h)));

		return new JCh((float)J, (float)C, (float)h, (float)alpha);
    }

    /**
	 * Returns a new array blending an array of JCh colors. weights[] should be
	 * an array of floats equal of length equal to colors[], with each value
	 * representing that color's weight in the blend. For example, if a color is
	 * weighted 1.0 colorBlend() will return just that color, whereas if two
	 * colors are both weighted 0.5 the result will be halfway between them.
	 * <P>
	 * Note that weights[] must be normalized such that total sum of all values
	 * in the array == 1.0; otherwise, the result will be distorted.
	 * 
	 * @param colors -
	 *            The array of colors to be blended together
	 * @param weights -
	 *            The array of weights specifying how much each color figures in
	 *            the final result
	 * @return A new blended JCh color in the default colorspace.
	 */
    public static JCh colorBlend(JCh[] colors, float[] weights) {
        double J = 0, a = 0, b = 0;

        // perform blending in Jab space
    	for(int i = 0; i < colors.length; i++) {
    		J += colors[i].get(0) * weights[i];

    		double hRad = Math.toRadians(colors[i].get(2));
    		a += (colors[i].get(1) * Math.cos(hRad)) * weights[i];
    		b += (colors[i].get(1) * Math.sin(hRad)) * weights[i];
    	}

    	// return to JCh
        double h = ColorTools.calculateAtan(a, b);
    	double C = a / Math.cos(Math.toRadians(h));

    	// return result
    	return new JCh((float)J, (float)C, (float)h, 1f);
    }

	/**
	 * Calculates the distance between two JCh colors using the CIE's
	 * recommended CAM02-UCS calculation.
	 * 
	 * @param color1
	 *            the first color
	 * @param color2
	 *            the second color
	 * @return the distance between two JCh colors.
	 */
    public static double distance(JCh color1, JCh color2) {
    	return distance(color1, color2, 1, 1);
    }

	/**
	 * Calculates the distance between two JCh colors using the CIE's
	 * recommended CAM02-UCS calculation and the specified weights.
	 * 
	 * @param color1
	 *            the first color
	 * @param color2
	 *            the second color
	 * @param lightnessWeight
	 *            the extent, relative to colorfulnessWeight, that lightness
	 *            should figure into the calculation
	 * @param colorfulnessWeight
	 *            the extent, relative to lightnessWeight, that colorfulness
	 *            should figure into the calculation
	 * @return the distance between two JCh colors.
	 */
    public static double distance(JCh color1, JCh color2, float lightnessWeight, float colorfulnessWeight) {
    	// Normalize weights
    	float max = Math.max(lightnessWeight, colorfulnessWeight);
    	if(max <= 0) {
    		lightnessWeight = colorfulnessWeight = 0;
    	} else {
        	lightnessWeight = lightnessWeight / max;
        	colorfulnessWeight = colorfulnessWeight / max;
    	}

    	// Calculate J' a'M b'M colors
    	double[] JabPrime1 = getJabPrime(color1);
    	double[] JabPrime2 = getJabPrime(color2);

    	// calculate Cartesian distance between JabPrime colors
    	return Math.sqrt(
    			Math.pow((JabPrime1[0] - JabPrime2[0]) * lightnessWeight, 2) +
    			Math.pow((JabPrime1[1] - JabPrime2[1]) * colorfulnessWeight, 2) +
    			Math.pow((JabPrime1[2] - JabPrime2[2]) * colorfulnessWeight, 2));
    }

    private static double[] getJabPrime(JCh color) {
    	double[] result = new double[4];

    	CS_JCh cspace1 = (CS_JCh)color.getColorSpace();
    	double M = cspace1.calculateM(color.get(1));
    	double MPrime = (1d / 0.0228) * Math.log(1d + 0.0228 * M);

    	result[0] = ((1 + 100 * 0.007) * color.get(0)) / (1d + 0.007 * color.get(0));
    	result[1] = MPrime * Math.cos(Math.toRadians(color.get(2)));
    	result[2] = MPrime * Math.sin(Math.toRadians(color.get(2)));
    	result[3] = color.getAlpha();

    	return result;
    }

    /**
     * a lightness comparator
     * @author Thu Huong
     *
     */
    public static class LightnessComparator implements Comparator<JCh> {
    	@Override
		public int compare(JCh a, JCh b) {
			if(a.get(0) < b.get(0)) {
				return -1;
			} else if(a.get(0) > b.get(0)) {
				return 1;
			} else {
				return 0;
			}
		}
    }

    @Override
	public JCh convertFrom(PColor color) {
    	if (color.getColorSpace().equals(this.getColorSpace()))
			return (JCh)color;
		return new JCh(color, (CS_JCh)this.getColorSpace());
	}

    @Override
	public JCh clone() {
		return new JCh(this, (CS_JCh) this.getColorSpace());
	}
}