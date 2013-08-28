package de.fhg.igd.pcolor.examples;

import static de.fhg.igd.pcolor.util.ColorTools.parseColor;
import static de.fhg.igd.pcolor.util.ColorTools.toHtml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;

import de.fhg.igd.pcolor.CAMLab;
import de.fhg.igd.pcolor.CAMLch;
import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_CAMLab;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;
import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * This example writes a table of colors and the result of different ways of
 * mixing these colors. The main point is to show that sRGB is not a good color
 * space to work in.
 * 
 * @author Simon Thum
 */
public class MixingIllustration {
	
	private static class MixingExample {
		public MixingExample(sRGB c1, sRGB c2) {
			this.c1 = c1;
			this.c2 = c2;
		}
		sRGB c1, c2;
	}
	
	MixingExample[] examples = new MixingExample[] {
			new MixingExample(sRGB.fromBytes(0,0,0), sRGB.fromBytes(255,255,255)),
			new MixingExample(sRGB.fromBytes(0,0,255), sRGB.fromBytes(0,255,255)),
			new MixingExample(sRGB.fromBytes(255,0,255), sRGB.fromBytes(0,255,0)),
			new MixingExample(sRGB.fromBytes(255,255,0), sRGB.fromBytes(0,0,255)),
			new MixingExample(sRGB.fromBytes(255,255,0), sRGB.fromBytes(0,255,255)),
			new MixingExample(sRGB.fromBytes(255,0,0), sRGB.fromBytes(0,255,0)),
			new MixingExample(sRGB.fromBytes(255,0,0), sRGB.fromBytes(0,0,255)),
			new MixingExample(sRGB.fromBytes(255,128,0), sRGB.fromBytes(0,128,255)),
			// some RandomPalette output
			new MixingExample(parseColor("#6e7c91"), parseColor("#d1f707")),
			new MixingExample(parseColor("#fd2f41"), parseColor("#2d0004")),
			new MixingExample(parseColor("#fafafe"), parseColor("#055505")),
			new MixingExample(parseColor("#020b3a"), parseColor("#e978fe")),
	};
	
	/**
	 * @param args the file name to write to
	 * @throws Exception on error ;)
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Please specify a file.");
			return;
		}
		
		OutputStreamWriter file = new OutputStreamWriter(new FileOutputStream(Paths.get(args[0]).toFile()));
		new MixingIllustration().write(file);
	}

	private void write(OutputStreamWriter out) throws Exception {
		out.write("<!DOCTYPE html>\n");
		out.write("<html>\n");
		out.write("<body style='background-color: #fff; text-color:#111'>\r\n");
		//out.write("<body style='background-color: #757575; text-color:#bbb'>\r\n");
		out.write("<table width = \"80%\" >\r\n");
		out.write(" <colgroup width='200' span='2'></colgroup>\r\n");
		out.write("<tr><th colspan='2'>These two colors mixed 50:50 according to ...</th>"
				+ "<th></th><th>JCh<br>(polar perceptual correlates<sup>1</sup>)</th>"
				+ "<th></th><th>Jab<sub>Ch</sub><br>(cartesian perceptual correlates)</th>"
				+ "<th></th><th>XYZ<br>(optical intensity<sup>2</sup>)</th>"
				+ "<th></th><th>sRGB<sup>3</sup></th></tr>\r\n");
		for (MixingExample e : examples) {
			out.write("<tr>\n");
			CAMLch jch1 = (CAMLch)PColor.convert(e.c1, CS_CAMLch.defaultJChInstance);
			CAMLch jch2 = (CAMLch)PColor.convert(e.c2, CS_CAMLch.defaultJChInstance);
			CIEXYZ xyz1 = (CIEXYZ)PColor.convert(e.c1, CS_CIEXYZ.instance);
			CIEXYZ xyz2 = (CIEXYZ)PColor.convert(e.c2, CS_CIEXYZ.instance);
			sRGB bad_mix = broken_sRGB_mix(e);
			
			CAMLab psychoAverage = CAMLab.average(new CAMLab[] {
					(CAMLab) PColor.convert(jch1, CS_CAMLab.defaultJaMbMInstance),
					(CAMLab) PColor.convert(jch2, CS_CAMLab.defaultJaMbMInstance)}
			);
			CAMLch psychoMidJCh = CAMLch.blend(jch1, jch2, 0.5f);
			CIEXYZ opticalAverage = CIEXYZ.average(new CIEXYZ[]{xyz1, xyz2});
			
			writeCell(out, e.c1, ColorTools.toCss(e.c1, false));
			writeCell(out, e.c2, ColorTools.toCss(e.c2, false));
			writeCell(out);
			writeCell(out, psychoMidJCh, ColorTools.toCssUnclipped(psychoMidJCh, false));
			writeCell(out);
			writeCell(out, psychoAverage, ColorTools.toCssUnclipped(psychoAverage, false));
			writeCell(out);
			writeCell(out, opticalAverage, ColorTools.toCssUnclipped(opticalAverage, false));
			writeCell(out);
			writeCell(out, bad_mix, ColorTools.toCssUnclipped(bad_mix, false));
			out.write("</tr>\n");
		}
		
		out.write("</table>");
		out.write("<p>Note 1: Especially for distant colors, this mode of mixing is best understood on a gradient, not a single sample.</p>");
		out.write("<p>Note 2: This is what blending colored lights should give: XYZ does not matter much, any intensity linear space would give identical results.</p>");
		out.write("<p>Note 3: Mixing sRGB is easy but incorrect for most applications and sub-par for the rest. Note it is usually darker than any other method.</p>");
		out.write("<p>Any sRGB values outside the 0..255 range cannot be faithfully reproduced on most monitors.</p>");
		out.write("<p>2013 Simon Thum, Fraunhofer IGD</p>");
		out.write("</body>");
		out.write("</html>");
		out.close();
	}

	/**
	 * "the middle" in sRGB. This is broken and only serves illustrative
	 * purposes! It therefore has no actual library support. Understandably but
	 * unfortunately, it's what people do intuitively. It simply does not get
	 * you what you want, most of the time. DON'T DO THIS AT HOME! See
	 * http://en.wikipedia.org/wiki/Gamma_correction
	 */
	private sRGB broken_sRGB_mix(MixingExample e) {
		return new sRGB(new float[]{
				(e.c1.get(sRGB.R) + e.c2.get(sRGB.R)) / 2.0f,
				(e.c1.get(sRGB.G) + e.c2.get(sRGB.G)) / 2.0f,
				(e.c1.get(sRGB.B) + e.c2.get(sRGB.B)) / 2.0f});
	}
	
	private void writeCell(OutputStreamWriter out) throws IOException {
		out.append("    <td>&nbsp;</td>");
	}

	private void writeCell(OutputStreamWriter out, PColor c, String text) throws IOException {
		out.write(String.format("    <td style='background-color: %s'>%s</td>\n", toHtml(c, false), text != null ? text : "&nbsp;"));
	}

}
