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

package de.fhg.igd.pcolor.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhg.igd.pcolor.CAMLab;
import de.fhg.igd.pcolor.CAMLch;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;

/**
 * A series of methods and utilities for dealing with various color operations.
 */
public class ColorTools {

    /**
	 * Returns the smallest difference between two hues as a absolute float
	 * (in degrees).
     * @param hue1 the first hue
     * @param hue2 the second hue
	 * @return distance between two hues.
	 */
    public static float hueDistance(float hue1, float hue2)
    {
		return Math.abs(hueDifference(hue1, hue2));
    }
    
    /**
	 * Returns the smallest difference between two hues as a absolute float
	 * (in the ring specified).
     * @param hue1 the first hue
     * @param hue2 the second hue
     * @param c the length of the full circle
	 * @return distance between two hues.
	 */
    public static float hueDistance(float hue1, float hue2, float c)
    {
		return Math.abs(hueDifference(hue1, hue2, c));
    }

	/**
	 * Returns the smallest difference between two hues as a float (in degrees);
	 * negative values mean that hue2 is closer in the 'negative' direction (for
	 * instance, if hue1 is 5.0, and hue2 is 0.0, this function will return
	 * -5.0).
     * @param hue1 the first hue
     * @param hue2 the second hue
	 * @return Smallest difference between two hues.
	 */
    public static float hueDifference(float hue1, float hue2)
    {
	    return hueDifference(hue1, hue2, 360);
    }
    
    /**
	 * Returns the smallest difference between two hues as a float where c is
	 * the length of the full circle;
	 * negative values mean that hue2 is closer in the 'negative' direction (for
	 * instance, if hue1 is 5.0, and hue2 is 0.0, this function will return
	 * -5.0).
     * @param hue1 the first hue
     * @param hue2 the second hue
     * @param c the length of the full circle
	 * @return Smallest difference between two hues.
	 */
    public static float hueDifference(float hue1, float hue2, float c)
    {
	    float difference = (hue2 - hue1) % c;
	    float ch = c / 2;
		if (difference > ch)
			difference -= c;
		if (difference < -ch)
			difference += c;
		return difference;
    }
	
	/**
	 * Returns a delta E distance in CAM02-UCS as published in "Uniform Colour Spaces Based on
	 * CIECAM02 Colour Appearance Model" (Luo et al.). This is shown to be a good
	 * measure of perceptual distance.
	 * @see CAMLab#distance(CAMLab, CAMLab)
	 * @param col1 the first color.
	 * @param col2 the second color
	 * @param vc the viewing conditions the distance is to be evaluated under
	 * @return a delta E, more accurately the CAM02-UCS distance between col1 and col2 
	 */
	public static float distance(PColor col1, PColor col2, ViewingConditions vc) {
		CS_CAMLch compSpace = new CS_CAMLch(vc, CS_CAMLch.JMh);
		CAMLch col1Lch = (CAMLch) PColor.convert(col1, compSpace);
		CAMLch col2Lch = (CAMLch) PColor.convert(col2, compSpace);
		return distance(col1Lch, col2Lch);
	}

	/**
	 * Returns a delta E distance in CAM02-UCS as published in "Uniform Colour Spaces Based on
	 * CIECAM02 Colour Appearance Model" (Luo et al.). This is shown to be a good
	 * measure of perceptual distance.
	 * @param col1 the first color.
	 * @param col2 the second color
	 * @return a delta E, more accurately the CAM02-UCS distance between col1 and col2 
	 */
	public static float distance(CAMLch col1, CAMLch col2) {
		// check for JMh correlates?
		float[] c1ucs = toUCS_Jab(col1);
		float[] c2ucs = toUCS_Jab(col2);
		return MathTools.vectorDistance(c1ucs, c2ucs);
	}

