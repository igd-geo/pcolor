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
		super(de.fhg.igd.pcolor.colorspace.CS_CIEXYZ.instance, color);
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
		super(de.fhg.igd.pcolor.colorspace.CS_CIEXYZ.instance, new float[] {X, Y, Z}, alpha);
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