package de.fhg.igd.pcolor.examples;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.CAMLch;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;
import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;
import de.fhg.igd.pcolor.colorspace.Surrounding;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorPredicates;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * This example writes random colors to a file. However, you can specify how
 * many random colors are created, and a list of colors that should be
 * considered pre-occupied. The first color is the surround against which colors
 * are compared to each other. You need to repeat it in order to avoid it as an
 * output color.
 * 
 * This gives you a random palette with a good level of distinctness of the
 * generated colors, which also differentiate against an already existing color
 * palette.
 * 
 * @author Simon Thum
 */
public class RandomPalette {
	
	private Random rand = new Random(); 
	
	/**
	 * @param args the file name to write to
	 * @throws Exception on error :P
	 */
	public static void main(String[] args) throws NumberFormatException, Exception {
		if (args.length < 3) {
			System.err.println("Please specify a file, a number of colors to create, a background color and any colors that should be considered pre-occupied.");
			return;
		}
		
		OutputStreamWriter file = new OutputStreamWriter(new FileOutputStream(Paths.get(args[0]).toFile()));
		int n = Integer.parseInt(args[1]);
		
		// construct colorspace using background color
		sRGB bgCol = ColorTools.parseColor(args[2]);
		CS_CAMLch cspace = new CS_CAMLch(
				ViewingConditions.createAdapted((CIEXYZ) PColor.convert(bgCol, CS_CIEXYZ.instance),
						200,
						200/5,
						Surrounding.averageSurrounding));
		
		
		Collection<CAMLch> occ = new ArrayList<>();
		
		for (int i = 3; i < args.length; i++) {
			occ.add((CAMLch) PColor.convert(ColorTools.parseColor(args[i]), cspace));
		}
		
		new RandomPalette().write(file, occ, n);
	}
	
	/**
	 * @return a randomly chosen JCH which is inside sRGB
	 */
	private CAMLch randCol() {
		while(true) {
			CAMLch c = new CAMLch(new float[] {
										rand.nextFloat() * 100, rand.nextFloat() * 120, rand.nextFloat() * 360
								  }, (float) 1.0, CS_CAMLch.defaultJChInstance);
			if (ColorPredicates.is_sRGB.apply(c)) {
				return c;
			}
		}
	}
	
	private float findMinDistance(Collection<CAMLch> colors, CAMLch col) {
		float min = Float.MAX_VALUE;
		for (CAMLch c : colors) {
			float distance = (float) CAMLch.distance(c, col);
			if (distance < min)
				min = distance;
		}
		return min;
	}
	
	private CAMLch findMostDistantRandomColor(Collection<CAMLch> colors, int n) {
		float maxMin = 0;
		CAMLch candidate = randCol(); //avoids null return, however unlikely 
		for (int x = 0; x < n; x++) {
			CAMLch r = randCol();
			float d = findMinDistance(colors, r);
			if (d > maxMin) {
				maxMin = d;
				candidate = r;
			}
		}
		return candidate;
	}

	private void write(OutputStreamWriter outputStreamWriter, Collection<CAMLch> preoccupied, int numberOfColors) {
		for (int i = 0; i < numberOfColors; i++) {
			CAMLch mostDistantRandomColor = findMostDistantRandomColor(preoccupied, 1000);
			System.out.println(ColorTools.toHtml(mostDistantRandomColor, false));
			preoccupied.add(mostDistantRandomColor);
		}
	}

}