	/**
	 * Return a float array representing CAM-UCS coordinates of the input.
	 * See M. R. Luo, G. Cui, and C. Li, “Uniform colour spaces based on CIECAM02
	 * colour appearance model,” Color Research & Application,
	 * vol. 31, no. 4, pp. 320–330, Aug. 2006.
	 * @param col the input color
	 * @return a float array representing J'a'b'
	 */
	public static float[] toUCS_Jab(CAMLch col) {
		float J = col.get(CAMLch.L);
		float M = col.get(CAMLch.c);
		float h = col.get(CAMLch.h);
		float sJ = ((1+100*0.007f)*J)/(1f + 0.007f * J);
		float sM = (float)((1.0/0.0228)*Math.log(1 + 0.0228 * M));
		float a = (float)(sM * Math.cos(Math.toRadians(h)));
		float b = (float)(sM * Math.sin(Math.toRadians(h)));
		return new float[] {sJ, a, b };
	}
	
	/**
	 * Create a new color which has one channel changed in comparison to the
	 * argument color.
	 * 
	 * @param color
	 *            the color
	 * @param channel
	 *            the channel index
	 * @param value
	 *            the value to set
	 * @return a new pcolor with the new channel value
	 */
	@SuppressWarnings("unchecked")
	public static <P extends PColor> P setChannel(P color, int channel, float value) {
		if (channel >= 0 && channel <= color.getColorSpace().getNumComponents()) {
			float[] arr = color.getRawComponents();
			arr[channel] = value;
			return (P) PColor.create(color.getColorSpace(), arr);
		} else {
			throw new IllegalArgumentException("channel " + channel + " is not in range");
		}
	}
	
	/**
	 * Set a channel on an array of colors, the palette.
	 * @param palette an array of colors
	 * @param channel the channel to set
	 * @param value the value to set the channel to, on each color.
	 * @return the input palette array (for convenience)
	 */
	public static <C extends PColor> C[] setChannel(C[] palette, int channel, float value) {
		for (int i = 0; i < palette.length; i++) {
			palette[i] = setChannel(palette[i], channel, value);
		}
		return palette;
	}
	
	/**
	 * Performs a binary search for a boundary of an implicitly defined
	 * partition in an arbitrary color space. For example, this can be used to
	 * find the most saturated or brightest possible color of a given hue.
	 * 
	 * Assuming that col is actually part of the partition identified by the
	 * predicate and the lower, upper boundary contains exactly one such boundary,
	 * this method tests the boundary until an answer with an error of at most e
	 * is found.
	 * 
	 * @param col
	 *            the color at the lower bound
	 * @param channel
	 *            the channel whose boundary is being tested
	 * @param lower
	 *            the lower bound on the channel
	 * @param upper
	 *            the upper bound on the channel
	 * @param e
	 *            the distance in JCh to allow for
	 * @param inside
	 *            a predicate defining a space whose boundary is tested
	 * @param vc the viewing conditions to use for comparison
	 * @return the boundary color, when found
	 */
	public static <C extends PColor> C determineBoundaryColor(C col, int channel, float lower,
			float upper, float e, Predicate<? super C> inside, ViewingConditions vc) {
		float middleValue = (upper + lower) / 2f;
		C middleColor = ColorTools.setChannel(col, channel, middleValue);
		// if we woudn't move far anyway, treat as found as col is
		// always assumed to be inside the space identified by the predicate
		if (distance(col, middleColor, vc) < e)
			return col;
		if (inside.apply(middleColor))
			return determineBoundaryColor(middleColor, channel, middleValue, upper,
					e, inside, vc);
		else
			return determineBoundaryColor(col, channel, lower, middleValue, e, inside, vc);
	}
	
	/**
	 * Performs a binary search for a boundary of an implicitly defined
	 * partition in an arbitrary color space. For example, this can be used to
	 * find the most saturated or brightest possible color of a given hue.
	 * 
	 * Assuming that col is actually part of the partition identified by the
	 * predicate and the lower, upper boundary contains exactly one such boundary,
	 * this method tests the boundary until an answer with an error of at most e
	 * is found.
	 * 
	 * @param col
	 *            the color at the lower bound
	 * @param channel
	 *            the channel whose boundary is being tested
	 * @param lower
	 *            the lower bound on the channel
	 * @param upper
	 *            the upper bound on the channel
	 * @param e
	 *            the distance in JCh to allow for
	 * @param inside
	 *            a predicate defining a space whose boundary is tested
	 * @return the boundary color, when found
	 */
	public static <C extends PColor> C determineBoundaryColor(C col, int channel, float lower,
			float upper, float e, Predicate<? super C> inside) {
		return determineBoundaryColor(col, channel, lower, upper,
				e, inside, ViewingConditions.sRGB_typical_envirnonment);
	}
	
