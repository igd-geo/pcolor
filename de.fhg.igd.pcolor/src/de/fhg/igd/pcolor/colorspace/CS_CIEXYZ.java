package de.fhg.igd.pcolor.colorspace;

import java.awt.color.ColorSpace;

/**
 * This class implements the CIE XYZ colorspace; calling toCIEXYZ(float[]
 * components) or fromCIEXYZ(float[] components) will simply return components.
 */
public class CS_CIEXYZ extends PColorSpace {

	private static final long serialVersionUID = -7923913005471667746L;
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
	 * default constructor
	 * creates CIEXYZ color space with three components
	 */
	public CS_CIEXYZ() {
		super(CS_CIEXYZ, 3);
	}

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		return colorvalue;
	}

	@Override
	public float[] fromRGB(float[] colorvalue) {
		ColorSpace sRGB = new CS_sRGB();
		float[] XYZComponents = sRGB.toCIEXYZ(colorvalue);
		return fromCIEXYZ(XYZComponents);
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		return colorvalue;
	}

	@Override
	public float[] toRGB(float[] colorvalue) {
		float[] XYZComponents = toCIEXYZ(colorvalue);
		ColorSpace sRGB = new CS_sRGB();
		return sRGB.fromCIEXYZ(XYZComponents);
	}

	@Override
	public float getMinValue(int component) {
		switch (component) {
		case X:
			return 0f;
		case Y:
			return 0f;
		case Z:
			return 0f;
		default:
			return Float.NaN;
		}
	}

	@Override
	public float getMaxValue(int component) {
		switch (component) {
		case X:
			return 1f;
		case Y:
			return 1f;
		case Z:
			return 1f;
		default:
			return Float.NaN;
		}
	}

	@Override
	public String getName(int component) {
		switch (component) {
		case X:
			return "X";
		case Y:
			return "Y";
		case Z:
			return "Z";
		default:
			return null;
		}
	}

	@Override
	public boolean equals(Object anObject) {
		if(anObject == null) return false;
	    if (this == anObject) return true;
	    if (anObject == null || anObject.getClass() != this.getClass()) return false;
	    return true;
	}

	@Override
	public int hashCode() {
		return 14;
	}
}