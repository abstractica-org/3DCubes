package org.abstractica.cubes;

import org.abstractica.cubes.features.SmartCubeFeatures;
import org.abstractica.javacsg.*;

import java.util.ArrayList;
import java.util.List;

public class Plates
{
	private final JavaCSG csg;
	private final SmartCubeFeatures features;
	private final double unit;
	private final int angularResolution;
	private final double turnPlateDiameter;

	public Plates(JavaCSG csg, double unit, int angularResolution)
	{
		this.csg = csg;
		this.unit = unit;
		this.angularResolution = angularResolution;
		this.features = new SmartCubeFeatures(csg, unit, angularResolution);
		//Turnplate
		this.turnPlateDiameter = 2*Math.sqrt(2*unit*unit);
	}

	public Geometry3D getSupportPlate(boolean xNegEdge, boolean xPosEdge, boolean yNegEdge, boolean yPosEdge)
	{
		Geometry3D plate = getPlateShape(8*unit, 3*unit, 0.5*unit, xNegEdge, xPosEdge, yNegEdge, yPosEdge);
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

	public Geometry3D getBigPlate()
	{
		Geometry3D plate = csg.box3D(unit*12, unit*12, 0.5*unit, false);
		Geometry3D ballsMid = features.getAddOnBalls();
		ballsMid = csg.translate3DZ(0.5*unit).transform(ballsMid);
		Geometry3D ballsNorth = csg.translate3DY(4*unit).transform(ballsMid);
		Geometry3D ballsSouth = csg.translate3DY(-4*unit).transform(ballsMid);
		Geometry3D ballsEast = csg.translate3DX(4*unit).transform(ballsMid);
		Geometry3D ballsWest = csg.translate3DX(-4*unit).transform(ballsMid);
		plate = csg.union3D(plate, ballsMid, ballsNorth, ballsSouth, ballsEast, ballsWest);
		Geometry3D baseCutoutMid = features.getConntorBaseCutout(0);
		Geometry3D baseCutoutNorth = csg.translate3DY(4*unit).transform(baseCutoutMid);
		Geometry3D baseCutoutSouth = csg.translate3DY(-4*unit).transform(baseCutoutMid);
		Geometry3D baseCutoutEast = csg.translate3DX(4*unit).transform(baseCutoutMid);
		Geometry3D baseCutoutWest = csg.translate3DX(-4*unit).transform(baseCutoutMid);
		plate = csg.difference3D(plate, baseCutoutMid, baseCutoutNorth, baseCutoutSouth, baseCutoutEast, baseCutoutWest);
		return plate;
	}

	public Geometry3D getInsideCornerSupport()
	{
		Geometry2D cornerProfile = cornerProfile();
		Geometry3D corner = csg.linearExtrude(3*unit, false, cornerProfile);
		Geometry3D balls = features.getAddOnBalls();
		Geometry3D balls1 = csg.rotate3DX(csg.degrees(90)).transform(balls);
		balls1 = csg.translate3D(2.5*unit, 0,1.5*unit).transform(balls1);
		Geometry3D balls2 = csg.rotate3DY(csg.degrees(90)).transform(balls);
		balls2 = csg.translate3D(0, 2.5*unit,1.5*unit).transform(balls2);
		corner = csg.union3D(corner, balls1, balls2);
		Geometry3D baseCutout = features.getConntorBaseCutout(0);
		Geometry3D cutout1 = csg.rotate3DY(csg.degrees(-90)).transform(baseCutout);
		Geometry3D cutout2 = csg.rotate3DZ(csg.degrees(90)).transform(cutout1);
		cutout1 = csg.translate3D(0.5*unit, 2.5*unit,1.5*unit).transform(cutout1);
		cutout2 = csg.translate3D(2.5*unit, 0.5*unit,1.5*unit).transform(cutout2);
		corner = csg.difference3D(corner, cutout1, cutout2);
		return corner;
	}

	private Geometry2D cornerProfile()
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0,0));
		points.add(csg.vector2D(4.5*unit,0));
		points.add(csg.vector2D(4.5*unit,0.5*unit));
		points.add(csg.vector2D(1*unit,0.5*unit));
		points.add(csg.vector2D(0.5*unit,1*unit));
		points.add(csg.vector2D(0.5*unit,4.5*unit));
		points.add(csg.vector2D(0,4.5*unit));
		return csg.polygon2D(points);
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

	private Geometry3D getPlateShape
		(
			double xSize, double ySize, double zSize,
			boolean xNegEdge, boolean xPosEdge, boolean yNegEdge, boolean yPosEdge
		)
	{
		double corner0x = 0;
		double corner0y = 0;
		double corner1x = 0;
		double corner1y = 0;
		double corner2x = 0;
		double corner2y = 0;
		double corner3x = 0;
		double corner3y = 0;
		if(xNegEdge)
		{
			corner0x = -zSize;
			corner3x = -zSize;
		}
		if(xPosEdge)
		{
			corner1x = zSize;
			corner2x = zSize;
		}
		if(yNegEdge)
		{
			corner0y = -zSize;
			corner1y = -zSize;
		}
		if(yPosEdge)
		{
			corner2y = zSize;
			corner3y = zSize;
		}

		List<Vector3D> vertices = new ArrayList<>();

		vertices.add(csg.vector3D(-0.5*xSize+corner0x,-0.5*ySize+corner0y,0)); //Corner 0
		vertices.add(csg.vector3D(0.5*xSize+corner1x,-0.5*ySize+corner1y,0)); //Corner 1
		vertices.add(csg.vector3D(0.5*xSize+corner2x,0.5*ySize+corner2y,0)); //Corner 2
		vertices.add(csg.vector3D(-0.5*xSize+corner3x,0.5*ySize+corner3y,0)); //Corner 3

		vertices.add(csg.vector3D(-0.5*xSize,-0.5*ySize,zSize)); //Corner 4
		vertices.add(csg.vector3D(0.5*xSize,-0.5*ySize,zSize)); //Corner 5
		vertices.add(csg.vector3D(0.5*xSize,0.5*ySize,zSize)); //Corner 6
		vertices.add(csg.vector3D(-0.5*xSize,0.5*ySize,zSize)); //Corner 7

		List<List<Integer>> polygons = new ArrayList<>();
		List<Integer> bottom = new ArrayList<>();
		bottom.add(0);
		bottom.add(1);
		bottom.add(2);
		bottom.add(3);
		polygons.add(bottom);
		List<Integer> top = new ArrayList<>();
		top.add(7);
		top.add(6);
		top.add(5);
		top.add(4);
		polygons.add(top);
		List<Integer> front = new ArrayList<>();
		front.add(4);
		front.add(5);
		front.add(1);
		front.add(0);
		polygons.add(front);
		List<Integer> back = new ArrayList<>();
		back.add(6);
		back.add(7);
		back.add(3);
		back.add(2);
		polygons.add(back);
		List<Integer> left = new ArrayList<>();
		left.add(3);
		left.add(7);
		left.add(4);
		left.add(0);
		polygons.add(left);
		List<Integer> right = new ArrayList<>();
		right.add(5);
		right.add(6);
		right.add(2);
		right.add(1);
		polygons.add(right);
		return csg.polyhedron3D(vertices, polygons);
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		Plates plates = new Plates(csg, 8, 128);

		//Geometry3D test = plates.getInsideCornerSupport();
		Geometry3D test = plates.getBigPlate();

		csg.view(test);

		//Geometry3D plate = plates.getSupportPlate(false, false, true, true);
		//csg.view(plate);
	}

}
