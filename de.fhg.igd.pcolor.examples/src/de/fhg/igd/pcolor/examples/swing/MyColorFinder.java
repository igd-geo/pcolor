
package de.fhg.igd.pcolor.examples.swing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.fhg.igd.pcolor.CAMLch;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * Finds colors
 * @author Martin Steiger
 */
public class MyColorFinder
{
	private static final ViewingConditions VIEW_ENV = ViewingConditions.sRGB_typical_envirnonment;
	private static final CS_CAMLch JCH_SPACE = CS_CAMLch.defaultJChInstance;
	
	/**
	 * @param L lightness (in 0..100)
	 * @param C chroma or saturation (in 0..100)
	 * @param minDist the minimum color distance between two colors in delta E (1.0 indicates a diff. that is perceivable by 50% of the population)
	 * @return a list of colors
	 */
	public static List<Color> findCAMColors(float L, float C, float minDist)
	{
		List<Color> colors = new ArrayList<>();

		CAMLch prevColor = new CAMLch(new float[] { L, C, 0 }, 1f, JCH_SPACE);
		CAMLch firstColor = prevColor;

		// sample a ring (0-360 degrees) in tiny steps to identify colors with a color distance of 1
		for (float hue = 0; hue < 360; hue += 0.1) 
		{
			CAMLch color = new CAMLch(new float[] { L, C, hue }, 1f, JCH_SPACE);

			double dist = ColorTools.distance(prevColor, color, VIEW_ENV);
			double distToFirst = ColorTools.distance(firstColor, color, VIEW_ENV);

			if (dist > minDist && distToFirst > minDist) 
			{
				colors.add(new Color(color.getARGB()));
				prevColor = color;
			}
		}

		return colors;
	}
}
