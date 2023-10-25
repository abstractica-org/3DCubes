package org.abstractica.smartcubes.base.bricks;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BasicBricks
{
	private final JavaCSG csg;
	private final double scale;
	private final Features features;

	public BasicBricks(JavaCSG csg, double scale, int angularResolution)
	{
		this.csg = csg;
		this.scale = scale;
		this.features = new Features(csg, scale, angularResolution);
	}

	public Geometry3D basicBrick(int x, int y, int z)
	{
		double sizeX = scale*(16*x+8);
		double sizeY = scale*(16*y+8);
		double sizeZ = scale*(16*z+8);
		Geometry3D block = csg.box3D(sizeX, sizeY, sizeZ, true);
		block = csg.translate3D(0.5*sizeX, 0.5*sizeY, 0.5*sizeZ).transform(block);
		List<Geometry3D> cutouts = new ArrayList<>();

		//xAxle cutouts
		Geometry3D xAxleCutout = features.brickAxleCutout(2*x+1);
		xAxleCutout = csg.rotate3DY(csg.degrees(90)).transform(xAxleCutout);
		xAxleCutout = csg.translate3D(0,scale*12,scale*12).transform(xAxleCutout);
		for(int iz = 0; iz < z; ++iz)
		{
			for(int iy = 0; iy < y; ++iy)
			{
				Geometry3D axleCutout = csg.translate3D(0, scale*iy*16, scale*iz*16).transform(xAxleCutout);
				cutouts.add(axleCutout);
			}
		}

		//yAxle cutouts
		Geometry3D yAxleCutout = features.brickAxleCutout(2*y+1);
		yAxleCutout = csg.rotate3DX(csg.degrees(-90)).transform(yAxleCutout);
		yAxleCutout = csg.translate3D(scale*12,0,scale*12).transform(yAxleCutout);
		for(int iz = 0; iz < z; ++iz)
		{
			for(int ix = 0; ix < x; ++ix)
			{
				Geometry3D axleCutout = csg.translate3D(scale*ix*16, 0, scale*iz*16).transform(yAxleCutout);
				cutouts.add(axleCutout);
			}
		}

		//zAxle cutouts
		Geometry3D zAxleCutout = features.brickAxleCutout(2*z+1);
		zAxleCutout = csg.translate3D(scale*12,scale*12,0).transform(zAxleCutout);
		for(int iy = 0; iy < y; ++iy)
		{
			for(int ix = 0; ix < x; ++ix)
			{
				Geometry3D axleCutout = csg.translate3D(scale*ix*16, scale*iy*16, 0).transform(zAxleCutout);
				cutouts.add(axleCutout);
			}
		}

		Geometry3D sphere = features.brickCenterSphereCutout();
		sphere = csg.translate3D(scale * 12, scale * 12, scale * 12).transform(sphere);
		for (int iz = 0; iz < z; ++iz)
		{
			for (int iy = 0; iy < y; ++iy)
			{
				for (int ix = 0; ix < x; ++ix)
				{
					Geometry3D sphereCutout =
						csg.translate3D(scale * ix * 16, scale * iy * 16, scale * iz * 16).transform(sphere);
					cutouts.add(sphereCutout);
				}
			}
		}

		//Ball cutouts
		Geometry3D ballCutoutShape = features.ballCutout();
		Geometry3D ballCutout;

		//Front
		ballCutout = csg.translate3D(scale*4, 0, scale*4).transform(ballCutoutShape);
		for(int iz = 0; iz <= z; ++iz)
		{
			for(int ix = 0; ix <= x; ++ix)
			{
				Geometry3D ballCutout2 = csg.translate3D(scale*ix*16, 0, scale*iz*16).transform(ballCutout);
				cutouts.add(ballCutout2);
			}
		}

		//Back
		ballCutout = csg.translate3D(scale*4, scale*y*16+(scale*8), scale*4).transform(ballCutoutShape);
		for(int iz = 0; iz <= z; ++iz)
		{
			for(int ix = 0; ix <= x; ++ix)
			{
				Geometry3D ballCutout2 = csg.translate3D(scale*ix*16, 0, scale*iz*16).transform(ballCutout);
				cutouts.add(ballCutout2);
			}
		}

		//Left
		ballCutout = csg.translate3D(0, scale*4, scale*4).transform(ballCutoutShape);
		for(int iz = 0; iz <= z; ++iz)
		{
			for(int iy = 0; iy <= y; ++iy)
			{
				Geometry3D ballCutout2 = csg.translate3D(0, scale*iy*16, scale*iz*16).transform(ballCutout);
				cutouts.add(ballCutout2);
			}
		}

		//Right
		ballCutout = csg.translate3D(scale*x*16+(scale*8), scale*4, scale*4).transform(ballCutoutShape);
		for(int iz = 0; iz <= z; ++iz)
		{
			for(int iy = 0; iy <= y; ++iy)
			{
				Geometry3D ballCutout2 = csg.translate3D(0, scale*iy*16, scale*iz*16).transform(ballCutout);
				cutouts.add(ballCutout2);
			}
		}

		//Bottom
		ballCutout = csg.translate3D(scale*4, scale*4, 0).transform(ballCutoutShape);
		for(int iy = 0; iy <= y; ++iy)
		{
			for(int ix = 0; ix <= x; ++ix)
			{
				Geometry3D ballCutout2 = csg.translate3D(scale*ix*16, scale*iy*16, 0).transform(ballCutout);
				cutouts.add(ballCutout2);
			}
		}

		//Top
		ballCutout = csg.translate3D(scale*4, scale*4, scale*z*16+(scale*8)).transform(ballCutoutShape);
		for(int iy = 0; iy <= y; ++iy)
		{
			for(int ix = 0; ix <= x; ++ix)
			{
				Geometry3D ballCutout2 = csg.translate3D(scale*ix*16, scale*iy*16, 0).transform(ballCutout);
				cutouts.add(ballCutout2);
			}
		}
		Geometry3D cutout = csg.union3D(cutouts);

		return csg.cache(csg.difference3D(block, cutout));
	}

	public static void main(String[] args) throws IOException
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		BasicBricks bb = new BasicBricks(csg, 1.0, 128);
		for(int i = 6; i <= 12; ++i)
		{
			csg.saveSTL("FinalParts/Cubes/Cube" + i + "x1x1.stl", bb.basicBrick(i, 1, 1));
		}
	}
}
