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

	public Geometry3D singleClickHole(boolean fixOverhang)
	{
		Geometry2D profile = clickHoleProfile(fixOverhang);
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
			Geometry2D section = clickHoleProfile(false);
			section = csg.translate2DY(scale*8*i).transform(section);
			sections.add(section);
		}
		return csg.union2D(sections);
	}

	private Geometry2D clickHoleProfile(boolean fixOverhang)
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0, 0));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5, 0));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5, scale * 2));
		points.add(csg.vector2D(scale * clickDiameter * 0.5, scale * 3));
		points.add(csg.vector2D(scale * clickDiameter * 0.5, scale * 5));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5, scale * 6));
		points.add(csg.vector2D(scale * brickHoleDiameter * 0.5, scale * 8));
		double overhangHeight = fixOverhang ? scale * brickHoleDiameter * 0.5 : 0;
		points.add(csg.vector2D(0, scale * 8 + overhangHeight));
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
		Geometry3D grid = make2DGrid(scale*4, scale*16, xCount, yCount, ballCutout());
		return csg.translate3DZ(height).transform(grid);
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
		Geometry2D profile = clickerBaseProfile(clickerBaseCutoutDiameterAdjust, 0);
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
		Geometry2D profile = clickerBaseProfile(clickerBaseCutoutDiameterAdjust, 0);
		return csg.cache(csg.rotateExtrude(csg.degrees(360), angularResolution, profile));
	}

	private Geometry3D clickerBase(double extraLength)
	{
		Geometry2D profile = clickerBaseProfile(-clickTolerance, extraLength);
		Geometry3D baseCutout = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D widthBox = csg.box3D(scale*16, scale*clickerWidth, scale*(8+extraLength), false);
		widthBox = csg.translate3DZ(-scale*extraLength).transform(widthBox);
		return csg.intersection3D(baseCutout, widthBox);
	}

	private Geometry2D clickerBaseProfile(double adjustRadius, double extraLength)
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0, -scale*extraLength));
		points.add(csg.vector2D(scale * baseDiameter * 0.5 + adjustRadius, -scale*extraLength));
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
		double l = (length-1) * 8.0;
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
		return baseClicker(length, 0);
	}

	public Geometry3D baseClicker(int length, double extraTail)
	{
		Geometry3D base = clickerBase(extraTail);
		Geometry3D tip = clickerTip(length);
		tip = csg.translate3D(0,0,scale*4).transform(tip);
		Geometry3D clicker = csg.union3D(base, tip);
		Geometry3D slit = csg.box3D(scale*clickerSlit, scale*16, scale*10, false);
		slit = csg.translate3DZ(scale*(4+(length-1)*8)).transform(slit);
		clicker = csg.difference3D(clicker, slit);
		return csg.cache(clicker);
	}

	public Geometry3D doubleBaseClicker(int lengthA, int lengthB)
	{
		Geometry3D bottom = baseClicker(lengthA, 0);
		Geometry3D top = csg.mirror3D(0,0,1).transform(baseClicker(lengthB, 0));
		//top = csg.translate3DZ(scale*8).transform(top);
		return csg.cache(csg.union3D(bottom, top));
	}

	// *****************************************  Special adapters ***************************************

	public Geometry3D getTTMotorAxleAdapter(int length)
	{
		double extraLength = 5.2 + length * 8;
		Geometry3D clickerBase = baseClicker(1, extraLength);
		clickerBase = csg.translate3DZ(extraLength).transform(clickerBase);
		Geometry3D cyl = csg.cylinder3D(5.35, 7.8, angularResolution/2, true);
		Geometry3D box = csg.box3D(3.8, 6, 10, true);
		Geometry3D axle = csg.intersection3D(cyl, box);
		axle = csg.translate3D(0, 0, 3.89).transform(axle);
		Geometry3D res = csg.difference3D(clickerBase, axle);
		res = csg.rotate3DX(csg.degrees(90)).transform(res);
		return res;
	}

	public Geometry3D getServoMotorClicker()
	{
		double extraLength = 4;
		Geometry3D clickerBase = baseClicker(1, extraLength);
		clickerBase = csg.rotate3DX(csg.degrees(90)).transform(clickerBase);
		return clickerBase;
	}

	public Geometry3D getServoMotorAxelMount()
	{
		Geometry3D cylinder = csg.cylinder3D(15, 9.6, angularResolution/2, false);
		Geometry3D hole = csg.cylinder3D(5.8, 5, angularResolution/2, false);
		hole = csg.translate3DZ(-1).transform(hole);
		Geometry3D res = csg.difference3D(cylinder, hole);
		Geometry3D axle = csg.cylinder3D(scale*baseDiameter, 5, angularResolution/2, false);
		Geometry3D widthBox = csg.box3D(scale*16, scale*clickerWidth, scale*8, false);
		axle = csg.intersection3D(axle, widthBox);
		axle = csg.translate3DZ(5.6).transform(axle);
		res = csg.difference3D(res, axle);
		return res;
	}

	public Geometry3D getServoMotorBallBearingClicker()
	{
		Geometry3D clickerBase = baseClicker(1, 9);
		clickerBase = csg.translate3DZ(9).transform(clickerBase);
		clickerBase = csg.rotate3DX(csg.degrees(180)).transform(clickerBase);
		Geometry3D axle = csg.cylinder3D(8, 8, angularResolution/2, false);
		Geometry3D widthBox = csg.box3D(scale*16, scale*clickerWidth, 8, false);
		axle = csg.intersection3D(axle, widthBox);
		Geometry3D res = csg.union3D(clickerBase, axle);

		/*
		//Temporary code to make the axle fit the ball bearing hole
		Geometry3D cutCyl1 = csg.cylinder3D(10, 15, angularResolution/2, false);
		cutCyl1 = csg.translate3DZ(-6).transform(cutCyl1);
		Geometry3D cutCyl2 = csg.cylinder3D(20, 20, angularResolution/2, false);
		cutCyl2 = csg.translate3DZ(-26).transform(cutCyl2);
		Geometry3D cutcyl = csg.union3D(cutCyl1, cutCyl2);
		res = csg.intersection3D(res, cutcyl);
		*/
		res = csg.rotate3DX(csg.degrees(90)).transform(res);
		return res;
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
		JavaCSG csg = JavaCSGFactory.createDefault();
		Features features = new Features(csg, 1.0, 128);

		//Geometry3D test = features.brickAxleCutout(3);

		//Geometry3D test = features.clickerBaseCutout();

		//Geometry3D test = features.baseClicker(1, 8);

		//Geometry3D test = features.singleClickHole(true);


		//Geometry3D test = features.clickerBase(0);

		//Geometry2D test = features.clickerProfile();

		//Geometry2D test = features.axle2EndProfile();

		//Geometry2D test = features.axle2TipProfile();

		//Geometry3D test = features.clickerTip();

		//Geometry2D test = features.axle2Profile(3);

		//Geometry3D test = features.axle2(6);
		//test = csg.rotate3DX(csg.degrees(90)).transform(test);

		//Geometry3D test = features.doubleBaseClicker(3, 1);
		//test = csg.rotate3DX(csg.degrees(90)).transform(test);

		//Geometry3D test = features.doubleBaseClicker(3,3);
		//test = csg.rotate3DX(csg.degrees(90)).transform(test);

		//Geometry2D test = features.axleProfile(3);

		//Geometry3D test = features.axle(3);
		//test = csg.rotate3DX(csg.degrees(90)).transform(test);

		//Geometry3D test = features.roundClickerBaseCutout();

		//Geometry3D test = features.getTTMotorAxleAdapter(2);

		//Geometry3D test = features.getServoMotorAxelMount();

		//Geometry3D test = features.getServoMotorBallBearingClicker();

		//Geometry3D test = features.getServoMotorClicker();

		Geometry3D test = features.getTTMotorAxleAdapter(2);

		csg.view(test, 1);
	}
}
