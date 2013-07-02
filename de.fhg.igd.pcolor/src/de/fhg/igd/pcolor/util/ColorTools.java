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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhg.igd.pcolor.JCh;
import de.fhg.igd.pcolor.Jab;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_Jab;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;

/**
 * A series of methods and utilities for dealing with various color operations.
 */
public class ColorTools {

	/**
	 * Returns a new ArrayList<C> in which all duplicate instances of any
	 * given color in list have been removed, leaving only unique colors.
	 */
    public static <C extends PColor> ArrayList<C> getUniqueColors(C[] list) {
    	ArrayList<C> colorList = new ArrayList<C>();
    	for(int i = 0; i < list.length; i++) {
    		// get components
    		float[] components = list[i].getRawComponents();
    		// test for repeats
    		boolean repeat = false;
    		for(int j = 0; j < colorList.size(); j++) {
    			if(Arrays.equals(colorList.get(j).getRawComponents(), components))
    				repeat = true;
    		}
    		// if not, add to list
    		if(!repeat)
    			colorList.add(list[i]);
    	}
    	return colorList;
    }

    /**
	 * Returns the smallest difference between two hues as a positive float
	 * (in degrees).
	 * 
	 * @return Smallest difference between two hues.
	 */
    public static float hueDistance(float hue1, float hue2)
    {
		return Math.abs(hueDifference(hue1, hue2));
    }

	/**
	 * Returns the smallest difference between two hues as a float (in degrees);
	 * negative values mean that hue2 is closer in the 'negative' direction (for
	 * instance, if hue1 is 5.0, and hue2 is 0.0, this function will return
	 * -5.0).
	 */
    public static float hueDifference(float hue1, float hue2)
    {
	    float difference = (hue2 - hue1) % 360;
		if (difference > 180)
			difference -= 360;
		if (difference < -180)
			difference += 360;
		return difference;
    }
    
	/**
	 * Calculate the distance in Jab. This is similar to and often better than a
	 * "delta E" distance based on Lab, i.e, is closer to the ideal that unity
	 * is equivalent to a "just noticeable distance". The Jab space is the one
	 * defined by {@link CS_Jab#defaultInstance} If you want to compare across
	 * viewing conditions, use the {@link #distance(Jab, Jab)}
	 * overload.
	 * 
	 * @see JCh#distance(JCh, JCh)
	 * @param col1
	 *            the first colour
	 * @param col2
	 *            the second colour
	 */
	public static float distance(PColor col1, PColor col2) {
		return distance(col1, col2, CS_Jab.defaultInstance);
	}
    
	/**
	 * Calculate the distance in Jab. This is similar to and often better than a
	 * "delta E" distance based on Lab, i.e, is closer to the ideal that unity
	 * is equivalent to a "just noticeable distance". The Jab space is wholly
	 * defined by the CIECAM viewing conditions. If you want to compare across
	 * viewing conditions, use the {@link #distance(Jab, Jab)}
	 * overload.
	 * 
	 * @see JCh#distance(JCh, JCh)
	 * @param col1
	 *            the first colour
	 * @param col2
	 *            the second colour
	 * @param cspace
	 *            the Jab-based colour space to use
	 */
	public static float distance(PColor col1, PColor col2, CS_Jab cspace) {
		Jab col1_ref = new Jab(col1, cspace);
		Jab col2_ref = new Jab(col2, cspace);
		return distance(col1_ref, col2_ref);
	}
	
	/**
	 * Calculate the distance in Jab. This is similar to and often better than a
	 * "delta E" distance based on Lab, i.e, is closer to the ideal that unity
	 * is equivalent to a "just noticeable distance".
	 * 
	 * @see JCh#distance(JCh, JCh)
	 * @param col1
	 *            the first colour
	 * @param col2
	 *            the second colour
	 */
	public static float distance(Jab col1, Jab col2) {
		float error = 0;
		for (int j = 0; j < 3; j++) {
			float diff = col1.get(j) - col2.get(j);
			error += diff * diff;
		}
		return (float) Math.sqrt(error);
	}
	
