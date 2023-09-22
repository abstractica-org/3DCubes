package org.abstractica.cubes;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class TestCube32
{


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		FunCube32 funCube = new FunCube32(csg);
		Geometry3D res = funCube.connector(false);
		csg.view(res);
	}
}
