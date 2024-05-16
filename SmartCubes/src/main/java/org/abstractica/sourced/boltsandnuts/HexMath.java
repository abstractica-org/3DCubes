package org.abstractica.sourced.boltsandnuts;

public class HexMath
{
	private static final double SQRT_3 = Math.sqrt(3.0);

	public static double hexagon_Diameter(double width)
	{
		return (2 * width) / SQRT_3;
	}

	public static double hexagon_Width(double diameter)
	{
		return diameter * SQRT_3 * 0.5;
	}
}