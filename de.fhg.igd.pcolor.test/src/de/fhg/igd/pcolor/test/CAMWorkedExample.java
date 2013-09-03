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

package de.fhg.igd.pcolor.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.colorspace.CS_CIECAM02;
import de.fhg.igd.pcolor.colorspace.Surrounding;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;

/**
 * tests conversion from CIEXYZ to CIECAM02 using the worked example
 * from CIE 159:2004 Section 9. This mainly serves to assure precision of the
 * implementation.
 * @author Simon Thum
 */
public class CAMWorkedExample {
	
	CIEXYZ testSample = CIEXYZ.fromCIEXYZ100(19.31f, 23.93f, 10.14f);
	CIEXYZ testIlluminant = CIEXYZ.fromCIEXYZ100(98.88f, 90f, 32.03f);
	
	ViewingConditions Cond20 = ViewingConditions.createAdapted(testIlluminant, 20, 18, Surrounding.averageSurrounding);
	ViewingConditions Cond200 = ViewingConditions.createAdapted(testIlluminant, 200, 18, Surrounding.averageSurrounding);
	
	CS_CIECAM02 CAM_20 = new CS_CIECAM02(Cond20);
	CS_CIECAM02 CAM_200 = new CS_CIECAM02(Cond200);
	
	/**
	 * Values from table 4 / 6.   
	 */
	@Test
	public void XYZtoCIECAM02_200() {
		float[] t200 = CAM_200.fromCIEXYZ(testSample.getComponents());
		
		assertEquals(191.0452f, t200[CS_CIECAM02.h], 0.00005);
		assertEquals(48.0314, t200[CS_CIECAM02.J], 0.00002);
		assertEquals(183.1240, t200[CS_CIECAM02.Q], 0.00004);
		assertEquals(46.0177, t200[CS_CIECAM02.s], 0.00002);
		assertEquals(38.7789, t200[CS_CIECAM02.C], 0.00002);
		assertEquals(38.7789, t200[CS_CIECAM02.M], 0.00002);
		assertEquals(240.8885, t200[CS_CIECAM02.H], 0.00005);
		// D in VC should be 0.98
		// H_c could not be determined
		
		
		float[] inverse200 = CAM_200.toCIEXYZ(t200);
		assertArrayEquals(testSample.getComponents(), inverse200, 1e-7f);
	}

	/**
	 * Values from table 4 / 6.   
	 */
	@Test
	public void XYZtoCIECAM02_20() {
		float[] t20 = CAM_20.fromCIEXYZ(testSample.getComponents());
		
		assertEquals(185.3445f, t20[CS_CIECAM02.h], 1e-4);
		assertEquals(47.6856, t20[CS_CIECAM02.J], 5e-5);
		assertEquals(113.8401, t20[CS_CIECAM02.Q], 5e-5);
		assertEquals(51.1275, t20[CS_CIECAM02.s], 5e-5);
		assertEquals(36.0527, t20[CS_CIECAM02.C], 5e-5);
		assertEquals(29.7580, t20[CS_CIECAM02.M], 5e-5);
		assertEquals(232.6630, t20[CS_CIECAM02.H], 5e-5);
		// D in VC should be 0.8584
		// H_c could not be determined
		
		
		float[] inverse20 = CAM_20.toCIEXYZ(t20);
		assertArrayEquals(testSample.getComponents(), inverse20, 5e-7f);
	}
	

		
}