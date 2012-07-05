package de.fhg.igd.pcolor;

import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;

/**
 * This class contains Pcolors in the CIE XYZ colorspace. While the XYZ
 * colorspace serves as the interchange format between all others, it is not
 * used in Painterly to represent permanent color data.
 */
public class CIEXYZ extends PColor {
	/**
	 * Red
	 */
	public static final int X = 0;
	/**
	 * Green
	 */
	public static final int Y = 1;
	/**
	 * Blue
	 */
	public static final int Z = 2;

	/**
	 * 
	 * @param color color
	 */
	public CIEXYZ(PColor color) {
		super(new CS_CIEXYZ(), color);
	}

	/**
	 * with 1 as alpha value
	 * @param X Red
	 * @param Y Green
	 * @param Z Blue
	 */
	public CIEXYZ(float X, float Y, float Z) {
		this(X, Y, Z, 1f);
	}

	/**
	 * 
	 * @param X Red
	 * @param Y Green
	 * @param Z Blue
	 * @param alpha alpha value
	 */
	public CIEXYZ(float X, float Y, float Z, float alpha) {
		super(new CS_CIEXYZ(), new float[] {X, Y, Z}, alpha);
	}

	@Override
	public CIEXYZ convertFrom(PColor color) {
		return new CIEXYZ(color);
	}

	@Override
	public CIEXYZ clone() {
		return new CIEXYZ(this);
	}
}