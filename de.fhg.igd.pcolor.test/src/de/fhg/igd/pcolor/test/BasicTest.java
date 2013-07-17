package de.fhg.igd.pcolor.test;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import de.fhg.igd.pcolor.JCh;
import de.fhg.igd.pcolor.Jab;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_JCh;
import de.fhg.igd.pcolor.colorspace.CS_Jab;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import static de.fhg.igd.pcolor.util.ColorTools.*;
import de.fhg.igd.pcolor.util.ColorTools;
import de.fhg.igd.pcolor.util.MathTools;

public class BasicTest {

	@Test
	public void testisInRange() {
		assertTrue(new sRGB(0.736f, 0.237f, 0.946f).isInRange(0, 0));
		assertTrue(new sRGB(1f, 1f, 1f).isInRange(0, 0));
		assertTrue(new sRGB(0f, 0f, 0f).isInRange(0, 0));
		
		assertFalse(new sRGB(0.736f, 1.237f, 0.946f).isInRange(0, 0));
		assertTrue(new sRGB(0.736f, 1.237f, 0.946f).isInRange(0, 0.25f));
		
		assertFalse(new sRGB(-0.1f, 0.237f, 0.946f).isInRange(0, 0));
		assertTrue(new sRGB(-0.1f, 0.237f, 0.946f).isInRange(0.1f, 0));
		assertTrue(new sRGB(-0.1f, 1.237f, 0.946f).isInRange(0.1f, 0.25f));
		assertFalse(new sRGB(-0.1f, 1.237f, 0.946f).isInRange(0, 0));
	}
	
	@Test
	public void testOutOfRange() {
		assertEquals(0.1, new sRGB(1.1f, 0.237f, 0.946f).outOfSpace(0, 0)[0], 0.0001);
		assertEquals(0.0, new sRGB(1.1f, 0.237f, 0.946f).outOfSpace(0, 0.1f)[0], 0.0001);
		
		assertEquals(-0.1, new sRGB(-0.1f, 0.237f, 0.946f).outOfSpace(0, 0)[0], 0.0001);
		assertEquals(0.0, new sRGB(-0.1f, 0.237f, 0.946f).outOfSpace(0.1f, 0)[0], 0.0001);
	}
	
	@Test
	public void testCreation() {
		assertTrue(PColor.create(CS_sRGB.instance, new float[] {1,1,1,0.5f}) instanceof sRGB);
		assertTrue(PColor.create(CS_JCh.defaultInstance, new float[] {1,1,1,0.5f}) instanceof JCh);
		assertTrue(PColor.create(CS_Jab.defaultInstance, new float[] {1,1,1,0.5f}) instanceof Jab);
	}
	
	private static boolean compareColor(PColor c1, PColor c2) {
		return MathTools.floatArrayEquals(c1.getComponents(), c2.getComponents(), 0.001f);
	}
	
	@Test
	public void testSrgbHelpers() {
		assertEquals(1.0, sRGB.fromArgb(0xaaffeedd).get(sRGB.R), /*delta*/ 0f);
		assertEquals("#ffeeddaa", toHtml(sRGB.fromArgb(0xaaffeedd), true));
		assertEquals("#ffeedd", toHtml(sRGB.fromArgb(0xaaffeedd), false));
		assertEquals("rgba(255, 238, 221, 0.67)", toCss(sRGB.fromArgb(0xaaffeedd), true));
		assertEquals("rgb(255, 238, 221)", toCss(sRGB.fromArgb(0xaaffeedd), false));
		
		// test padding
		sRGB tc1 = sRGB.fromArgb(0x04010203);
		sRGB tc1A = sRGB.fromArgb(0x7f010203); // opaque reference for no-alpha text notations
		assertEquals("#01020304", toHtml(tc1, true));
		assertEquals("rgba(  1,   2,   3, 0.02)", toCss(tc1, true));
		
		// test unclipped
		sRGB tc2 = sRGB.fromBytes(-1, 20, 300, 8);
		assertEquals("rgba( -1,  20, 300, 0.03)", toCssUnclipped(tc2, true));
		
		assertTrue(compareColor(tc1, parseColor(toHtml(tc1, true))));
		assertTrue(compareColor(tc1A, parseColor(toHtml(tc1, false))));
		assertTrue(compareColor(tc1A, parseColor(toHtml(tc1, true))));
		assertTrue(compareColor(tc1, parseColor(toCss(tc1, true))));
		assertTrue(compareColor(tc1A, parseColor(toCss(tc1, false))));
		assertTrue(compareColor(tc1A, parseColor(toCss(tc1, true))));

		// character expansion
		assertTrue(compareColor(sRGB.fromArgb(0x44332211), parseColor("#3214")));
		
		// unclipped
		assertTrue(compareColor(tc2, parseColor(toCssUnclipped(tc2, true))));
	}
	
	@Test
	public void testHueDifference() {
		Random r = new Random();
		for (int i = 0; i < 1000; i++) {
			float hue1 = r.nextFloat() * 720 - 360;
			float hue2 = r.nextFloat() * 720 - 360;
			float d = ColorTools.hueDifference(hue1, hue2);
			assertTrue(d <= 180 && d >= -180);
			assertTrue(hueDistance(hue2, hue1 + hueDifference(hue1, hue2)) < 0.0001);
		}
	}

}
