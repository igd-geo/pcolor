package de.fhg.igd.pcolor.colorspace;

import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.JCh;

/**
 * This class implements the Jab colorspace, which is derived from the CIECAM02
 * color appearance model. The advantage of using CIECAM02's red-green (a) and
 * yellow-blue (b) correlates is that the colorspace is Euclidian (as opposed to
 * JCh, which uses a polar coordinate system) and can therefore be used for
 * convolution filtering or rescaling.
 */
public class CS_Jab extends CS_CIECAM02 {

	private static final long serialVersionUID = 5134072948736740218L;

	/**
	 * Lightness
	 */
	public static final int J = 0;

	/**
	 * Red-Green
	 */
	public static final int a = 1;

	/**
	 * Yellow-Blue
	 */
	public static final int b = 2;

	/**
	 * default constructor
	 * creates CIECAM02 color space with seven components
	 */
	public CS_Jab() {
		super();
	}

	/**
	 * 
	 * @param XYZWhitePoint XYZ white point
	 * @param L_A average luminance of visual surround
	 * @param Y_b adaptation luminance of color background
	 * @param sur surrounding
	 */
	public CS_Jab(double[] XYZWhitePoint, double L_A, double Y_b, Surrounding sur) {
		super(XYZWhitePoint, L_A, Y_b, sur);
	}

	/**
	 *
	 * @param cond viewing conditions
	 */
	public CS_Jab(ViewingConditions cond) {
		super(cond);
	}

	@Override
	public int getNumComponents() {
		return 3;
	}

	@Override
	public int getType() {
		return TYPE_3CLR;
	}

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		double[] XYZ = new double[] { colorvalue[0] * 100.0, colorvalue[1] * 100.0, colorvalue[2] * 100.0 };

		// calculcate RGBPrime_a
		double[] RGB = forwardPreAdaptationConeResponse(XYZ);
		double[] RGB_c = forwardPostAdaptationConeResponse(RGB);
		double[] RGBPrime = forwardHPEConeFundamentals(RGB_c);
		double[] RGBPrime_a = forwardResponseCompression(RGBPrime);

		// calculate lightness
		double A = forwardA(RGBPrime_a);
		double J = forwardJ(A);

		// calculate hue
		double a = forwarda(RGBPrime_a);
		double b = forwardb(RGBPrime_a);

		float[] result = new float[] { (float) J, (float) a, (float) b };
		return result;
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		// get p2
		double A = reverseA(colorvalue[J]);
		double p2 = reversep2(A);

		// get XYZ
		double[] RGBPrime_a = reverseResponseCompression(colorvalue[a], colorvalue[b], p2);
		double[] RGBPrime = reverseHPEConeFundamentals(RGBPrime_a);
		double[] RGB = reversePreAdaptationConeResponse(RGBPrime);
		double[] XYZ = reverseXYZ(RGB);

		float[] result = new float[] { (float) (XYZ[0] / 100.0), (float) (XYZ[1] / 100.0), (float) (XYZ[2] / 100.0) };
		return result;
	}

	/**
	 * converts JCh to Jab
	 * @param colorvalue JCh color
	 * @return Jab color
	 */
	public float[] fromJCh(float[] colorvalue) {
		// get a, b
		double e = gete(colorvalue[JCh.h]);
		double A = reverseA(colorvalue[JCh.J]);
		double t = reverset(colorvalue[JCh.J], colorvalue[JCh.C]);
		double p2 = reversep2(A);
		double[] ab = reverseab(colorvalue[JCh.h], e, t, p2);

		return new float[] {colorvalue[JCh.J], (float) ab[0], (float) ab[1]};
	}

	/**
	 * converts Jab to JCh
	 * @param colorvalue Jab color
	 * @return JCh color
	 */
	public float[] toJCh(float[] colorvalue) {
		// get h
		double h = calculateh(colorvalue[a], colorvalue[b]);

		// get RGBPrime_a
		double A = reverseA(colorvalue[J]);
		double p2 = reversep2(A);
		double[] RGBPrime_a = reverseResponseCompression(colorvalue[a], colorvalue[b], p2);

		// get C
		double e = gete(h);
		double t = forwardt(e, colorvalue[a], colorvalue[b], RGBPrime_a);
		double C = forwardC(colorvalue[J], t);

		return new float[] {colorvalue[J], (float) C, (float) h};
	}

	@Override
	public String getName(int component) {
		switch (component) {
		case J:
			return "J";
		case a:
			return "a";
		case b:
			return "b";
		default:
			return null;
		}
	}

	@Override
	public float getMinValue(int component) {
		switch (component) {
		case J:
			return 0f;
		case a:
			return -Float.MAX_VALUE;
		case b:
			return -Float.MAX_VALUE;
		default:
			return Float.NaN;
		}
	}

	@Override
	public float getMaxValue(int component) {
		switch (component) {
		case J:
			return 100f;
		case a:
			return Float.MAX_VALUE;
		case b:
			return Float.MAX_VALUE;
		default:
			return Float.NaN;
		}
	}
}
