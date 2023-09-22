package org.abstractica.cubes;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class TestCube16
{


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		FunCube16 funCube = new FunCube16(csg);
		//Geometry3D res = funCube.connector(false);
		Geometry3D res = funCube.cube();
		csg.view(res);
	}
}
