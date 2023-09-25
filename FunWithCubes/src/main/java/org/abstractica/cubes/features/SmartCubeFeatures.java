package org.abstractica.cubes.features;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class SmartCubeFeatures
{
	private final JavaCSG csg;
	private final int angularResolution;
	private final double unit;

	//Connector tension settings
	private final double barbSize = 0.8; //1.0 is very strong, 0.6 is weak


	//Adjust values

	private final double centerSphereDiameterAdjust = 0.2;
	private final double lockingBallCutoutAdjust = 0.2;
	private final double cutoutAxleDiameterAdjust = 0.2;
	private final double connectorToAxleDist = 0.0;
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



	public SmartCubeFeatures(JavaCSG csg, double unit, int angularResolution)
	{
		this.csg = csg;
		this.unit = unit;
		this.angularResolution = angularResolution;
		this.centerSphereDiameter = 2*unit;
		this.cubeSize = 4*unit;
		this.cubeDistance = unit;
		this.lockingBallDiameter = 0.5*unit;
		this.lockingBallPosition = unit;
		this.connectorConeHeight = 1.25*unit+0.6;
		this.axleLeadin = 0.1*unit;
		this.innerDiameter = unit+1; //Test bigger holes
		this.outerDiameter = 1.25*unit+1; //Test bigger holes

		//Connector
		this.connectorSlitWidth = 0.4*unit;
		this.connectorSlitLength = unit;
		this.connectorWidth = 0.75*unit; // Must be the same as axle width
		this.connectorBaseDiameter = 1.5*unit+1; //Test bigger holes

		//Turnplate
		this.turnPlateDiameter = 2*Math.sqrt(2*unit*unit);
	}

	public double getUnit()
	{
		return unit;
	}

	public Geometry3D getCenterSphereCutout()
	{
		return csg.sphere3D(centerSphereDiameter + centerSphereDiameterAdjust, angularResolution, true);
	}

	private Geometry3D getBallsShape(double diameterAdjust)
	{
		Geometry3D ball = csg.sphere3D(lockingBallDiameter + diameterAdjust, angularResolution/2, true);
		Geometry3D balls = csg.translate3D(lockingBallPosition, lockingBallPosition, 0).transform(ball);
		balls = csg.union3D(balls, csg.translate3D(lockingBallPosition, -lockingBallPosition, 0).transform(ball));
		balls = csg.union3D(balls, csg.translate3D(-lockingBallPosition, lockingBallPosition, 0).transform(ball));
		balls = csg.union3D(balls, csg.translate3D(-lockingBallPosition, -lockingBallPosition, 0).transform(ball));
		return balls;
	}

	public Geometry3D getCutoutBalls()
	{
		return getBallsShape(lockingBallCutoutAdjust);
	}

	public Geometry3D getAddOnBalls()
	{
		return getBallsShape(0);
	}

	private Geometry3D getAxleShape(double length, double adjustDiameter, double adjustWidth)
	{
		Geometry3D axle = csg.cylinder3D(unit + adjustDiameter, length, angularResolution, false);
		Geometry3D widthBox = csg.box3D(2*unit, connectorWidth + adjustWidth, length, false);
		return csg.intersection3D(axle, widthBox);
	}

	private Geometry3D getAxleHoleShape(double outsideDiameter, double insideDiameter, double leadIn, double adjust)
	{
		double totalLength = 0.5*cubeSize;
		Geometry3D bottom = csg.cylinder3D(outsideDiameter+adjust, leadIn, angularResolution, false);
		double coneHeight = 0.5 * (outsideDiameter - insideDiameter);
		Geometry3D middle = csg.cone3D
			(
				outsideDiameter+adjust,
				insideDiameter+adjust,
				coneHeight,
				angularResolution,false
			);
		middle = csg.translate3DZ(leadIn).transform(middle);
		Geometry3D top = csg.cylinder3D(insideDiameter+adjust, totalLength - leadIn - coneHeight, angularResolution, false);
		top = csg.translate3DZ(leadIn + coneHeight).transform(top);
		return csg.union3D(bottom, middle, top);
	}

	public Geometry3D getAxleCutout(double length)
	{
		return getAxleShape(length, cutoutAxleDiameterAdjust, baseHoleSideAdjust);
	}

	public Geometry3D getAxle(double length)
	{
		return getAxleShape(length, 0, 0);
	}

	public Geometry3D getCubeAxleCutout()
	{
		Geometry3D axleCutout = getAxleHoleShape(outerDiameter, innerDiameter, axleLeadin + 0.5*cubeDistance, cutoutAxleDiameterAdjust);
		axleCutout = csg.translate3DZ(-0.5*cubeSize).transform(axleCutout);
		return axleCutout;
	}

	public Geometry3D getSideCutout()
	{
		Geometry3D balls = getBallsShape(lockingBallCutoutAdjust);
		balls = csg.translate3DZ(-0.5*(cubeSize - cubeDistance)).transform(balls);
		Geometry3D sideCutout = csg.union3D(balls, getCubeAxleCutout());
		return csg.cache(sideCutout);
	}

	public Geometry3D getConnectorClicker()
	{
		//Axle
		Geometry3D connector = getAxleHoleShape(outerDiameter, innerDiameter, axleLeadin, 0);
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

	public Geometry3D getBaseConnector(double extraLength)
	{
		Geometry3D connector = getConnectorClicker();
		connector = csg.translate3DZ(unit*extraLength).transform(connector);
		Geometry3D base = getConnectorBaseShape(0,0, extraLength);
		connector = csg.union3D(connector, base);
		return connector;
	}

	public Geometry3D getDoubleBaseConnector(double extraLength)
	{
		Geometry3D connector = getBaseConnector(extraLength/2);
		connector = csg.union3D(connector, csg.mirror3D(0,0,1).transform(connector));
		connector = csg.rotate3DX(csg.degrees(90)).transform(connector);
		return connector;
	}

	public Geometry3D getConntorBaseCutout(double extraLength)
	{
		return getConnectorBaseShape(baseHoleSideAdjust, baseHoleEndAdjust, extraLength);
	}



	public Geometry3D getConnectorBaseShape(double adjustSide, double adjustEnd, double extraLength)
	{
		double baseHeight = 0.5*cubeDistance;
		double largeDiameter = connectorBaseDiameter+2*adjustEnd;
		double smallDiameter = outerDiameter+2*adjustEnd;
		//Base space
		Geometry3D baseShape = csg.cylinder3D(largeDiameter, 0.25*baseHeight+unit*extraLength, angularResolution, false);
		baseShape = csg.translate3DZ(-unit*extraLength).transform(baseShape);
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
		Geometry3D widthBox = csg.box3D(cubeSize, connectorWidth+2*adjustSide, cubeSize+unit*extraLength, false);
		widthBox = csg.translate3DZ(-unit*extraLength).transform(widthBox);
		baseShape = csg.intersection3D(baseShape, widthBox);
		baseShape = csg.translate3DZ(unit*extraLength).transform(baseShape);
		return baseShape;
	}

	public Geometry3D getNutCutout(double nutWidth, double nutHeight,  double threadDiameter, double threadLength, double nutInsert)
	{
		double nutDiameter = (2 * nutWidth) / Math.sqrt(3.0);
		Geometry3D nutCutout = csg.cylinder3D(nutDiameter, nutHeight, 6, false);
		Geometry3D insertBox = csg.box3D(2*unit, nutWidth, nutHeight, false);
		insertBox = csg.translate3DX(unit).transform(insertBox);
		nutCutout = csg.union3D(nutCutout, insertBox);
		nutCutout = csg.translate3DZ(nutInsert).transform(nutCutout);
		Geometry3D thread = csg.cylinder3D(threadDiameter, threadLength, angularResolution/2, false);
		nutCutout = csg.union3D(nutCutout, thread);
		nutCutout = csg.rotate3DZ(csg.degrees(-90)).transform(nutCutout);
		nutCutout = csg.mirror3D(0,0,1).transform(nutCutout);
		return nutCutout;
	}


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createDefault();
		SmartCubeFeatures features = new SmartCubeFeatures(csg, 8, 128);

		//Geometry3D test = features.getConnectorBaseShape(0,0);
		//Geometry3D test = features.getDoubleBaseConnector(2);
		//Geometry3D test = features.getNutCutout(5.6, 3.2, 3.4, 6, 2.0);
		Geometry3D test = features.getDoubleBaseConnector(0);
		csg.view(test);
	}


}
