package org.abstractica.yat;

import org.abstractica.javacsg.*;

import java.util.ArrayList;
import java.util.List;

public class BBAxlesAndPlates
{
	private final JavaCSG csg;
	private final int angularResolution;
	private final Features features;

	private final double axleDiameter = 8.1;
	private final double axleWidth = 7;
	private final double slitWidth = 2.4;
	private final double axleCutoutTolerance = 0.2;

	public BBAxlesAndPlates(JavaCSG csg, int angularResolution)
	{
		this.csg = csg;
		this.angularResolution = angularResolution;
		this.features = new Features(csg, 1, this.angularResolution);
	}

	public Geometry3D axle(int length)
	{
		double l = 2*6.5 + length*8;
		Geometry2D profile = axleProfile(2*6.5 + length*8);
		Geometry3D axle = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D widthBox = csg.box3D(16, axleWidth, 20 + 8*length, false);
		axle = csg.intersection3D(axle, widthBox);
		Geometry3D slit = csg.box3D(slitWidth, 16, 15, false);
		slit = csg.translate3DZ(l+1-10).transform(slit);
		axle = csg.difference3D(axle, slit);
		return axle;
	}

	public Geometry3D axleLock()
	{
		Geometry2D profile = axleProfile(5);
		Geometry3D lock = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D widthBox = csg.box3D(16, slitWidth, 20, false);
		lock = csg.intersection3D(lock, widthBox);
		Geometry3D slit = csg.box3D(4, 16, 15, false);
		slit = csg.translate3DZ(2).transform(slit);
		lock = csg.difference3D(lock, slit);
		return lock;
	}

