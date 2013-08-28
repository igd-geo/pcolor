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
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.MathTools;


/**
 * This class implements any colorspace derived from the CIECAM02
 * color appearance model by selecting correlates. The class features
 * a set of pre-defined correlate configurations which completely
 * define an appearance.
 */
public class CS_CAMLch extends CS_CIECAM02 {

	private static final long serialVersionUID = -7026433492783612490L;

	/**
	 * Pseudo-correlate for Brightness(Q) or lightness (J)
	 */
	public static final int L = 0;

	/**
	 * Pseudo-correlate for saturation (s), chroma (C), or colorfulness (M)
	 */
	public static final int c = 1;

	/**
	 * Pseudo-correlate for hue (h), or hue composition (H)
	 */
	@SuppressWarnings("hiding")
	public static final int h = 2;
	
	/**
	 * the correlate configuration; the array of correlate indexes representing L, c, h in order.
	 */
	private final int[] correlateIndex;

	/**
	 * @param whitePoint XYZ white point
	 * @param L_A average luminance of visual surround
	 * @param Y_b adaptation luminance of color background
	 * @param correlates the CIECAM02 correlates to use for the L, c, h pseudo-correlates
	 * @param sur surrounding
	 */
	public CS_CAMLch(CIEXYZ whitePoint, double L_A, double Y_b, Surrounding sur, int... correlates) {
		super(whitePoint, L_A, Y_b, sur);
		if (correlates.length != getNumComponents())
			throw new IllegalArgumentException("correlates have the wrong size");
		this.correlateIndex = correlates;
	}

	/**
	 * @param cond viewing conditions
	 * @param correlates the CIECAM02 correlates to use for the L, c, h pseudo-correlates
	 */
	public CS_CAMLch(ViewingConditions cond, int... correlates) {
		super(cond);
		if (correlates.length != getNumComponents())
			throw new IllegalArgumentException("correlates have the wrong size");
		this.correlateIndex = correlates;
	}

	@Override
	public int getNumComponents() {
		return 3;
	}
	
	/**
	 * @return the color space's correlate configuration
	 */
	public int[] getCorrelateConfiguration() {
		return correlateIndex.clone();
	}

	@Override
	public int getType() {
		return TYPE_3CLR;
	}

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		float[] cam = super.fromCIEXYZ(colorvalue);
		return fromCIECAM(cam);
	}

	
	protected float[] fromCIECAM(float[] cam) {
		return fromCIECAMCorrelates(cam, correlateIndex);
	}

	private float[] fromCIECAMCorrelates(float[] cam, int[] cIndex) {
		return new float[] { cam[cIndex[L]], cam[cIndex[c]], cam[cIndex[h]] };
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		float[] cam = toCIECAM(colorvalue);
		return super.toCIEXYZ(cam);
	}

	/**
	 * Un-pack colorvalue from Lch to its contained CIECAM02 correlates.
	 * Unset correlates will be NaN. 
	 * @param colorvalue the Lch value
	 * @return an array packed with the corresponding CIECAM02 correlates
	 */
	protected float[] toCIECAM(float[] colorvalue) {
		float[] cam = new float[super.getNumComponents()];
		Arrays.fill(cam, Float.NaN);
		// translate channels
		cam[correlateIndex[L]] = colorvalue[L];
		cam[correlateIndex[c]] = colorvalue[c];
		cam[correlateIndex[h]] = colorvalue[h];
		return cam;
	}
	
	/**
	 * Reconfigure to a different CIECAM02 correlate configuration.
	 * This is less cumbersome and more precise than a full
	 * CAM -> XYZ -> CAM cycle.
	 * @param colorvalue the value to convert
	 * @param correlates the correlates to switch to
	 * @return a CAMLch color value based on the given correlates
	 */
	public float[] reconfigure(float[] colorvalue, int... correlates) {
		float[] cam = toCIECAM(colorvalue);
		// complete the unset correlates (some corner cases need)
		fillForward(cam);
		fillReverse(cam);
		// in most cases this will just be testing for completeness,
		// but some cases need the final forward fill
		// if (!MathTools.isReal(cam))
		fillForward(cam);
		float[] lch = fromCIECAMCorrelates(cam, correlates);
		if (!MathTools.isReal(cam))
			throw new IllegalArgumentException("colorvalue could not be completed to new correlates");
		return lch;
	}

	@Override
	public String getName(int component) {
		return super.getName(correlateIndex[component]);
	}

	@Override
	public float getMaxValue(int component) {
		return super.getMaxValue(correlateIndex[component]);
	}

	@Override
	public float getMinValue(int component) {
		return super.getMinValue(correlateIndex[component]);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(correlateIndex);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CS_CAMLch other = (CS_CAMLch) obj;
		if (!Arrays.equals(correlateIndex, other.correlateIndex))
			return false;
		return true;
	}
	
	/**
	 * the JCh correlate configuration
	 */
	public static int[] JCh = new int[] { CS_CIECAM02.J, CS_CIECAM02.C, CS_CIECAM02.h };
	/**
	 * the JCH correlate configuration
	 */
	public static int[] JCH = new int[] { CS_CIECAM02.J, CS_CIECAM02.C, CS_CIECAM02.H };
	/**
	 * the JMH correlate configuration
	 */
	public static int[] JMh = new int[] { CS_CIECAM02.J, CS_CIECAM02.M, CS_CIECAM02.h };
	/**
	 * the JMh correlate configuration
	 */
	public static int[] JMH = new int[] { CS_CIECAM02.J, CS_CIECAM02.M, CS_CIECAM02.H};
	/**
	 * the Jsh correlate configuration
	 */
	public static int[] Jsh = new int[] { CS_CIECAM02.J, CS_CIECAM02.s, CS_CIECAM02.h };
	/**
	 * the JsH correlate configuration
	 */
	public static int[] JsH = new int[] { CS_CIECAM02.J, CS_CIECAM02.s, CS_CIECAM02.H };
	/**
	 * the QCh correlate configuration
	 */
	public static int[] QCh = new int[] { CS_CIECAM02.Q, CS_CIECAM02.C, CS_CIECAM02.h };
	/**
	 * the QCH correlate configuration
	 */
	public static int[] QCH = new int[] { CS_CIECAM02.Q, CS_CIECAM02.C, CS_CIECAM02.H };
	/**
	 * the QMh correlate configuration
	 */
	public static int[] QMh = new int[] { CS_CIECAM02.Q, CS_CIECAM02.M, CS_CIECAM02.h };
	/**
	 * the QMH correlate configuration
	 */
	public static int[] QMH = new int[] { CS_CIECAM02.Q, CS_CIECAM02.M, CS_CIECAM02.H };
	/**
	 * the Qsh correlate configuration
	 */
	public static int[] Qsh = new int[] { CS_CIECAM02.Q, CS_CIECAM02.s, CS_CIECAM02.h };
	/**
	 * the QsH correlate configuration
	 */
	public static int[] QsH = new int[] { CS_CIECAM02.Q, CS_CIECAM02.s, CS_CIECAM02.H };
	
	/**
	 * An array containing all supported correlate configurations 
	 */
	public static int[][] correlateConfigurations = new int[][] {
														JCh, JCH, JMh, JMH, Jsh, JsH,
														QCh, QCH, QMh, QMH, Qsh, QsH, };
	
	/**
	 * An instance with default viewing conditions and JCh correlate configuration.
	 */
	public static final CS_CAMLch defaultJChInstance = new CS_CAMLch(defaultContext, JCh);
	
}
	