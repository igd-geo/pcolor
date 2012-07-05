package de.fhg.igd.pcolor.colorspace;

import java.awt.color.ColorSpace;
import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * This class implements the CIECAM02 color appearance model, providing forward
 * and backwards transformations from the CIE XYZ colorspace. Each CS_CIECAM02
 * object contains within a small Context class, which is responsible for
 * determining the numerous pre-calculated surround variables associated with
 * the CIECAM02 model. Constructors are provided for setting these variables to
 * non-default values; however, it is usually only necessary to invoke the
 * class' no-argument constructor, which makes use of a static Context object to
 * conserve memory.
 */
public class CS_CIECAM02 extends PColorSpace {

	private static final long serialVersionUID = -4262171288421726143L;

	/**
	 * Lightness
	 */
	public static final int J = 0;

	/**
	 * Brightness
	 */
	public static final int Q = 1;

	/**
	 * Chroma
	 */
	public static final int C = 2;

	/**
	 * Colorfulness
	 */
	public static final int M = 3;

	/**
	 * Saturation
	 */
	public static final int s = 4;

	/**
	 * Hue Composition
	 */
	public static final int H = 5;

	/**
	 * Hue
	 */
	public static final int h = 6;

	/**
	 * The XYZ whitepoint of standard illuminant D50, which is what Java uses.
	 */
	public static double[] D50White = new double[] {96.422, 100.0, 82.521};

	/**
	 * The XYZ whitepoint of standard illuminant D65, which is what sRGB uses.
	 */
	public static double[] D65White = new double[] {95.047, 100.0, 108.883};

	/**
	 * default context
	 */
	protected static ViewingConditions defaultContext;

	/**
	 * context
	 */
	protected ViewingConditions context;

	/**
	 * default constructor
	 * creates a CIECAM02 color space with default context
	 */
	public CS_CIECAM02() {
		super(TYPE_7CLR, 7);
		if(defaultContext == null) instantiateDefaultContext();
		context = defaultContext;
	}

	/**
	 * sets default context with D65 white point, L_A = 64, Y_b = 20 and c = 0.69
	 */
	public static void instantiateDefaultContext() {
		defaultContext = new ViewingConditions(D65White, 64.0, 20.0, Surrounding.averageSurrounding);
	}

	/**
	 * 
	 * @param XYZWhitePoint XYZ white point
	 * @param L_A average luminance of visual surround
	 * @param Y_b adaptation luminance of color background
	 * @param sur surrounding
	 */
	public CS_CIECAM02(double[] XYZWhitePoint, double L_A, double Y_b, Surrounding sur) {
		super(TYPE_7CLR, 7);
		this.context = new ViewingConditions(XYZWhitePoint, L_A, Y_b, sur);
	}

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		double[] XYZ = new double[] {colorvalue[0] * 100.0, colorvalue[1] * 100.0, colorvalue[2] * 100.0};

		// calculate sharpened cone response
		double[] RGB = forwardPreAdaptationConeResponse(XYZ);

		// calculate corresponding (sharpened) cone response considering various luminance level and surround conditions in D
		double[] RGB_c = forwardPostAdaptationConeResponse(RGB);

		// calculate HPE equal area cone fundamentals
		double[] RGBPrime = forwardHPEConeFundamentals(RGB_c);

		// calculate response-compressed postadaptation cone response
		double[] RGBPrime_a = forwardResponseCompression(RGBPrime);

		// calculate achromatic response
		double A = forwardA(RGBPrime_a);

		// calculate lightness
		double J = forwardJ(A);

		// calculate brightness
		double Q = calculateQ(J);

		// calculate redness-greenness and yellowness-blueness color opponent values
		double a = forwarda(RGBPrime_a);
		double b = forwardb(RGBPrime_a);

		// calculate hue angle
		double h = calculateh(a, b);

		// calculate hue composition
		double H = calculateH(h);

		// calculate eccentricity
		double e = gete(h);

		// get t
		double t = forwardt(e, a, b, RGBPrime_a);

		// calculate the correlates of chroma, colorfulness, and saturation
		double C = forwardC(J, t);
		double M = calculateM(C);
		double s = calculates(M, Q);

