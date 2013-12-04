package de.fhg.igd.pcolor.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import de.fhg.igd.pcolor.CAMLab;
import de.fhg.igd.pcolor.CAMLch;
import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.Illuminant;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_CAMLab;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.Surrounding;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

// these could probably become more precise, but seemingly CIECAM02
// itself has some open issues with numerical stability. Until these
// are resolved, this is probably as good as it gets (Jan 2013)
// and may serve to signal detoriation.
public class TranspositionTest {

	@Test
	public void testThatssRGBWhiteLooksWhite() {
		CAMLab jab = new CAMLab(new sRGB(1.0f, 1.0f, 1.0f), CS_CAMLab.defaultJaMbMInstance);
		assertEquals(100.0, jab.get(CAMLab.L), 0.0001);
		// sRGB whitepoint is not really center
		assertEquals(0.0, jab.get(CAMLab.a), 2);  
		assertEquals(0.0, jab.get(CAMLab.b), 2);
		
		CAMLch jch = new CAMLch(new sRGB(1.0f, 1.0f, 1.0f), CS_CAMLch.defaultJChInstance);
		assertEquals(100.0, jch.get(CAMLch.L), 0.0001);
		assertEquals(0.0, jch.get(CAMLch.c), 2);
	}
	
	@Test
	public void testSomethingThatLooksWhiteBecomesWhite() {
		CAMLab jab = new CAMLab(new float[] {100.0f, 0.0f, 0.0f}, 1.0f, CS_CAMLab.defaultJaMbMInstance);
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
	 * related to transposing colors. So far unsuccessful.
	 **********************************************/
	@Ignore
	public void matchResultsGreyscale() {
		sRGB[] p1 = new sRGB[] {ColorTools.parseColor("#444"), ColorTools.parseColor("#5d5f5f")};
		sRGB[] p2 = new sRGB[] {ColorTools.parseColor("#888"), ColorTools.parseColor("#9c9fa0")};
		sRGB[] p3 = new sRGB[] {ColorTools.parseColor("#ccc"), ColorTools.parseColor("#d4d8d9")};
		
		// create office VC
		float[] mixedWhitepoint = ViewingConditions.mixedWhitepoint(Illuminant.D65, Illuminant.F2, 1f).toxyY();
		
		// white background VC
		ViewingConditions vcWhite = ViewingConditions.createAdapted(
				CIEXYZ.fromxyY(mixedWhitepoint[0], mixedWhitepoint[1], 500f), 100, 159, Surrounding.averageSurrounding);
		CS_CAMLab cs_white = new CS_CAMLab(vcWhite, CS_CAMLab.JCH);
		
		// dark background VC
		ViewingConditions vcBlack = ViewingConditions.createAdapted(
				CIEXYZ.fromxyY(mixedWhitepoint[0], mixedWhitepoint[1], 100f), 100, 159, Surrounding.averageSurrounding);
		CS_CAMLab cs_black = new CS_CAMLab(vcBlack, CS_CAMLab.JCh);
		
		testTransposition(p1[0], p1[1], cs_white, cs_black, 0.015f);
		testTransposition(p2[0], p2[1], cs_white, cs_black, 0.015f);
		testTransposition(p3[0], p3[1], cs_white, cs_black, 0.015f);
	}

	// test that g1 becomes g2 when transposed from cs1 to cs2 
	private void testTransposition(sRGB g1, sRGB g2, CS_CAMLab cs1, CS_CAMLab cs2, float d) {
		// to JCh w/ black BG
		CAMLab lab1 = new CAMLab(g1, cs2);
		
		// transpose
		lab1 = (CAMLab) lab1.transpose(cs1);
		
		// to RGB
		sRGB c2 = (sRGB) PColor.convert(lab1, CS_sRGB.instance);
		
		// compare
		assertArrayEquals(g2.getComponents(), c2.getComponents(), d);
	}
	
	

}
