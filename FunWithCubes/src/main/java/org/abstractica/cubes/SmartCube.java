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
		//xCylinder.debugMark();
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


	public Geometry3D tapShape(double adjust)
	{
		Geometry3D tap = csg.cylinder3D(0.5*unit + adjust, 0.25*unit, angularResolution/2, false);
		Geometry3D tapTop = csg.sphere3D(0.5*unit + adjust, angularResolution/2, true);
		tapTop = csg.translate3DZ(0.25*unit).transform(tapTop);
		tap = csg.union3D(tap, tapTop);
		return tap;
	}

	public static void main(String[] args) throws IOException
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		SmartCube sc = new SmartCube(csg, 8, 128);
		//Geometry3D supportPlate = sc.getSupportPlate();
		//csg.view(supportPlate);
		Geometry3D block = sc.getBasicBlock(4);
		csg.view(block);

		/*
		Geometry3D halfBlock = csg.slice3DZ(-20, 0,block);

		Geometry3D tapFemale = sc.tapShape(0);
		tapFemale = csg.mirror3D(0,0,1).transform(tapFemale);
		Geometry3D tapMale = sc.tapShape(0);

		Geometry3D tap1 = csg.translate3D(8, 8, 0).transform(tapMale);
		Geometry3D tap2 = csg.translate3D(-8, -8, 0).transform(tapMale);
		Geometry3D tap3 = csg.translate3D(8, -8, 0).transform(tapFemale);
		Geometry3D tap4 = csg.translate3D(-8, 8, 0).transform(tapFemale);

		halfBlock = csg.union3D(halfBlock, tap1, tap2);
		halfBlock = csg.difference3D(halfBlock, tap3, tap4);
		csg.view(halfBlock);

		//csg.view(sc.getAxle(4));
		*/

		/*
		for(int i = 1; i <= 10; ++i)
		{
			System.out.println("Creating block: " + i);
			Geometry3D block = sc.getBasicBlock(i);
			csg.saveSTL("OpenSCAD/NewBlocks/Block_"+i+".stl", block);
			System.out.println("Block " + i + " Done!");
		}
		*/

	}



}
