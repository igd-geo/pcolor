package de.fhg.igd.pcolor.test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import de.fhg.igd.pcolor.colorspace.CS_CIECAM02;
import de.fhg.igd.pcolor.colorspace.CS_JCh;
import de.fhg.igd.pcolor.colorspace.CS_Jab;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.Surrounding;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;

/**
 * testing conversion with pcolor
 * @author Thu Huong
 */
public class ConversionTest {
	ViewingConditions brightCond = new ViewingConditions(CS_CIECAM02.D65White, 318.31, 20.0, Surrounding.averageSurrounding);
	ViewingConditions darkCond = new ViewingConditions(CS_CIECAM02.D65White, 31.83, 20.0, Surrounding.averageSurrounding);

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back.   
	 */
	@Test
	public void XYZtoCIECAM02_1() {
		float[] xyz = new float[]{0.1901f, 0.2f, 0.2178f};

		CS_CIECAM02 csc = new CS_CIECAM02(brightCond);
		float[] ciecam = csc.fromCIEXYZ(xyz);
		float[] back_xyz = csc.toCIEXYZ(ciecam);

		System.out.println("XYZtoCIECAM02_1:");
		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2]);
		System.out.println("ciecam: "+ ciecam[0] +" "+ ciecam[1] +" "+ ciecam[2] +" "+ ciecam[3] +" "+ ciecam[4] +" "+ ciecam[5] +" "+ ciecam[6] +"\n");

		assertArrayEquals(xyz, back_xyz, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back. 
	 */
	@Test
	public void XYZtoCIECAM02_2() {
		float[] xyz = new float[]{0.5706f, 0.4306f, 0.3196f};

		CS_CIECAM02 csc = new CS_CIECAM02(darkCond);
		float[] ciecam = csc.fromCIEXYZ(xyz);
		float[] back_xyz = csc.toCIEXYZ(ciecam);

		System.out.println("XYZtoCIECAM02_2:");
		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2]);
		System.out.println("ciecam: "+ ciecam[0] +" "+ ciecam[1] +" "+ ciecam[2] +" "+ ciecam[3] +" "+ ciecam[4] +" "+ ciecam[5] +" "+ ciecam[6] +"\n");

		assertArrayEquals(xyz, back_xyz, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back. 
	 */
	@Test
	public void XYZtoCIECAM02_3() {
		float[] xyz = new float[]{0.0353f, 0.0656f, 0.0214f};

		CS_CIECAM02 csc = new CS_CIECAM02(brightCond);
		float[] ciecam = csc.fromCIEXYZ(xyz);
		float[] back_xyz = csc.toCIEXYZ(ciecam);

		System.out.println("XYZtoCIECAM02_3:");
		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2]);
		System.out.println("ciecam: "+ ciecam[0] +" "+ ciecam[1] +" "+ ciecam[2] +" "+ ciecam[3] +" "+ ciecam[4] +" "+ ciecam[5] +" "+ ciecam[6] +"\n");

		assertArrayEquals(xyz, back_xyz, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to JCh and back.   
	 */
	@Test
	public void XYZtoJCh() {
		float[] xyz = new float[]{0.1901f, 0.2f, 0.2178f};

		CS_JCh csJCh = new CS_JCh(brightCond);
		float[] jch = csJCh.fromCIEXYZ(xyz);
		float[] back_xyz = csJCh.toCIEXYZ(jch);

		System.out.println("XYZtoJCh:");
		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2]);
		System.out.println("ciecam: "+ jch[0] +" "+ jch[1] +" "+ jch[2] +"\n");

		assertArrayEquals(xyz, back_xyz, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to Jab and back. 
	 */
	@Test
	public void XYZtoJab() {
		float[] xyz = new float[]{0.5706f, 0.4306f, 0.3196f};

		CS_Jab csJab = new CS_Jab(darkCond);
		float[] jab = csJab.fromCIEXYZ(xyz);
		float[] back_xyz = csJab.toCIEXYZ(jab);

		System.out.println("XYZtoJab:");
		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2]);
		System.out.println("Jab: "+ jab[0] +" "+ jab[1] +" "+ jab[2] +"\n");

		assertArrayEquals(xyz, back_xyz, 0.001f);
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

		System.out.println("RGBtoCIECAM02:");
		System.out.println("rgb before: "+ rgb[0] +" "+ rgb[1] +" "+ rgb[2]);
		System.out.println("rgb after: "+ back[0] +" "+ back[1] +" "+ back[2]);
		System.out.println("ciecam: "+ ciecam[0] +" "+ ciecam[1] +" "+ ciecam[2] +" "+ ciecam[3] +" "+ ciecam[4] +" "+ ciecam[5] +" "+ ciecam[6] +"\n");

		assertArrayEquals(rgb, back, 0.001f);
	}

	/**
	 * tests conversion from sRGB to JCh and back. checks if CIECAM02 values are correct.
	 */
	@Test
	public void RGBtoJCh() {
		float[] rgb = new float[]{0.5f, 0.5f, 0.5f};

		CS_JCh csJCh = new CS_JCh(brightCond);
		float[] jch = csJCh.fromRGB(rgb);
		float[] back = csJCh.toRGB(jch);

		System.out.println("RGBtoJCh:");
		System.out.println("rgb before: "+ rgb[0] +" "+ rgb[1] +" "+ rgb[2]);
		System.out.println("rgb after: "+ back[0] +" "+ back[1] +" "+ back[2]);
		System.out.println("JCh: "+ jch[0] +" "+ jch[1] +" "+ jch[2] +"\n");

		assertArrayEquals(rgb, back, 0.001f);
	}

	/**
	 * tests conversion from sRGB to JCh and back. checks if CIECAM02 values are correct.
	 */
	@Test
	public void RGBtoJab() {
		float[] rgb = new float[]{0.736f, 0.237f, 0.946f};

		CS_Jab csJab = new CS_Jab(brightCond);
		float[] jab = csJab.fromRGB(rgb);
		float[] back = csJab.toRGB(jab);

		System.out.println("RGBtoJab:");
		System.out.println("rgb before: "+ rgb[0] +" "+ rgb[1] +" "+ rgb[2]);
		System.out.println("rgb after: "+ back[0] +" "+ back[1] +" "+ back[2]);
		System.out.println("JCh: "+ jab[0] +" "+ jab[1] +" "+ jab[2] +"\n");

		assertArrayEquals(rgb, back, 0.001f);
	}

	/**
	 * tests conversion from CIEXYZ to sRGB and back. 
	 */
	@Test
	public void XYZtoRGB() {
		float[] xyz = new float[]{0.0353f, 0.0656f, 0.0214f};

		CS_sRGB sRGB = new CS_sRGB();
		float[] rgb = sRGB.fromCIEXYZ(xyz);
		float[] back_xyz = sRGB.toCIEXYZ(rgb);

		System.out.println("XYZtoRGB:");
		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2]);
		System.out.println("rgb: "+ rgb[0] +" "+ rgb[1] +" "+ rgb[2]);

		assertArrayEquals(xyz, back_xyz, 0.001f);
	}
}