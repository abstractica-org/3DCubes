package org.abstractica.smartcubes.base.plates;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BasePlates
{
	private final JavaCSG csg;
	private final double scale;
	private final Features features;
	private final int angularResolution;


	public BasePlates(JavaCSG csg, double scale, int angularResolution)
	{
		this.csg = csg;
		this.scale = scale;
		this.features = new Features(csg, scale, angularResolution);
		this.angularResolution = angularResolution;
	}

	public Geometry3D turnPlate()
	{
		Geometry3D plate = csg.cylinder3D(scale*24, scale*4, angularResolution, false);
		Geometry3D cutOut = features.roundClickerBaseCutout();
		Geometry3D turnPlate = csg.difference3D(plate, cutOut);
		turnPlate = csg.rotate3DX(csg.degrees(180)).transform(turnPlate);
		turnPlate = csg.translate3DZ(scale*4).transform(turnPlate);
		return turnPlate;
	}

	public Geometry3D basePlate(int x, int y,
	                             int extraNorth, int extraSouth, int extraEast, int extraWest,
	                             boolean roundHoles)
	{
		return basePlate(x, y, extraNorth, extraSouth, extraEast, extraWest, roundHoles, null);
	}

	public Geometry3D basePlate(int x, int y,
	                             int extraNorth, int extraSouth, int extraEast, int extraWest,
	                             boolean roundHoles,
	                             Set<Vector2i> removeHoles)
	{
		double xSize = scale*(16*x+8);
		double ySize = scale*(16*y+8);
		double totalXSize = xSize + scale*4*extraEast + scale*4*extraWest;
		double totalYSize = ySize + scale*4*extraNorth + scale*4*extraSouth;

		//Plate
		Geometry3D plate = csg.box3D(totalXSize, totalYSize, scale*4, false);
		plate = csg.translate3D(0.5*totalXSize, 0.5*totalYSize, 0).transform(plate);
		if(extraEast > 0)
		{
			plate = csg.translate3DX(-scale*4*extraEast).transform(plate);
		}
		if(extraSouth > 0)
		{
			plate = csg.translate3DY(-scale * 4 * extraSouth).transform(plate);
		}


		//Holes
		Geometry3D hole = roundHoles ? features.roundClickerBaseCutout() : features.doubleClickerBaseCutout();
		List<Geometry3D> holes = new ArrayList<>();
		for(int ix = 0; ix < x; ++ix)
		{
			for(int iy = 0; iy < y; ++iy)
			{
				if(removeHoles == null || !removeHoles.contains(new Vector2i(ix, iy)))
				{
					holes.add(csg.translate3D(scale * (12 + 16 * ix), scale * (12 + 16 * iy), 0).transform(hole));
				}
			}
		}
		Geometry3D allHoles = csg.union3D(holes);
		plate = csg.difference3D(plate, allHoles);


		//Balls
		Geometry3D balls = features.ballAddonGrid(x+1, y+1);
		balls = csg.translate3DZ(scale*4).transform(balls);
		plate = csg.union3D(plate, balls);


		return plate;
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createDefault();
		BasePlates sp = new BasePlates(csg, 1.0, 64);
		Set<Vector2i> removeHoles = Set.of(new Vector2i(1, 0));
		csg.view(sp.basePlate(3, 1, 2, 1, 1, 0, false, removeHoles));
		//csg.view(sp.longPlate(2, false));
		//csg.view(sp.turnPlate());
	}

}
