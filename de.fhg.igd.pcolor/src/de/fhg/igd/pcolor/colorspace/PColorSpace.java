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
