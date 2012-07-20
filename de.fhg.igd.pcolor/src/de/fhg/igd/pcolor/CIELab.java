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

import de.fhg.igd.pcolor.colorspace.CS_CIELab;

/**
 * This class contains PColors in the CIE L*a*b* colorspace. It is not currently
 * used; JCh is preferred, since it corrects many of CIE Lab's issues with bent
 * lines of consistent hue and uneven chroma spacing.
 */
public class CIELab extends PColor {
	/**
	 * Lightness
	 */
	public static final int L = 0;
	/**
	 * Red-Green
	 */
	public static final int a = 1;
	/**
	 * Yellow-Blue
	 */
	public static final int b = 2;

	/**
	 * 
	 * @param color color
	 */
	public CIELab(PColor color) {
		super(new CS_CIELab(), color);
	}

	/**
	 * with 1 as alpha value
	 * @param L Lightness
	 * @param a Red-Green
	 * @param b Yellow-Blue
	 */
	public CIELab(float L, float a, float b) {
		this(L, a, b, 1f);
	}

	/**
	 * 
	 * @param L Lightness
	 * @param a Red-Green
	 * @param b Yellow-Blue
	 * @param alpha alpha value
	 */
	public CIELab(float L, float a, float b, float alpha) {
		super(new CS_CIELab(), new float[] {L, a, b}, alpha);
	}

	@Override
	public CIELab convertFrom(PColor color) {
		return new CIELab(color);
	}

	@Override
	public CIELab clone() {
		return new CIELab(this);
	}
}