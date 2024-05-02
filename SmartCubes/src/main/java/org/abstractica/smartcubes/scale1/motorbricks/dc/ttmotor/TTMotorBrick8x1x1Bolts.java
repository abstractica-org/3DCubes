package org.abstractica.smartcubes.scale1.motorbricks.dc.ttmotor;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;
import org.abstractica.smartcubes.base.Part;
import org.abstractica.sourced.boltsandnuts.BoltAndHexNutCutout;
import org.abstractica.sourced.motors.dc.TTMotor;

import java.util.ArrayList;
import java.util.List;

public class TTMotorBrick8x1x1Bolts implements Part
{
	public TTMotorBrick8x1x1Bolts()
	{

	}

	@Override
	public Geometry3D getGeometry(JavaCSG csg, double scale, int angularResolution)
	{
		Features features = new Features(csg, scale, angularResolution);
		TTMotor motor = new TTMotor(csg, true, 0, 0.8);
		double sizeX = 16*8+8;
		double sizeY = 24;
		double sizeZ = 24;
		Geometry3D block = csg.box3D(sizeX, sizeY, sizeZ, true);
		block = csg.translate3DX(-24).transform(block);
		Geometry3D topBlockCutout = csg.box3D(6*16-8, 24, 12, false);
		topBlockCutout = csg.translate3DX(-24).transform(topBlockCutout);
		block = csg.difference3D(block, topBlockCutout);
		//block.debugMark();
		List<Geometry3D> cutouts = new ArrayList<>();

		// Add motor cutout
		Geometry3D motorCutout = motor.getCutout();
		motorCutout = csg.rotate3DZ(csg.degrees(-90)).transform(motorCutout);
		cutouts.add(motorCutout);

		//Create bolt cutouts
		BoltAndHexNutCutout bolt = new BoltAndHexNutCutout(24, 3.4, 6, 3.5, 5.7, 4.5);
		Geometry3D boltCutout = bolt.getCutout(csg, angularResolution/2);
		boltCutout = csg.translate3DZ(-12).transform(boltCutout);
		cutouts.add(csg.translate3D(16, 6, 0).transform(boltCutout));
		cutouts.add(csg.translate3D(16, -6, 0).transform(boltCutout));
		cutouts.add(csg.translate3D(-4*16, 6, 0).transform(boltCutout));
		cutouts.add(csg.translate3D(-4*16, -6, 0).transform(boltCutout));

		//Create topblock
		Geometry3D topBlock = csg.box3D(6*16-8, 24, 12, false);
		topBlock = csg.translate3DX(-24).transform(topBlock);
		topBlock = csg.difference3D(topBlock, cutouts);
		topBlock = csg.rotate3DX(csg.degrees(180)).transform(topBlock);
		topBlock = csg.translate3DY(20).transform(topBlock);

		// Create all cutout balls
		Geometry3D ballCutoutGridXYPlane = features.ballCutoutGrid(2, 2, 0);
        ballCutoutGridXYPlane = csg.translate3D(-12, -12, 0).transform(ballCutoutGridXYPlane);
		Geometry3D ballCutoutGridXZPlane = csg.rotate3DX(csg.degrees(90)).transform(ballCutoutGridXYPlane);
		Geometry3D ballCutoutGridYZPlane = csg.rotate3DY(csg.degrees(90)).transform(ballCutoutGridXYPlane);

		Geometry3D gridE = csg.translate3DX(2*16+12).transform(ballCutoutGridYZPlane);
		cutouts.add(gridE);
		Geometry3D gridW = csg.translate3DX(-5*16-12).transform(ballCutoutGridYZPlane);
		cutouts.add(gridW);
		Geometry3D gridBE = csg.translate3D(2*16, 0 , -12).transform(ballCutoutGridXYPlane);
		cutouts.add(gridBE);
		Geometry3D gridTE = csg.translate3D(2*16, 0 , 12).transform(ballCutoutGridXYPlane);
		cutouts.add(gridTE);
		Geometry3D gridBW = csg.translate3D(-5*16, 0 , -12).transform(ballCutoutGridXYPlane);
		cutouts.add(gridBW);
		Geometry3D gridTW = csg.translate3D(-5*16, 0 , 12).transform(ballCutoutGridXYPlane);
		cutouts.add(gridTW);
		Geometry3D gridFE = csg.translate3D(2*16, -12 , 0).transform(ballCutoutGridXZPlane);
		cutouts.add(gridFE);
		Geometry3D gridBaE = csg.translate3D(2*16, 12 , 0).transform(ballCutoutGridXZPlane);
		cutouts.add(gridBaE);
		Geometry3D gridFW = csg.translate3D(-5*16, -12 , 0).transform(ballCutoutGridXZPlane);
		cutouts.add(gridFW);
		Geometry3D gridBaW = csg.translate3D(-5*16, 12 , 0).transform(ballCutoutGridXZPlane);
		cutouts.add(gridBaW);

		//Create holes
		Geometry3D holeAxle = features.brickAxleCutout(3);
		holeAxle = csg.translate3DZ(-12).transform(holeAxle);
		cutouts.add(csg.translate3DX(2*16).transform(holeAxle));
		cutouts.add(csg.translate3DX(-5*16).transform(holeAxle));
		holeAxle = csg.rotate3DX(csg.degrees(90)).transform(holeAxle);
		cutouts.add(csg.translate3DX(2*16).transform(holeAxle));
		cutouts.add(csg.translate3DX(-5*16).transform(holeAxle));

		Geometry3D holeAxle2 = features.brickAxleCutout(1);
		holeAxle2 = csg.rotate3DY(csg.degrees(90)).transform(holeAxle2);
		cutouts.add(csg.translate3DX(2*16+4).transform(holeAxle2));
		cutouts.add(csg.translate3DX(-6*16+4).transform(holeAxle2));

		// Add spheres
		Geometry3D sphere = features.brickCenterSphereCutout();
		cutouts.add(csg.translate3DX(2*16).transform(sphere));
		cutouts.add(csg.translate3DX(-5*16).transform(sphere));


		//Temp code to view what is going on
		//cutouts.add(block);
		//Geometry3D res = csg.union3D(cutouts);
		Geometry3D bottomBlock = csg.difference3D(block, cutouts);
		bottomBlock = csg.translate3DY(-20).transform(bottomBlock);
		Geometry3D res = csg.union3D(topBlock, bottomBlock);
		return res;
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		TTMotorBrick8x1x1Bolts brick = new TTMotorBrick8x1x1Bolts();
		Geometry3D geometry = brick.getGeometry(csg, 1, 128);
		csg.view(geometry);
	}

}
