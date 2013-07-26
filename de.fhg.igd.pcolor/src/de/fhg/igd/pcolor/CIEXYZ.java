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
		super(CS_CIEXYZ.instance, color, 1);
	}

	/**
	 * with 1 as alpha value
	 * @param X tristimulus value
	 * @param Y tristimulus value, also known as luminance
	 * @param Z tristimulus value
	 */
	public CIEXYZ(float X, float Y, float Z) {
		this(X, Y, Z, 1f);
	}

	/**
	 * @param X tristimulus value
	 * @param Y tristimulus value, also known as luminance
	 * @param Z tristimulus value
	 * @param alpha alpha value
	 */
	public CIEXYZ(float X, float Y, float Z, float alpha) {
		super(CS_CIEXYZ.instance, new float[] {X, Y, Z}, alpha);
	}
	
	/**
	 * Construct XYZ from the xy chrominance and Y luminance
	 * @param xyY  xy chrominance and Y luminance
	 * @return the corresponding XYZ
	 */
	public static CIEXYZ fromxyY(float... xyY) {
		float x = xyY[0];
		float y = xyY[1];
		float Y = xyY[2];
		if (y == 0)
			return new CIEXYZ(0, 0, 0);
		else
			return new CIEXYZ(x*Y/y, Y, (1-x-y)*Y/y);
	}
	
	/**
	 * Convert to xyY. This is a minor derived helper space so it has no own type.
	 * @return a float array containing xyY respectively
	 * @see CIEXYZ#fromxyY(float...)
	 */
	public float[] toxyY() {
		float s = get(X) + get(Y) + get(Z);
		if (s == 0) {
			return new float[] {0f,0f,0f};
		}
		return new float[] {
			get(X) / s,
			get(Y) / s,
			get(Y)
		};
	}
	
	/**
	 * Calculates the mean JCh color in an array of JCh colors. Differences in
	 * their individual color spaces are not being accounted for.
	 * 
	 * @param jchColors
	 * 			An array of JCh colors
	 * @return A new average JCh color in the colorspace of the first input color.
	 */
    public static CIEXYZ average(CIEXYZ[] colors) {
    	return blend(colors, null);
    }
	
	/**
	 * Returns a new XYZ color blending an array of JCh colors. weights[] should be
	 * an array of floats equal of length equal to colors[], with each value
	 * representing that color's weight in the blend. For example, if a color is
	 * weighted 1.0 blend() will return just that color, whereas if two
	 * colors are both weighted 0.5 the result will be optically halfway between them.
	 * <P>
	 * Note that weights[] must be normalized such that total sum of all values
	 * in the array == 1.0; otherwise, the result will be distorted.
	 * 
	 * @param colors
	 *            The array of colors to be blended together
	 * @param weights
	 *            The array of weights specifying how much each color figures in
	 *            the final result
	 * @return A new optically blended XYZ color.
	 */
    public static CIEXYZ blend(CIEXYZ[] colors, float[] weights) {
        double X = 0, Y = 0, Z = 0, alpha = 0;

        // perform blending in XYZ space
    	for(int i = 0; i < colors.length; i++) {
    		float w = weights == null ? (float)(1.0/colors.length) : weights[i];
    		X += colors[i].get(CIEXYZ.X) * w;
    		Y += colors[i].get(CIEXYZ.Y) * w;
    		Z += colors[i].get(CIEXYZ.Z) * w;
    		alpha += colors[i].getAlpha() * w;
    	}
    	
    	// return result
    	return new CIEXYZ((float)X, (float)Y, (float)Z, (float)alpha);
    }

}