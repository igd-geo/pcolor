package de.fhg.igd.pcolor;

import de.fhg.igd.pcolor.colorspace.CS_CIELab;

/**
 * This class contains PColors in the CIE L*a*b* colorspace. It is not currently
 * used; JCh is preferred, since it corrects many of CIE Lab's issues with bent
 * lines of consistent hue and uneven chroma spacing.
 */
public class CIELab extends PColor {
	/**
	 * Lightness
	 */
	public static final int L = 0;
	/**
	 * Red-Green
	 */
	public static final int a = 1;
	/**
	 * Yellow-Blue
	 */
	public static final int b = 2;

	/**
	 * 
	 * @param color color
	 */
	public CIELab(PColor color) {
		super(new CS_CIELab(), color);
	}

	/**
	 * with 1 as alpha value
	 * @param L Lightness
	 * @param a Red-Green
	 * @param b Yellow-Blue
	 */
	public CIELab(float L, float a, float b) {
		this(L, a, b, 1f);
	}

	/**
	 * 
	 * @param L Lightness
	 * @param a Red-Green
	 * @param b Yellow-Blue
	 * @param alpha alpha value
	 */
	public CIELab(float L, float a, float b, float alpha) {
		super(new CS_CIELab(), new float[] {L, a, b}, alpha);
	}

	@Override
	public CIELab convertFrom(PColor color) {
		return new CIELab(color);
	}

	@Override
	public CIELab clone() {
		return new CIELab(this);
	}
}