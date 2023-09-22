package org.abstractica.cubes;

import org.abstractica.javacsg.Geometry2D;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;

public class FunCube32
{
	private final JavaCSG csg;
	private final double wedge = 0.8;
	private final double connectorDiameter = 8;
	private final double connectorWidth = 6;
	private final double cubeSize = 32;
	private final double sphereDiameter = 20;
	private final double barbSize = 1.2;
	private final double slitWidth = 3;
	private final double slitLength = 8;

	public FunCube32(JavaCSG csg)
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

	private Geometry3D starCylinder(double diameter, double wedge, double height, boolean centerZ)
	{
		double r = 0.5 * diameter + wedge;
		double s = Math.sqrt(2*r*r);
		Geometry2D rect = csg.rectangle2D(s, s);
		rect = csg.rotate2D(csg.degrees(45)).transform(rect);
		Geometry2D circle = csg.circle2D(diameter, 128);
		Geometry2D star = csg.union2D(rect, circle);
		return csg.linearExtrude(height, centerZ, star);
	}

	private Geometry3D cutout()
	{
		Geometry3D sphere = csg.sphere3D(sphereDiameter+0.2, 64, true);
		Geometry3D axis = starCylinder(connectorDiameter+0.2, wedge, cubeSize, true);
		Geometry3D bottom = top(1.25*connectorDiameter+0.2, connectorDiameter+0.2, 3);
		bottom = csg.translate3DZ(-0.5*cubeSize).transform(bottom);
		axis = csg.union3D(axis, bottom, csg.mirror3D(0,0,1).transform(bottom));
		Geometry3D cutout = csg.union3D(axis, sphere);
		axis = csg.rotate3DX(csg.degrees(90)).transform(axis);
		cutout = csg.union3D(cutout, axis);
		axis = csg.rotate3DZ(csg.degrees(90)).transform(axis);
		cutout = csg.union3D(cutout, axis);
		return cutout;
	}

	public Geometry3D getDebugCube()
	{
		Geometry3D cube = cube();
		cube.debugMark();
		Geometry3D connector = connector(true);
		connector = csg.rotate3DX(csg.degrees(90)).transform(connector);
		connector = csg.translate3DZ(-0.5*cubeSize).transform(connector);
		connector = csg.union3D(connector, csg.translate3DZ(cubeSize).transform(connector));
		connector = csg.union3D(connector, csg.rotate3DY(csg.degrees(90)).transform(connector));
		return csg.union3D(cube, connector);
	}

	public Geometry3D connector(boolean hasWedge)
	{
		double lift = 5;
		Geometry3D cylinder = hasWedge ?
			starCylinder(connectorDiameter, wedge, 0.5*cubeSize, false)
			: csg.cylinder3D(connectorDiameter, 0.5*cubeSize, 128, false);
		Geometry3D bottom = top(connectorDiameter+2, connectorDiameter, 3);
		bottom = csg.mirror3D(0,0,1).transform(bottom);
		bottom = csg.translate3DZ(0.5*cubeSize).transform(bottom);
		Geometry3D sphere = csg.sphere3D(sphereDiameter, 64, true);
		Geometry3D connector = csg.union3D(cylinder, sphere);
		double diameter1 = connectorDiameter + 2*barbSize;
		double diameter2 = connectorDiameter*0.75;
		double l = 0.5*(diameter1 - diameter2);
		Geometry3D top = top(diameter1, diameter2, 0.5*cubeSize);
		top = csg.translate3DZ(-0.5*cubeSize).transform(top);
		top = csg.mirror3D(0,0,1).transform(top);
		top = csg.translate3DZ(lift).transform(top);
		connector = csg.intersection3D(connector, top);
		connector = csg.union3D(connector, bottom);
		connector = csg.translate3DZ(-1).transform(connector);
		Geometry3D spacer = csg.cylinder3D(connectorDiameter+8, 1, 128, false);
		spacer = csg.translate3DZ(0.5*cubeSize-1).transform(spacer);
		connector = csg.union3D(connector, spacer);
		csg.view(connector, 1);
		Geometry3D widthBox = csg.box3D(0.5*cubeSize+2, connectorWidth, 0.5*cubeSize, false);
		connector = csg.intersection3D(connector, widthBox);
		Geometry3D slit = csg.box3D(slitWidth, 0.5*cubeSize, slitLength, false);
		slit = csg.translate3DZ(lift-1).transform(slit);
		connector = csg.difference3D(connector, slit);
		connector = csg.translate3DZ(-0.5*cubeSize).transform(connector);
		connector = csg.union3D(connector, csg.mirror3D(0,0,1).transform(connector));
		connector = csg.rotate3DX(csg.degrees(90)).transform(connector);
		return connector;
	}

	public Geometry3D cube()
	{
		Geometry3D cube = csg.box3D(cubeSize, cubeSize, cubeSize, true);
		Geometry3D cutout = cutout();
		return csg.difference3D(cube, cutout);
	}
}
