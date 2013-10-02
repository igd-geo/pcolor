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

import de.fhg.igd.pcolor.colorspace.CS_CAMLab;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;

/**
 * CAMLab models a catesian mapping to any three-component space derived from
 * the CIECAM02 color appearance model.
 * <p>
 * The name CAMLab is not established; it seeks to represent the concept
 * of a Lab-type space rooted in a color appearance model.
 * <p>
 * Note that optical (light-based) processes such as casting shadows or
 * determining the average light response of a distant phenomenon (scaling)
 * <b>do happen in the optical domain</b>. We just perceive the end result. In
 * other words, please <b>do NOT filter, scale, convolve etc. in this space</b>
 * unless you have very specific reasons to do so. Most likely, <b>doing so is
 * just plain wrong!<b/> Please use XYZ ({@link CIEXYZ}) for that.
 */
public class CAMLab extends PColor {

	/**
	 * Pseudo-correlate for Brightness(Q) or lightness (J)
	 */
	public static final int L = CS_CAMLab.L;

	/**
	 * Red-Green axis
	 */
	public static final int a = CS_CAMLab.a;

	/**
	 * Yellow-blue axis
	 */
	public static final int b = CS_CAMLab.b;

	/**
	 * Construct a CAMLab from any PColor, eventually involving a 
	 * stimulus-preserving conversion. This is true even if the
	 * color is a CAM-based color.
	 * @param color color
	 * @param cspace JCh color space
	 */
	public CAMLab(PColor color, CS_CAMLab cspace) {
		super(cspace, color);
	}
	
	/**
	 * CAMLab Full ctor. Interprets correlates in the given space.
	 * @param cspace JCh color space
	 * @param alpha alpha value
	 * @param comp the components array
	 */
	public CAMLab(float[] comp, float alpha, CS_CAMLab cspace) {
		super(cspace, comp.clone(), alpha);
	}

	@Override
	public CS_CAMLab getColorSpace() {
		return (CS_CAMLab) super.getColorSpace();  // narrow as enforced by ctor
	}

	/**
	 * Calculates the mean CAMLab color in an array of CAMLab colors. Differences in
	 * their individual color spaces are not being accounted for.
	 * 
	 * @param colors
	 * 			An array of CAMLab colors
	 * @return A new average CAMLab color in the colorspace of the first input color.
	 */
    public static CAMLab average(CAMLab[] colors) {
    	return blend(colors, null);
    }

    /**
	 * Returns a new color blending an array of CAMLab colors. weights[] should be
	 * an array of floats equal of length equal to colors[], with each value
	 * representing that color's weight in the blend. For example, if a color is
	 * weighted 1.0 blend() will return just that color, whereas if two
	 * colors are both weighted 0.5 the result will be perceptually halfway between them.
	 * <P>
	 * Note that weights[] must be normalized such that total sum of all values
	 * in the array == 1.0; otherwise, the result will be distorted. Also note that
	 * differences in the individual color spaces are not being accounted for.
	 * 
	 * @param colors
	 *            The array of colors to be blended together
	 * @param weights
	 *            The array of weights specifying how much each color figures in
	 *            the final result
	 * @return A new perceptually blended JCh color in the colorspace of the first input color.
	 * @see CIEXYZ#blend(CIEXYZ[], float[])
	 */
    public static CAMLab blend(CAMLab[] colors, float[] weights) {
        double acc_L = 0, acc_a = 0, acc_b = 0, acc_alpha = 0;

        // perform blending in Jab space
    	for(int i = 0; i < colors.length; i++) {
    		float w = weights == null ? (float)(1.0/colors.length) : weights[i];
    		acc_L += colors[i].get(L) * w;
    		acc_a += colors[i].get(a) * w;
    		acc_b += colors[i].get(b) * w;
    		
    		// handle alpha
    		acc_alpha += colors[i].getAlpha() * w;
    	}

    	// return result
    	return new CAMLab(new float[] { (float)acc_L, (float)acc_a, (float)acc_b }, (float)acc_alpha, colors[0].getColorSpace());
    }

	/**
	 * Calculates the distance between two CAMLab colors.
	 * 
	 * @param color1
	 *            the first color
	 * @param color2
	 *            the second color
	 * @return the distance between two CAMLab colors.
	 */
    public static double distance(CAMLab color1, CAMLab color2) {
    	return distance(color1, color2, 1, 1);
    }

	/**
	 * Calculates the distance between two Lab colors with weighted lightness/colorfulness weights.
	 * 
	 * @param color1
	 *            the first color
	 * @param color2
	 *            the second color
	 * @param lightnessWeight
	 *            the extent, relative to colorfulnessWeight, that lightness
	 *            should figure into the calculation
	 * @param colorfulnessWeight
	 *            the extent, relative to lightnessWeight, that colorfulness
	 *            should figure into the calculation
	 * @return the distance between two JCh colors.
	 */
    public static double distance(CAMLab color1, CAMLab color2, float lightnessWeight, float colorfulnessWeight) {
    	// Normalize weights
    	float max = Math.max(lightnessWeight, colorfulnessWeight);
    	if(max <= 0) {
    		lightnessWeight = colorfulnessWeight = 0;
    	} else {
        	lightnessWeight = lightnessWeight / max;
        	colorfulnessWeight = colorfulnessWeight / max;
    	}

    	// calculate weighted cartesian distance between colors
    	return Math.sqrt(
    			Math.pow((color1.get(L) - color2.get(L)) * lightnessWeight, 2) +
    			Math.pow((color1.get(a) - color2.get(a)) * colorfulnessWeight, 2) +
    			Math.pow((color1.get(b) - color2.get(b)) * colorfulnessWeight, 2));
    }
    
	@Override
	public PColor transpose(ColorSpace to) {
		// TODO prepend conversion so we can transpose to any perceptual space
		if (to instanceof CS_CAMLch) {
			// CS_CAMLch or CS_CAMLab by virtue of subtyping:
			// reduce to reconfiguration and conversion
			CS_CAMLch toCsLch = (CS_CAMLch) to;
			float[] components = getColorSpace().reconfigure(getComponents(), toCsLch.getCorrelateConfiguration());
			
			// derive ab from ch?
			if (toCsLch instanceof CS_CAMLab) {
				CS_CAMLab camLab = (CS_CAMLab) toCsLch;
				components = camLab.lchToLab(components);
			}
			
			return PColor.create(toCsLch, components, getAlpha());
		} else {
			throw new UnsupportedOperationException("this method supports only CAM-based target spaces");
		}
	}

}