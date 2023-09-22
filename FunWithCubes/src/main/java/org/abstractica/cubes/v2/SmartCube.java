package org.abstractica.cubes.v2;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class SmartCube
{
	private final JavaCSG csg;
	private final int angularResolution = 128;

	private final double centerSphereDiameter = 16;
	private final double centerSphereDiameterAdjust = 0.2;
	private final double cubeSize = 32;
	private final double cubeDistance = 5;


	//Locking balls
	private final double lockingBallDiameter = 4;
	private final double lockingBallPosition = 9;
	private final double lockingBallCutoutAdjust = 0.2;


	//Connector
	private final double barbSize = 1;
	private final double outerDiameter = 8;
	private final double innerDiameter = 7;
	private final double CutoutAxleDiameterAdjust = 0.2;
	private final double axleLeadin = 1;
	private final double connectorEaseInHeight = 9.4;
	private final double connectorToAxleDist = 1.0;
	private final double connectorEndDiameterSpace = 0.4;
	private final double connectorWidth = 6;
	private final double connectorBaseDiameter = 16;
	private final double connectorSlitWidth = 3;
	private final double connectorSlitLength = 8;


	public SmartCube(JavaCSG csg)
	{
		this.csg = csg;
	}

	public double getCubeSize()
	{
		return cubeSize;
	}

	public Geometry3D getBaseCube()
	{
		double size = cubeSize-cubeDistance;
		Geometry3D cube = csg.box3D(size, size, size, true);
		Geometry3D sphere = csg.sphere3D(centerSphereDiameter + centerSphereDiameterAdjust, angularResolution, true);
		return csg.difference3D(cube, sphere);
	}

	private Geometry3D getLockingPlate(double size, double thickness, double ballDiameter, double ballPosition)
	{
		Geometry3D plate = csg.box3D(size, size, thickness, false);
		Geometry3D ball = csg.sphere3D(ballDiameter, angularResolution, true);
		ball = csg.translate3DZ(thickness).transform(ball);
		Geometry3D ballCutter = csg.box3D(ballDiameter+2, ballDiameter+2, ballDiameter+thickness, false);
		ball = csg.intersection3D(ball, ballCutter);
		plate = csg.union3D(plate, csg.translate3D(ballPosition, ballPosition, 0).transform(ball));
		plate = csg.union3D(plate, csg.translate3D(-ballPosition, ballPosition, 0).transform(ball));
		plate = csg.union3D(plate, csg.translate3D(ballPosition, -ballPosition, 0).transform(ball));
		plate = csg.union3D(plate, csg.translate3D(-ballPosition, -ballPosition, 0).transform(ball));
		return plate;
	}

	private Geometry3D getBallCutout()
	{
		Geometry3D ball = csg.sphere3D(lockingBallDiameter + lockingBallCutoutAdjust, angularResolution, true);
		ball = csg.translate3DZ(0.5*cubeDistance).transform(ball);
		Geometry3D balls = csg.translate3D(lockingBallPosition, lockingBallPosition, 0).transform(ball);
		balls = csg.union3D(balls, csg.translate3D(lockingBallPosition, -lockingBallPosition, 0).transform(ball));
		balls = csg.union3D(balls, csg.translate3D(-lockingBallPosition, lockingBallPosition, 0).transform(ball));
		balls = csg.union3D(balls, csg.translate3D(-lockingBallPosition, -lockingBallPosition, 0).transform(ball));
		balls = csg.translate3DZ(-0.5*cubeSize).transform(balls);
		return balls;
	}

	private Geometry3D getAxleCutout()
	{
		Geometry3D axle = getAxle(outerDiameter, innerDiameter, axleLeadin + 0.5*cubeDistance, CutoutAxleDiameterAdjust);
		axle = csg.translate3DZ(-0.5*cubeSize).transform(axle);
		return axle;
	}

	private Geometry3D getSideCutout()
	{
		return csg.union3D(getBallCutout(), getAxleCutout());
	}

	private Geometry3D getFullCutout()
	{
		Geometry3D res = getSideCutout();
		res = csg.union3D(res, csg.mirror3D(0,0,1).transform(res));
		return csg.union3D(res, csg.rotate3DY(csg.degrees(90)).transform(res), csg.rotate3DX(csg.degrees(90)).transform(res));
	}

	private Geometry3D getAxle(double outsideDiameter, double insideDiameter, double leadIn, double adjust)
	{
		double totalLength = 0.5*cubeSize;
		Geometry3D bottom = csg.cylinder3D(outsideDiameter+adjust, leadIn, angularResolution, false);
		double coneHeight = 0.5 * (outsideDiameter - insideDiameter);
		Geometry3D middle = csg.cone3D(outsideDiameter+adjust, insideDiameter+adjust, coneHeight, angularResolution, false);
		middle = csg.translate3DZ(leadIn).transform(middle);
		Geometry3D top = csg.cylinder3D(insideDiameter+adjust, totalLength - leadIn - coneHeight, angularResolution, false);
		top = csg.translate3DZ(leadIn + coneHeight).transform(top);
		return csg.union3D(bottom, middle, top);
	}

	private Geometry3D getConnector()
	{
		Geometry3D connector = getAxle(outerDiameter, innerDiameter, axleLeadin, 0);
		connector = csg.translate3DZ(0.5*cubeDistance).transform(connector);
		Geometry3D sphere = csg.sphere3D(centerSphereDiameter, angularResolution, true);
		sphere = csg.translate3DZ(0.5*cubeSize).transform(sphere);
		connector = csg.union3D(connector, sphere);
		Geometry3D cylinder = csg.cylinder3D(innerDiameter+2*barbSize, connectorEaseInHeight, angularResolution, false);
		connector = csg.intersection3D(connector, cylinder);
		double coneHeight = 0.5*cubeSize-connectorEaseInHeight-0.5*outerDiameter-connectorToAxleDist;
		Geometry3D cone = csg.cone3D
			(
				innerDiameter+2*barbSize,
				innerDiameter-connectorEndDiameterSpace,
				coneHeight,
				angularResolution,
				false
			);
		cone = csg.translate3DZ(connectorEaseInHeight).transform(cone);
		connector = csg.union3D(connector, cone);
		Geometry3D baseSpace = csg.cylinder3D(connectorBaseDiameter, 0.5*cubeDistance, angularResolution, false);
		connector = csg.union3D(connector, baseSpace);
		Geometry3D widthBox = csg.box3D(cubeSize, connectorWidth, cubeSize, false);
		connector = csg.intersection3D(connector, widthBox);
		Geometry3D slit = csg.box3D(connectorSlitWidth, cubeSize, cubeSize, false);
		slit = csg.translate3DZ(connectorEaseInHeight + coneHeight - connectorSlitLength).transform(slit);
		connector = csg.difference3D(connector, slit);
		return connector;
	}

	/*
	private Geometry3D getSideCutout()
	{
		Geometry3D plate = getLockingPlate
			(
				cubeSize,
				0.5*cubeDistance-cubeSizeAdjust,
				lockingBallDiameter+lockingBallCutoutAdjust,
				lockingBallPosition
			);
		Geometry3D axle = getAxle
			(
				outerDiameter,
				innerDiameter,
				axleLeadin + 0.5*cubeDistance,
				CutoutAxleDiameterAdjust
			);
		return csg.union3D(plate, axle);
	}
	*/


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		SmartCube smartCube = new SmartCube(csg);
		csg.view(smartCube.getConnector(),1);
	}
}
