package de.fhg.igd.pcolor;

/**
 * Constants for reference illuminants.
 * @author Simon Thum
 */
public class Illuminant {
	/**
	 * The XYZ whitepoint of standard illuminant D50, which is what JAI and ICC Profiles use.
	 */
	public static final CIEXYZ D50 = new CIEXYZ(96.422f, 100.0f, 82.521f);

	/**
	 * The XYZ whitepoint of standard illuminant D65, which is what sRGB uses.
	 */
	public static final CIEXYZ D65 = new CIEXYZ (95.047f, 100.0f, 108.883f);
	
	/**
	 * The XYZ whitepoint E (equilibrium), useful for relative colorimetry
	 */
	public static final CIEXYZ E = new CIEXYZ (100.0f, 100.0f, 100.0f);
	
	/**
	 * CIE F2, a common fluorescent illuminant. Also known as F, F02,
	 * Fcw, CFW, CFW2, cool white fluorescent. 4230K.
	 */
	public static final CIEXYZ F2 = CIEXYZ.fromxyY(0.37208f, 0.37529f, 100f);
	
	/**
	 * CIE F7, a broadband fluorescent illuminant. Approximates D65. 6500K.
	 */
	public static final CIEXYZ F7 = CIEXYZ.fromxyY(0.31292f, 0.32933f, 100f);
	
	/**
	 * CIE F11, a narrow tri-band fluorescent illuminant. 4000K.
	 */
	public static final CIEXYZ F11 = CIEXYZ.fromxyY(0.38052f, 0.37713f, 100f);
	
}
