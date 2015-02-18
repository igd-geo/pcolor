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
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * CAMLch models any three-component space derived from the CIECAM02 color appearance
 * model, where L represents J or Q, c represents chroma, colorfulness or saturation,
 * and h represents hue angle(h) or hue composition (H).
 * <p>
 * Note that optical (light-based) processes such as casting shadows or
 * determining the average light response of a distant phenomenon (scaling)
 * <b>do happen in the optical domain</b>. We just perceive the end result. In
 * other words, please <b>do NOT filter, scale, convolve etc. in this space</b>
 * unless you have very specific reasons to do so. Most likely, <b>doing so is
 * just plain wrong!</b> Please use XYZ ({@link CIEXYZ}) for that.
 */
public class CAMLch extends PColor {

	/**
	 * Pseudo-correlate for Brightness(Q) or lightness (J)
	 */
	public static final int L = CS_CAMLch.L;

	/**
	 * Pseudo-correlate for saturation (s), chroma (C), or colorfulness (M)
	 */
	public static final int c = CS_CAMLch.c;

	/**
	 * Pseudo-correlate for hue (h), or hue composition (H)
	 */
	public static final int h = CS_CAMLch.h;

	/**
	 * Construct a CAMLch from any PColor, eventually involving a 
	 * stimulus-preserving conversion. This is true even if the
	 * color is a CAM color.
	 * @param color color
	 * @param cspace a CAMLch color space
	 */
	public CAMLch(PColor color, CS_CAMLch cspace) {
		super(cspace, color);
		if (cspace instanceof CS_CAMLab)
			throw new IllegalArgumentException("mismatching colorspace given.");
	}
	
	/**
	 * CS_CAMLab Full ctor. Interprets correlates in the given space.
	 * @param cspace JCh color space
	 * @param alpha alpha value
	 * @param comp the components array
	 */
	public CAMLch(float[] comp, float alpha, CS_CAMLch cspace) {
		super(cspace, comp.clone(), alpha);
		if (cspace instanceof CS_CAMLab)
			throw new IllegalArgumentException("mismatching colorspace given.");
	}

	@Override
	public CS_CAMLch getColorSpace() {
		return (CS_CAMLch) super.getColorSpace();
	}

	/**
	 * Calculates the distance between two CAMLch colors. This distance is
	 * a polar distance, which is mainly useful to check distances.
	 * 
	 * @param color1
	 *            the first color
	 * @param color2
	 *            the second color
	 * @return the distance between two JCh colors.
	 */
    public static double distance(CAMLch color1, CAMLch color2) {
    	return distance(color1, color2, 1, 1, 1);
    }

	/**
	 * Calculates the distance between two CAMLch colors. This distance is
	 * a polar distance, which is mainly useful to check distances.
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
	 * @param hueWeight the weight to give to hue distance.
	 * @return the distance between two JCh colors.
	 */
    public static double distance(CAMLch color1, CAMLch color2, float lightnessWeight, float colorfulnessWeight, float hueWeight) {
    	assert color1.getColorSpace().equals(color2.getColorSpace());
    	// Normalize weights
    	float max = Math.max(lightnessWeight, Math.max(colorfulnessWeight, hueWeight));
    	if(max <= 0) {
    		lightnessWeight = colorfulnessWeight = hueWeight = 1;
    	} else {
        	lightnessWeight = lightnessWeight / max;
        	colorfulnessWeight = colorfulnessWeight / max;
        	hueWeight = hueWeight / max;
    	}
    	
    	// normalize weights to correlate range
    	lightnessWeight /= color1.getColorSpace().getMaxValue(L);
    	colorfulnessWeight /= color1.getColorSpace().getMaxValue(c);
    	hueWeight /= color1.getColorSpace().getMaxValue(h);

    	// calculate sort-of polar distance
		return Math.sqrt(
		    			Math.pow((color1.get(L) - color2.get(L)) * lightnessWeight, 2) +
		    			Math.pow((color1.get(c) - color2.get(c)) * colorfulnessWeight, 2) +
		    			Math.pow(ColorTools.hueDistance(color1.get(h), color2.get(h), color2.getColorSpace().getMaxValue(h)) * hueWeight, 2));
    }

    
    
	/**
	 * Blends two CAMLch colors such that the shortest angular hue
	 * difference is blended. This operation is quite pointless on
	 * more than one color.
	 * 
	 * @param a the first color
	 * @param b the second color
	 * @param b_weight the weight of b, ranging from 0..1 
	 * @return a blended CAMLch color
	 */
    public static CAMLch blend(CAMLch a, CAMLch b, float b_weight) {
    	assert a.getColorSpace().equals(b.getColorSpace());
    	if (b_weight <= 0)
    		return a;
    	else if (b_weight >= 1)
    		return b;
    	float hdiff = ColorTools.hueDifference(
    			a.get(CAMLch.h),
    			b.get(CAMLch.h),
    			a.getColorSpace().getMaxValue(h));
    	float Jdiff = b.get(CAMLch.L) - a.get(CAMLch.L);
    	float Cdiff = b.get(CAMLch.c) - a.get(CAMLch.c);
    	float adiff = b.getAlpha() - a.getAlpha();
    	return new CAMLch(new float[] {
	    			a.get(CAMLch.L) + Jdiff * b_weight,
	    			a.get(CAMLch.c) + Cdiff * b_weight,
	    			a.get(CAMLch.h) + hdiff * b_weight
    			},
    			a.getAlpha() + adiff * b_weight,
    			a.getColorSpace());
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
			if (toCsLch instanceof CS_CAMLab && !(getColorSpace() instanceof CS_CAMLab)) {
				CS_CAMLab camLab = (CS_CAMLab) toCsLch;
				components = camLab.lchToLab(components);
			}
			
			return PColor.create(toCsLch, components, getAlpha());
		} else {
			throw new UnsupportedOperationException("this method supports only CAM-based target spaces");
		}
	}

}