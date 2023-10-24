package org.abstractica.smartcubes.base.bricks;

import org.abstractica.javacsg.*;
import org.abstractica.smartcubes.base.Features;

import java.util.ArrayList;
import java.util.List;

public class CrossBricks
{
	private final JavaCSG csg;
	private final double scale;
	private final Features features;

	public CrossBricks(JavaCSG csg, double scale, int angularResolution)
	{
		this.csg = csg;
		this.scale = scale;
		this.features = new Features(csg, scale, angularResolution);
	}

	public Geometry3D crossBrick(int length, int width, int height)
	{
		double h = scale*(16*height+8);
		Geometry3D brick = csg.linearExtrude(h, false, crossBrickProfile(length, width));
		List<Geometry3D> cutouts = new ArrayList<>();

		//Horizontal plane
		//Axles
		Geometry3D zAxle = features.brickAxleCutout(height*2+1);
		List<Geometry3D> zAxleParts = new ArrayList<>();
		zAxleParts.add(zAxle);
		Geometry3D sphere = features.brickCenterSphereCutout();
		for(int i = 0; i < height; ++i)
		{
			zAxleParts.add(csg.translate3DZ(scale*(i*16+12)).transform(sphere));
		}
		zAxle = csg.union3D(zAxleParts);
		//Diagonal
		int diag = length + width - 1;
		for(int i = 0; i < diag; ++i)
		{
			double pos = scale*(12+i*16);
			Geometry3D axleCutout = csg.translate3D(pos, pos, 0).transform(zAxle);
			cutouts.add(axleCutout);
		}
		//xAxis
		int l = length + width - 2;
		for(int i = 1; i < width; ++i)
		{
			double tx = scale*(12+i*16);
			for(int j = 0; j < l; ++j)
			{
				double ty = scale*(12+j*16);
				Geometry3D axleCutout = csg.translate3D(tx, ty, 0).transform(zAxle);
				cutouts.add(axleCutout);
				tx += scale*16;
			}
			--l;
		}
		//yAxis
		l = length + width - 2;
		for(int i = 1; i < width; ++i)
		{
			double ty = scale*(12+i*16);
			for(int j = 0; j < l; ++j)
			{
				double tx = scale*(12+j*16);
				Geometry3D axleCutout = csg.translate3D(tx, ty, 0).transform(zAxle);
				cutouts.add(axleCutout);
				ty += scale*16;
			}
			--l;
		}
		//Balls
		Geometry3D ball = features.ballCutout();
		Geometry3D ballAxle = csg.union3D(ball, csg.translate3DZ(scale*(8+height*16)).transform(ball));
		int columns = length + width;
		int columnLength = width + 1;
		double tyBall = scale*4;
		for(int c = 0; c < columns; ++c)
		{
			double tx = scale*(4+c*16);
			for(int j = 0; j < columnLength; ++j)
			{
				cutouts.add(csg.translate3D(tx, tyBall+scale*j*16, 0).transform(ballAxle));
			}
			if(c < length-1)
			{
				++columnLength;
			}
			if(c >= width)
			{
				--columnLength;
				tyBall += scale*16;
			}
		}
		//Vertical planes
		Geometry3D widthAxle = features.brickAxleCutout(width*2+1);
		//XZ planes
		Geometry3D yAxis = csg.rotate3DX(csg.degrees(-90)).transform(widthAxle);
		//Front
		for(int i = 0; i < height; ++i)
		{
			double tz = scale*(i*16+12);
			for(int j = 0; j < width; ++j)
			{
				double tx = scale*(j*16+12);
				cutouts.add(csg.translate3D(tx, 0, tz).transform(yAxis));
			}
		}
		for(int i = 0; i <= height; ++i)
		{
			double tz = scale*(i*16+4);
			for(int j = 0; j <= width; ++j)
			{
				double tx = scale*(j*16+4);
				cutouts.add(csg.translate3D(tx, 0, tz).transform(ball));
			}
		}
		//Back
		for(int i = 0; i < height; ++i)
		{
			double tz = scale*(i*16+12);
			for(int j = 0; j < width; ++j)
			{
				double tx = scale*(j*16+12);
				cutouts.add(csg.translate3D(scale*(length-1)*16+tx, scale*(length-1)*16, tz).transform(yAxis));
			}
		}
		for(int i = 0; i <= height; ++i)
		{
			double tz = scale*(i*16+4);
			for(int j = 0; j <= width; ++j)
			{
				double tx = scale*(j*16+4);
				cutouts.add(csg.translate3D(scale*(length-1)*16+tx, scale*((length-1)*16+width*16+8), tz).transform(ball));
			}
		}

		//YZ planes
		Geometry3D xAxis = csg.rotate3DY(csg.degrees(90)).transform(widthAxle);
		//Left
		for(int i = 0; i < height; ++i)
		{
			double tz = scale*(i*16+12);
			for(int j = 0; j < width; ++j)
			{
				double ty = scale*(j*16+12);
				cutouts.add(csg.translate3D(0, ty, tz).transform(xAxis));
			}
		}
		for(int i = 0; i <= height; ++i)
		{
			double tz = scale*(i*16+4);
			for(int j = 0; j <= width; ++j)
			{
				double ty = scale*(j*16+4);
				cutouts.add(csg.translate3D(0, ty, tz).transform(ball));
			}
		}
		//Right
		for(int i = 0; i < height; ++i)
		{
			double tz = scale*(i*16+12);
			for(int j = 0; j < width; ++j)
			{
				double ty = scale*(j*16+12);
				cutouts.add(csg.translate3D(scale*(length-1)*16, scale*(length-1)*16+ty, tz).transform(xAxis));
			}
		}
		for(int i = 0; i <= height; ++i)
		{
			double tz = scale*(i*16+4);
			for(int j = 0; j <= width; ++j)
			{
				double ty = scale*(j*16+4);
				cutouts.add(csg.translate3D( scale*((length-1)*16+width*16+8),scale*(length-1)*16+ty, tz).transform(ball));
			}
		}

		return csg.difference3D(brick, cutouts);
	}

	private Geometry2D crossBrickProfile(int length, int width)
	{
		double crossSide = (length-1)*2*8*scale;
		double widthSide = scale*(width*16+8);

		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0,0));
		points.add(csg.vector2D(widthSide, 0));
		points.add(csg.vector2D(widthSide+crossSide, crossSide));
		points.add(csg.vector2D(widthSide+crossSide, crossSide+widthSide));
		points.add(csg.vector2D(crossSide, crossSide+widthSide));
		points.add(csg.vector2D(0, widthSide));

		return csg.polygon2D(points);
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		CrossBricks cb = new CrossBricks(csg, 1.0, 128);
		Geometry3D test = cb.crossBrick(5, 1, 1);
		csg.view(test, 0);
	}
}
