package org.abstractica.smartcubes.base.plates.instances;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.plates.BasePlates;
import org.abstractica.smartcubes.base.plates.HolePosition;
import org.abstractica.smartcubes.base.Part;

import java.util.Set;

public class Plate3x3_4Holes implements Part
{
	@Override
	public Geometry3D getGeometry(JavaCSG csg, double scale, int angularResolution)
	{
		BasePlates bp = new BasePlates(csg, scale, angularResolution);
		Set<HolePosition> removeHoles = Set.of(
				new HolePosition(1, 0),
				new HolePosition(0, 1),
				new HolePosition(1, 1),
				new HolePosition(2, 1),
				new HolePosition(1, 2)
		);
		return bp.basePlate(3, 3, 0, 0, 0, 0, false, removeHoles);
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		Plate3x3_4Holes plate = new Plate3x3_4Holes();
		System.out.println("Generating " + plate.getName() + " as view0.scad");
		Geometry3D geometry = plate.getGeometry(csg, 1, 128);
		csg.view(geometry);
	}
}
