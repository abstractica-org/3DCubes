package org.abstractica.smartcubes.bambustuff;

import org.abstractica.javacsg.Geometry2D;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;

import java.util.ArrayList;
import java.util.List;

public class NamePlate
{
	private final JavaCSG csg;
	private final double scale;
	private final int angularResolution;
	private final Features features;

	public NamePlate(JavaCSG csg, double scale, int angularResolution)
	{
		this.csg = csg;
		this.scale = scale;
		this.angularResolution = angularResolution;
		this.features = new Features(csg, scale, angularResolution);
	}

	public Geometry3D namePlate(int length, String name, boolean textOnly)
	{
		if(textOnly) return namePlateText(length, name, true);
		double l = scale*length*16 + 48;
		double letterWidth = 12;
		double letterHeight = 20;
		double textWidth = name.length()*letterWidth;
		List<Geometry3D> cutouts = new ArrayList<>();
		Geometry2D text2D = csg.text2D(name, scale*letterWidth, scale*letterHeight, angularResolution);
		text2D = csg.mirror2D(1, 0).transform(text2D);
		text2D = csg.translate2DX(textWidth*scale).transform(text2D);
		Geometry3D text3D = csg.linearExtrude(0.4,false, text2D);
		text3D = csg.translate3D(0.5*l-0.5*scale*textWidth, scale*6, 0).transform(text3D);
		cutouts.add(text3D);
		Geometry3D plate = csg.box3D(l, scale*24, scale*4, false);
		plate = csg.translate3D(0.5*l, scale*12, 0).transform(plate);
		Geometry3D balls = features.ballAddonGrid(3+length, 2);
		balls = csg.translate3D(scale*4, 0, scale*4).transform(balls);
		plate = csg.union3D(plate, balls);

		Geometry3D clickHole = features.doubleClickerBaseCutout();
		cutouts.add(csg.translate3D(scale*16, scale*12, 0).transform(clickHole));
		cutouts.add(csg.translate3D(scale*16*(length+2), scale*12, 0).transform(clickHole));
		Geometry3D res = csg.difference3D(plate, cutouts);
		return res;
	}

	private Geometry3D namePlateText(int length, String name, boolean textOnly)
	{
		double l = scale*length*16 + 48;
		double letterWidth = 12;
		double letterHeight = 20;
		double textWidth = name.length()*letterWidth;
		double adjust = textOnly ? -0.4 : 0;
		Geometry2D text2D = csg.text2D(name, scale*letterWidth, scale*letterHeight, angularResolution);
		text2D = csg.mirror2D(1, 0).transform(text2D);
		text2D = csg.translate2DX(textWidth*scale+adjust).transform(text2D);
		Geometry3D text3D = csg.linearExtrude(0.4,false, text2D);
		text3D = csg.translate3D(0.5*l-0.5*scale*textWidth, scale*6, 0).transform(text3D);
		return text3D;
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		NamePlate namePlate = new NamePlate(csg, 1, 128);
		Geometry3D geometry = namePlate.namePlate(5, "P1S 11", false);
		csg.view(geometry);
	}
}