	/**
	 * Optimize a specific channel of a palette such that all colours in the
	 * palette satisfy the predicate while sharing the same value for this
	 * channel.
	 * <p>
	 * This assumes that any "more lower" value for that channel than
	 * already established to satisfy the predicate will also satisfy the
	 * predicate. In other words, it will optimize towards upper,
	 * but it is not required that lower < upper.
	 * 
	 * Not that the whole idea only makes sense if the predicate's space is smaller
	 * than can be represented in the color space. For example, JCh values that
	 * satisfy sRGB are a useful predicate.
	 * 
	 * @param palette the palette to optimize. Will not be modified. The values in
	 * 		  the optimized channel are ignored.
	 * @param channel the channel to optimize
	 * @param lower the lower bound for which all colors must satisfy the predicate
	 * @param upper the upper bound, which may satisfy the predicate for most colors
	 * @param e the distance in JCh to allow for
	 * @param predicate a predicate defining a color space whose boundary is tested
	 * @return new new array of optimized colors
	 */
	public static <C extends PColor> C[] optimizePalette(C[] palette,
			int channel, float lower, float upper, float e, Predicate<? super C> predicate) {
		// find the common maximum value that satisfies the predicate
		float common_max = upper;
		for (C color : palette) {
			// check if lower satisfies predicate; this is strictly an assumption
			// to this method but it is better to check than be wrong.
			C ctemp = setChannel(color, channel, lower);
			if (!predicate.apply(ctemp))
				throw new IllegalArgumentException("lower bound does not satisfy predicate for " + color.toString());
			// lower the common maximum if we need to
			if (!predicate.apply(setChannel(color, channel, common_max)))
				common_max = determineBoundaryColor(ctemp, channel, lower, common_max, e, predicate).get(channel);
		}
		return setChannel(palette.clone(), channel, common_max);
	}

	/**
	 * Format a color as HTML hex color string ("simple color", i.e. "#aabbcc") with 24/32 bit sRGB.
	 * @param c the color
	 * @param alpha whether to include alpha
	 * @return a string representing c in sRGB, clipped if needed
	 */
	public static String toHtml(PColor c, boolean alpha) {
		int argb = c.getARGB();
		return String.format(Locale.US, alpha ? "#%02x%02x%02x%02x" : "#%02x%02x%02x",
				argb >> 16 & 0xff, argb >> 8 & 0xff, argb & 0xff, argb >> 24 & 0xff);
	}
	
	/**
	 * Format a color as CSS "functional notation" color specification ("rgb(1, 2, 3)") with 24/32 bit sRGB.
	 * @param c the color
	 * @param alpha whether to include alpha
	 * @return a string representing c in sRGB, clipped if needed
	 */
	public static String toCss(PColor c, boolean alpha) {
		sRGB rgb = (sRGB) PColor.convert(c, CS_sRGB.instance);
		return String.format(
				Locale.US, 
				alpha ? "rgba(%3d, %3d, %3d, %1.2f)" : "rgb(%3d, %3d, %3d)",
				rgb.getByte(sRGB.R),
				rgb.getByte(sRGB.G),
				rgb.getByte(sRGB.B),
				rgb.getAlpha());
	}
	
