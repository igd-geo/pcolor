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

package de.fhg.igd.pcolor;

import de.fhg.igd.pcolor.colorspace.CS_sRGB;


/**
 * sRGB is a prevalent colorspace in computer graphics, the standard on the web
 * also adopted by Java's libraries.
 * <p>
 * If you don't know which color space you're in, you're likely in sRGB. Note
 * this class works normalised to 0..1, not 0..255.
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
	 * Create an sRGB representation of any PColor.
	 * @param color color
	 */
	public sRGB(PColor color) {
		super(CS_sRGB.instance, color);
	}
	
	/**
	 * Construct an opaque sRGB instance.
	 * @param R Red
	 * @param G Green
	 * @param B Blue
	 */
	public sRGB(float R, float G, float B) {
		this(R, G, B, 1f);
	}
	
	/**
	 * Full ctor.
	 * @param R Red 
	 * @param G Green
	 * @param B Blue
	 * @param alpha alpha value
	 */
	public sRGB(float R, float G, float B, float alpha) {
		super(CS_sRGB.instance, new float[] {R, G, B}, alpha);
	}
	
	/**
	 * @param rgba array of floats in rgba order (a is optional) 
	 */
	public sRGB(float[] rgba) {
		super(CS_sRGB.instance, new float[] {rgba[R], rgba[G], rgba[B]}, rgba.length > 3 ? rgba[3] : 1);
	}
	
    @Override
	public sRGB convertFrom(PColor color) {
    	if (color.getColorSpace().equals(this.getColorSpace()))
			return (sRGB)color;
		return new sRGB(color);
	}
    
    /**
	 * Unpack a 32-Bit ARGB int, normalising to 0..1
	 * @param argb argb color
	 */
	public static sRGB fromArgb(int argb) {
		return new sRGB((argb >> 16 & 0xff) / 255f, (argb >> 8 & 0xff) / 255f, (argb & 0xff) / 255f, (argb >> 24 & 0xff) / 255f);
	}
	
	/**
	 * Create sRGB from RGBA bytes (given as integers)
	 * @param rgba the bytes in RGBA order
	 */
	public static sRGB fromBytes(int... rgba) {
		if (rgba.length == 3)
			return new sRGB(rgba[0] / 255.0f, rgba[1] / 255.0f, rgba[2] / 255.0f);
		else if (rgba.length == 4)
			return new sRGB(rgba[0] / 255.0f, rgba[1] / 255.0f, rgba[2] / 255.0f, rgba[3] / 255.0f);
		else throw new IllegalArgumentException("3 or 4 integers needed");
	}

}