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
 * This class implements the CIE XYZ colorspace; calling toCIEXYZ(float[]
 * components) or fromCIEXYZ(float[] components) will simply return components.
 */
public class CS_CIEXYZ extends PColorSpace {

	private static final long serialVersionUID = -7923913005471667746L;
	/**
	 * Red
	 */
	public static final int X = 0;
	/**
	 * Green
	 */
	public static final int Y = 1;
	/**
	 * Blue
	 */
	public static final int Z = 2;

	/**
	 * default constructor
	 * creates CIEXYZ color space with three components
	 */
	private CS_CIEXYZ() {
		super(CS_CIEXYZ, 3);
	}
	
	public static CS_CIEXYZ instance = new CS_CIEXYZ();

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		return colorvalue;
	}

	@Override
	public float[] fromRGB(float[] colorvalue) {
		float[] XYZComponents = de.fhg.igd.pcolor.colorspace.CS_sRGB.instance.toCIEXYZ(colorvalue);
		return fromCIEXYZ(XYZComponents);
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		return colorvalue;
	}

	@Override
	public float[] toRGB(float[] colorvalue) {
		float[] XYZComponents = toCIEXYZ(colorvalue);
		return de.fhg.igd.pcolor.colorspace.CS_sRGB.instance.fromCIEXYZ(XYZComponents);
	}

	@Override
	public float getMinValue(int component) {
		switch (component) {
		case X:
			return 0f;
		case Y:
			return 0f;
		case Z:
			return 0f;
		default:
			return Float.NaN;
		}
	}

	@Override
	public float getMaxValue(int component) {
		switch (component) {
		case X:
			return 1f;
		case Y:
			return 1f;
		case Z:
			return 1f;
		default:
			return Float.NaN;
		}
	}

	@Override
	public String getName(int component) {
		switch (component) {
		case X:
			return "X";
		case Y:
			return "Y";
		case Z:
			return "Z";
		default:
			return null;
		}
	}

	@Override
	public boolean equals(Object anObject) {
		if(anObject == null) return false;
	    if (this == anObject) return true;
	    if (anObject == null || anObject.getClass() != this.getClass()) return false;
	    return true;
	}

	@Override
	public int hashCode() {
		return 14;
	}
}