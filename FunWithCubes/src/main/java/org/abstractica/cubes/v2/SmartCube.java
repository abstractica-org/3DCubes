package org.abstractica.cubes.v2;

import org.abstractica.javacsg.Geometry2D;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class SmartCube
{
	private final JavaCSG csg;
	private final int angularResolution = 128;
	private final double unitSize;

	//Connector tension settings
	private final double barbSize = 0.6;


	//Adjust values
	private final double centerSphereDiameterAdjust = 0.0;
	private final double lockingBallCutoutAdjust = 0.2;
	private final double CutoutAxleDiameterAdjust = 0.2;
	private final double connectorToAxleDist = 0.4;
	private final double connectorEndDiameterSpace = 0.4;
	private final double baseHoleSideAdjust = 0.1;
	private final double baseHoleEndAdjust = 0.1;


	private final double centerSphereDiameter;
	private final double cubeSize;
	private final double cubeDistance;


	//Locking balls
	private final double lockingBallDiameter;
	private final double lockingBallPosition;

	//Axle
	private final double innerDiameter;
	private final double outerDiameter;
	private final double axleLeadin;


	//Connector
	private final double connectorSlitWidth;
	private final double connectorSlitLength;
	private final double connectorConeHeight;
	private final double connectorWidth;
	private final double connectorBaseDiameter;

	//Turn plate
	private final double turnPlateDiameter;



	public SmartCube(JavaCSG csg, double unitSize)
	{
		this.csg = csg;
		this.unitSize = unitSize;
		this.centerSphereDiameter = 2*unitSize;
		this.cubeSize = 4*unitSize;
		this.cubeDistance = unitSize;
		this.lockingBallDiameter = 0.5*unitSize;
		this.lockingBallPosition = unitSize;
		this.connectorConeHeight = 1.25*unitSize;
		this.axleLeadin = 0.1*unitSize;
		this.innerDiameter = unitSize;
		this.outerDiameter = 1.25*unitSize;

		//Connector
		this.connectorSlitWidth = 0.5*unitSize;
		this.connectorSlitLength = unitSize;
		this.connectorWidth = 0.75*unitSize;
		this.connectorBaseDiameter = 1.5*unitSize;

		//Turnplate
		this.turnPlateDiameter = 2*Math.sqrt(2*unitSize*unitSize);
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

	private Geometry3D getSmartCube()
	{
		Geometry3D cube = getBaseCube();
		Geometry3D cutout = getFullCutout();
		return csg.difference3D(cube, cutout);
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

	private Geometry3D getBalls(double diameterAdjust)
	{
		Geometry3D ball = csg.sphere3D(lockingBallDiameter + diameterAdjust, angularResolution, true);
		//ball = csg.translate3DZ(0.5*cubeDistance).transform(ball);
		Geometry3D balls = csg.translate3D(lockingBallPosition, lockingBallPosition, 0).transform(ball);
		balls = csg.union3D(balls, csg.translate3D(lockingBallPosition, -lockingBallPosition, 0).transform(ball));
		balls = csg.union3D(balls, csg.translate3D(-lockingBallPosition, lockingBallPosition, 0).transform(ball));
		balls = csg.union3D(balls, csg.translate3D(-lockingBallPosition, -lockingBallPosition, 0).transform(ball));
		//balls = csg.translate3DZ(-0.5*cubeSize).transform(balls);
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
		Geometry3D balls = getBalls(lockingBallCutoutAdjust);
		balls = csg.translate3DZ(-0.5*(cubeSize - cubeDistance)).transform(balls);
		return csg.union3D(balls, getAxleCutout());
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

	private Geometry3D getConnectorClicker()
	{
		//Axle
		Geometry3D connector = getAxle(outerDiameter, innerDiameter, axleLeadin, 0);
		connector = csg.translate3DZ(0.5*cubeDistance).transform(connector);

		//Sphere head
		Geometry3D sphere = csg.sphere3D(centerSphereDiameter, angularResolution, true);
		sphere = csg.translate3DZ(0.5*cubeSize).transform(sphere);
		Geometry3D sphereCutOff = csg.cylinder3D(innerDiameter+2*barbSize, cubeSize, angularResolution, false);
		sphere = csg.intersection3D(sphere, sphereCutOff);
		connector = csg.union3D(connector, sphere);

		//Hight cutoff
		Geometry3D heightBox = csg.box3D(cubeSize, cubeSize, connectorConeHeight, false);
		connector = csg.intersection3D(connector, heightBox);

		//Cone
		double coneHeight = 0.5*cubeSize- connectorConeHeight -0.5*innerDiameter-connectorToAxleDist;
		Geometry3D cone = csg.cone3D
			(
				innerDiameter+2*barbSize,
				innerDiameter-connectorEndDiameterSpace,
				coneHeight,
				angularResolution,
				false
			);
		cone = csg.translate3DZ(connectorConeHeight).transform(cone);
		connector = csg.union3D(connector, cone);

		//Width cutoff
		Geometry3D widthBox = csg.box3D(cubeSize, connectorWidth, cubeSize, false);
		connector = csg.intersection3D(connector, widthBox);

		//Slit
		Geometry3D slit = csg.box3D(connectorSlitWidth, cubeSize, cubeSize, false);
		slit = csg.translate3DZ(connectorConeHeight + coneHeight - connectorSlitLength).transform(slit);
		connector = csg.difference3D(connector, slit);

		return connector;
	}

	public Geometry3D getBaseConnector()
	{
		Geometry3D connector = getConnectorClicker();
		Geometry3D base = getBaseShape(0,0);
		connector = csg.union3D(connector, base);
		return connector;
	}

	public Geometry3D getDoubleBaseConnector()
	{
		Geometry3D connector = getBaseConnector();
		connector = csg.union3D(connector, csg.mirror3D(0,0,1).transform(connector));
		return connector;
	}

	private Geometry3D getBaseShape(double adjustSide, double adjustEnd)
	{
		double baseHeight = 0.5*cubeDistance;
		double largeDiameter = connectorBaseDiameter+2*adjustEnd;
		double smallDiameter = outerDiameter+2*adjustEnd;
		//Base space
		Geometry3D baseShape = csg.cylinder3D(largeDiameter, 0.25*baseHeight, angularResolution, false);
		Geometry3D cone = csg.cone3D
			(
				largeDiameter,
				smallDiameter,
				0.5*baseHeight,
				angularResolution,
				false
			);
		cone = csg.translate3DZ(0.25*baseHeight).transform(cone);
		baseShape = csg.union3D(baseShape, cone);
		Geometry3D cylinder = csg.cylinder3D(smallDiameter, 0.25*baseHeight, angularResolution, false);
		cylinder = csg.translate3DZ(0.75*baseHeight).transform(cylinder);
		baseShape = csg.union3D(baseShape, cylinder);

		//Width cutoff
		Geometry3D widthBox = csg.box3D(cubeSize, connectorWidth+2*adjustSide, cubeSize, false);
		baseShape = csg.intersection3D(baseShape, widthBox);

		return baseShape;
	}

	private Geometry3D getTurnPlate()
	{
		Geometry3D turnPlate = csg.cylinder3D(turnPlateDiameter, 0.5*cubeDistance, angularResolution, false);
		Geometry3D hole = getBaseShape(baseHoleSideAdjust, baseHoleEndAdjust);
		return csg.difference3D(turnPlate, hole);
	}

	private Geometry3D getLockPlate()
	{
		Geometry3D plate = getBaseLockPlate();

		Geometry3D base = getBaseShape(baseHoleSideAdjust,baseHoleEndAdjust);
		plate = csg.difference3D(plate, base);

		return plate;
	}

	private Geometry3D getBaseLockPlate()
	{
		Geometry3D plate = csg.box3D(cubeSize-cubeDistance, cubeSize-cubeDistance, 0.5*cubeDistance, false);
		Geometry3D balls = getBalls(0);
		balls = csg.translate3DZ(0.5*cubeDistance).transform(balls);
		plate = csg.union3D(plate, balls);
		return plate;
	}

	private Geometry3D getLockPlateWithEdges()
	{
		Geometry2D rect = csg.rectangle2D(cubeSize-0.2, cubeSize-0.2);
		Geometry3D plate = csg.linearExtrude
			(
				0.5*cubeDistance,
				csg.degrees(0),
				(cubeSize-cubeDistance)/cubeSize,
				1,
				false,
				rect
			);

		Geometry3D base = getBaseShape(baseHoleSideAdjust,baseHoleEndAdjust);
		plate = csg.difference3D(plate, base);

		Geometry3D balls = getBalls(0);
		balls = csg.translate3DZ(0.5*cubeDistance).transform(balls);
		plate = csg.union3D(plate, balls);
		return plate;
	}

	public Geometry3D getBBPlate(double bbDiameter, double bbWidth)
	{
		Geometry3D plate = getBaseLockPlate();
		Geometry3D bb = csg.cylinder3D(bbDiameter, 0.5 * bbWidth, angularResolution, false);
		double coneHeight = 0.5 * (cubeDistance-bbWidth);
		double topDiameter = bbDiameter - 2 * coneHeight;
		Geometry3D cone = csg.cone3D(bbDiameter, topDiameter, coneHeight, angularResolution, false);
		cone = csg.translate3DZ(0.5*bbWidth).transform(cone);
		bb = csg.union3D(bb, cone);
		return csg.difference3D(plate, bb);
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		SmartCube smartCube = new SmartCube(csg, 8);
		Geometry3D res = smartCube.getBBPlate(13.2, 5);
		//res = csg.rotate3DX(csg.degrees(90)).transform(res);
		csg.view(res,1);
	}
}
