package org.abstractica.smartcubes.base;

import org.abstractica.javacsg.*;

import java.util.ArrayList;
import java.util.List;

public class Features
{
	private final JavaCSG csg;
	private final double scale;
	private final double clickDiameter = 9;
	private final double brickHoleDiameter = clickDiameter + 2;
	private final double baseDiameter = brickHoleDiameter + 2;
	private final double clickerWidth = 7;
	private final double clickerSlit = 4.0;
	private final double clickTolerance = 0.1;
	private final double clickerBaseCutoutDiameterAdjust = 0.1;
	private final double clickerBaseCutoutWidthAdjust = 0.1;

	private final int angularResolution;



	public Features(JavaCSG csg, double scale, int angularResolution)
	{
		this.csg = csg;
		this.scale = scale;
		this.angularResolution = angularResolution;
	}

	//*********************************************  Brick axle cutout  ********************************************
	public Geometry3D brickAxleCutout(int length)
	{
		Geometry2D profile = brickAxleCutoutProfile(length);
		return csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
	}

	public Geometry3D singleClickHole()
	{
		Geometry2D profile = clickHoleProfile();
		return csg.cache(csg.rotateExtrude(csg.degrees(360), angularResolution, profile));
	}

	private Geometry2D brickAxleCutoutProfile(int length)
	{
		if(length < 1)
		{
			throw new IllegalArgumentException("length must be >= 1");
		}
		List<Geometry2D> sections = new ArrayList<>();
		for(int i = 0; i < length; ++i)
		{
			Geometry2D section = clickHoleProfile();
			section = csg.translate2DY(scale*8*i).transform(section);
			sections.add(section);
		}
		return csg.union2D(sections);
	}

