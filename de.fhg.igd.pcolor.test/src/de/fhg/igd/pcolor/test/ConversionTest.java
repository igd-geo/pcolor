package de.fhg.igd.pcolor.test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Ignore;
import org.junit.Test;

import de.fhg.igd.pcolor.colorspace.CS_CIECAM02;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.Surrounding;

/**
 * testing conversion with pcolor
 * @author Thu Huong
 */
public class ConversionTest {

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back. checks if CIECAM02 values are correct.  
	 */
	@Ignore
	@Test
	public void XYZtoCIECAM02_1() {
		float[] xyz = new float[]{0.1901f, 0.2f, 0.2178f};
		float[] result = new float[]{41.73f, 195.37f, 0.10f, 0.11f, 2.36f, 278.1f, 219.0f};

		System.out.println("XYZtoCIECAM02_1");

		CS_CIECAM02 csc = new CS_CIECAM02(CS_CIECAM02.D65White, 318.31, 20.0, Surrounding.averageSurrounding);
		float[] ciecam = csc.fromCIEXYZ(xyz);
		float[] back_xyz = csc.toCIEXYZ(ciecam);

		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2]);

		System.out.println("result: "+ result[0] +" "+ result[1] +" "+ result[2] +" "+ result[3] +" "+ result[4] +" "+ result[5] +" "+ result[6]);
		System.out.println("ciecam: "+ ciecam[0] +" "+ ciecam[1] +" "+ ciecam[2] +" "+ ciecam[3] +" "+ ciecam[4] +" "+ ciecam[5] +" "+ ciecam[6] +"\n");

		assertArrayEquals(result, ciecam, 0.1f);
		assertArrayEquals(xyz, back_xyz, 0.1f);
	}

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back. checks if CIECAM02 values are correct.
	 */
	@Ignore
	@Test
	public void XYZtoCIECAM02_2() {
		float[] xyz = new float[]{0.5706f, 0.4306f, 0.3196f};
		float[] result = new float[]{65.96f, 152.67f, 48.57f, 41.67f, 52.25f, 399.6f, 19.6f};

		System.out.println("XYZtoCIECAM02_2");

		CS_CIECAM02 csc = new CS_CIECAM02(CS_CIECAM02.D65White, 31.83, 20.0, Surrounding.averageSurrounding);
		float[] ciecam = csc.fromCIEXYZ(xyz);
		float[] back = csc.toCIEXYZ(ciecam);

		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back[0] +" "+ back[1] +" "+ back[2]);

		System.out.println("result: "+ result[0] +" "+ result[1] +" "+ result[2] +" "+ result[3] +" "+ result[4] +" "+ result[5] +" "+ result[6]);
		System.out.println("ciecam: "+ ciecam[0] +" "+ ciecam[1] +" "+ ciecam[2] +" "+ ciecam[3] +" "+ ciecam[4] +" "+ ciecam[5] +" "+ ciecam[6] +"\n");

		assertArrayEquals(result, ciecam, 0.1f);
		assertArrayEquals(xyz, back, 0.1f);
	}

	/**
	 * tests conversion from CIEXYZ to CIECAM02 and back. checks if CIECAM02 values are correct.
	 */
	@Ignore
	@Test
	public void XYZtoCIECAM02_3() {
		float[] xyz = new float[]{0.0353f, 0.0656f, 0.0214f};
		float[] result = new float[]{21.79f, 141.17f, 46.94f, 48.8f, 58.79f, 220.4f, 177.1f};

		System.out.println("XYZtoCIECAM02_3");

		CS_CIECAM02 csc = new CS_CIECAM02(CS_CIECAM02.D65White, 318.31, 20.0, Surrounding.averageSurrounding);
		float[] ciecam = csc.fromCIEXYZ(xyz);
		float[] back = csc.toCIEXYZ(ciecam);

		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("xyz after: "+ back[0] +" "+ back[1] +" "+ back[2]);

		System.out.println("result: "+ result[0] +" "+ result[1] +" "+ result[2] +" "+ result[3] +" "+ result[4] +" "+ result[5] +" "+ result[6]);
		System.out.println("ciecam: "+ ciecam[0] +" "+ ciecam[1] +" "+ ciecam[2] +" "+ ciecam[3] +" "+ ciecam[4] +" "+ ciecam[5] +" "+ ciecam[6] +"\n");

		assertArrayEquals(result, ciecam, 0.1f);
		assertArrayEquals(xyz, back, 0.1f);
	}

	/**
	 * tests conversion from sRGB to CIECAM02 and back. checks if CIECAM02 values are correct.
	 */
	@Ignore
	@Test
	public void RGBtoCIECAM02_1() {
		float[] rgb = new float[]{0.03725068f, 0.33136493f, 0.10736899f};
		float[] result = new float[]{21.79f, 141.17f, 46.94f, 48.80f, 58.79f, 220.4f, 177.1f};

		System.out.println("RGBtoCIECAM02");

		CS_CIECAM02 csc = new CS_CIECAM02(CS_CIECAM02.D65White, 318.31, 20.0, Surrounding.averageSurrounding);
		float[] ciecam = csc.fromRGB(rgb);
		float[] back = csc.toRGB(ciecam);

		System.out.println("rgb before: "+ rgb[0] +" "+ rgb[1] +" "+ rgb[2]);
		System.out.println("rgb after: "+ back[0] +" "+ back[1] +" "+ back[2]);

		System.out.println("result: "+ result[0] +" "+ result[1] +" "+ result[2] +" "+ result[3] +" "+ result[4] +" "+ result[5] +" "+ result[6]);
		System.out.println("ciecam: "+ ciecam[0] +" "+ ciecam[1] +" "+ ciecam[2] +" "+ ciecam[3] +" "+ ciecam[4] +" "+ ciecam[5] +" "+ ciecam[6] +"\n");

		assertArrayEquals(result, ciecam, 0.1f);
		assertArrayEquals(rgb, back, 0.1f);
	}

	/**
	 * tests conversion from sRGB to CIECAM02 and back. starting with CIEXYZ -> sRGB -> CIECAM02 and back.
	 */
	@Test
	public void RGBtoCIECAM02_2() {
		float[] xyz = new float[]{0.0353f, 0.0656f, 0.0214f};

		CS_sRGB sRGB = new CS_sRGB();
		float[] rgb = sRGB.fromCIEXYZ(xyz);

		CS_CIECAM02 csc = new CS_CIECAM02(CS_CIECAM02.D65White, 318.31, 20.0, Surrounding.averageSurrounding);
		float[] ciecam = csc.fromRGB(rgb);
		float[] back_xyz = csc.toCIEXYZ(ciecam);

		float[] back_rgb = sRGB.fromCIEXYZ(back_xyz);

		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("rgb: "+ rgb[0] +" "+ rgb[1] +" "+ rgb[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2] +"\n");

		assertArrayEquals(xyz, back_xyz, 0.001f);
		assertArrayEquals(rgb, back_rgb, 0.001f);
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
		float[] back_rgb = sRGB.fromCIEXYZ(back_xyz);

		System.out.println("xyz before: "+ xyz[0] +" "+ xyz[1] +" "+ xyz[2]);
		System.out.println("rgb: "+ rgb[0] +" "+ rgb[1] +" "+ rgb[2]);
		System.out.println("xyz after: "+ back_xyz[0] +" "+ back_xyz[1] +" "+ back_xyz[2] +"\n");

		assertArrayEquals(xyz, back_xyz, 0.001f);
		assertArrayEquals(rgb, back_rgb, 0.001f);
	}

}