package org.abstractica.sourced.boltsandnuts;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;

public class BoltAndHexNutCutout
{
	private final double totalLength;
	private final double boltDiameter;
	private final double boltHeadDiameter;
	private final double boltHeadHeight;
	private final double nutWidth;
	private final double nutHeight;

	public BoltAndHexNutCutout(double totalLength, double boltDiameter, double boltHeadDiameter, double boltHeadHeight, double nutWidth, double nutHeight)
	{
		this.totalLength = totalLength;
		this.boltDiameter = boltDiameter;
		this.boltHeadDiameter = boltHeadDiameter;
		this.boltHeadHeight = boltHeadHeight;
		this.nutWidth = nutWidth;
		this.nutHeight = nutHeight;
	}

	public Geometry3D getCutout(JavaCSG csg, int angularResolution)
	{
		Geometry3D bolt = csg.cylinder3D(boltDiameter, totalLength, angularResolution, false);
		Geometry3D nut = csg.cylinder3D(HexMath.hexagon_Diameter(nutWidth), nutHeight, 6, false);
		Geometry3D head = csg.cylinder3D(boltHeadDiameter, boltHeadHeight, angularResolution, false);
		head = csg.translate3DZ(totalLength - boltHeadHeight).transform(head);
		return csg.cache(csg.union3D(bolt, nut, head));
	}


}
