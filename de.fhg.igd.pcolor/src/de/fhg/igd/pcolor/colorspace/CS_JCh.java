package de.fhg.igd.pcolor.colorspace;


/**
 * This class implements the JCh colorspace, which is derived from the CIECAM02
 * color appearance model. Like CS_HSV, this class extends its parent space
 * CS_CIECAM02, but unlike CS_HSV this class' conversion methods are overriden
 * to provide slightly faster transformation to and from JCh than they would be
 * to and from CIECAM02.
 */
public class CS_JCh extends CS_CIECAM02 {

	private static final long serialVersionUID = -7026433492783612490L;

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
	 * default constructor
	 * creates CIECAM02 color space with seven components
	 */
	public CS_JCh() {
		super();
	}

	/**
	 * 
	 * @param XYZWhitePoint XYZ white point
	 * @param L_A average luminance of visual surround
	 * @param Y_b adaptation luminance of color background
	 * @param sur surrounding
	 */
	public CS_JCh(double[] XYZWhitePoint, double L_A, double Y_b, Surrounding sur) {
		super(XYZWhitePoint, L_A, Y_b, sur);
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
		double[] XYZ = new double[] {colorvalue[0] * 100.0, colorvalue[1] * 100.0, colorvalue[2] * 100.0};

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
		double h = calculateh(a, b);

		// calculate chroma
		double e = gete(h);
		double t = forwardt(e, a, b, RGBPrime_a);
		double C = forwardC(J, t);

		float[] result = new float[] {(float)J, (float)C, (float)h};
		return result;
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		// get a, b, p2
		double e = gete(colorvalue[h]);
		double A = reverseA(colorvalue[J]);
		double t = reverset(colorvalue[J], colorvalue[C]);
		double p2 = reversep2(A);
		double[] ab = reverseab(colorvalue[h], e, t, p2);
		double a = ab[0]; double b = ab[1];

		// get XYZ
		double[] RGBPrime_a = reverseResponseCompression(a, b, p2);
		double[] RGBPrime = reverseHPEConeFundamentals(RGBPrime_a);
		double[] RGB = reversePreAdaptationConeResponse(RGBPrime);
		double[] XYZ = reverseXYZ(RGB);

		float[] result = new float[] {(float)(XYZ[0] / 100.0), (float)(XYZ[1] / 100.0), (float)(XYZ[2] / 100.0)};
		return result;
	}

	@Override
	public String getName(int component) {
		switch (component) {
		case J:
			return "J";
		case C:
			return "C";
		case h:
			return "h";
		default:
			return null;
		}
	}

	@Override
	public float getMaxValue(int component) {
		switch (component) {
		case J:
			return 100f;
		case C:
			return 120f;
		case h:
			return 360f;
		default:
			throw new IllegalArgumentException(Integer.toString(component));
		}
	}

	@Override
	public float getMinValue(int component) {
		switch (component) {
		case J:
			return 0f;
		case C:
			return 0f;
		case h:
			return 0f;
		default:
			throw new IllegalArgumentException(Integer.toString(component));
		}
	}

	@Override
	public ColorSpaceType getColorSpaceType() {
		return new PolarCoordinateType(1, 2);
	}
}