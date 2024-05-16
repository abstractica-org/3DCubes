package org.abstractica.smartcubes.base.plates;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlatesOld
{
	private final JavaCSG csg;
	private final double scale;
	private final Features features;
	private final int angularResolution;


	public PlatesOld(JavaCSG csg, double scale, int angularResolution)
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

	public Geometry3D simplePlate(int x, int y, boolean roundHoles)
	{
		return simplePlate(x, y, roundHoles, null);
	}

	public Geometry3D simplePlate(int x, int y, boolean roundHoles, Set<HolePosition> removeHoles)
	{
		double xSize = scale*(16*x+8);
		double ySize = scale*(16*y+8);

		//Plate
		Geometry3D plate = csg.box3D(xSize, ySize, scale*4, false);
		plate = csg.translate3D(0.5*xSize, 0.5*ySize, 0).transform(plate);

		//Holes
		Geometry3D hole = roundHoles ? features.roundClickerBaseCutout() : features.doubleClickerBaseCutout();
		List<Geometry3D> holes = new ArrayList<>();
		for(int ix = 0; ix < x; ++ix)
		{
			for(int iy = 0; iy < y; ++iy)
			{
				if(removeHoles == null || !removeHoles.contains(new HolePosition(ix, iy)))
				{
					holes.add(csg.translate3D(scale * (12 + 16 * ix), scale * (12 + 16 * iy), 0).transform(hole));
				}
			}
		}
		plate = csg.difference3D(plate, holes);


		//Balls
		Geometry3D balls = features.ballAddonGrid(x+1, y+1);
		balls = csg.translate3DZ(scale*4).transform(balls);
		plate = csg.union3D(plate, balls);


		return plate;
	}

	public Geometry3D longPlate(int length, boolean extraLength)
	{
		Geometry3D tile = tile(false);
		Geometry3D space = csg.box3D(scale*4, scale*24, scale*4, false);
		space = csg.translate3D(scale*2, scale*12, 0).transform(space);
		List<Geometry3D> parts = new ArrayList<>();
		double xPos = 0;
		for(int i = 0; i < length; ++i)
		{
			if(i > 0 || extraLength)
			{
				parts.add(csg.translate3DX(xPos).transform(space));
				xPos += scale*4;
			}
			parts.add(csg.translate3DX(xPos).transform(tile));
			xPos += scale*24;
			if(i < length-1 || extraLength)
			{
				parts.add(csg.translate3DX(xPos).transform(space));
				xPos += scale*4;
			}
		}
		return csg.union3D(parts);
	}

	public Geometry3D squarePlate()
	{
		Geometry3D plate = csg.box3D(scale*56, scale*56, scale*4, false);
		plate = csg.translate3D(scale*28, scale*28, 0).transform(plate);
		List<Geometry3D> cutouts = new ArrayList<>();
		Geometry3D baseCutout = features.doubleClickerBaseCutout();
		cutouts.add(csg.translate3D(scale*12, scale*12, 0).transform(baseCutout));
		cutouts.add(csg.translate3D(scale*44, scale*12, 0).transform(baseCutout));
		cutouts.add(csg.translate3D(scale*12, scale*44, 0).transform(baseCutout));
		cutouts.add(csg.translate3D(scale*44, scale*44, 0).transform(baseCutout));
		Geometry3D cutout = csg.union3D(cutouts);
		plate = csg.difference3D(plate, cutout);
		Geometry3D balls = csg.translate3DZ(scale*4).transform(features.ballAddonGrid(4, 4));
		return csg.union3D(plate, balls);
	}

	public Geometry3D sidePlate(int xNeg, int xPos, int yNeg, int yPos)
	{
		List<Geometry3D> parts = new ArrayList<>();
		Geometry3D tile = tile(false);
		tile = csg.translate3D(scale*-12, scale*-12, 0).transform(tile);
		parts.add(tile);
		Geometry3D xSpace = csg.box3D(scale*4, scale*24, scale*4, false);
		Geometry3D xSpaceNeg = csg.translate3DX(scale*-2).transform(xSpace);
		Geometry3D xTileNeg = csg.translate3DX(scale*-12).transform(tile);
		double x = scale*-12;
		if(xNeg > 0)
		{
			parts.add(csg.translate3DX(x).transform(xSpaceNeg));
			x -= scale*4;
		}
		for(int i = 0; i < xNeg; ++i)
		{
			parts.add(csg.translate3DX(x).transform(xSpaceNeg));
			x -= scale*4;
			parts.add(csg.translate3DX(x).transform(xTileNeg));
			x -= scale*24;
			parts.add(csg.translate3DX(x).transform(xSpaceNeg));
			x -= scale*4;
		}
		Geometry3D xSpacePos = csg.translate3DX(scale*2).transform(xSpace);
		Geometry3D xTilePos = csg.translate3DX(scale*12).transform(tile);
		x = scale*12;
		if(xPos > 0)
		{
			parts.add(csg.translate3DX(x).transform(xSpacePos));
			x += scale*4;
		}
		for(int i = 0; i < xPos; ++i)
		{
			parts.add(csg.translate3DX(x).transform(xSpacePos));
			x += scale*4;
			parts.add(csg.translate3DX(x).transform(xTilePos));
			x += scale*24;
			parts.add(csg.translate3DX(x).transform(xSpacePos));
			x += scale*4;
		}

		Geometry3D ySpace = csg.box3D(scale*24, scale*4, scale*4, false);
		Geometry3D ySpaceNeg = csg.translate3DY(scale*-2).transform(ySpace);
		Geometry3D yTileNeg = csg.translate3DY(scale*-12).transform(tile);
		double y = scale*-12;
		if(yNeg > 0)
		{
			parts.add(csg.translate3DY(y).transform(ySpaceNeg));
			y -= scale*4;
		}
		for(int i = 0; i < yNeg; ++i)
		{
			parts.add(csg.translate3DY(y).transform(ySpaceNeg));
			y -= scale*4;
			parts.add(csg.translate3DY(y).transform(yTileNeg));
			y -= scale*24;
			parts.add(csg.translate3DY(y).transform(ySpaceNeg));
			y -= scale*4;
		}
		Geometry3D ySpacePos = csg.translate3DY(scale*2).transform(ySpace);
		Geometry3D yTilePos = csg.translate3DY(scale*12).transform(tile);
		y = scale*12;
		if(yPos > 0)
		{
			parts.add(csg.translate3DY(y).transform(ySpacePos));
			y += scale*4;
		}
		for(int i = 0; i < yPos; ++i)
		{
			parts.add(csg.translate3DY(y).transform(ySpacePos));
			y += scale*4;
			parts.add(csg.translate3DY(y).transform(yTilePos));
			y += scale*24;
			parts.add(csg.translate3DY(y).transform(ySpacePos));
			y += scale*4;
		}
		return csg.union3D(parts);
	}

	public Geometry3D insideCorner(int a, int b)
	{
		List<Geometry3D> parts = new ArrayList<>();
		Geometry3D xTile = tile(true);
		xTile = csg.rotate3DX(csg.degrees(90)).transform(xTile);
		xTile = csg.translate3DY(scale*4).transform(xTile);
		Geometry3D yTile = tile(true);
		yTile = csg.rotate3DY(csg.degrees(-90)).transform(yTile);
		yTile = csg.translate3DX(scale*4).transform(yTile);
		Geometry3D space = csg.box3D(scale*4, scale*4, scale*24, false);
		space = csg.translate3D(scale*2, scale*2, 0).transform(space);
		parts.add(space);
		double xPos = scale*4;
		for(int i = 0; i < a; ++i)
		{
			parts.add(csg.translate3DX(xPos).transform(space));
			xPos += scale*4;
			parts.add(csg.translate3DX(xPos).transform(xTile));
			xPos += scale*24;
			parts.add(csg.translate3DX(xPos).transform(space));
			xPos += scale*4;
		}
		double yPos = scale*4;
		for(int i = 0; i < b; ++i)
		{
			parts.add(csg.translate3DY(yPos).transform(space));
			yPos += scale*4;
			parts.add(csg.translate3DY(yPos).transform(yTile));
			yPos += scale*24;
			parts.add(csg.translate3DY(yPos).transform(space));
			yPos += scale*4;
		}
		return csg.union3D(parts);
	}

	public Geometry3D outsideCorner(int a, int b)
	{
		List<Geometry3D> parts = new ArrayList<>();
		Geometry3D xTile = tile(true);
		xTile = csg.rotate3DX(csg.degrees(-90)).transform(xTile);
		xTile = csg.translate3D(0, scale*-4, scale*24).transform(xTile);
		Geometry3D yTile = tile(true);
		yTile = csg.rotate3DY(csg.degrees(90)).transform(yTile);
		yTile = csg.translate3D(scale*-4, 0, scale*24).transform(yTile);
		Geometry3D space = csg.box3D(scale*4, scale*4, scale*24, false);
		space = csg.translate3D(scale*-2, scale*-2, 0).transform(space);
		parts.add(space);
		Geometry3D xSpace = csg.translate3DX(scale*4).transform(space);
		double xPos = 0;
		for(int i = 0; i < a; ++i)
		{
			if(i > 0)
			{
				parts.add(csg.translate3DX(xPos).transform(xSpace));
				xPos += scale * 4;
			}
			parts.add(csg.translate3DX(xPos).transform(xTile));
			xPos += scale*24;
			parts.add(csg.translate3DX(xPos).transform(xSpace));
			xPos += scale*4;
		}
		Geometry3D ySpace = csg.translate3DY(scale*4).transform(space);
		double yPos = 0;
		for(int i = 0; i < b; ++i)
		{
			if(i > 0)
			{
				parts.add(csg.translate3DY(yPos).transform(ySpace));
				yPos += scale * 4;
			}
			parts.add(csg.translate3DY(yPos).transform(yTile));
			yPos += scale*24;
			parts.add(csg.translate3DY(yPos).transform(ySpace));
			yPos += scale*4;
		}
		return csg.union3D(parts);
	}

	private Geometry3D tile(boolean roundHole)
	{
		Geometry3D section = csg.box3D(scale*24, scale*24, scale*4, false);
		section = csg.translate3D(scale*12, scale*12, 0).transform(section);
		Geometry3D balls = features.ballAddonGrid(2,2);
		balls = csg.translate3DZ(scale*4).transform(balls);
		section = csg.union3D(section, balls);
		Geometry3D baseCutout = roundHole ? features.roundClickerBaseCutout() : features.doubleClickerBaseCutout();
		baseCutout = csg.translate3D(scale*12, scale*12, 0).transform(baseCutout);
		section = csg.difference3D(section, baseCutout);
		return csg.cache(section);
	}

	public Geometry3D printPlateOfCorners()
	{
		List<Geometry3D> plate = new ArrayList<>();
		Geometry3D outsideCorner = outsideCorner(2,2);
		Geometry3D outSideCornerXNeg = csg.rotate3DZ(csg.degrees(135)).transform(outsideCorner);
		for(int i = 0; i < 4; ++i)
		{
			plate.add(csg.translate3DX(-scale*16*(i+1)).transform(outSideCornerXNeg));
		}
		Geometry3D outSideCornerXPos = csg.rotate3DZ(csg.degrees(-45)).transform(outsideCorner);
		for(int i = 0; i < 4; ++i)
		{
			plate.add(csg.translate3DX(scale*16*(i+1)).transform(outSideCornerXPos));
		}
		Geometry3D outSideCornerYNeg = csg.rotate3DZ(csg.degrees(225)).transform(outsideCorner);
		for(int i = 0; i < 4; ++i)
		{
			plate.add(csg.translate3DY(-scale*16*(i+1)).transform(outSideCornerYNeg));
		}
		Geometry3D outSideCornerYPos = csg.rotate3DZ(csg.degrees(45)).transform(outsideCorner);
		for(int i = 0; i < 4; ++i)
		{
			plate.add(csg.translate3DY(scale*16*(i+1)).transform(outSideCornerYPos));
		}
		Geometry3D insideCorner = insideCorner(1,1);
		Geometry3D insideCornerXNegYPos = csg.rotate3DZ(csg.degrees(90)).transform(insideCorner);
		for(int i = 0; i < 4; ++i)
		{
			plate.add(csg.translate3D(-scale*8*(i+6), scale*8*(i+6), 0).transform(insideCornerXNegYPos));
		}

		Geometry3D insideCornerXPosYPos = csg.rotate3DZ(csg.degrees(0)).transform(insideCorner);;
		for(int i = 0; i < 4; ++i)
		{
			plate.add(csg.translate3D(scale*8*(i+6), scale*8*(i+6), 0).transform(insideCornerXPosYPos));
		}

		Geometry3D insideCornerXPosYNeg = csg.rotate3DZ(csg.degrees(-90)).transform(insideCorner);;
		for(int i = 0; i < 4; ++i)
		{
			plate.add(csg.translate3D(scale*8*(i+6), -scale*8*(i+6), 0).transform(insideCornerXPosYNeg));
		}

		Geometry3D insideCornerXNegYNeg = csg.rotate3DZ(csg.degrees(180)).transform(insideCorner);;
		for(int i = 0; i < 4; ++i)
		{
			plate.add(csg.translate3D(-scale*8*(i+6), -scale*8*(i+6), 0).transform(insideCornerXNegYNeg));
		}
		return csg.union3D(plate);
	}

	public Geometry3D scalableTile(int nHolesWidth, int nHolesHeight, boolean roundHoles, int extraWidth, int extraHeight)
	{
		if (nHolesWidth < 2 || nHolesHeight < 2) throw new IllegalArgumentException("Tile must be at least 2x2");

		double extraH = scale *4 *2 * extraHeight;
		double extraL = scale *4 *2 * extraWidth;

		double x = nHolesWidth * 16 + 8;
		double y = nHolesHeight * 16 + 8;

		double width = scale * x + extraL;
		double height = scale * y + extraH;

		Geometry3D plate = csg.box3D(width, height, scale*4, false);
		plate = csg.translate3D(width/2-extraL/2, height/2-extraH/2, 0).transform(plate);

		// Hole cutouts
		Geometry3D baseCutout = roundHoles ? features.roundClickerBaseCutout() : features.doubleClickerBaseCutout();

		Geometry3D rightDown =  csg.translate3D(scale*x-scale*12, scale*12, 0).transform(baseCutout);
		Geometry3D leftDown =  csg.translate3D(scale*12, scale*12, 0).transform(baseCutout);
		Geometry3D rightUp =  csg.translate3D(scale*x-scale*12, scale*y-scale*12, 0).transform(baseCutout);
		Geometry3D leftUp =  csg.translate3D(scale*12, scale*y-scale*12, 0).transform(baseCutout);

		Geometry3D totalCutout = csg.union3D(rightDown, leftDown, rightUp, leftUp);

		plate = csg.difference3D(plate, totalCutout);

		// Balls
		Geometry3D balls = features.ballAddonGrid(2,2);
		balls = csg.translate3DZ(scale*4).transform(balls);

		Geometry3D ballsRightDown = csg.translate3D(scale*x-scale*24, 0, 0).transform(balls);
		Geometry3D ballsLeftDown = csg.translate3D(0, 0, 0).transform(balls);
		Geometry3D ballsRightUp = csg.translate3D(scale*x-scale*24, scale*y-scale*24, 0).transform(balls);
		Geometry3D ballsLeftUp = csg.translate3D(0, scale*y-scale*24, 0).transform(balls);

		Geometry3D total = csg.union3D(plate, ballsRightDown, ballsLeftDown, ballsRightUp, ballsLeftUp);

		return csg.cache(total);
	}

	public Geometry3D angleTile(int nHolesWidth, int nHolesHeight, int nHolesOffsetY, int extraOffsetY)
	{
		double x = nHolesWidth * 16 + 8;
		double y = nHolesHeight * 16 + 8;
		double offsetY = nHolesOffsetY * 16 + 8 + (scale * 4 * extraOffsetY);

		double width = scale * x;
		double height = scale * y;
		double offset = scale * offsetY;

		Geometry3D plate = csg.box3D(width, height, scale*4, false);
		plate = csg.translate3D(width/2, height/2, 0).transform(plate);

		Geometry3D angle = csg.rotate3DX(csg.degrees(-45)).transform(plate);
		angle = csg.translate3DY(offset).transform(angle);
		return angle;
	}


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		PlatesOld sp = new PlatesOld(csg, 1.0, 256);
		// Geometry3D tile = sp.tile(false);
		// csg.view(sp.longPlate(3, false));
		// csg.view(sp.scalableTile(8, 14, false, false, false));
		// csg.view(sp.scalableTile(8, 8, false, 0, 0));

		Geometry3D plate = sp.scalableTile(8, 8, false, 0, 2);
		Geometry3D angle = sp.angleTile(8, 1, 8, 1);
		csg.view(csg.union3D(plate, angle),1);


		// csg.view(tile);
	}

}
