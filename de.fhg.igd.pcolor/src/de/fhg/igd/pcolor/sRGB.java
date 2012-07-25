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
		super(de.fhg.igd.pcolor.colorspace.CS_sRGB.instance, color);
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
		super(de.fhg.igd.pcolor.colorspace.CS_sRGB.instance, new float[] {R, G, B}, alpha);
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