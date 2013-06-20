package de.fhg.igd.pcolor.util;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;

/**
 * This class is a collection of (currently few) useful color predicates.
 * @author Simon Thum
 */
public class ColorPredicates {

	private ColorPredicates() { throw new UnsupportedOperationException(); }

	/**
	 * This predicate decides if a color is contained in the sRGB gamut.
	 */
	public static Predicate<PColor> is_sRGB = new Predicate<PColor>() {
		/**
		 * @param col
		 * @return true if col converts to sRGB without loss
		 */
		@Override
		public boolean apply(PColor col) {
			sRGB rgb = (sRGB) PColor.convert(col, CS_sRGB.instance);
			return rgb.isInRange(0, 0);
		}
	};
	
}
