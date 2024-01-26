package org.abstractica.smartcubes.bambustuff;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;

import java.util.ArrayList;
import java.util.List;

public class PrinterStand
{
	private final JavaCSG csg;
	private final double scale;
	private final int angularResolution;
	private final Features features;

	public PrinterStand(JavaCSG csg, double scale, int angularResolution)
	{
		this.csg = csg;
		this.scale = scale;
		this.angularResolution = angularResolution;
		this.features = new Features(csg, scale, angularResolution);
	}

	public Geometry3D printerStand()
	{
		Geometry3D plate = csg.box3D(scale*56, scale*56, scale*16, false);
		plate = csg.translate3D(scale*28, scale*28, 0).transform(plate);
		List<Geometry3D> cutouts = new ArrayList<>();
		Geometry3D clickHole = features.singleClickHole(true);
		cutouts.add(csg.translate3D(scale*12, scale*12, 0).transform(clickHole));
		cutouts.add(csg.translate3D(scale*44, scale*12, 0).transform(clickHole));
		cutouts.add(csg.translate3D(scale*12, scale*44, 0).transform(clickHole));
		cutouts.add(csg.translate3D(scale*44, scale*44, 0).transform(clickHole));
		Geometry3D footHole = csg.cylinder3D(28, 4, angularResolution, true);
		footHole = csg.translate3D(28+5, 28+5, scale*16).transform(footHole);
		cutouts.add(footHole);
		cutouts.add(features.ballCutoutGrid(4, 4, 0));
		return csg.difference3D(plate, cutouts);
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		PrinterStand printerStand = new PrinterStand(csg, 1, 128);
		Geometry3D printerStandGeometry = printerStand.printerStand();
		csg.view(printerStandGeometry);
	}
}