	/**
	 * Calculates the distance between two JCh colors using the CIE's
	 * recommended CAM02-UCS calculation.
	 * 
	 * @param color1
	 *            the first color
	 * @param color2
	 *            the second color
	 * @return the distance between two JCh colors.
	 */
	public static float distance(JCh color1, JCh color2) {
		return (float) JCh.distance(color1, color2);
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
	 * @return
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
	 * @return
	 */
	public static <C extends PColor> C determineBoundaryColor(C col, int channel, float lower,
			float upper, float e, Predicate<? super C> inside) {
		float middleValue = (upper + lower) / 2f;
		C middleColor = ColorTools.setChannel(col, channel, middleValue);
		// if we woudn't move far anyway, treat as found as col is
		// always assumed to be inside the space identified by the predicate
		if (distance(col, middleColor) < e)
			return col;
		if (inside.apply(middleColor))
			return determineBoundaryColor(middleColor, channel, middleValue, upper,
					e, inside);
		else
			return determineBoundaryColor(col, channel, lower, middleValue, e, inside);
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

	private static String formatSrgbColor(int argb, String format) {
		return String.format(Locale.US, format,
				argb >> 16 & 0xff, argb >> 8 & 0xff, argb & 0xff, argb >> 24 & 0xff);
	}
	
	/**
	 * Format a color as HTML hex color string ("simple color", i.e. "#aabbcc") with 24/32 bit sRGB.
	 * @param c the color
	 * @param alpha whether to include alpha
	 * @return a string representing c in sRGB, clipped if needed
	 */
	public static String toHtml(PColor c, boolean alpha) {
		return formatSrgbColor(c.getARGB(), alpha?"#%02x%02x%02x%02x":"#%02x%02x%02x");
	}
	
	/**
	 * Format a color as CSS "functional notation" color specification ("rgb(1, 2, 3)") with 24/32 bit sRGB.
	 * @param c the color
	 * @param alpha whether to include alpha
	 * @return a string representing c in sRGB, clipped if needed
	 */
	public static String toCss(PColor c, boolean alpha) {
		return formatSrgbColor(c.getARGB(), alpha?"rgba(%3d, %3d, %3d, %3d)":"rgb(%3d, %3d, %3d)");
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
				alpha?"rgba(%3d, %3d, %3d, %3d)":"rgb(%3d, %3d, %3d)",
				Math.round(rgb.get(sRGB.R) * 255),
				Math.round(rgb.get(sRGB.G) * 255),
				Math.round(rgb.get(sRGB.B) * 255),
				Math.round(rgb.getAlpha() * 255));
	}
	
	private final static Pattern rgbColorPatternX1 = Pattern.compile("#(\\p{XDigit}{1})(\\p{XDigit}{1})(\\p{XDigit}{1})");
	private final static Pattern rgbColorPatternX1A = Pattern.compile("#(\\p{XDigit}{1})(\\p{XDigit}{1})(\\p{XDigit}{1})(\\p{XDigit}{1})");
	private final static Pattern rgbColorPatternX2 = Pattern.compile("#(\\p{XDigit}{2})(\\p{XDigit}{2})(\\p{XDigit}{2})");
	private final static Pattern rgbColorPatternX2A = Pattern.compile("#(\\p{XDigit}{2})(\\p{XDigit}{2})(\\p{XDigit}{2})(\\p{XDigit}{2})");
	
	private final static String intP = "\\s*(-?\\d+)\\s*";
	private final static Pattern rgbColorPatternD = Pattern.compile("rgb\\(" + intP + "," + intP + "," + intP + "\\)");
	private final static Pattern rgbColorPatternDA = Pattern.compile("rgba\\(" + intP + "," + intP + "," + intP + "," + intP +"\\)");

	/**
	 * Parses the HTML/CSS sRGB "simple colors", some "legacy colors" and CSS
	 * colors with notations such as #111, #222F, #33445566, rgb(2, 6, 111).
	 * 
	 * @param c the color string
	 * @return a corresponding {@link sRGB} color
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
					Integer.parseInt(m.group(4)));
		}
		throw new IllegalArgumentException("Found none of the supported color notations");
	}
	
}