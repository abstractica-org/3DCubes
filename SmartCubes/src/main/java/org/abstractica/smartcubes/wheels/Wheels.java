package org.abstractica.smartcubes.wheels;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;

import java.util.ArrayList;
import java.util.List;

public class Wheels
{
	private final JavaCSG csg;
	private final int angularResolution;

	public Wheels(JavaCSG csg, int angularResolution)
	{
		this.csg = csg;
		this.angularResolution = angularResolution;
	}

	public Geometry3D tire(double height, double outerDiameter, double thickness, double slitSize, double slitWidth)
	{
		double h = (height - 2*slitSize - slitWidth)/2;
		double d1 = outerDiameter - 2*thickness;
		double d2 = outerDiameter - 2*thickness-2*slitSize;
		List<Geometry3D> cutouts = new ArrayList<>();
		Geometry3D cutout1 = csg.cylinder3D(d1, h, angularResolution, false);
		cutouts.add(cutout1);
		Geometry3D cutout2 = csg.cone3D(d1, d2, slitSize, angularResolution, false);
		cutout2 = csg.translate3DZ(h).transform(cutout2);
		cutouts.add(cutout2);
		Geometry3D cutout3 = csg.cylinder3D(d2, slitWidth, angularResolution, false);
		cutout3 = csg.translate3DZ(h+slitSize).transform(cutout3);
		cutouts.add(cutout3);
		Geometry3D cutout4 = csg.cone3D(d2, d1, slitSize, angularResolution, false);
		cutout4 = csg.translate3DZ(h+slitSize+slitWidth).transform(cutout4);
		cutouts.add(cutout4);
		Geometry3D cutout5 = csg.cylinder3D(d1, h, angularResolution, false);
		cutout5 = csg.translate3DZ(h+slitSize+slitWidth+slitSize).transform(cutout5);
		cutouts.add(cutout5);

		Geometry3D tire = csg.cylinder3D(outerDiameter, height, angularResolution, false);
		tire = csg.difference3D(tire, cutouts);
		return tire;
	}

	public Geometry3D wheel(double height, double outerDiameter, double thickness, double slitSize, double slitWidth)
	{
		List<Geometry3D> cutouts = new ArrayList<>();
		Geometry3D tire = tire(height, outerDiameter, thickness, slitSize, slitWidth);
		cutouts.add(tire);
		Geometry3D rimCutout = csg.cylinder3D(outerDiameter-4*thickness, height, angularResolution, false);
		cutouts.add(rimCutout);
		Geometry3D rim = csg.cylinder3D(outerDiameter, height, angularResolution, false);
		rim = csg.difference3D(rim, cutouts);
		List<Geometry3D> union = new ArrayList<>();
		union.add(rim);
		double centerDiameter = Math.sqrt(12*12+12*12)*2;
		Geometry3D center = csg.cylinder3D(centerDiameter, height, angularResolution, false);
		union.add(center);
		Geometry3D spoke = csg.box3D(outerDiameter-3*thickness, 8, height, false);
		union.add(spoke);
		for(int i = 1; i < 4; ++i)
		{
			union.add(csg.rotate3DZ(csg.degrees(45*i)).transform(spoke));
		}
		Geometry3D wheel = csg.union3D(union);
		Features features = new Features(csg, 1, angularResolution);
		List<Geometry3D> featureCutouts = new ArrayList<>();
		featureCutouts.add(features.brickAxleCutout(3));
		Geometry3D sphere = features.brickCenterSphereCutout();
		sphere = csg.translate3DZ(12).transform(sphere);
		featureCutouts.add(sphere);
		featureCutouts.add(csg.translate3D(-12,-12, 0).transform(features.ballCutoutGrid(2,2, 0)));
		featureCutouts.add(csg.translate3D(-12, -12, 0).transform(features.ballCutoutGrid(2,2, 24)));
		wheel = csg.difference3D(wheel, featureCutouts);
		return wheel;
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		Wheels wheels = new Wheels(csg, 128);
		Geometry3D tire = wheels.wheel(24,200, 8, 4, 8);
		csg.view(tire);
	}
}
