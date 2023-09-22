package org.abstractica.cubes;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class TestConnector
{


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		FunCube32 funCube = new FunCube32(csg);
		Geometry3D connector = funCube.connector(true);
		connector = csg.rotate3DX(csg.degrees(90)).transform(connector);
		csg.view(connector);
	}
}