	Geometry2D axleProfile(double length)
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0, 0));
		points.add(csg.vector2D(axleDiameter * 0.5+1, 0));
		points.add(csg.vector2D(axleDiameter * 0.5+1, 1.8));
		points.add(csg.vector2D(axleDiameter * 0.5, 1.8));
		points.add(csg.vector2D(axleDiameter * 0.5, length+2));
		points.add(csg.vector2D(axleDiameter * 0.5+0.6, length+2));
		points.add(csg.vector2D(axleDiameter * 0.5+0.6, length+3));
		points.add(csg.vector2D(axleDiameter * 0.5-0.6, length+4.2));
		points.add(csg.vector2D(0, length+4.2));
		return csg.polygon2D(points);
	}

	public Geometry3D bbPlate()
	{
		Geometry3D plate = csg.box3D(24, 24, 3.6, false);
		Geometry3D bbCutout = csg.cylinder3D(16.1, 2.2, angularResolution, false);
		Geometry3D bbLock = csg.cone3D(16.1, 13.3, 1.4, angularResolution, false);
		bbLock = csg.translate3DZ(2.2).transform(bbLock);
		bbCutout = csg.union3D(bbCutout, bbLock);
		plate = csg.difference3D(plate, bbCutout);
		Geometry3D ball = features.ballAddon();
		List<Geometry3D> union = new ArrayList<>();
		union.add(plate);
		union.add(csg.translate3D(8, 8, 3.4).transform(ball));
		union.add(csg.translate3D(-8, 8, 3.4).transform(ball));
		union.add(csg.translate3D(8, -8, 3.4).transform(ball));
		union.add(csg.translate3D(-8, -8, 3.4).transform(ball));
		plate = csg.union3D(union);
		return plate;
	}

	public Geometry3D bbPlate2()
	{
		Geometry3D plate = csg.box3D(24, 24, 3.6, false);
		Geometry3D bbCutout = csg.cylinder3D(16.1, 3.6, angularResolution, false);
		Geometry3D cross = csg.linearExtrude(1.4, false, roundCross(0.1));
		cross = csg.translate3DZ(2.2).transform(cross);
		bbCutout = csg.union3D(bbCutout, cross);
		plate = csg.difference3D(plate, bbCutout);
		Geometry3D ball = features.ballAddon();
		List<Geometry3D> union = new ArrayList<>();
		union.add(plate);
		union.add(csg.translate3D(8, 8, 3.4).transform(ball));
		union.add(csg.translate3D(-8, 8, 3.4).transform(ball));
		union.add(csg.translate3D(8, -8, 3.4).transform(ball));
		union.add(csg.translate3D(-8, -8, 3.4).transform(ball));
		plate = csg.union3D(union);
		return plate;
	}

	public Geometry3D bbPlate2AxleLock()
	{
		Geometry3D plate = bbPlate2Extension();
		Geometry3D cutout = axleCutout(5);
		plate = csg.difference3D(plate, cutout);
		return plate;
	}

	public Geometry3D bbPlate2AxleFree()
	{
		Geometry3D plate = bbPlate2Extension();
		Geometry3D cutout = csg.cylinder3D(9, 5, angularResolution, false);
		plate = csg.difference3D(plate, cutout);
		return plate;
	}

	public Geometry3D bbThinSpacer()
	{
		return spacer(1.4);
	}

	public Geometry3D bbUnitSpacer()
	{
		return spacer(8);
	}

	public Geometry3D bbExtensionSingleSupportPlate()
	{
		Geometry3D plate = csg.box3D(40, 24, 4, false);
		plate = csg.translate3D(20, 12, 0).transform(plate);
		List<Geometry3D> addons = new ArrayList<>();
		addons.add(plate);
		Geometry3D balls = features.ballAddonGrid(3, 2);
		balls = csg.translate3DZ(4).transform(balls);
		addons.add(balls);
		plate = csg.union3D(addons);
		Geometry3D baseHole = features.doubleClickerBaseCutout();
		baseHole = csg.translate3D(12, 12, 0).transform(baseHole);
		plate = csg.difference3D(plate, baseHole);
		return plate;
	}

	public Geometry3D bbExtensionRingA()
	{
		Geometry3D ring = csg.cylinder3D(16.1, 9.4, angularResolution, false);
		Geometry3D hole = csg.cylinder3D(11, 9.4, angularResolution, false);
		ring = csg.difference3D(ring, hole);
		return ring;
	}

	public Geometry3D bbExtensionRingB()
	{
		Geometry3D ring = csg.cylinder3D(16.1, 1.4, angularResolution, false);
		Geometry3D hole = csg.cylinder3D(11, 1.4, angularResolution, false);
		ring = csg.difference3D(ring, hole);
		return ring;
	}

	public Geometry3D bbExtensionRingC()
	{
		Geometry3D ring = csg.cylinder3D(16.1, 3, angularResolution, false);
		Geometry3D hole = csg.cylinder3D(11, 3, angularResolution, false);
		ring = csg.difference3D(ring, hole);
		return ring;
	}

	public Geometry3D bbExtension()
	{
		List<Geometry3D> cutouts = new ArrayList<>();
		List<Geometry3D> addons = new ArrayList<>();
		Geometry3D bottom = csg.cylinder3D(11, 1.4, angularResolution, false);
		Geometry3D top = csg.cylinder3D(16.2, 16-1.4, angularResolution, false);
		top = csg.translate3DZ(1.4).transform(top);
		Geometry3D cutout = csg.union3D(bottom, top);
		cutouts.add(cutout);
		Geometry3D ballCutout = features.ballCutout();
		cutouts.add(csg.translate3D(8, 8, 0).transform(ballCutout));
		cutouts.add(csg.translate3D(-8, 8, 0).transform(ballCutout));
		cutouts.add(csg.translate3D(8, -8, 0).transform(ballCutout));
		cutouts.add(csg.translate3D(-8, -8, 0).transform(ballCutout));

		cutouts.add(csg.translate3D(8, 12, 4).transform(ballCutout));
		cutouts.add(csg.translate3D(-8, 12, 4).transform(ballCutout));

		cutouts.add(csg.translate3D(8, -12, 4).transform(ballCutout));
		cutouts.add(csg.translate3D(-8, -12, 4).transform(ballCutout));

		cutouts.add(csg.translate3D(12, 8, 4).transform(ballCutout));
		cutouts.add(csg.translate3D(12, -8, 4).transform(ballCutout));

		cutouts.add(csg.translate3D(-12, 8, 4).transform(ballCutout));
		cutouts.add(csg.translate3D(-12, -8, 4).transform(ballCutout));


		Geometry3D block = csg.box3D(24, 24, 16, false);

		block = csg.difference3D(block, cutouts);

		Geometry3D ballAddon = features.ballAddon();
		addons.add(block);
		addons.add(csg.translate3D(8, 8, 16).transform(ballAddon));
		addons.add(csg.translate3D(-8, 8, 16).transform(ballAddon));
		addons.add(csg.translate3D(8, -8, 16).transform(ballAddon));
		addons.add(csg.translate3D(-8, -8, 16).transform(ballAddon));
		block = csg.union3D(addons);
		return block;
	}

	private Geometry3D axleCutout(double length)
	{
		Geometry3D cutout = csg.cylinder3D(axleDiameter+axleCutoutTolerance, length, angularResolution, false);
		Geometry3D widthBox = csg.box3D(16, axleWidth+axleCutoutTolerance, length, false);
		return csg.intersection3D(cutout, widthBox);
	}

	private Geometry3D spacer(double width)
	{
		Geometry3D spacer = csg.cylinder3D(10.4, width, angularResolution, false);
		Geometry3D hole = axleCutout(width);
		return csg.difference3D(spacer, hole);
	}

	private Geometry3D bbPlate2Extension()
	{
		List<Geometry3D> union = new ArrayList<>();
		Geometry3D cross = csg.linearExtrude(1.4, false, roundCross(0.0));
		union.add(cross);
		Geometry3D cylinder1 = csg.cylinder3D(16, 1.4, angularResolution, false);
		union.add(cylinder1);
		Geometry3D cylinder2 = csg.cylinder3D(10.8, 2, angularResolution, false);
		union.add(csg.translate3DZ(1.4).transform(cylinder2));
		Geometry3D cone = csg.cone3D(10.8, 8.8, 1, angularResolution, false);
		union.add(csg.translate3DZ(3.4).transform(cone));
		Geometry3D plate = csg.union3D(union);
		return plate;
	}

	private Geometry2D roundCross(double diameterAdjust)
	{
		Geometry2D c = csg.circle2D(4+diameterAdjust, angularResolution);
		Geometry2D circle1 = csg.translate2DX(8).transform(c);
		Geometry2D circle2 = csg.translate2DX(-8).transform(c);
		Geometry2D shape = csg.hull2D(circle1, circle2);
		Geometry2D cross = csg.union2D(csg.rotate2D(csg.degrees(90)).transform(shape), shape);
		return cross;
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		BBAxlesAndPlates bbAxlesAndPlates = new BBAxlesAndPlates(csg, 32);

		//Geometry3D test = bbAxlesAndPlates.axle(6);
		//Geometry3D test = bbAxlesAndPlates.axleLock();
		//test = csg.rotate3DX(csg.degrees(90)).transform(test);

		//Geometry3D test = bbAxlesAndPlates.bbPlate();
		//Geometry3D test = bbAxlesAndPlates.bbExtensionRingC();
		//Geometry3D test = bbAxlesAndPlates.axleLock();

		//Geometry3D test = bbAxlesAndPlates.bbThinSpacer();
		//Geometry3D test = bbAxlesAndPlates.bbUnitSpacer();

		//Geometry3D test = csg.box3D(24, 12, 4, false);
		//test = csg.difference3D(test, bbAxlesAndPlates.axleCutout(12));

		//Geometry3D test = bbAxlesAndPlates.bbExtensionSingleSupportPlate();

		Geometry3D test = bbAxlesAndPlates.bbPlate2AxleLock();
		csg.view(test);
	}
}
