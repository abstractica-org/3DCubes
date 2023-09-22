package org.abstractica.cubes;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class Debug
{
	public static void main(String[] args)
	{
		double cubeSize = 32;
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		FunCube32 funCube = new FunCube32(csg);
		Geometry3D debug = funCube.getDebugCube();
		csg.view(debug);
	}
}
