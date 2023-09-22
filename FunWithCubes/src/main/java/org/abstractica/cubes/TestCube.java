package org.abstractica.cubes;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class TestCube
{


	public static void main(String[] args)
	{
		double cubeSize = 32;
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		FunCube32 funCube = new FunCube32(csg);
		Geometry3D cube = funCube.cube();
		Geometry3D box = csg.box3D(cubeSize, cubeSize, 0.3*cubeSize, false);
		box = csg.translate3DZ(-0.5*cubeSize).transform(box);
		cube = csg.intersection3D(cube, box);
		csg.view(cube);
	}
}
