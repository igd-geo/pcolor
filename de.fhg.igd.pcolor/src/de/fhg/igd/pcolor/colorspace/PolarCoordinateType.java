package de.fhg.igd.pcolor.colorspace;

public class PolarCoordinateType extends ColorSpaceType {

	private int radiusIndex, angleIndex;

	/**
	 * creates a polar coordinate
	 * @param radiusIndex radius index
	 * @param angleIndex angle index
	 */
	public PolarCoordinateType(int radiusIndex, int angleIndex) {
		super(CoordinateType.POLAR);

		this.radiusIndex = radiusIndex;
		this.angleIndex = angleIndex;
	}

	/**
	 * 
	 * @return polar radius index
	 */
	public int getPolarRadiusIndex() {
		return radiusIndex;
	}

	/**
	 * 
	 * @return polar angle index
	 */
	public int getPolarAngleIndex() {
		return angleIndex;
	}
}
