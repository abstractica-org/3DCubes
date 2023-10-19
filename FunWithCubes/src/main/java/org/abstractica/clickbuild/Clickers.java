package org.abstractica.clickbuild;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

import java.util.ArrayList;
import java.util.List;

public class Clickers
{
	private final JavaCSG csg;
	private final double scale;
	private final int angularResolution;
	private final ClickBuildFeatures cbf;

	public Clickers(JavaCSG csg, double scale, int angularResolution)
	{
		this.csg = csg;
		this.scale = scale;
		this.angularResolution = angularResolution;
		this.cbf = new ClickBuildFeatures(csg, scale, angularResolution);
	}

	public Geometry3D doubleClicker()
	{
		Geometry3D tip1 = cbf.clickerTip();
		Geometry3D tip2 = csg.mirror3D(0,0,1).transform(tip1);
		Geometry3D res = csg.union3D(tip1, tip2);
		return res;
	}

	public Geometry3D baseClicker()
	{
		Geometry3D base = cbf.clickerBase();
		Geometry3D tip = cbf.clickerTip();
		tip = csg.translate3D(0,0,scale*4).transform(tip);
		Geometry3D clicker = csg.union3D(base, tip);
		return clicker;
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		Clickers clickers = new Clickers(csg, 1.0, 128);
		//csg.view(csg.rotate3DX(csg.degrees(90)).transform(clickers.baseClicker()));
		csg.view(csg.rotate3DX(csg.degrees(90)).transform(clickers.doubleClicker()));
	}
}
