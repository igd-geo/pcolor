package de.fhg.igd.pcolor.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.Illuminant;
import de.fhg.igd.pcolor.JCh;
import de.fhg.igd.pcolor.Jab;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_JCh;
import de.fhg.igd.pcolor.colorspace.CS_Jab;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.Surrounding;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;
import de.fhg.igd.pcolor.util.MathTools;

// these could probably become more precise, but seemingly CIECAM02
// itself has some open issues with numerical stability. Until these
// are resolved, this is probably as good as it gets (Jan 2013)
// and may serve to signal detoriation.
public class TranspositionTest {

	@Test
	public void testThatssRGBWhiteLooksWhite() {
		Jab jab = new Jab(new sRGB(1.0f, 1.0f, 1.0f), CS_Jab.defaultInstance);
		assertEquals(100.0, jab.get(Jab.J), 0.0001);
		assertEquals(0.0, jab.get(Jab.a), 0.02);
		assertEquals(0.0, jab.get(Jab.b), 0.02);
		
		JCh jch = new JCh(new sRGB(1.0f, 1.0f, 1.0f), CS_JCh.defaultInstance);
		assertEquals(100.0, jch.get(JCh.J), 0.0001);
		// this is quite substantial
		assertEquals(0.0, jch.get(JCh.C), 2);
		// hue is irrelevant in white
	}
	
	@Test
	public void testSomethingThatLooksWhiteBecomesWhite() {
		Jab jab = new Jab(100.0f, 0.0f, 0.0f, 1.0f, CS_Jab.defaultInstance);
		sRGB rgb = (sRGB) PColor.convert(jab, CS_sRGB.instance);
		assertEquals(rgb.get(sRGB.R), 1.0, 0.01);
		assertEquals(rgb.get(sRGB.G), 1.0, 0.01);
		assertEquals(rgb.get(sRGB.B), 1.0, 0.01);
	}
	
	/***********************************************
	 * Transposition tests
	 * 
	 * These try to reproduce the results shown on
	 * http://scanline.ca/ciecam02/
	 * related to transposing colors.
	 **********************************************/
	@Test
	public void matchResultsGreyscale() {
		sRGB[] p1 = new sRGB[] {ColorTools.parseColor("#444"), ColorTools.parseColor("#5d5f5f")};
		sRGB[] p2 = new sRGB[] {ColorTools.parseColor("#888"), ColorTools.parseColor("#9c9fa0")};
		sRGB[] p3 = new sRGB[] {ColorTools.parseColor("#ccc"), ColorTools.parseColor("#d4d8d9")};
		
		// create office roughly VC following CIE 159:2004 sec. 5
		float[] mixedWhitepoint = ViewingConditions.mixedWhitepoint(Illuminant.D65, Illuminant.F2, 1f).toxyY();
		// scene white luminance
		ViewingConditions vcWhite = ViewingConditions.createAdapted(
				CIEXYZ.fromxyY(mixedWhitepoint[0], mixedWhitepoint[1], 80f), 16, 159, Surrounding.averageSurrounding);
		CS_JCh cs_white = new CS_JCh(vcWhite);
		
		ViewingConditions vcBlack = ViewingConditions.createAdapted(
				CIEXYZ.fromxyY(mixedWhitepoint[0], mixedWhitepoint[1], 40f), 20/*very invariant*/, 159, Surrounding.averageSurrounding);
		CS_JCh cs_black = new CS_JCh(vcBlack);
		
		// assertArrayEquals(p1[0].getComponents(), ColorTools.parseColor("#444").getComponents(), 0.00001f);
		
		testTransposition(p1[0], p1[1], cs_white, cs_black, 0.015f);
		testTransposition(p2[0], p2[1], cs_white, cs_black, 0.015f);
		testTransposition(p3[0], p3[1], cs_white, cs_black, 0.015f);
	}

	// test that g1 becomes g2 when transposed from cs1 to cs2 
	private void testTransposition(sRGB g1, sRGB g2, CS_JCh cs1, CS_JCh cs2, float d) {
		// to JCh w/ black BG
		JCh jch1 = new JCh(g1, cs2);
		
		// transpose
		jch1 = (JCh) jch1.transpose(cs1);
		
		// to RGB
		sRGB c2 = (sRGB) PColor.convert(jch1, CS_sRGB.instance);
		
		// compare
		assertArrayEquals(g2.getComponents(), c2.getComponents(), d);
	}
	
	

}