	private Geometry2D clickHoleProfile()
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0, 0));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5, 0));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5, scale * 2));
		points.add(csg.vector2D(scale * clickDiameter * 0.5, scale * 3));
		points.add(csg.vector2D(scale * clickDiameter * 0.5, scale * 5));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5, scale * 6));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5, scale * 8));
		points.add(csg.vector2D(0, scale * 8));
		return csg.polygon2D(points);
	}

	//*********************************************  Brick Center Sphere  ********************************************

	public Geometry3D brickCenterSphereCutout()
	{
		return csg.cache(csg.sphere3D(scale*16, angularResolution, true));
	}

	//*********************************************  Brick Balls  ****************************************************

	public Geometry3D ballCutout()
	{
		Geometry3D res = csg.sphere3D(scale*4+0.2, angularResolution/2, true);
		return csg.cache(res);
	}
	public Geometry3D ballCutoutRow(int xCount)
	{
		return makeRow(scale*4, scale*16, xCount, ballCutout());
	}
	public Geometry3D ballCutoutGrid(int xCount, int yCount, double height)
	{
		return make2DGrid(scale*4, scale*16, xCount, yCount, ballCutout());
	}

	public Geometry3D ballAddon()
	{
		Geometry3D res = csg.sphere3D(scale*4, angularResolution/2, true);
		return csg.cache(res);
	}

	public Geometry3D ballAddonRow(int xCount)
	{
		return makeRow(scale*4, scale*16, xCount, ballAddon());
	}

	public Geometry3D ballAddonGrid(int xCount, int yCount)
	{
		return make2DGrid(scale*4, scale*16, xCount, yCount, ballAddon());
	}

	//******************************* Clicker Base and Clicker Base cutout ********************************************

	public Geometry3D clickerBaseCutout()
	{
		Geometry2D profile = clickerBaseProfile(clickerBaseCutoutDiameterAdjust);
		Geometry3D baseCutout = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D widthBox = csg.box3D(scale*16, scale*clickerWidth+ clickerBaseCutoutWidthAdjust, scale*8, false);
		return csg.cache(csg.intersection3D(baseCutout, widthBox));
	}

	public Geometry3D doubleClickerBaseCutout()
	{
		Geometry3D baseA = clickerBaseCutout();
		Geometry3D baseB = csg.rotate3DZ(csg.degrees(90)).transform(baseA);
		Geometry3D base = csg.union3D(baseA, baseB);
		return csg.cache(base);
	}

	public Geometry3D roundClickerBaseCutout()
	{
		Geometry2D profile = clickerBaseProfile(clickerBaseCutoutDiameterAdjust);
		return csg.cache(csg.rotateExtrude(csg.degrees(360), angularResolution, profile));
	}

	private Geometry3D clickerBase()
	{
		Geometry2D profile = clickerBaseProfile(-clickTolerance);
		Geometry3D baseCutout = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D widthBox = csg.box3D(scale*16, scale*clickerWidth, scale*8, false);
		return csg.intersection3D(baseCutout, widthBox);
	}

	private Geometry2D clickerBaseProfile(double adjustRadius)
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0, 0));
		points.add(csg.vector2D(scale * baseDiameter * 0.5 + adjustRadius, 0));
		points.add(csg.vector2D(scale * baseDiameter * 0.5 + adjustRadius, scale * 1.5));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5 + adjustRadius, scale * 2.5));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5 + adjustRadius, scale * 4));
		points.add(csg.vector2D(0, scale * 4));
		return csg.polygon2D(points);
	}

	//*********************************************  Clicker Tip  ****************************************************

	private Geometry3D clickerTip(int length)
	{
		Geometry2D profile = clickerTipProfile(length);
		Geometry3D clicker = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D widthBox = csg.box3D(scale*16, scale*clickerWidth, scale*8*length, false);
		clicker = csg.intersection3D(clicker, widthBox);
		return clicker;
	}

	private Geometry2D clickerTipProfile(int length)
	{
		double d = 0.4;
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0, 0));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5 - clickTolerance, 0));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5 - clickTolerance, scale * 2));
		points.add(csg.vector2D(scale * clickDiameter * 0.5 - clickTolerance, scale * 3));
		double l = scale * (length-1) * 8.0;
		points.add(csg.vector2D(scale * clickDiameter * 0.5 - clickTolerance, scale * (l+5)));
		points.add(csg.vector2D(scale * (brickHoleDiameter * 0.5 - 0.4) - clickTolerance, scale * (l+5.6)));
		points.add(csg.vector2D(scale * (brickHoleDiameter * 0.5 - 0.4) - clickTolerance, scale * (l+6.2)));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5 - clickTolerance - scale * 0.8, scale * (l+7)));
		points.add(csg.vector2D(0, scale * (l+7)));
		return csg.polygon2D(points);
	}

	//********************************************* Base Clickers ****************************************************

	public Geometry3D baseClicker(int length)
	{
		Geometry3D base = clickerBase();
		Geometry3D tip = clickerTip(length);
		tip = csg.translate3D(0,0,scale*4).transform(tip);
		Geometry3D clicker = csg.union3D(base, tip);
		Geometry3D slit = csg.box3D(scale*clickerSlit, scale*16, scale*(10), false);
		slit = csg.translate3DZ(scale*4+(length-1)*8).transform(slit);
		clicker = csg.difference3D(clicker, slit);
		return csg.cache(clicker);
	}

	public Geometry3D doubleBaseClicker(int lengthA, int lengthB)
	{
		Geometry3D bottom = baseClicker(lengthA);
		Geometry3D top = csg.mirror3D(0,0,1).transform(baseClicker(lengthB));
		//top = csg.translate3DZ(scale*8).transform(top);
		return csg.cache(csg.union3D(bottom, top));
	}
	// ***************************************** Helper functions ****************************************

	private Geometry3D makeRow(double offSet, double spacing, int xCount, Geometry3D geometry)
	{
		List<Geometry3D> row = new ArrayList<>();
		for(int i = 0; i < xCount; ++i)
		{
			Geometry3D g = csg.translate3DX(offSet + spacing*i).transform(geometry);
			row.add(g);
		}
		return csg.union3D(row);
	}

	private Geometry3D make2DGrid(double offSet, double spacing, int xCount, int yCount, Geometry3D geometry)
	{
		List<Geometry3D> grid = new ArrayList<>();
		for(int y = 0; y < yCount; ++y)
		{
			for(int x = 0; x < xCount; ++x)
			{
				Geometry3D g =
					csg.translate3D(offSet + spacing*x, offSet + spacing*y, 0).transform(geometry);
				grid.add(g);
			}
		}
		return csg.union3D(grid);
	}


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		Features features = new Features(csg, 0.5, 128);

		//Geometry3D test = features.brickAxleCutout(3);

		//Geometry3D test = features.baseCutout();

		//Geometry3D test = features.clickerBase();

		//Geometry2D test = features.clickerProfile();

		//Geometry2D test = features.axle2EndProfile();

		//Geometry2D test = features.axle2TipProfile();

		//Geometry3D test = features.clickerTip();

		//Geometry2D test = features.axle2Profile(3);

		//Geometry3D test = features.axle2(6);
		//test = csg.rotate3DX(csg.degrees(90)).transform(test);

		Geometry3D test = features.doubleBaseClicker(1, 1);
		test = csg.rotate3DX(csg.degrees(90)).transform(test);

		//Geometry3D test = features.doubleBaseClicker(3,3);
		//test = csg.rotate3DX(csg.degrees(90)).transform(test);

		//Geometry2D test = features.axleProfile(3);

		//Geometry3D test = features.axle(3);
		//test = csg.rotate3DX(csg.degrees(90)).transform(test);

		//Geometry3D test = features.roundClickerBaseCutout();

		csg.view(test, 0);
	}
}
