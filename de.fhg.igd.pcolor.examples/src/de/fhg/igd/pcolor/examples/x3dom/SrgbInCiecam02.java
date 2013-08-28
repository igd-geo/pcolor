package de.fhg.igd.pcolor.examples.x3dom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import de.fhg.igd.pcolor.CAMLch;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.util.ColorPredicates;
import de.fhg.igd.pcolor.util.ColorTools;
import de.fhg.igd.pcolor.util.MathTools;

/**
 * Creates a 3D boundary representation of the sRGB space within
 * CIECAM02 (JCh). Boundaries are determined using binary search.
 * 
 * The output is written as an HTML page containing an X3DOM
 * 3D canvas to display the  result.
 * @author Simon Thum
 */
public class SrgbInCiecam02 {
	
	static SortedMap<Float, List<CAMLch>> create3DColorMap(CS_CAMLch jchSpace) {
		TreeMap<Float, List<CAMLch>> rings = new TreeMap<>(); 
		for (float L = 0; L <= 100; L += 4 /* lower than 4 scares x3dom */) {
			ArrayList<CAMLch> ring = new ArrayList<>(360);
			// create a ring (0-360 degree) with maximum colorfulness
			for (float hue = 0; hue < 360; hue += 1) {
				CAMLch startColor = new CAMLch(new float[]{L, 0, hue}, 1f, jchSpace);
				assert ColorPredicates.is_sRGB.apply(startColor);
				ring.add(ColorTools.determineBoundaryColor(startColor, CAMLch.c, 0f, 150f, 0.01f, ColorPredicates.is_sRGB));
			}
			rings.put(L, ring);
		}
		return rings;
	}
	
	static float[] getCoords(CAMLch col) {
		float[] c = new float[3];
		c[0] = (col.get(CAMLch.L)/100f) - 0.5f;
		c[1] = (float) (Math.sin(Math.toRadians(col.get(CAMLch.h))) * col.get(CAMLch.c) / 100f); 
		c[2] = (float) (Math.cos(Math.toRadians(col.get(CAMLch.h))) * col.get(CAMLch.c) / 100f);
		return c;
	}
	
	// create a ring in 3D space by connecting corresponding ring elements (same index)
	// with triangles. It actually creates two arrays, one for linearized coordinates and one for RGB values
	static float[][] create3DRing(List<CAMLch> lower, List<CAMLch> upper) {
		int ringsize = lower.size();
		float[] coordinates = new float[3*3*2*ringsize];
		float[] colors = new float[3*3*2*ringsize];
		assert ringsize == upper.size();
		for (int idx = 0; idx < ringsize; idx ++) {
			int offset = 3*3*2*idx;
			CAMLch ll = lower.get(idx);
			CAMLch lr = lower.get(MathTools.modulo(idx+1, ringsize));
			CAMLch ul = upper.get(idx);
			CAMLch ur = upper.get(MathTools.modulo(idx+1, ringsize));
			// 1st triangle (ll-ul-ur)
			System.arraycopy(getCoords(ll), 0, coordinates, offset+0, 3);
			System.arraycopy(getCoords(ul), 0, coordinates, offset+3, 3);
			System.arraycopy(getCoords(ur), 0, coordinates, offset+6, 3);
			// 2nd triangle (lr-ll-ur)
			System.arraycopy(getCoords(lr), 0, coordinates, offset+9, 3);
			System.arraycopy(getCoords(ll), 0, coordinates, offset+12, 3);
			System.arraycopy(getCoords(ur), 0, coordinates, offset+15, 3);
			// colors
			System.arraycopy(PColor.convert(ll, CS_sRGB.instance).getComponents(), 0, colors, offset+0, 3);
			System.arraycopy(PColor.convert(ul, CS_sRGB.instance).getComponents(), 0, colors, offset+3, 3);
			System.arraycopy(PColor.convert(ur, CS_sRGB.instance).getComponents(), 0, colors, offset+6, 3);
			System.arraycopy(PColor.convert(lr, CS_sRGB.instance).getComponents(), 0, colors, offset+9, 3);
			System.arraycopy(PColor.convert(ll, CS_sRGB.instance).getComponents(), 0, colors, offset+12, 3);
			System.arraycopy(PColor.convert(ur, CS_sRGB.instance).getComponents(), 0, colors, offset+15, 3);
		}
		return new float[][]{coordinates, colors};
	}
	
	static void emitRings(SortedMap<Float, List<CAMLch>> rings, PrintWriter w) {
		List<CAMLch> lowerRing = null;
		for(Entry<Float, List<CAMLch>> r : rings.entrySet()) {
			if (lowerRing != null) {
				w.append("<shape><indexedFaceSet lit='false' ");
				float[][] ring3d = create3DRing(lowerRing, r.getValue());
				float[] coords = ring3d[0];
				float[] colors = ring3d[1];
				
				// emit index
				w.write(" coordIndex='");
				for (int i = 0; i < coords.length/3; i++) {
					if (i > 0 && i % 3 == 0) {
						w.print(-1);
						w.append(' ');
					}
					w.print(i);
					w.append(' ');
				}
				w.println("-1' >");  // completes indexedFaceSet opening tag
				
				// coords (own xml tag / x3d object)
				w.append("<coordinate point='");
				for (int j = 0; j < coords.length; j++) {
					w.printf(Locale.US, "%.5f", coords[j]);
					w.print(' ');
				}
				w.println("' ></coordinate>");
				
				// and colors
				w.append("<color color='");
				for (int j = 0; j < colors.length; j++) {
					w.printf(Locale.US, "%.3f", colors[j]);
					w.print(' ');
				}
				w.println("' ></color>");
				
				// close geom
				w.println("</indexedFaceSet></shape>");
			}
			lowerRing = r.getValue();
		}
		
	}
	
	public static void writeX3domHtmlFile(PrintWriter w) {
		X3domWriter.putHtmlX3domScene(w);
		
		SortedMap<Float, List<CAMLch>> rings = create3DColorMap(CS_CAMLch.defaultJChInstance);
		emitRings(rings, w);
		
		// emit J coordinate axis
		w.append(
				"<transform rotation='0 0 1 " + Math.PI/2 + "'>"+
				" <shape>\r\n"+
				"  <cylinder radius='0.01'></cylinder>\r\n"+
				" </shape>\r\n"+
				"</transform>"
				);
		
		X3domWriter.closeHtmlX3domScene(w);
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		if (args.length < 1) {
			System.err.println("Please specify a file name to use. Note that the resulting scene" +
					"may need a recent browser and access to the x3dom scripts to work.");
			return;
		}
		
		File f = new File(args[0]);
		PrintWriter printWriter = new PrintWriter(f, "utf-8");
		writeX3domHtmlFile(printWriter);
		printWriter.close();
	}
	
}
