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

    /**
     * Gets the euclidean distance between two rgb colors
     */
    public static float distance(sRGB rgb1, sRGB rgb2) {
    	return distance(rgb1, rgb2, new float[] {1, 1, 1, 0});
    }

    /**
     * calculates the distance between two sRGB colors
     * @param rgb1 sRGB color
     * @param rgb2 sRGB color
     * @param weights
     * @return the distance
     */
    public static float distance(sRGB rgb1, sRGB rgb2, float[] weights) {
    	// normalize weights to 1.0
    	float highest = Float.MIN_VALUE;
    	for(int i = 0; i < weights.length; i++) {
    		if(weights[i] > highest) highest = weights[i];
    	}
    	float[] normalizedWeights = new float[weights.length];
    	for(int i = 0; i < weights.length; i++) {
    		normalizedWeights[i] = weights[i] * 1 / highest;
    	}

    	// perform distance calculation 
        float distance = 0;
        distance += Math.pow(rgb1.get(R) - rgb2.get(R), 2) * weights[R];
        distance += Math.pow(rgb1.get(G) - rgb2.get(G), 2) * weights[G];
        distance += Math.pow(rgb1.get(B) - rgb2.get(B), 2) * weights[B];
        distance += Math.pow(rgb1.getAlpha() - rgb2.getAlpha(), 2) * weights[3];
        return (float)Math.sqrt(distance);
    }

    /**
     * 
     * @return the highest channel
     */
    public float getHighestChannel() {
    	return Math.max(this.get(R), Math.max(this.get(G), this.get(B)));
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