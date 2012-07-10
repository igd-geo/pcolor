package de.fhg.igd.pcolor.colorspace;

public abstract class ColorSpaceType {

	/**
	 * coordinate types
	 * @author Thu Huong
	 *
	 */
	public enum CoordinateType {
		CARTESIAN,
		POLAR
	}

	/**
	 * color space type's coordinate type
	 */
	private CoordinateType type;

	/**
	 * creates color space type with given coordinate type
	 * @param type coordinate type
	 */
	protected ColorSpaceType(CoordinateType type) {
		this.type = type;
	}

	/**
	 * 
	 * @return coordinate type
	 */
	public final CoordinateType getType() {
		return type;
	}
}
