package de.fhg.igd.pcolor.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fhg.igd.pcolor.JCh;
import de.fhg.igd.pcolor.Jab;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_JCh;
import de.fhg.igd.pcolor.colorspace.CS_Jab;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;

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
	
	

}
