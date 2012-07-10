package de.fhg.igd.pcolor;

import de.fhg.igd.pcolor.colorspace.CS_Alpha;

/**
 * The Alpha colorspace is essentially a null colorspace containing no color
 * components. PColor and PColorCanvas objects making use of this colorspace
 * will instead contain only one channel, the alpha channel, which may be used
 * to represent single-channel information.
 */
public class Alpha extends PColor {

	/**
	 * creates an alpha color given a pColor
	 * @param color a color
	 */
	public Alpha(PColor color) {
		super(new CS_Alpha(), color);
	}

	/**
	 * creates an alpha color given an alpha value
	 * @param alpha alpha value
	 */
	public Alpha(float alpha) {
		super(new CS_Alpha(), new float[0], alpha);
	}

	@Override
	public Alpha convertFrom(PColor color) {
		return new Alpha(color.getAlpha());
	}

	@Override
	public Alpha clone() {
		return new Alpha(this.getAlpha());
	}
}