		float[] result = new float[] {(float)J, (float)Q, (float)C, (float)M, (float)s, (float)H, (float)h};
		return result;
	}

	/**
	 * calculate sharpened cone response
	 * @param XYZ XYZ color
	 * @return sharpened cone response
	 */
	protected double[] forwardPreAdaptationConeResponse(double[] XYZ) {
		return XYZtoCAT02(XYZ);
	}

	/**
	 * converts from CIEXYZ to CIECAT02
	 * @param XYZ CIEXYZ color
	 * @return CIECAT02 color
	 */
	public static double[] XYZtoCAT02(double[] XYZ) {
		double[] RGB = new double[3];
		RGB[0] =  0.7328 * XYZ[0] + 0.4296 * XYZ[1] - 0.1624 * XYZ[2];
		RGB[1] = -0.7036 * XYZ[0] + 1.6975 * XYZ[1] + 0.0061 * XYZ[2];
		RGB[2] =  0.0030 * XYZ[0] + 0.0136 * XYZ[1] + 0.9834 * XYZ[2];
		return RGB;
	}

	/**
	 * calculate corresponding (sharpened) cone response considering various luminance level and surround conditions in D
	 * @param RGB sharpened cone response
	 * @return corresponding (sharpened) cone response
	 */
	protected double[] forwardPostAdaptationConeResponse(double[] RGB) {
		return new double[] {context.getD_RGB()[0] * RGB[0], context.getD_RGB()[1] * RGB[1], context.getD_RGB()[2] * RGB[2]};
	}

	/**
	 * calculate HPE equal area cone fundamentals
	 * @param RGB_c corresponding (sharpened) cone response
	 * @return HPE cone fundamentals
	 */
	protected double[] forwardHPEConeFundamentals(double[] RGB_c) {
		return CAT02toHPE(RGB_c);
	}

	/**
	 * convertes from CIECAT02 to Hunt–Pointer–Estévez space
	 * @param RGB sRGB color
	 * @return color in HPE 
	 */
	public static double[] CAT02toHPE(double[] RGB) {
		double[] RGBPrime = new double[3];
		RGBPrime[0] =  0.7409792 * RGB[0] + 0.2180250 * RGB[1] + 0.0410058 * RGB[2];
		RGBPrime[1] =  0.2853532 * RGB[0] + 0.6242014 * RGB[1] + 0.0904454 * RGB[2];
		RGBPrime[2] = -0.0096280 * RGB[0] - 0.0056980 * RGB[1] + 1.0153260 * RGB[2];
		return RGBPrime;
	}

	/**
	 * calculate response-compressed postadaptation cone response
	 * @param RGB HPE cone fundamentals
	 * @return postadaptation cone response
	 */
	protected double[] forwardResponseCompression(double[] RGB) {
		double[] result = new double[3];
		for(int channel = 0; channel < RGB.length; channel++) {
			if(RGB[channel] >= 0) {
				double n = Math.pow(context.getF_L() * RGB[channel] / 100.0, 0.42);
				result[channel] = 400.0 * n / (n + 27.13) + 0.1;
			} else {
				double n = Math.pow(-1.0 * context.getF_L() * RGB[channel] / 100.0, 0.42);
				result[channel] = -400.0 * n / (n + 27.13) + 0.1;
			}
		}
		return result;
	}

	/**
	 * calculate achromatic response
	 * @param RGB postadaptation cone response
	 * @return achromatic response
	 */
	protected double forwardA(double[] RGB) {
		return (2.0 * RGB[0] + RGB[1] + RGB[2] / 20.0 - 0.305) * context.getN_bb();
	}

	/**
	 * calculate lightness
	 * @param A achromatic response
	 * @return lightness
	 */
	protected double forwardJ(double A) {
		return 100.0 * Math.pow(A / context.getA_w(), context.getZ() * context.getSurrounding().getC());
	}

	/**
	 * calculate redness-greenness color opponent values
	 * @param RGBPrime_a postadaptation cone response
	 * @return redness-greenness color opponent values
	 */
	protected double forwarda(double[] RGBPrime_a) {
		return RGBPrime_a[0] + (-12.0 * RGBPrime_a[1] + RGBPrime_a[2]) / 11.0;
	}

	/**
	 * calculate yellowness-blueness color opponent values
	 * @param RGBPrime_a postadaptation cone response
	 * @return yellowness-blueness color opponent values
	 */
	protected double forwardb(double[] RGBPrime_a) {
		return (RGBPrime_a[0] + RGBPrime_a[1] - 2.0 * RGBPrime_a[2]) / 9.0;
	}

	/**
	 * calculate eccentricity
	 * @param h hue angle
	 * @return eccentricity
	 */
	protected double gete(double h) {
		return ((12500.0 / 13.0) * context.getSurrounding().getN_c() * context.getN_cb()) * (Math.cos(Math.toRadians(h) + 2.0) + 3.8);
	}

	/**
	 * calculate preliminary magnitude
	 * @param e eccentricity
	 * @param a redness-greenness color opponent values
	 * @param b yellowness-blueness color opponent values
	 * @param RGBPrime_a postadaptation cone response
	 * @return preliminary magnitude
	 */
	protected double forwardt(double e, double a, double b, double[] RGBPrime_a) {
		return e * Math.sqrt(Math.pow(a, 2.0) + Math.pow(b, 2.0)) / (RGBPrime_a[0] + RGBPrime_a[1] + 1.05 * RGBPrime_a[2]);
	}

	/**
	 * calculate the correlates of chroma
	 * @param J lightness
	 * @param t preliminary magnitude
	 * @return Chroma
	 */
	protected double forwardC(double J, double t) {
		return Math.signum(t) * Math.pow(Math.abs(t), 0.9) * Math.sqrt(J / 100.0) * Math.pow(1.64-Math.pow(0.29, context.getN()), 0.73);
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		// calculate e
		double e = gete(colorvalue[h]);

		// calculate achromatic response
		double A = reverseA(colorvalue[J]);

		// calculate a and b
		double t = reverset(colorvalue[J], colorvalue[C]);
		double p2 = reversep2(A);
		double[] ab = reverseab(colorvalue[h], e, t, p2);
		double a = ab[0]; double b = ab[1];

		// calculate postadaptation cone response (resulting in dynamic range compression)
		double[] RGBPrime_a = reverseResponseCompression(a, b, p2);

		// calculate HPE response
		double[] RGBPrime = reverseHPEConeFundamentals(RGBPrime_a);

		// calculate sharpened cone responses
		double[] RGB = reversePreAdaptationConeResponse(RGBPrime);

		// calculate XYZ tristimulus values
		double[] XYZ = reverseXYZ(RGB);

		float[] result = new float[] {(float)(XYZ[0] / 100.0), (float)(XYZ[1] / 100.0), (float)(XYZ[2] / 100.0)};
		return result;
	}

	/**
	 * calculate achromatic response
	 * @param J lightness
	 * @return achromatic response
	 */
	protected double reverseA(double J) {
		return context.getA_w() * Math.pow(J / 100.0, 1 / (context.getSurrounding().getC() * context.getZ()));
	}

	/**
	 * calculate preliminary magnitude t
	 * @param J lightness
	 * @param C chroma
	 * @return preliminary magnitude t
	 */
	protected double reverset(double J, double C) {
		double temp = (Math.sqrt(J / 100.0) * Math.pow(1.64 - Math.pow(0.29, context.getN()), 0.73));
		if(temp == 0.0) return 0.0;
		else return Math.pow(C / temp, 1.0 / 0.9);
	}

	/**
	 * calculate preliminary magnitude p2
	 * @param A achromatic response
	 * @return preliminary magnitude p2
	 */
	protected double reversep2(double A) {
		return A / context.getN_bb() + 0.305;
	}

	/**
	 * calculate a and b
	 * @param h hue
	 * @param e eccentricity
	 * @param t
	 * @param p2
	 * @return red-green, yellow-blue
	 */
	protected double[] reverseab(double h, double e, double t, double p2) {
		double a, b;
		double p3 = 1.05;
		if(t != 0) {
			double hRad = Math.toRadians(h);
			double p1 = e * (1.0 / t);
			double i;
			if(Math.abs(Math.sin(hRad)) >= Math.abs(Math.cos(hRad))) {
				i = Math.cos(hRad) / Math.sin(hRad);
				double p4 = p1 / Math.sin(hRad);
				b = (p2 * (2 + p3) * (460.0 / 1403.0)) / (p4 + (2 + p3) * (220.0 / 1403.0) * i - (27.0 / 1403.0) + p3 * (6300.0 / 1403.0));
				a = b * i;
			} else {
				i = Math.sin(hRad) / Math.cos(hRad);
				double p5 = p1 / Math.cos(hRad);
				a = (p2 * (2.0 + p3) * (460.0 / 1403.0)) / (p5 + (2.0 + p3) * (220.0 / 1403.0) - (27.0 / 1403.0 - p3 * 6300.0 / 1403.0) * i);
				b = a * i;
			}
		} else {
			a = b = 0;
		}
		return new double[] {a, b};
	}

	/**
	 * calculate postadaptation cone response (resulting in dynamic range compression)
	 * @param a red-green
	 * @param b yellow-blue
	 * @param p2
	 * @return postadaptation cone response 
	 */
	protected double[] reverseResponseCompression(double a, double b, double p2) {
		double[] RGBPrime_a = new double[3];
		double j = 460.0 / 1403.0 * p2;
		RGBPrime_a[0] = j + 451.0 / 1403.0 * a + 288.0 / 1403.0 * b;
		RGBPrime_a[1] = j - 891.0 / 1403.0 * a - 261.0 / 1403.0 * b;
		RGBPrime_a[2] = j - 220.0 / 1403.0 * a - 6300.0 / 1403.0 * b;
		return RGBPrime_a;
	}

	/**
	 * calculate HPE response
	 * @param RGBPrime_a postadaptation cone response
	 * @return HPE response
	 */
	protected double[] reverseHPEConeFundamentals(double[] RGBPrime_a) {
		double[] RGBPrime = new double[3];
		for(int i = 0; i < RGBPrime_a.length; i++) {
			double n = RGBPrime_a[i] - 0.1;
			if(n == 0) {
				RGBPrime[i] = 0.0;
			} else {
				double k = Math.abs(RGBPrime_a[i] - 0.1);
				RGBPrime[i] = 100.0 / context.getF_L() * Math.pow((27.13 * k) / (400.0 - k), 1.0 / 0.42);
				if(n < 0) RGBPrime[i] *= -1.0;
			}
		}
		return RGBPrime;
	}

	/**
	 * calculate sharpened cone responses
	 * @param RGBPrime HPE response
	 * @return sharpened cone responses
	 */
	protected double[] reversePreAdaptationConeResponse(double[] RGBPrime) {
		double[] RGB = HPEtoCAT02(RGBPrime);
		for(int i = 0; i < RGB.length; i++) {
			RGB[i] /= context.getD_RGB()[i];
		}
		return RGB;
	}

	private static double[] HPEtoCAT02(double[] RGBPrime) {
		double[] RGB = new double[3];
		RGB[0] =  1.5591524816 * RGBPrime[0] - 0.54472286880 * RGBPrime[1] - 0.0144452544 * RGBPrime[2];
		RGB[1] = -0.7143269842 * RGBPrime[0] + 1.85030961140 * RGBPrime[1] - 0.1359760488 * RGBPrime[2];
		RGB[2] =  0.0107755110 * RGBPrime[0] + 0.00521876240 * RGBPrime[1] + 0.9840056152 * RGBPrime[2];
		return RGB;
	}

	/**
	 * calculate XYZ tristimulus values
	 * @param RGB sharpened cone responses
	 * @return XYZ tristimulus values
	 */
	protected double[] reverseXYZ(double[] RGB) {
		return CAT02toXYZ(RGB);
	}

	private static double[] CAT02toXYZ(double[] RGB) {
		double[] XYZ = new double[3];
		XYZ[0] =  1.096124 * RGB[0] - 0.278869 * RGB[1] + 0.182745 * RGB[2];
		XYZ[1] =  0.454369 * RGB[0] + 0.473533 * RGB[1] + 0.072098 * RGB[2];
		XYZ[2] = -0.009628 * RGB[0] - 0.005698 * RGB[1] + 1.015326 * RGB[2];
		return XYZ;
	}

	@Override
	public float[] fromRGB(float[] colorvalue) {
		ColorSpace sRGB = new CS_sRGB();
		float[] XYZComponents = sRGB.toCIEXYZ(colorvalue);
		return fromCIEXYZ(XYZComponents);
	}

	@Override
	public float[] toRGB(float[] colorvalue) {
		float[] XYZComponents = toCIEXYZ(colorvalue);
		ColorSpace sRGB = new CS_sRGB();
		return sRGB.fromCIEXYZ(XYZComponents);
	}

	/**
	 * An efficient method for determining only the lightness (J) of an arbitrary color
	 * @param color - The color whose lightness will be returned.
	 * @return A float value containing color's lightness correlate.
	 */
	public static float getLightness(PColor color) {
		return getLightness(color, new CS_CIECAM02());
	}

	/**
	 * An efficient method for determining only the lightness (J) of an arbitrary color
	 * @param color - The color whose lightness will be returned.
	 * @param cspace - The CIECAM02 colorspace in which color's lightness will be calculated.
	 * @return A float value containing color's lightness correlate.
	 */
	public static float getLightness(PColor color, CS_CIECAM02 cspace) {
		if(color.getColorSpace().equals(cspace)) {
			return color.get(0);
		} else {
			float[] comp = new CIEXYZ(color).getComponents();
			double[] XYZ = new double[] {comp[0] * 100, comp[1] * 100, comp[2] * 100};

			// get RGBPrime_a
			double[] RGB = cspace.forwardPreAdaptationConeResponse(XYZ);
			double[] RGB_c = cspace.forwardPostAdaptationConeResponse(RGB);
			double[] RGBPrime = cspace.forwardHPEConeFundamentals(RGB_c);
			double[] RGBPrime_a = cspace.forwardResponseCompression(RGBPrime);

			// return lightness
			double A = cspace.forwardA(RGBPrime_a);
			return (float)cspace.forwardJ(A);
		}
	}

	@Override
	public String getName(int component) {
		switch (component) {
		case J:
			return "J";
		case Q:
			return "Q";
		case C:
			return "C";
		case M:
			return "M";
		case s:
			return "s";
		case H:
			return "H";
		case h:
			return "h";
		default:
			return null;
		}
	}

	/**
	 * calculate lightness
	 * @param Q brightness
	 * @return lightness
	 */
	public double calculateJ(double Q) {
		return 6.25 * Math.pow(context.getSurrounding().getC() * Q / ((context.getA_w() + 4) * Math.pow(context.getF_L(), 0.25)), 2.0);
	}

	/**
	 * calculate brightness
	 * @param J lightness
	 * @return brightness
	 */
	public double calculateQ(double J) {
		return (4.0 / context.getSurrounding().getC()) * Math.sqrt(J / 100.0) * (context.getA_w() + 4.0) * Math.pow(context.getF_L(), 0.25);
	}

	/**
	 * calculate chroma
	 * @param M colorfulness
	 * @return chroma
	 */
	public double calculateC(double M) {
		return M / Math.pow(context.getF_L(), 0.25);
	}

	/**
	 * calfulate chroma
	 * @param s saturation
	 * @param Q brightness
	 * @return chroma
	 */
	public double calculateC(double s, double Q) {
		return Math.pow(s / 100.0, 2.0) * Q / Math.pow(context.getF_L(), 0.25);
	}

	/**
	 * calculate colorfulness
	 * @param C chroma
	 * @return colorfulness
	 */
	public double calculateM(double C) {
		return C * Math.pow(context.getF_L(), 0.25);
	}

	/**
	 * calculate saturation
	 * @param M colorfulness
	 * @param Q brightness
	 * @return saturation
	 */
	public static double calculates(double M, double Q) {
		return 100.0 * Math.sqrt(M / Q);
	}

	/**
	 * calculate hue composition
	 * @param h hue
	 * @return hue composition
	 */
	public static double calculateH(double h) {
		double i;
		if (h < 20.14) {
			i = (h + 122.47) / 1.2;
			return 300.0 + 100.0 * i / (i + (20.14 - h) / 0.8);
		} else if (h < 90.0) {
			i = (h - 20.14) / 0.8;
			return 100.0 * i / (i + (90 - h) / 0.7);
		} else if (h < 164.25) {
			i = (h - 90) / 0.7;
			return 100.0 + 100.0 * i / (i + (164.25 - h) / 1);
		} else if (h < 237.53) {
			i = (h - 164.25) / 1.0;
			return 200.0 + 100.0 * i / (i + (237.53 - h) / 1.2);
		} else {
			i = (h - 237.53) / 1.2;
			return 300.0 + 100.0 * i / (i + (380.14 - h) / 0.8);
		}
	}

	/**
	 * calculate hue
	 * @param a red-green
	 * @param b yellow-blue
	 * @return hue
	 */
	public static double calculateh(double a, double b) {
		return ColorTools.calculateAtan(a, b);
	}

	/**
	 * calculate hue
	 * @param H hue composition
	 * @return hue
	 */
	public static double calculateh(double H) {
		double i, h;
		if (H < 100.0) {
			i = H;
			h = (i * -57.902 - 1409.8) / (i * -0.1 - 70.0);
		} else if (H < 200.0) {
			i = H - 100;
			h = (i * -24.975 - 9000.0) / (i * 0.3 - 100.0);
		} else if (H < 300.0) {
			i = H - 200.0;
			h = (i * -40.43 - 19710.0) / (i * 0.2 - 120.0);
		} else {
			i = H - 300.0;
			h = (i * -266.144 - 19002.4) / (i * -0.4 - 80.0);
		}
		if(h > 360.0) return h - 360.0;
		else return h;
	}

	@Override
	public boolean equals(Object anObject) {
	    if (this == anObject) return true;
	    if (anObject == null || anObject.getClass() != this.getClass()) return false;
	    CS_CIECAM02 object = (CS_CIECAM02)anObject;
	    return this.context.equals(object.context);
	}

	@Override
	public int hashCode() {
		return context.hashCode();
	}
}