package de.fhg.igd.pcolor;

import de.fhg.igd.pcolor.colorspace.CS_sRGB;

/**
 * sRGB is a prevalent colorspace in computer graphics, and as such the standard
 * adopted by Java's libraries. In Painterly, sRGB color components are
 * normalized to 0.0-1.0.
 */
public class sRGB extends PColor {
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
	 * 
	 * @param color color
	 */
	public sRGB(PColor color) {
		super(new CS_sRGB(), color);
	}
	
	/**
	 * with 1 as alpha
	 * @param R Red
	 * @param G Green
	 * @param B Blue
	 */
	public sRGB(float R, float G, float B) {
		this(R, G, B, 1f);
	}
	
	/**
	 * 
	 * @param R Red 
	 * @param G Green
	 * @param B Blue
	 * @param alpha alpha value
	 */
	public sRGB(float R, float G, float B, float alpha) {
		super(new CS_sRGB(), new float[] {R, G, B}, alpha);
	}
	
	/**
	 * 
	 * @param argb argb color
	 */
	public sRGB(int argb) {
		this((argb >> 16 & 0xff) / 255f, (argb >> 8 & 0xff) / 255f, (argb & 0xff) / 255f, (argb >> 24 & 0xff) / 255f);
	}

    @Override
	public sRGB convertFrom(PColor color) {
		return new sRGB(color);
	}
	
    @Override
	public sRGB clone() {
		return new sRGB(this);
	}
}