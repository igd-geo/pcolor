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

import java.awt.color.ColorSpace;

import org.junit.Test;

import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.Illuminant;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_CAMLab;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;
import de.fhg.igd.pcolor.colorspace.CS_CIECAM02;
import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.Surrounding;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;

/**
 * Test conversion with pcolor
 * @author Thu Huong
 * @author Simon Thum
 */
public class ConversionTest {
	ViewingConditions brightCond = ViewingConditions.createAdapted(Illuminant.D65, 318.31, 20.0, Surrounding.averageSurrounding);
	ViewingConditions darkCond = ViewingConditions.createAdapted(Illuminant.D65, 31.83, 6.0, Surrounding.averageSurrounding);

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back.   
	 */
	@Test
	public void XYZtoCIECAM02_1() {
		float[] xyz = new float[]{0.1901f, 0.2f, 0.2178f};

		CS_CIECAM02 csc = new CS_CIECAM02(brightCond);
		testXYZForwardBackward(xyz, csc, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back. 
	 */
	@Test
	public void XYZtoCIECAM02_2() {
		float[] xyz = new float[]{0.5706f, 0.4306f, 0.3196f};

		CS_CIECAM02 csc = new CS_CIECAM02(darkCond);
		testXYZForwardBackward(xyz, csc, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back. 
	 */
	@Test
	public void XYZtoCIECAM02_3() {
		float[] xyz = new float[]{0.0353f, 0.0656f, 0.0214f};

		CS_CIECAM02 csc = new CS_CIECAM02(brightCond);
		testXYZForwardBackward(xyz, csc, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to JCh and back.   
	 */
	@Test
	public void XYZtoJCh() {
		float[] xyz = new float[]{0.1901f, 0.2f, 0.2178f};

		CS_CAMLch csJCh = new CS_CAMLch(brightCond, CS_CAMLch.JCh);
		testXYZForwardBackward(xyz, csJCh, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to Jab and back. 
	 */
	@Test
	public void XYZtoJab() {
		float[] xyz = new float[]{0.5706f, 0.4306f, 0.3196f};

		CS_CAMLab csJab = new CS_CAMLab(darkCond, CS_CAMLch.JMh);
		testXYZForwardBackward(xyz, csJab, 0.001f);
		
		xyz = new float[]{0.5706f, 0.4306f, 0.3196f};
		csJab = new CS_CAMLab(brightCond, CS_CAMLch.JMh);
		testXYZForwardBackward(xyz, csJab, 0.001f);
		
		xyz = new float[]{0.1706f, 0.4306f, 0.7196f};
		csJab = new CS_CAMLab(brightCond, CS_CAMLch.JMh);
		testXYZForwardBackward(xyz, csJab, 0.001f);
		
		xyz = new float[]{0.1706f, 0.4306f, 0.7196f};
		csJab = new CS_CAMLab(brightCond, CS_CAMLch.JMh);
		testXYZForwardBackward(xyz, csJab, 0.001f);
	}
	
	/**
	 * tests conversion from CIEXYZ to Jab and back. 
	 */
	@Test
	public void XYZtoJCh1() {
		float[] xyz = new float[]{0.5706f, 0.4306f, 0.3196f};

		CS_CAMLch csJch = new CS_CAMLch(darkCond, CS_CAMLch.JMh);
		testXYZForwardBackward(xyz, csJch, 0.001f);
		
		xyz = new float[]{0.5706f, 0.4306f, 0.3196f};
		csJch = new CS_CAMLch(brightCond, CS_CAMLch.JMh);
		testXYZForwardBackward(xyz, csJch, 0.001f);
		
		xyz = new float[]{0.1706f, 0.4306f, 0.7196f};
		csJch = new CS_CAMLch(brightCond, CS_CAMLch.JMh);
		testXYZForwardBackward(xyz, csJch, 0.001f);
		
		xyz = new float[]{0.1706f, 0.4306f, 0.7196f};
		csJch = new CS_CAMLch(brightCond, CS_CAMLch.JMh);
		testXYZForwardBackward(xyz, csJch, 0.001f);
	}

	/**
	 * tests conversion from sRGB to CIECAM02 and back. 
	 */
	@Test
	public void RGBtoCIECAM02() {
		float[] rgb = new float[]{0.03725068f, 0.33136493f, 0.10736899f};

		CS_CIECAM02 csc = new CS_CIECAM02(brightCond);
		float[] ciecam = csc.fromRGB(rgb);
		float[] back = csc.toRGB(ciecam);

		assertArrayEquals(rgb, back, 0.001f);
	}

	/**
	 * tests conversion from sRGB to JCh and back. checks if CIECAM02 values are correct.
	 */
	@Test
	public void RGBtoJCh() {
		float[] rgb = new float[]{0.4f, 0.5f, 0.8f};

		CS_CAMLch csJCh = new CS_CAMLch(brightCond, CS_CAMLch.JCh);
		testForwardBackward(new sRGB(rgb), csJCh, 0.001f);
		testRGBForwardBackward(rgb, csJCh, 0.001f);
	}
	
	/**
	 * tests conversion from sRGB to JCh and back. checks if CIECAM02 values are correct.
	 */
	@Test
	public void RGBtoJCH2() {
		float[] rgb = new float[]{0.9f, 0.5f, 0.1f};

		CS_CAMLch csJCh = new CS_CAMLch(brightCond, CS_CAMLch.JCh);
		testForwardBackward(new sRGB(rgb), csJCh, 0.001f);
		testRGBForwardBackward(rgb, csJCh, 0.001f);
	}

	/**
	 * tests conversion from sRGB to JaCbC and back.
	 */
	@Test
	public void RGBtoJab() {
		CS_CAMLab csJab = new CS_CAMLab(brightCond, CS_CAMLab.JCh);
		sRGB test = new sRGB(0.736f, 0.237f, 0.946f);
		testForwardBackward(test, csJab, 0.001f);
		testRGBForwardBackward(test.getComponents(), csJab, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to sRGB and back with PColor sRGB definition.
	 */
	@Test
	public void XYZtoRGBAndBackPColor() {
		float[] xyz = new float[]{0.0353f, 0.0656f, 0.0214f};
		testXYZForwardBackward(xyz, CS_sRGB.instance, 0.001f);
		
		xyz = new float[]{0.653f, 0.956f, 0.704f};
		testXYZForwardBackward(xyz, CS_sRGB.instance, 0.001f);
	}
	
	/**
	 * tests conversion from CIEXYZ to sRGB and back using the standard AWT
	 * sRGB. This introduces some error which is why we keep {@link CS_sRGB}.
	 * Needs some more investigation, in principle, and is likely to be platform
	 * dependent. On my machine it fails (tm).
	 */
	@Test(expected = AssertionError.class)
	public void XYZtoRGBAndBackAWT() {
		float[] xyz = new float[]{0.0353f, 0.0656f, 0.0214f};
		testXYZForwardBackward(xyz, ColorSpace.getInstance(ColorSpace.CS_sRGB), 0.001f);
	}

	@Test(expected = AssertionError.class)
	public void XYZtoRGBAndBackAWT2() {
		float[] xyz = new float[]{0.353f, 0.656f, 0.214f};
		testXYZForwardBackward(xyz, ColorSpace.getInstance(ColorSpace.CS_sRGB), 0.001f);
	}
	
	// test XYZ -> CS -> XYZ
	private void testXYZForwardBackward(float[] xyz, ColorSpace cs, float delta) {
		// test color space ops
		float[] other = cs.fromCIEXYZ(xyz);
		float[] back_xyz = cs.toCIEXYZ(other);
		assertArrayEquals(xyz, back_xyz, delta);
		
		if (cs.getClass() == CS_CIECAM02.class)
			return; // no pcolor equiv. for CIECAM02 (why not?)
		
		// test equivalent pcolor conversion ops
		CIEXYZ ciexyz = new CIEXYZ(xyz);
		PColor otherpcol = PColor.convert(ciexyz, cs);
		PColor pcback = PColor.convert(otherpcol, CS_CIEXYZ.instance);
		assertArrayEquals(ciexyz.getComponents(), pcback.getComponents(), delta);
		
		// inter-compare method's results (should be equal or very close
		// as the code paths should be the same)
		assertArrayEquals(back_xyz, pcback.getComponents(), 0.0000001f);
	}
	
	@Test
	public void testAllCAMreconfigs() {
		sRGB test = new sRGB(0.736f, 0.237f, 0.946f);
		
		for (int[] a : CS_CAMLch.correlateConfigurations) {
			for (int[] b : CS_CAMLch.correlateConfigurations) {
				testReconfCH(test, a, b);
				testReconfAB(test, a, b);
			}
		}
	}

	private void testReconfCH(sRGB test, int[] a, int[] b) {
		// -> a
		PColor testLch_a = PColor.convert(test, new CS_CAMLch(brightCond, a));
		assertArrayEquals(test.getComponents(), PColor.convert(testLch_a, CS_sRGB.instance).getComponents(), 0.00001f);
		
		// -> a -> b
		PColor testLch_transposed_b = PColor.convert(testLch_a, new CS_CAMLch(brightCond, b));
		assertArrayEquals(test.getComponents(), PColor.convert(testLch_transposed_b, CS_sRGB.instance).getComponents(), 0.00001f);
		
		// -> a -> b -> a
		PColor testLch_transposed_back = PColor.convert(testLch_transposed_b, new CS_CAMLch(brightCond, a));
		assertArrayEquals(test.getComponents(), PColor.convert(testLch_transposed_back, CS_sRGB.instance).getComponents(), 0.00001f);
	}
	
	private void testReconfAB(sRGB test, int[] a, int[] b) {
		// -> a
		PColor test_Lab_a = PColor.convert(test, new CS_CAMLab(brightCond, a));
		assertArrayEquals(test.getComponents(), PColor.convert(test_Lab_a, CS_sRGB.instance).getComponents(), 0.00001f);
		
		// -> a -> b
		PColor test_Lab_b = PColor.convert(test_Lab_a, new CS_CAMLab(brightCond, b));
		assertArrayEquals(test.getComponents(), PColor.convert(test_Lab_b, CS_sRGB.instance).getComponents(), 0.00001f);
		
		// -> a -> b -> a
		PColor converted = PColor.convert(PColor.convert(test_Lab_b, new CS_CAMLab(brightCond, a)), CS_sRGB.instance);
		assertArrayEquals(test.getComponents(), converted.getComponents(), 0.00001f);	
	}
	
	// test RGB -> CS -> RGB
	private void testRGBForwardBackward(float[] rgb, ColorSpace cs, float delta) {
		// test color space ops
		float[] other = cs.fromRGB(rgb);
		float[] back_rgb = cs.toRGB(other);
		assertArrayEquals(rgb, back_rgb, delta);
		
		// test equivalent pcolor conversion ops
		sRGB sRGB = new sRGB(rgb);
		PColor otherpcol = PColor.convert(sRGB, cs);
		PColor pcback = PColor.convert(otherpcol, CS_sRGB.instance);
		assertArrayEquals(sRGB.getComponents(), pcback.getComponents(), delta);
		
		// inter-compare method's results (should be equal or very close
		// as the code paths should be the same)
		assertArrayEquals(back_rgb, pcback.getComponents(), 0.0000001f);
	}
	
	// test CS_in -> cs_test -> CS_in
	private void testForwardBackward(PColor in, ColorSpace cs, float delta) {
		PColor b = PColor.convert(in, cs);
		PColor test = PColor.convert(b, in.getColorSpace());
		assertArrayEquals(in.getComponents(), test.getComponents(), delta);
	}
	
}