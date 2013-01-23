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

import java.awt.color.ColorSpace;

import de.fhg.igd.pcolor.colorspace.CS_Jab;

/**
 * Jab is a three-component sub-space of the CIECAM02 color appearance model,
 * where J represents lightness, a represents red-green color, and b represents
 * yellow-blue color. The advantage of using CIECAM02's red-green (a) and
 * yellow-blue (b) correlates is that the colorspace is Euclidian (as opposed to
 * JCh, which uses a polar coordinate system) and can therefore be used for
 * color distance measurement or perceptual interpolation.
 * <p>
 * Note that optical (light-based) processes such as casting shadows or
 * determining the average light response of a distant phenomenon (scaling)
 * <b>do happen in the optical domain</b>. We just perceive the end result. In
 * other words, <b>do NOT filter, scale, convolve etc. in this space</b> unless
 * you have very specific reasons to do so. Most likely, <b>doing so is just
 * plain wrong!<b/>
 */
public class Jab extends PColor {
	/**
	 * Lightness
	 */
	public static final int J = 0;

	/**
	 * Red-Green
	 */
	public static final int a = 1;

	/**
	 * Yellow-Blue
	 */
	public static final int b = 2;

	/**
	 * Construct a Jab from any PColor, eventually involving a 
	 * stimulus-preserving conversion.
	 * @param color color
	 * @param cspace Jab color space
	 */
	public Jab(PColor color, CS_Jab cspace) {
		super(cspace, color);
	}

	/**
	 * Creates a Jab instance which reflects the input appearance under default
	 * viewing conditions with opaque alpha.
	 * @param J Lightness
	 * @param a Red-Green
	 * @param b Yellow-Blue
	 */
	public Jab(float J, float a, float b) {
		this(J, a, b, 1f);
	}
	
	/**
	 * Creates a Jab instance which reflects the input appearance under default
	 * viewing conditions.
	 * @param color color
	 */
	public Jab(float[] color) {
		super(CS_Jab.defaultInstance, color, 1);
	}

	/**
	 * Creates a Jab instance which reflects the input appearance under default
	 * viewing conditions.
	 * @param J Lightness
	 * @param a Red-Green
	 * @param b Yellow-Blue
	 * @param alpha alpha value
	 */
	public Jab(float J, float a, float b, float alpha) {
		super(CS_Jab.defaultInstance, new float[] {J, a, b}, alpha);
	}

	/**
	 * Full ctor. Interprets correlates in the given space.
	 * @param J Lightness
	 * @param a Red-Green
	 * @param b Yellow-Blue
	 * @param alpha alpha value
	 * @param cspace Jab color space
	 */
	public Jab(float J, float a, float b, float alpha, CS_Jab cspace) {
		super(cspace, new float[] {J, a, b}, alpha);
	}
	
	/**
	 * Full ctor. Interprets correlates in the given space. The array
	 * is cloned to prevent shared state.
	 * @param comp the components
	 * @param alpha alpha value
	 * @param cspace Jab color space
	 */
	public Jab(float[] comp, float alpha, CS_Jab cspace) {
		super(cspace, comp.clone(), alpha);
	}
	
	@Override
	public PColor transpose(ColorSpace to) {
		// TODO prepend conversion so we can transpose to any perceptual space
		if (to instanceof CS_Jab) {
			float[] components = getComponents();
			return new Jab(components[J],
					components[a],
					components[b],
					getAlpha(),
					(CS_Jab) to);
		} else {
			throw new UnsupportedOperationException("this method supports only Jab target spaces");
		}
	}

	@Override
	public Jab convertFrom(PColor color) {
		if (color.getColorSpace().equals(this.getColorSpace()))
			return (Jab)color;
		return new Jab(color, (CS_Jab)this.getColorSpace());
	}

}