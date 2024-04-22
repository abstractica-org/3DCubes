package org.abstractica.tools;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class PoopBox
{
	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		Geometry3D poopBox = poopBox(csg, 200, 200, 120, 1);
		csg.view(poopBox);
	}

	private static Geometry3D poopBox(JavaCSG csg, double x, double y, double z, double wallWidth)
	{
		Geometry3D outer = csg.box3D(x, y, z, false);
		Geometry3D inner = csg.box3D(x - 2*wallWidth, y - 2*wallWidth, z, false);
		inner = csg.translate3DZ(wallWidth).transform(inner);
		return csg.difference3D(outer, inner);
	}

}
