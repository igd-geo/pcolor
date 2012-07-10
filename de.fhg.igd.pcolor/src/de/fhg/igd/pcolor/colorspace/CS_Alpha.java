package de.fhg.igd.pcolor.colorspace;

/**
 * The CS_Alpha colorspace is essentially a null colorspace containing no color
 * components. PColor and PColorCanvas objects making use of this colorspace
 * will instead contain only one channel, the alpha channel, which may be used
 * to represent single-channel information.
 */
public class CS_Alpha extends PColorSpace {

	private static final long serialVersionUID = 4771429392405445137L;

	/**
	 * default constructor
	 * creates alpha color space with color space type 0 and no components
	 */
	public CS_Alpha() {
		super(0, 0);
	}

	@Override
	public float[] fromCIEXYZ(float[] arg0) {
		return new float[0];
	}

	@Override
	public float[] fromRGB(float[] arg0) {
		return new float[0];
	}

	@Override
	public float[] toCIEXYZ(float[] arg0) {
		return new float[3];
	}

	@Override
	public float[] toRGB(float[] arg0) {
		return new float[3];
	}
}
