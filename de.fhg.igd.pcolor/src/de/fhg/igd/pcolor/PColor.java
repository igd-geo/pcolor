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

import de.fhg.igd.pcolor.colorspace.CS_CIELab;
import de.fhg.igd.pcolor.colorspace.CS_JCh;
import de.fhg.igd.pcolor.colorspace.CS_Jab;

/**
 * PColors represent single colors and provide operations on them.
 * <p>
 * PColors, like {@link java.awt.Color}s, consist of two major components: The
 * color itself as represented by an array of float components as well as an
 * alpha channel, and a colorspace object that describes the color space in
 * which the components are to be interpreted. Unlike its cousin, it avoids
 * simplistic operations such as darker(). It is also responsible for
 * transforming the components to and from the CIE XYZ reference colorspace.
 * <p>
 * PColors typically use a whitepoint of D65 (the same as sRGB), an average
 * surround luminance La of 64 cd/m2, and a background Yb of 20% gray (CIECAM02
 * convention).
 * <p>
 * PColors can be converted freely, for which there are type-safe convertFrom
 * methods and a static {@link #convert(PColor, ColorSpace)} helper. These
 * mostly avoid actual conversion if possible and if not, they use XYZ as the
 * intermediary. This means conversion maintains lighting but not necessarily
 * appearance correlates, i.e. the stimulus remains the same without addressing
 * the appearance.
 * <p>
 * PColors based on CIECAM02 (Jab and JCh, currently) may also be transposed,
 * i.e. one may try to reproduce identical appearance correlates under different
 * viewing conditions. This usually leads to another color stimulus that,
 * however, is perceived to look the same given it appears in the target viewing
 * conditions. See {@link #transpose(ColorSpace)}.
 */
public abstract class PColor implements Cloneable {

	/**
	 * the color space
	 */
	private final ColorSpace colorspace;
	/**
	 * the components
	 */
	private final float[] components;
	/**
	 * alpha value
	 */
	private final float alpha;

	/**
	 * This constructor creates a PColor, converting if necessary.
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
	 * This constructor creates a PColor with strictly the values given.
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
	 * Returns a reference to this color's components array. <b>This array MUST
	 * NOT BE MODIFED! Behaviour is undefined if you modify this array.</b>
	 * <p>
	 * PColors are designed to be immutable, this loophole merely exists for
	 * performance reasons. Modify at your peril.
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
	 * returns the component at the given index
	 * @param component component index
	 * @return component
	 */
	public float get(int component) {
		return components[component];
	}

	/**
	 * returns alpha
	 * @return alpha
	 */
	public float getAlpha() {
		return alpha;
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
	 * This method provides arbitrary conversion between PColor objects.
	 * 
	 * @param color
	 *            The color to be converted
	 * @return A PColor in its native colorspace.
	 */
	public abstract PColor convertFrom(PColor color);

	/**
	 * Converts this color to the colorspace specified by the color parameter.
	 * 
	 * @param color -
	 *            a PColor containing the colorspace to which this color will be
	 *            converted.
	 * @return A new PColor<E> in the ColorSpace of color.
	 */
	@SuppressWarnings("unchecked")
	public <C extends PColor> C convertTo(C color) {
		return (C)(color.convertFrom(this));
	}
	
	/**
	 * Transposes this color (if defined by appearance correlates) to another
	 * color which has the same appearance correlates within other viewing
	 * conditions, as represented by the given color space.
	 * 
	 * @param to
	 *            the target color space (and viewing conditions)
	 * @return the transposed color
	 */
	public PColor transpose(ColorSpace to) {
		throw new UnsupportedOperationException("transposing is only available for perceptually uniform color spaces, e.g. CIECAM02-based ones.");
	}

	@Override
	protected PColor clone() {
		// PColors are immutable
		return this;
	}
	
	/**
	 * Convert a PColor instance to another color space, optimising the case
	 * where no actual conversion has to take place. In that case, the color is
	 * just returned. The conversion goes over XYZ, i.e. will try to preserve
	 * optical lighting not appearance.
	 * 
	 * Implementation note: the targetSpace is assumed as default for Lab, sRGB
	 * and XYZ. Except for Lab, that is intentional as there is a single
	 * reference for sRGB and XYZ. The Lab implementation is limited.
	 * 
	 * @param in
	 *            the color to be converted
	 * @param targetSpace
	 *            the target color space
	 * @return a PColor object hat conforms to the target color space
	 */
	public static PColor convert(PColor in, ColorSpace targetSpace) {
		if (in.getColorSpace().equals(targetSpace))
			return in;
		if (targetSpace instanceof CS_Jab)
			return new Jab(in, (CS_Jab)targetSpace);
		else if (targetSpace instanceof CS_JCh)
			return new JCh(in, (CS_JCh)targetSpace);
		// we could check the getType() type for Lab but Lab has an illuminant
		// we assume to be E. Better throw than pretend we handle this.
		else if (targetSpace instanceof CS_CIELab)
			return new CIELab(in /*, (CS_CIELab)targetSpace*/ );
		else if (targetSpace.isCS_sRGB())
			return new sRGB(in /*, (CS_sRGB)targetSpace */ );
		else if (targetSpace.getType() == ColorSpace.CS_CIEXYZ)
			return new CIEXYZ(in /*, (CS_CIEXYZ)targetSpace*/ );
		else 
			throw new IllegalStateException(
					"Target space not supported: " + targetSpace.toString());
	}
}
