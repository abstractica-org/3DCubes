package org.abstractica.smartcubes.bambustuff;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;

import java.util.ArrayList;
import java.util.List;

public class CardBoardSpoolAdapter3DE
{
	private final JavaCSG csg;
	private final int angularResolution;

	public CardBoardSpoolAdapter3DE(JavaCSG csg, int angularResolution)
	{
		this.csg = csg;
		this.angularResolution = angularResolution;
	}

	public Geometry3D spoolAdapter()
	{
		Geometry3D cutOut1 = csg.cylinder3D(185, 2, angularResolution, false);
		Geometry3D cutOut2 = csg.cylinder3D(195, 4, angularResolution, false);
		cutOut2 = csg.translate3DZ(2).transform(cutOut2);
		Geometry3D cutOut3 = csg.cone3D(195, 193, 2, angularResolution, false);
		cutOut3 = csg.translate3DZ(6).transform(cutOut3);
		Geometry3D cutOut4 = csg.cone3D(193, 195, 2, angularResolution, false);
		cutOut4 = csg.translate3DZ(8).transform(cutOut4);
		Geometry3D cutOut = csg.union3D(cutOut1, cutOut2, cutOut3, cutOut4);
		Geometry3D spoolAdapter = csg.cylinder3D(200, 10, angularResolution, false);
		spoolAdapter = csg.difference3D(spoolAdapter, cutOut);
		return spoolAdapter;
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		CardBoardSpoolAdapter3DE adapterFactory = new CardBoardSpoolAdapter3DE(csg, 512);
		Geometry3D spoolAdapter = adapterFactory.spoolAdapter();
		csg.view(spoolAdapter);
	}
}
