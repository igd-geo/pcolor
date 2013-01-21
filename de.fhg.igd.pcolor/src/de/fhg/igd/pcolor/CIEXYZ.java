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

import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;


/**
 * This class contains colors in the CIE XYZ colorspace. While the XYZ
 * colorspace serves as the interchange space between all others, it is not
 * suitable for perceptual tasks. It is a linear intensity space based on (and
 * not to be confused with) the L, M, S primaries found in the human retina.
 */
public class CIEXYZ extends PColor {
	/**
	 * X tristimulus value
	 */
	public static final int X = CS_CIEXYZ.X;
	/**
	 * Y tristimulus value, also known as luminance
	 */
	public static final int Y = CS_CIEXYZ.Y;
	/**
	 * Z tristimulus value
	 */
	public static final int Z = CS_CIEXYZ.Z;

	/**
	 * Creates a CIEXYZ instance which reflects the input color.
	 * @param color color
	 */
	public CIEXYZ(PColor color) {
		super(CS_CIEXYZ.instance, color);
	}
	
	/**
	 * Creates a CIEXYZ instance which reflects the input color.
	 * @param color color
	 */
	public CIEXYZ(float[] color) {
		super(CS_CIEXYZ.instance, color, 
				color.length == 4 ? color[3] : 1);
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
		super(CS_CIEXYZ.instance, new float[] {X, Y, Z}, alpha);
	}

	@Override
	public CIEXYZ convertFrom(PColor color) {
		if (color.getColorSpace().equals(this.getColorSpace()))
			return (CIEXYZ)color;
		return new CIEXYZ(color);
	}

}