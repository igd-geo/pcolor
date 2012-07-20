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

/**
 * The CS_Alpha colorspace is essentially a null colorspace containing no color
 * components. PColor and PColorCanvas objects making use of this colorspace
 * will instead contain only one channel, the alpha channel, which may be used
 * to represent single-channel information.
 */
public class CS_Alpha extends PColorSpace {

	private static final long serialVersionUID = 4771429392405445137L;

	/**
	 * default constructor
	 * creates alpha color space with color space type 0 and no components
	 */
	public CS_Alpha() {
		super(0, 0);
	}

	@Override
	public float[] fromCIEXYZ(float[] arg0) {
		return new float[0];
	}

	@Override
	public float[] fromRGB(float[] arg0) {
		return new float[0];
	}

	@Override
	public float[] toCIEXYZ(float[] arg0) {
		return new float[3];
	}

	@Override
	public float[] toRGB(float[] arg0) {
		return new float[3];
	}
}
