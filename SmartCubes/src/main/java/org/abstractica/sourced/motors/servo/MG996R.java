package org.abstractica.sourced.motors.servo;


import org.abstractica.javacsg.*;

import java.util.ArrayList;
import java.util.List;

public class MG996R
{
	private final JavaCSG csg;
	private final int angularResolution;

	public MG996R(JavaCSG csg, int angularResolution)
	{
		this.csg = csg;
		this.angularResolution = angularResolution;
	}

	public Geometry3D motorCutout(double axleDiameter, double axleLength)
	{
		Geometry3D block = csg.box3D(40, 20, 37, false);
		block = csg.translate3DX(-10).transform(block);
		Geometry3D block2 = csg.box3D(27, 18, 40, false);
		block2 = csg.translate3DX(-0.5*27).transform(block2);
		Geometry3D block3 = csg.box3D(55, 20, 2.6, false);
		block3 = csg.translate3D(-10, 0, 28).transform(block3);

		Geometry3D cyl = csg.cylinder3D(20, 40, angularResolution, false);
		Geometry3D axleCutout = csg.cylinder3D(axleDiameter, axleLength+1, angularResolution, false);
		axleCutout = csg.translate3DZ(40-1).transform(axleCutout);
		Geometry3D wedge1 = wedge();
		wedge1 = csg.translate3D(10, 0, 30.6).transform(wedge1);
		Geometry3D wedge2 = wedge();
		wedge2 = csg.rotate3DZ(csg.degrees(180)).transform(wedge2);
		wedge2 = csg.translate3D(-30, 0, 30.6).transform(wedge2);
		Geometry3D res = csg.union3D(block, block2, block3, wedge1, wedge2, cyl, axleCutout);
		res = csg.translate3DZ(-40).transform(res);
		return res;
	}

	private Geometry3D wedge()
	{
		List<Vector2D> vertices = new ArrayList<>();
		vertices.add(csg.vector2D(0, 0));
		vertices.add(csg.vector2D(7.5, 0));
		vertices.add(csg.vector2D(0, 1.6));
		Geometry2D profile = csg.polygon2D(vertices);
		Geometry3D res = csg.linearExtrude(1.4, true, profile);
		res = csg.rotate3DX(csg.degrees(90)).transform(res);
		return res;
	}

	private Geometry3D testHole()
	{
		Geometry3D block = csg.box3D(10, 10, 10, false);
		Geometry3D cyl = csg.cylinder3D(6, 10, 64, false);
		cyl = csg.translate3DZ(2).transform(cyl);
		return csg.difference3D(block, cyl);
	}


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		MG996R motor = new MG996R(csg, 128);
		Geometry3D cutout = motor.motorCutout(12, 20);
		cutout = csg.rotate3DX(csg.degrees(-90)).transform(cutout);
		cutout = csg.translate3DY(10).transform(cutout);
		Geometry3D testBlock = csg.box3D(64, 24, 12, false);
		testBlock = csg.translate3DX(-10).transform(testBlock);
		Geometry3D test = csg.difference3D(testBlock, cutout);
		test = csg.rotate3DY(csg.degrees(180)).transform(test);
		csg.view(test);
	}
}
