package org.abstractica.tools;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.bricks.BasicBricks;
import org.abstractica.smartcubes.base.bricks.CrossBricks;
import org.abstractica.smartcubes.base.plates.PlatesOld;

public class PoopBox
{
	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		/*Geometry3D poopBox = poopBox(csg, 200, 200, 120, 1);*/
		// csg.view(poopBox);
		poopBox();
	}

	/*private static Geometry3D poopBox(JavaCSG csg, double x, double y, double z, double wallWidth)
	{
		Geometry3D outer = csg.box3D(x, y, z, false);
		Geometry3D inner = csg.box3D(x - 2*wallWidth, y - 2*wallWidth, z, false);
		inner = csg.translate3DZ(wallWidth).transform(inner);
		return csg.difference3D(outer, inner);
	}*/

	// Script assumes the following parts are available
	// 17x 1x1 tile
	// 11x double clicker
	// 17x half clicker
	private static void poopBox()
	{
		int partnum = 0;

		JavaCSG csg = JavaCSGFactory.createNoCaching();
		BasicBricks bb = new BasicBricks(csg, 1, 128);
		CrossBricks cb = new CrossBricks(csg, 1, 128);
		PlatesOld p = new PlatesOld(csg, 1, 128);

		Geometry3D brick8 = bb.basicBrick(8, 1, 1);
		csg.view(brick8, partnum++);

		for (int i = 0; i < 2; i++)
		{
			Geometry3D brick6 = bb.basicBrick(6, 1, 1);

			Geometry3D brick12 = bb.basicBrick(12, 1, 1);
			Geometry3D brick9 = bb.basicBrick(9, 1, 1);
			Geometry3D plate11 = p.scalableTile(8, 11, false, 1, 1);
			Geometry3D connector = p.longPlate(3, false);

			csg.view(brick6, partnum++);
			csg.view(brick12, partnum++);
			csg.view(brick9, partnum++);
			csg.view(plate11, partnum++);
			csg.view(connector, partnum++);
		}

		Geometry3D plate14 = p.scalableTile(8, 14, false, 0, 1);
		csg.view(plate14, partnum++);

		Geometry3D plate8 = p.scalableTile(8, 8, false, 0, 0);
		csg.view(plate8, partnum++);

		Geometry3D plate8xH = p.scalableTile(8, 8, false, 0, 2);
		Geometry3D angle = p.angleTile(8, 1, 8, 1);
		Geometry3D front = csg.union3D(plate8xH, angle);
		csg.view(front, partnum++);

		Geometry3D crossBrick5 = cb.crossBrick(5, 1, 1);
		csg.view(crossBrick5, partnum++);

		Geometry3D cube = bb.basicBrick(1,1,1);
		csg.view(cube, partnum);
	}
}
