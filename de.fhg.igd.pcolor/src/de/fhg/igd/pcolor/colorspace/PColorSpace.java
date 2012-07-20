package de.fhg.igd.pcolor.colorspace;

import java.awt.color.ColorSpace;

public abstract class PColorSpace extends ColorSpace {

	private static final long serialVersionUID = 9164851006288790616L;

	/**
	 * creates a PColor space of given type and with numcomponents components
	 * @param type type of color space
	 * @param numcomponents number of components
	 */
	protected PColorSpace(int type, int numcomponents) {
		super(type, numcomponents);
	}

	/**
	 * Returns the component type for the specified component. A component's
	 * type is meant to specify its relationship to other components (for
	 * example, whether it is a polar angle component or a polar radius
	 * component); this knowledge is useful during scale and blur operations.
	 * 
	 * @return the component type for the specified component
	 */
	public ColorSpaceType getColorSpaceType() {
		return new CartesianType();
	}
}
