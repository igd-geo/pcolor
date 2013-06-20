package de.fhg.igd.pcolor.util;

/**
 * This class contains shared math routines that do not directly
 * apply to color computation.
 * @author Simon Thum
 */
public class MathTools {
	
	// hide ctor
	private MathTools(){}

	/**
	 * The so-called 'euclidean' modulo, a modulo which won't yield
	 * negative results
	 * @param x the number to divide
	 * @param mod the divisor
	 * @return the euclidean modulo
	 */
	public static int modulo(int x, int mod) {
		if (x >= 0) {
			return x % mod;
		}
		int n = 1 + (-x / mod);
		x += n * mod;
		return x % mod;
	}
	
	/**
	 * Enforces a value range using saturation (clipping)
	 * @param in the input value
	 * @param lower the lower limit
	 * @param upper the upper limit
	 * @return the saturated value
	 */
	public static int saturate(int in, int lower, int upper){
		if (in >= lower && in <= upper) {
			return in;
		}
		if (in < lower) {
			return lower;
		}
		return upper;
	}
	
	/**
	 * Enforces a value range using saturation (clipping)
	 * @param in the input value
	 * @param lower the lower limit
	 * @param upper the upper limit
	 * @return the saturated value
	 */
	public static float saturate(float in, float lower, float upper){
		if (in >= lower && in <= upper) {
			return in;
		}
		if (in < lower) {
			return lower;
		}
		return upper;
	}
	
	/**
	 * Convert a double to a float array.
	 * @param v a double array
	 * @return a float array
	 */
	public static float[] doubleToFloatArray(double[] v) {
		float[] vr = new float[v.length];
		for (int i = 0; i < v.length; i++) {
			vr[i] = (float) v[i];
		}
		return vr;
	}
	
	/**
	 * Convert a float to a double array.
	 * @param v a double array
	 * @return a float array
	 */
	public static double[] floatToDoubleArray(float[] v) {
		double[] vr = new double[v.length];
		for (int i = 0; i < v.length; i++) {
			vr[i] = (double) v[i];
		}
		return vr;
	}
	
	
	public static boolean floatArrayEquals(float[] a, float[] a2, float eps) {
        if (a==a2)
            return true;
        if (a==null || a2==null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i=0; i<length; i++)
            if (Math.abs(a[i] - a2[i]) > eps)
                return false;

        return true;
    }

	/**
	 * Calculates an angle, in degrees, between 0 and 360 given its sine and
	 * cosine values.
	 */
	public static double calculateAtan(double cos, double sin) {
		double result = Math.toDegrees(Math.atan2(sin , cos)); 
		if(result < 0) 
			return 360 + result;
		else
			return result; 
	}

}
