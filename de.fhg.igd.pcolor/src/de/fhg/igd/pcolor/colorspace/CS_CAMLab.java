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
 * This class implements a cartesian CIECAM02-derived colorspace. It builds upon
 * any {@link CS_CAMLch} space to provide cartesian coordinates (a, b) from the
 * correlates represented by (c, h) pseudo-correlates. It goes a bit beyond what
 * is covered by CIECAM02 as the a, b cartesian correlates can be based not just
 * on h but also on H.
 * <p>
 * Please note that this space is not related to CIELAB/Lab/L*a*b*.
 * <p>
 * The advantage of using CIECAM02's red-green (a) and yellow-blue (b)
 * correlates is that the colorspace is Euclidian (as opposed to JCh, which uses
 * a polar coordinate system) and can therefore be used for perceptual
 * difference estimation and other perceptually proportional operations.
 */
public class CS_CAMLab extends CS_CAMLch {

	private static final long serialVersionUID = 5134072948736740218L;

	/**
	 * Pseudo-correlate for Brightness(Q) or lightness (J)
	 */
	@SuppressWarnings("hiding")
	public static final int L = 0;

	/**
	 * Red-Green
	 */
	public static final int a = 1;

	/**
	 * Yellow-Blue
	 */
	public static final int b = 2;
	
	/**
	 * @param cond viewing conditions
	 * @param correlates the CIECAM02 correlates to use for the L, c, h pseudo-correlates
	 */
	public CS_CAMLab(ViewingConditions cond, int... correlates) {
		super(cond, correlates);
	}
	
	/**
	 * Factory for deriving CAMLab from CAMLch based on identical correlates;
	 * optimizes the case where the Lch is actually a Lab to help with subtyping
	 * pitfalls.
	 * 
	 * @param base
	 *            the base colorspace
	 * @return a CS_CAMLab instance based on the given Lch
	 */
	public static CS_CAMLab deriveFromLch(CS_CAMLch base) {
		// be a subtyping helper
		if (base instanceof CS_CAMLab)
			return (CS_CAMLab) base;
		return new CS_CAMLab(base.getViewingconditions(), base.getCorrelateConfiguration());
	}

	@Override
	protected float[] fromCIECAM(float[] cam) {
		float[] Lch = super.fromCIECAM(cam);
		return lchToLab(Lch);
	}

	
	/**
	 * Convert a Lch triplet to Lab. This solely involves a geometric transform,
	 * not much color calculation.
	 * @param Lch the triplet
	 * @return the Lab values
	 */
	public float[] lchToLab(float[] Lch) {
		double toRad = toRadFactor();
		return new float[] {Lch[L], Lch[c] * (float)Math.cos(Lch[h] * toRad), Lch[c] * (float)Math.sin(Lch[h] * toRad) };
	}

	@Override
	protected float[] toCIECAM(float[] colorvalue) {
		float[] Lch = labToLch(colorvalue);
		return super.toCIECAM(Lch);
	}

	/**
	 * Convert a Lab triplet to Lch. This solely involves a geometric transform,
	 * not much color calculation.
	 * @param colorvalue the triplet
	 * @return the Lch values
	 */
	public float[] labToLch(float[] colorvalue) {
		float len = (float) Math.hypot(colorvalue[a], colorvalue[b]);
		float ang = (float) Math.atan2(colorvalue[b], colorvalue[a]);
		if (ang < 0)
			ang += (float) Math.PI * 2;
		ang /= toRadFactor();
		float [] Lch = new float[] { colorvalue[L], len, ang };
		return Lch;
	}

	@Override
	public int getNumComponents() {
		return 3;
	}

	@Override
	public int getType() {
		return TYPE_3CLR;
	}

	/**
	 * return the to radians factor required for the h pseudocorrelate.
	 */
	private double toRadFactor() {
		switch ((int)super.getMaxValue(CS_CAMLch.h)) {
		case 360:
			return Math.PI / 180;
		case 400:
			return Math.PI / 200;
		default:
			throw new IllegalStateException("hue correlate cannot be coverted");
		}
	}

	@Override
	public String getName(int component) {
		switch (component) {
		case a:
			return "a_" + super.getName(c) + super.getName(h);
		case b:
			return "b_" + super.getName(c) + super.getName(H);
		default:
			return super.getName(component);
		}
	}
	
	@Override
	public float getMinValue(int component) {
		switch (component) {
		case a:
		case b:
			return - super.getMaxValue(c);
		default:
			return super.getMinValue(component);
		}
	}

	@Override
	public float getMaxValue(int component) {
		switch (component) {
		case a:
		case b:
			return super.getMaxValue(c);
		default:
			return super.getMaxValue(component);
		}
	}
	
	/**
	 * A CS_CAMLab instance based on JMh (Ja<sub>M</sub>b<sub>M</sub>).
	 */
	public static final CS_CAMLab defaultJaMbMInstance = new CS_CAMLab(defaultContext, CS_CAMLch.JMh);
	
	/**
	 * An instance with default viewing conditions and JaMbM correlate configuration.
	 */
	@SuppressWarnings("hiding") // we actually do not want to end up with pure CIECAM from here
	public static final CS_CAMLch defaultInstance = defaultJaMbMInstance;

}
