package de.fhg.igd.pcolor.colorspace;

public class Surrounding {

	/**
	 * factor determining degree of adaptation
	 */
	private final double F;
	/**
	 * impact of surrounding
	 */
	private final double c;
	/**
	 * chromatic induction factor
	 */
	private final double N_c;

	private Surrounding(double F, double c, double Nc) {
		this.F = F;
		this.c = c;
		this.N_c = Nc;
	}

	public final static Surrounding averageSurrounding = new Surrounding(1.0, 0.69, 1.0);

	public final static Surrounding dimSurrounding = new Surrounding(0.9, 0.59, 0.95);

	public final static Surrounding darkSurrounding = new Surrounding(0.8, 0.525, 0.8);

	/**
	 * 
	 * @return factor determining degree of adaptation
	 */
	public double getF() {
		return F;
	}

	/**
	 * 
	 * @return impact of surrounding
	 */
	public double getC() {
		return c;
	}

	/**
	 * 
	 * @return chromatic induction factor
	 */
	public double getN_c() {
		return N_c;
	}

	
}
