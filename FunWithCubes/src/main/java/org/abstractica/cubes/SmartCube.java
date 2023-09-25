package org.abstractica.cubes;

import org.abstractica.cubes.features.SmartCubeFeatures;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SmartCube
{
	private final JavaCSG csg;
	private final SmartCubeFeatures features;
	private final double unit;
	private final int angularResolution;
	private final double turnPlateDiameter;

	public SmartCube(JavaCSG csg, double unit, int angularResolution)
	{
		this.csg = csg;
		this.unit = unit;
		this.angularResolution = angularResolution;
		this.features = new SmartCubeFeatures(csg, unit, angularResolution);
		//Turnplate
		this.turnPlateDiameter = 2*Math.sqrt(2*unit*unit);
	}

	public Geometry3D getBasicBlock(int length)
	{
		double xSize = (2 * length + 1) * unit;
		double yzSize = 3 * features.getUnit();

		//Create the block
		Geometry3D block = csg.box3D(xSize, yzSize, yzSize, true);

		//Move block to first axle hole
		block = csg.translate3DX(0.5*xSize-1.5*unit).transform(block);

		//Create the list of cutouts
		List<Geometry3D> cutoutList = new ArrayList<>();

		Geometry3D xCylinder = csg.cylinder3D(unit+1, xSize - 3*unit, angularResolution, false); //Hard coded for now! DO FIX!
		xCylinder = csg.rotate3DY(csg.degrees(90)).transform(xCylinder);
		xCylinder.debugMark();
		cutoutList.add(xCylinder);

		//Get the sphere cutouts
		Geometry3D sphereCutout = features.getCenterSphereCutout();

		for(int i = 0; i < length; ++i)
		{
			cutoutList.add(csg.translate3DX(i*2*unit).transform(sphereCutout));
		}

		//Get the side cutout
		Geometry3D sideCutout = features.getSideCutout();

		//Cut the front
		Geometry3D frontCutout = csg.rotate3DY(csg.degrees(90)).transform(sideCutout);
		cutoutList.add(frontCutout);

		//Cut the back
		Geometry3D backCutout = csg.mirror3D(1,0,0).transform(frontCutout);
		backCutout = csg.translate3DX((length-1)*2*unit).transform(backCutout);
		cutoutList.add(backCutout);

		Geometry3D[] sidesWithBalls = new Geometry3D[4];
		sidesWithBalls[0] = sideCutout;
		sidesWithBalls[1] = csg.rotate3DX(csg.degrees(90)).transform(sideCutout);
		sidesWithBalls[2] = csg.rotate3DX(csg.degrees(180)).transform(sideCutout);
		sidesWithBalls[3] = csg.rotate3DX(csg.degrees(270)).transform(sideCutout);

		//Do the evens first, since balls overlap
		for(int i = 0; i < length; i=i+2)
		{
			for(int j = 0; j < 4; ++j)
			{
				cutoutList.add(csg.translate3DX(i*2*unit).transform(sidesWithBalls[j]));
			}
		}

		Geometry3D[] sidesWithoutBalls = new Geometry3D[4];
		sidesWithoutBalls[0] = features.getCubeAxleCutout();
		sidesWithoutBalls[1] = csg.rotate3DX(csg.degrees(90)).transform(sideCutout);
		sidesWithoutBalls[2] = csg.rotate3DX(csg.degrees(180)).transform(sideCutout);
		sidesWithoutBalls[3] = csg.rotate3DX(csg.degrees(270)).transform(sideCutout);

		//Now we do the odds
		for(int i = 1; i < length; i=i+2)
		{
			for(int j = 0; j < 4; ++j)
			{
				cutoutList.add(csg.translate3DX(i*2*unit).transform(sidesWithoutBalls[j]));
			}
		}

		block = csg.difference3D(block, cutoutList);
		return csg.cache(block);
	}

	public Geometry3D getAxle(double length)
	{
		double l = length*unit-0.5*unit;
		Geometry3D axle = features.getAxle(l);
		Geometry3D base = features.getConnectorBaseShape(0,0,0);
		axle = csg.union3D(axle, base);
		Geometry3D nutCutout = features.getNutCutout(5.6, 3, 3.4, 6, 2.0);
		nutCutout = csg.translate3DZ(l).transform(nutCutout);
		axle = csg.difference3D(axle, nutCutout);
		axle = csg.rotate3DX(csg.degrees(-90)).transform(axle);
		return axle;
	}

	public Geometry3D getSupportPlate()
	{
		Geometry3D plate = csg.box3D(8*unit, 3*unit, 0.5*unit, false);
		Geometry3D balls = features.getAddOnBalls();
		balls = csg.translate3DZ(0.5*unit).transform(balls);
		balls = csg.translate3DX(-unit*2).transform(balls);
		balls = csg.union3D(balls, csg.translate3DX(unit*4).transform(balls));
		plate = csg.union3D(plate, balls);
		Geometry3D baseCutout = features.getConntorBaseCutout(0);
		baseCutout = csg.translate3DX(-unit*2).transform(baseCutout);
		baseCutout = csg.union3D(baseCutout, csg.translate3DX(unit*4).transform(baseCutout));
		plate = csg.difference3D(plate, baseCutout);
		return plate;
	}

	public Geometry3D getScrewHead()
	{
		Geometry3D base = features.getConnectorBaseShape(0,0,0);
		Geometry3D head = csg.cylinder3D(6.4, 2, angularResolution/2, false);
		Geometry3D thread = csg.cylinder3D(3.4, unit, angularResolution/2, false);
		Geometry3D cutout = csg.union3D(head, thread);
		base = csg.difference3D(base, cutout);
		base = csg.rotate3DX(csg.degrees(90)).transform(base);
		return base;
	}

	public Geometry3D getThrustBBPlate()
	{
		Geometry3D plate = csg.box3D(3*unit, 3*unit, 0.5*unit-0.4, false);
		plate = csg.translate3DZ(0.4).transform(plate);
		Geometry3D balls = features.getAddOnBalls();
		balls = csg.translate3DZ(0.5*unit).transform(balls);
		plate = csg.union3D(plate, balls);
		double height = 2.4;
		Geometry3D bottom = csg.cylinder3D(16.2, height, angularResolution, false);
		Geometry3D middle = csg.cone3D(16.2, 16.2-(0.5*unit - height), 0.5*unit - height, angularResolution, false);
		middle = csg.translate3DZ(height).transform(middle);
		Geometry3D cutout = csg.union3D(bottom, middle);
		plate = csg.difference3D(plate, cutout);
		return plate;
	}

	public Geometry3D getThrustBBPlateA()
	{
		Geometry3D plate = csg.box3D(3*unit, 3*unit, 2.2, false);
		Geometry3D balls = features.getAddOnBalls();
		balls = csg.translate3DZ(2.2).transform(balls);
		plate = csg.union3D(plate, balls);
		Geometry3D cutout = csg.cylinder3D(16.2, 2, angularResolution, false);
		Geometry3D edge = csg.cylinder3D(15.8, 0.2, angularResolution, false);
		edge = csg.translate3DZ(2).transform(edge);
		plate = csg.difference3D(plate, cutout, edge);
		return plate;
	}

	public Geometry3D getThrustBBPlateB()
	{
		Geometry3D plate = csg.box3D(3*unit, 3*unit, 2.4, false);
		Geometry3D balls = features.getAddOnBalls();
		balls = csg.translate3DZ(2.4).transform(balls);
		plate = csg.union3D(plate, balls);
		Geometry3D cutoutBalls = features.getCutoutBalls();
		plate = csg.difference3D(plate, cutoutBalls);
		Geometry3D cutoutHole = features.getAxleCutout(unit);
		plate = csg.difference3D(plate, cutoutHole);
		return plate;
	}


	public static void main(String[] args) throws IOException
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		SmartCube sc = new SmartCube(csg, 8, 128);
		csg.view(sc.getBasicBlock(3),1);

		/*
		for(int i = 1; i <= 10; ++i)
		{
			System.out.println("Creating block: " + i);
			Geometry3D block = sc.getBasicBlock(i);
			csg.saveSTL("OpenSCAD/Blocks/Block_"+i+".stl", block);
			System.out.println("Block " + i + " Done!");
		}
		*/
	}


}
