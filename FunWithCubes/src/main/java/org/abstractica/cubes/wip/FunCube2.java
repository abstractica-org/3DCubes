package org.abstractica.cubes.wip;

import org.abstractica.javacsg.Geometry2D;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;

public class FunCube2
{
	private final JavaCSG csg;
	private final double wedge = 1;
	private final double connectorDiameter = 8;
	private final double connectorWidth = 6;
	private final double sleeveDiameter = 12;
	private final double cubeSize = 30;
	private final double sphereDiameter = 16;
	private final double barbSize = 0.6;
	private final double slitWidth = 3;
	private final double slitLength = 10;

	public FunCube2(JavaCSG csg)
	{
		this.csg = csg;
	}

	private Geometry3D top(double dBottom, double dTop, double height)
	{
		double dDelta = dBottom - dTop;
		Geometry3D top = csg.cone3D(dBottom, dTop, dDelta, 128, false);
		top = csg.translate3DZ(height-dDelta).transform(top);
		Geometry3D bottom = csg.cylinder3D(dBottom, height-dDelta, 128, false);
		return csg.union3D(top, bottom);
	}

	private Geometry3D star(double dSmall, double dLarge, double height)
	{
		double r = 0.5 * dLarge;
		double s = Math.sqrt(2*r*r);
		Geometry2D rect = csg.rectangle2D(s, s);
		Geometry2D circle = csg.circle2D(dSmall, 128);
		Geometry2D star = csg.union2D(rect, circle);
		return csg.linearExtrude(height, false, star);
	}

	private Geometry3D starTop(double height)
	{
		Geometry3D star = star(sleeveDiameter, sleeveDiameter +2*wedge, height);
		Geometry3D top = top(sleeveDiameter +2*wedge, connectorDiameter, height);
		return csg.intersection3D(star, top);
	}

	private Geometry3D axis(double wedge, double dSmall, double dLarge, double length, double middleLength)
	{
		double h = 0.5* (length - middleLength);
		Geometry3D bottom = starTop(h);
		bottom = csg.translate3DZ(-0.5*length).transform(bottom);
		Geometry3D top = csg.mirror3D(0,0,1).transform(bottom);
		Geometry3D middle = csg.cylinder3D(dSmall, middleLength, 128, true);
		return csg.union3D(bottom, middle, top);
	}

	private Geometry3D cutout()
	{
		Geometry3D sphere = csg.sphere3D(sphereDiameter, 64, true);
		Geometry3D axis = axis(wedge, connectorDiameter, sleeveDiameter, cubeSize, sphereDiameter);
		Geometry3D cutout = csg.union3D(axis, sphere);
		axis = csg.rotate3DX(csg.degrees(90)).transform(axis);
		cutout = csg.union3D(cutout, axis);
		axis = csg.rotate3DZ(csg.degrees(90)).transform(axis);
		cutout = csg.union3D(cutout, axis);
		return cutout;
	}

	public Geometry3D connector()
	{
		double lift = 3;
		Geometry3D cylinder = csg.cylinder3D(connectorDiameter, 0.5*cubeSize, 128, false);
		Geometry3D sphere = csg.sphere3D(sphereDiameter, 64, true);
		Geometry3D connector = csg.union3D(cylinder, sphere);
		double diameter1 = connectorDiameter + 2*barbSize;
		double diameter2 = connectorDiameter - 2;
		double l = 0.5*(diameter1 - diameter2);
		Geometry3D top = top(diameter1, diameter2, 0.5*cubeSize);
		top = csg.translate3DZ(-0.5*cubeSize).transform(top);
		top = csg.mirror3D(0,0,1).transform(top);
		top = csg.translate3DZ(lift).transform(top);
		connector = csg.intersection3D(connector, top);
		Geometry3D widthBox = csg.box3D(0.5*cubeSize, connectorWidth, 0.5*cubeSize, false);
		connector = csg.intersection3D(connector, widthBox);
		Geometry3D slit = csg.box3D(slitWidth, 0.5*cubeSize, slitLength, false);
		slit = csg.translate3DZ(lift).transform(slit);
		connector = csg.difference3D(connector, slit);
		connector = csg.translate3DZ(-0.5*cubeSize).transform(connector);
		connector = csg.union3D(connector, csg.mirror3D(0,0,1).transform(connector));
		return connector;
	}

	public Geometry3D cube()
	{
		Geometry3D cube = csg.box3D(cubeSize, cubeSize, cubeSize, true);
		Geometry3D cutout = cutout();
		return csg.difference3D(cube, cutout);
	}
}
