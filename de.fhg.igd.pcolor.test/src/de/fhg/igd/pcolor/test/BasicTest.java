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
import de.fhg.igd.pcolor.util.ColorTools;

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
	
	@Test
	public void testSrgbHelpers() {
		assertEquals(1.0, sRGB.fromArgb(0xaaffeedd).get(sRGB.R), /*delta*/ 0f);
		assertEquals("#ffeeddaa", ColorTools.toHtml(sRGB.fromArgb(0xaaffeedd), true));
		assertEquals("#ffeedd", ColorTools.toHtml(sRGB.fromArgb(0xaaffeedd), false));
		assertEquals("rgba(255, 238, 221, 170)", ColorTools.toCss(sRGB.fromArgb(0xaaffeedd), true));
		assertEquals("rgb(255, 238, 221)", ColorTools.toCss(sRGB.fromArgb(0xaaffeedd), false));
		
		// test padding
		assertEquals("#01020304", ColorTools.toHtml(sRGB.fromArgb(0x04010203), true));
		assertEquals("rgba(  1,   2,   3,   4)", ColorTools.toCss(sRGB.fromArgb(0x04010203), true));
		
		// test unclipped
		assertEquals("rgba( -1,  20, 300,   4)", ColorTools.toCssUnclipped(sRGB.fromBytes(-1, 20, 300, 4), true));
	}

}
