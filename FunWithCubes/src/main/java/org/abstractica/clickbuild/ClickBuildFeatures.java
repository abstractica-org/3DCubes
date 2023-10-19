package org.abstractica.clickbuild;

import org.abstractica.javacsg.*;

import java.util.ArrayList;
import java.util.List;

public class ClickBuildFeatures
{
	private final JavaCSG csg;
	private final double scale;
	private final double clickTolerance = 0.1;
	private final double axleDiameterAdjust = 0;
	private final double clickerBaseCutoutDiameterAdjust = 0.2;
	private final double widthCutoutAdjust = 0.2;
	private final int angularResolution;
	private final double slitWidth = 4.0;
	private final double axleWidth = 6.4;


	public ClickBuildFeatures(JavaCSG csg, double scale, int angularResolution)
	{
		this.csg = csg;
		this.scale = scale;
		this.angularResolution = angularResolution;
	}

	public Geometry3D ball()
	{
		Geometry3D ball = csg.sphere3D(scale*4, angularResolution/2, true);
		return ball;
	}

	public Geometry3D cutoutBall()
	{
		Geometry3D ball = csg.sphere3D(scale*4+0.2, angularResolution/2, true);
		return ball;
	}

	public Geometry3D clickHoleCutout()
	{
		Geometry2D profile = clickHoleProfile();
		Geometry3D cutout = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		return cutout;
	}

	public Geometry3D clickerTip()
	{
		Geometry2D profile = clickerTipProfile();
		Geometry3D tip = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D slitBox = csg.box3D(scale*slitWidth, scale*16, scale*7, false);
		slitBox = csg.translate3DZ(scale).transform(slitBox);
		tip = csg.difference3D(tip, slitBox);
		Geometry3D widthBox = csg.box3D(scale*16, scale*axleWidth, scale*8, false);
		return csg.intersection3D(tip, widthBox);
	}

	public Geometry3D clickerTipAxle()
	{
		Geometry2D profile = clickerTipProfile();
		Geometry3D tip = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D slitBox = csg.box3D(scale*slitWidth, scale*16, scale*7, false);
		slitBox = csg.translate3DZ(scale).transform(slitBox);
		tip = csg.difference3D(tip, slitBox);
		Geometry3D widthBox = csg.box3D(scale*16, scale*axleWidth, scale*8, false);
		return csg.intersection3D(tip, widthBox);
	}

	public Geometry3D clickerBase()
	{
		Geometry2D profile = clickerBaseProfile(0, 0.2, clickTolerance);
		Geometry3D base = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D widthBox = csg.box3D(scale*16, scale*axleWidth, scale*8, false);
		base = csg.intersection3D(base, widthBox);
		return base;
	}

	public Geometry3D clickerBaseCutout()
	{
		Geometry2D profile = clickerBaseProfile(clickerBaseCutoutDiameterAdjust, 0, 0);
		Geometry3D base = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		Geometry3D widthBox = csg.box3D(scale*16, scale*axleWidth+widthCutoutAdjust, scale*8, false);
		return csg.intersection3D(base, widthBox);
	}

	public Geometry3D axleSection(double length)
	{
		Geometry3D axleSection = flatCylinder(scale*8, scale*axleWidth, length);
		return axleSection;
	}

	public Geometry3D axleCutout(int length)
	{
		Geometry2D profile = axleHoleProfile(length);
		Geometry3D axleCutout = csg.rotateExtrude(csg.degrees(360), angularResolution, profile);
		return axleCutout;
	}

	private Geometry3D flatCylinder(double diameter, double width, double length)
	{
		Geometry3D axleShape = csg.cylinder3D(diameter, length, angularResolution, false);
		Geometry3D widthBox = csg.box3D(diameter+2, width, length, false);
		return csg.intersection3D(axleShape, widthBox);
	}

	private Geometry2D axleHoleProfile(int length)
	{
		if(length < 2) throw new IllegalArgumentException("Length must be at least 2");
		Geometry2D bottom = clickHoleProfile();
		Geometry2D top = clickHoleProfile();
		top = csg.translate2DY(scale * (length - 1.0) * 8 ).transform(top);
		if(length == 2)
		{
			return csg.union2D(bottom, top);
		}
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0,scale*8));
		points.add(csg.vector2D(scale*5.5, scale*8));
		points.add(csg.vector2D(scale*5.5, scale*8*(length-1)));
		points.add(csg.vector2D(0, scale*8*(length-1)));
		Geometry2D mid = csg.polygon2D(points);
		return csg.union2D(bottom, mid, top);
	}

	private Geometry2D clickHoleProfile()
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0,0));
		points.add(csg.vector2D(scale*5.5, 0));
		points.add(csg.vector2D(scale*5.5, scale*2));
		points.add(csg.vector2D(scale*4.5, scale*3));
		points.add(csg.vector2D(scale*4.5, scale*5));
		points.add(csg.vector2D(scale*5.5, scale*6));
		points.add(csg.vector2D(scale*5.5, scale*8));
		points.add(csg.vector2D(0, scale*8));
		return csg.polygon2D(points);
	}

	private Geometry2D clickerTipProfile()
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0,0));
		points.add(csg.vector2D(scale*5.5-clickTolerance, 0));
		points.add(csg.vector2D(scale*5.5-clickTolerance, scale*2));
		points.add(csg.vector2D(scale*4.5-clickTolerance, scale*3));
		points.add(csg.vector2D(scale*4.5-clickTolerance, scale*5));
		points.add(csg.vector2D(scale*5.3-clickTolerance, scale*5.8));
		points.add(csg.vector2D(scale*5.3-clickTolerance, scale*6.6));
		points.add(csg.vector2D(scale*4.5-clickTolerance, scale*7.4));
		points.add(csg.vector2D(0, scale*7.4));
		return csg.polygon2D(points);
	}

	private Geometry2D clickerTipAxleProfile()
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0,0));
		points.add(csg.vector2D(scale*4.5-clickTolerance, 0));
		points.add(csg.vector2D(scale*4.5-clickTolerance, scale*5));
		points.add(csg.vector2D(scale*5.3-clickTolerance, scale*5.8));
		points.add(csg.vector2D(scale*5.3-clickTolerance, scale*6.6));
		points.add(csg.vector2D(scale*4.5-clickTolerance, scale*7.4));
		points.add(csg.vector2D(0, scale*7.4));
		return csg.polygon2D(points);
	}

	private Geometry2D clickerBaseProfile(double diameterAdjust, double bottomAdjust, double clickTolerance)
	{
		List<Vector2D> points = new ArrayList<>();
		points.add(csg.vector2D(0,bottomAdjust));
		points.add(csg.vector2D(scale*6.5-0.5*diameterAdjust-clickTolerance, bottomAdjust));
		points.add(csg.vector2D(scale*6.5-0.5*diameterAdjust-clickTolerance, scale*2));
		points.add(csg.vector2D(scale*5.5-0.5*diameterAdjust-clickTolerance, scale*3));
		points.add(csg.vector2D(scale*5.5-0.5*diameterAdjust-clickTolerance, scale*4));
		points.add(csg.vector2D(0, scale*4));
		return csg.polygon2D(points);
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		ClickBuildFeatures cbf = new ClickBuildFeatures(csg, 1.0, 128);
		//csg.view(cbf.axleCutout(6));
		//csg.view(csg.rotate3DX(csg.degrees(90)).transform(cbf.flatCylinder(8, 6.4, 32)));
		csg.view(cbf.clickerTipProfile());
	}
}
