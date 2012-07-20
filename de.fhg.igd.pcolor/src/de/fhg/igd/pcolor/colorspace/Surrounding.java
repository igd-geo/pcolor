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

public class Surrounding {

	/**
	 * factor determining degree of adaptation
	 */
	private final double F;
	/**
	 * impact of surrounding
	 */
	private final double c;
	/**
	 * chromatic induction factor
	 */
	private final double N_c;

	private Surrounding(double F, double c, double Nc) {
		this.F = F;
		this.c = c;
		this.N_c = Nc;
	}

	public final static Surrounding averageSurrounding = new Surrounding(1.0, 0.69, 1.0);

	public final static Surrounding dimSurrounding = new Surrounding(0.9, 0.59, 0.95);

	public final static Surrounding darkSurrounding = new Surrounding(0.8, 0.525, 0.8);

	/**
	 * 
	 * @return factor determining degree of adaptation
	 */
	public double getF() {
		return F;
	}

	/**
	 * 
	 * @return impact of surrounding
	 */
	public double getC() {
		return c;
	}

	/**
	 * 
	 * @return chromatic induction factor
	 */
	public double getN_c() {
		return N_c;
	}

	
}
