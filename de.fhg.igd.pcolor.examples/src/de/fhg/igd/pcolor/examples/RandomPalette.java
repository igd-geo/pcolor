package de.fhg.igd.pcolor.examples;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import de.fhg.igd.pcolor.JCh;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_JCh;
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
		if (args.length < 2) {
			System.err.println("Please specify a file, a number of colors to create, a background color and any colors that should be considered pre-occupied.");
			return;
		}
		
		OutputStreamWriter file = new OutputStreamWriter(new FileOutputStream(Paths.get(args[0]).toFile()));
		int n = Integer.parseInt(args[1]);
		
		// construct colorspace using background color
		sRGB bgCol = ColorTools.parseColor(args[2]);
		CS_JCh cspace = new CS_JCh(
				new ViewingConditions(bgCol.getColorSpace().toCIEXYZ(bgCol.getComponents()),
						200,
						200/5,
						Surrounding.averageSurrounding));
		
		
		Collection<JCh> occ = new ArrayList<>();
		
		for (int i = 3; i < args.length; i++) {
			occ.add((JCh) PColor.convert(ColorTools.parseColor(args[i]), cspace));
		}
		
		new RandomPalette().write(file, occ, n);
	}
	
	/**
	 * @return a randomly chosen JCH which is inside sRGB
	 */
	private JCh randCol() {
		while(true) {
			JCh c = new JCh(rand.nextFloat() * 100, rand.nextFloat() * 100, rand.nextFloat() * 360);
			if (ColorPredicates.is_sRGB.apply(c)) {
				return c;
			}
		}
	}
	
	private float findMinDistance(Collection<JCh> colors, JCh col) {
		float min = Float.MAX_VALUE;
		for (JCh c : colors) {
			float distance = ColorTools.distance(c, col);
			if (distance < min)
				min = distance;
		}
		return min;
	}
	
	private JCh findMostDistantRandomColor(Collection<JCh> colors, int n) {
		float maxMin = 0;
		JCh candidate = randCol(); //avoids null return, however unlikely 
		for (int x = 0; x < n; x++) {
			JCh r = randCol();
			float d = findMinDistance(colors, r);
			if (d > maxMin) {
				maxMin = d;
				candidate = r;
			}
		}
		return candidate;
	}

	private void write(OutputStreamWriter outputStreamWriter, Collection<JCh> preoccupied, int numberOfColors) {
		for (int i = 0; i < numberOfColors; i++) {
			JCh mostDistantRandomColor = findMostDistantRandomColor(preoccupied, 1000);
			System.out.println(ColorTools.toHtml(mostDistantRandomColor, false));
			preoccupied.add(mostDistantRandomColor);
		}
	}

}
