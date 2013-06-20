package de.fhg.igd.pcolor.examples;


import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;

import de.fhg.igd.pcolor.JCh;
import de.fhg.igd.pcolor.Jab;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_JCh;
import de.fhg.igd.pcolor.colorspace.CS_Jab;
import de.fhg.igd.pcolor.util.ColorTools;
import de.fhg.igd.pcolor.util.MathTools;

/**
 * Example that outputs an HTML page that shows a matrix of colors of identical J
 * but varying hue and colorfulness.
 * @author Simon Thum
 */
public class TwoDColorMatrix {
	
	public static void main(String[] args) throws Throwable {
		if (args.length < 2) {
			System.err.println("Please specify a file and then J (0-100)");
			return;
		}
		new TwoDColorMatrix().emitTable(
				new OutputStreamWriter(new FileOutputStream(Paths.get(args[0]).toFile())),
				Integer.parseInt(args[1]));
	}

	public void emitTable(OutputStreamWriter out, int theJ) throws Throwable {
		JCh start_col = new JCh(new float[] {theJ, 0, 0}, 1, CS_JCh.defaultInstance);
		
		out.write("<!DOCTYPE html>\n");
		out.write("<html>\n");
		out.write("<body style='background-color: #757575; text-color:#bbb'>\r\n");
		out.write("<h3>Colors of equal Lightness (J = " + start_col.get(JCh.J) + ")</h3>\r\n");
		out.write("<p>Colorfulness (C) and Hue (h) spread uniformly according to CIECAM02; darker colors are outside of the sRGB gamut.</p>");
		out.write("<table width = \"90%\">\r\n");
		
		final int numColors = 16;
		// table header - same as inner loop plus start column
		out.write("<th>Start color</th>");
		for (float C = 100; C >= 0; C -= 10) {
			out.write("<th>(C = " + Float.toString(C) + ")</th>\r\n");
		}
		// color table
		for (int i = 0; i < numColors; i++) {
			JCh col = ColorTools.setChannel(start_col, JCh.h, (float)(i * (360.0 / numColors)));
			out.write("<tr>\n");
			String colStr = String.format("J %.0f C %.0f h %.0f", col.get(JCh.J), col.get(JCh.C), col.get(JCh.h));
			out.write(String.format("  <td>%d (" + colStr + ")</td>\n", i));			
			for (float C = 100; C >= 0; C -= 10) {
				col = ColorTools.setChannel(col, JCh.C, C);
				float[] rgb_f = col.getColorSpace().toRGB(col.getComponents());
				int[] rgb = new int[3];
				for (int j = 0; j < 3; j++)
					rgb[j] = MathTools.saturate((int)(rgb_f[j] * 255.0), 0, 255);
				
				Jab col_ref = (Jab) PColor.convert(col, CS_Jab.defaultInstance);
				Jab col_back = (Jab) PColor.convert(new sRGB(rgb[0] / 255.0f, rgb[1] / 255.0f, rgb[2] / 255.0f), CS_Jab.defaultInstance);
				float error = 0;
				for (int j = 0; j < 3; j++) {
					float diff = col_ref.get(j) - col_back.get(j);
					error += diff * diff;
				}
				
				JCh emitCol = col;
				// tone down cells with noticeable error
				error = (float) Math.sqrt(error);
				if (error > 1) {
					emitCol = ColorTools.setChannel(col, JCh.J, theJ / 2);
				}
				
				// finally, emit the color as a table cell
				out.write(String.format("    <td bgcolor=%s>delta E: %2.1f</td>\n", ColorTools.toHtml(emitCol, false), error));
			}
			out.write("</tr>\n");
		}
		out.write("</table>");	
		out.write("<p>2013 Simon Thum (Fraunhofer IGD)</p>");
		out.write("</body>");
		out.write("</html>");
		out.close();
	}

}