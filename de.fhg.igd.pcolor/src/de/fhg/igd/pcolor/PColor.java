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

/**
 * PColors, like java.awt.Colors, consist of two major components: The color
 * itself as represented by an array of float components as well as an alpha
 * channel, and a colorspace object that is responsible for transforming these
 * components to and from the CIE XYZ colorspace.
 * <p>
 * PColors typically use a whitepoint of D65 (the same as sRGB), an average surround
 * luminance La of 64 cd/m2, and a background Yb of 20% gray (CIECAM02 convention).
 */
public abstract class PColor implements Cloneable {

	/**
	 * the color space
	 */
	private final ColorSpace colorspace;
	/**
	 * the components
	 */
	private float[] components;
	/**
	 * alpha value
	 */
	private float alpha;

	/**
	 * 
	 * @param cspace color space
	 * @param color color
	 */
	public PColor(ColorSpace cspace, PColor color) {
		ColorSpace space = color.getColorSpace();
		if(space.equals(cspace)) {
			this.colorspace = cspace;
			float[] colorComponents = color.getComponents();
			this.components = new float[colorComponents.length];
			System.arraycopy(colorComponents, 0, this.components, 0, color.getComponents().length);
			this.alpha = color.getAlpha();
		} else {
			float[] CIEXYZ = space.toCIEXYZ(color.getComponents());
			float[] result = cspace.fromCIEXYZ(CIEXYZ);
			this.colorspace = cspace;
			this.components = result;
			this.alpha = color.getAlpha();
		}
	}

	/**
	 * 
	 * @param cspace color space
	 * @param components components
	 * @param alpha alpha value
	 */
	public PColor(ColorSpace cspace, float[] components, float alpha) {
		this.colorspace = cspace;
		this.components = components;
		this.alpha = alpha;
	}

	/**
	 * Converts this color to sRGB and packs it into a single 32 bit int value
	 * in format ARGB.
	 */
	public int getARGB() {
		if(this.getColorSpace().isCS_sRGB()) {
			return packARGB(components, alpha);
		} else {
			float[] rgbComponents = this.getColorSpace().toRGB(this.components);
			return packARGB(rgbComponents, alpha);
		}
	}

	/**
	 * Packs a float[] array containing RGB color channels as well as a float
	 * containing an alpha value into a single int.
	 */
	private int packARGB(float[] rgb, float alpha) {
		return Math.round(alpha * 255f) << 24 |
		Math.round(rgb[0] * 255f) << 16 |
		Math.round(rgb[1] * 255f) << 8 |
		Math.round(rgb[2] * 255f);
	}

	/**
	 * Returns a reference to this color's ColorSpace.
	 */
	public ColorSpace getColorSpace() {
		return colorspace;
	}

    /**
     * Returns a reference to this color's components array.
     */
	public float[] getComponents() {
		return components;
	}

    /**
	 * Returns a new float array combining the color channels and alpha channel
	 * (in that order) of the given PColor.
	 */
    public float[] getRawComponents() {
    	float[] components = new float[this.getColorSpace().getNumComponents() + 1];
    	System.arraycopy(this.getComponents(), 0, components, 0, components.length - 1);
    	components[components.length - 1] = this.getAlpha();
    	return components;
    }

	/**
	 * Copies the values in the specified float array to this color's components
	 * array. Performance is undefined when the length of the specified array
	 * differs from that of this color's components.
	 * 
	 * @param components
	 */
	public void setComponents(float[] components) {
		System.arraycopy(components, 0, this.components, 0, components.length);
	}

	/**
	 * Copies the values in the specified float array to this color's components
	 * array as well as its alpha channel; the new alpha value should be the
	 * last entry in the specified float[], following the color values.
	 * Performance is undefined when the length of the specified array differs
	 * from that of this color's components + 1.
	 * 
	 * @param components
	 */
	public void setRawComponents(float[] components) {
		System.arraycopy(components, 0, this.components, 0, components.length - 1);
		setAlpha(components[components.length - 1]);
	}

	/**
	 * returns the component at the given index
	 * @param component component index
	 * @return component
	 */
	public float get(int component) {
		return components[component];
	}

	/**
	 * sets the component to a given value
	 * @param component component index
	 * @param value value
	 */
	public void set(int component, float value) {
		components[component] = value;
	}

	/**
	 * returns alpha
	 * @return alpha
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * sets alpha to given value
	 * @param alpha new value
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/**
	 * Returns a String containing this color's components and alpha value in
	 * the format "R: 1.0 B: 1.0 G: 1.0 Alpha: 1.0"
	 */
	@Override
	public String toString() {
		String result = "";
		for(int i = 0; i < components.length; i++) {
			result += this.getColorSpace().getName(i) + ": " + components[i] + " ";
		}
		result += "Alpha: " + this.getAlpha();
		return result;
	}

	/**
	 * Returns this color's color component values in a space-separated String;
	 * does not include alpha.
	 */
	public String implodeColor() {
		StringBuilder sb = new StringBuilder();
		for (float aColor : this.getComponents()) {
			sb.append(aColor).append(" ");
		}
		return sb.toString().trim();
	}

	/**
	 * This method provides arbitrary conversion between
	 * PColor objects.
	 * 
	 * PColor objects should
	 * provide constructors in the form of PColor(PColor<?> color) for
	 * convenience that do the same thing.
	 * 
	 * @param color The color to be converted
	 * @return A PColor in its native colorspace.
	 */
	public abstract PColor convertFrom(PColor color);

	/**
	 * Converts this color to the colorspace specified by c1
	 * 
	 * @param color -
	 *            a PColor containing the colorspace to which this color will be
	 *            converted.
	 * @return A new PColor<E> in the ColorSpace of c1.
	 */
	@SuppressWarnings("unchecked")
	public <C extends PColor> C convertTo(C color) {
		return (C)(color.convertFrom(this));
	}

	@Override
	public abstract PColor clone();
}