	/**
	 * Format a CSS3 color "unclipped" which is not precisely defined but may,
	 * under circumstances, allow a compliant browser to use an extended gamut
	 * beyond sRGB. However, it should be expected some browsers will simply
	 * fail.
	 * <p>
	 * See <a
	 * href="http://www.w3.org/TR/css3-color/#rgb-color">http://www.w3.org
	 * /TR/css3-color/#rgb-color</a> Section 4.2.1
	 * 
	 * @param c
	 *            the color
	 * @param alpha
	 *            whether to include alpha
	 * @return an unclipped functional notation css3 color
	 */
	public static String toCssUnclipped(PColor c, boolean alpha) {
		sRGB rgb = (sRGB) PColor.convert(c, CS_sRGB.instance);
		return String.format(Locale.US,
				alpha?"rgba(%3d, %3d, %3d, %1.2f)":"rgb(%3d, %3d, %3d)",
				Math.round(rgb.get(sRGB.R) * 255),
				Math.round(rgb.get(sRGB.G) * 255),
				Math.round(rgb.get(sRGB.B) * 255),
				rgb.getAlpha());
	}
	
	private final static Pattern rgbColorPatternX1 = Pattern.compile("#(\\p{XDigit}{1})(\\p{XDigit}{1})(\\p{XDigit}{1})");
	private final static Pattern rgbColorPatternX1A = Pattern.compile("#(\\p{XDigit}{1})(\\p{XDigit}{1})(\\p{XDigit}{1})(\\p{XDigit}{1})");
	private final static Pattern rgbColorPatternX2 = Pattern.compile("#(\\p{XDigit}{2})(\\p{XDigit}{2})(\\p{XDigit}{2})");
	private final static Pattern rgbColorPatternX2A = Pattern.compile("#(\\p{XDigit}{2})(\\p{XDigit}{2})(\\p{XDigit}{2})(\\p{XDigit}{2})");
	
	private final static String intP = "\\s*(-?\\d+)\\s*";
	private final static String alphaP = "\\s*([\\d\\.]+)\\s*";
	private final static Pattern rgbColorPatternD = Pattern.compile("rgb\\(" + intP + "," + intP + "," + intP + "\\)");
	private final static Pattern rgbColorPatternDA = Pattern.compile("rgba\\(" + intP + "," + intP + "," + intP + "," + alphaP +"\\)");

	/**
	 * Parses the HTML/CSS sRGB "simple colors", some "legacy colors" and CSS
	 * colors with notations such as #111, #222F, #33445566, rgb(2, 6, 111).
	 * 
	 * @param c the color string
	 * @return a corresponding {@link sRGB} color
	 * @throws IllegalArgumentException is the input cannot be parsed
	 */
	public static sRGB parseColor(String c) {
		Matcher m = rgbColorPatternX1.matcher(c);
		if (m.matches()) {
			return sRGB.fromBytes(Integer.parseInt(m.group(1), 16) * 17,
					Integer.parseInt(m.group(2), 16) * 17,
					Integer.parseInt(m.group(3), 16) * 17);
		}
		m = rgbColorPatternX1A.matcher(c);
		if (m.matches()) {
			return sRGB.fromBytes(Integer.parseInt(m.group(1), 16) * 17,
					Integer.parseInt(m.group(2), 16) * 17,
					Integer.parseInt(m.group(3), 16) * 17,
					Integer.parseInt(m.group(4), 16) * 17);
		}
		m = rgbColorPatternX2.matcher(c);
		if (m.matches()) {
			return sRGB.fromBytes(Integer.parseInt(m.group(1), 16),
					Integer.parseInt(m.group(2), 16),
					Integer.parseInt(m.group(3), 16));
		}
		m = rgbColorPatternX2A.matcher(c);
		if (m.matches()) {
			return sRGB.fromBytes(Integer.parseInt(m.group(1), 16),
					Integer.parseInt(m.group(2), 16),
					Integer.parseInt(m.group(3), 16),
					Integer.parseInt(m.group(4), 16));
		}
		m = rgbColorPatternD.matcher(c);
		if (m.matches()) {
			return sRGB.fromBytes(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)),
					Integer.parseInt(m.group(3)));
		}
		m = rgbColorPatternDA.matcher(c);
		if (m.matches()) {
			return sRGB.fromBytes(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)),
					Integer.parseInt(m.group(3)),
					(int)(Float.parseFloat(m.group(4)) * 255));
		}
		throw new IllegalArgumentException("Found none of the supported color notations");
	}
	
}