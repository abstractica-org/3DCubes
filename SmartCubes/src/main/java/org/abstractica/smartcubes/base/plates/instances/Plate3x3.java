package org.abstractica.smartcubes.base.plates.instances;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.plates.BasePlates;
import org.abstractica.smartcubes.base.Part;

public class Plate3x3 implements Part
{
	@Override
	public Geometry3D getGeometry(JavaCSG csg, double scale, int angularResolution)
	{
		BasePlates bp = new BasePlates(csg, scale, angularResolution);
		return bp.basePlate(3, 3, 0, 0, 0, 0, false);
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		Plate3x3 plate = new Plate3x3();
		System.out.println("Generating " + plate.getName() + " as view0.scad");
		Geometry3D geometry = plate.getGeometry(csg, 1, 128);
		csg.view(geometry);
	}
}
