package org.abstractica.smartcubes.scale1.motorbricks.servo.mg996r;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import org.abstractica.smartcubes.base.Features;
import org.abstractica.smartcubes.base.Part;
import org.abstractica.smartcubes.scale1.motorbricks.dc.ttmotor.TTMotorBrick8x1x1;
import org.abstractica.sourced.boltsandnuts.BoltAndHexNutCutout;
import org.abstractica.sourced.motors.servo.MG996R;

import java.util.ArrayList;
import java.util.List;

public class MG996RBrick implements Part
{
	@Override
	public Geometry3D getGeometry(JavaCSG csg, double scale, int angularResolution)
	{
		Features features = new Features(csg, 1, angularResolution);
		return mainPart(csg, angularResolution, features);
	}

	private Geometry3D mainPart(JavaCSG csg, int angularResolution, Features features)
	{
		Geometry3D mount = mountBrick(csg, angularResolution, features);
		Geometry3D servoTop = csg.box3D(64, 24, 28, false);
		servoTop = csg.translate3DX(12).transform(servoTop);
		Geometry3D servoBottom = csg.translate3DZ(-28).transform(servoTop);
		Geometry3D bearingTop = servoTop;
		Geometry3D bearingBottom = servoBottom;
		//Bolts
		BoltAndHexNutCutout bolt = new BoltAndHexNutCutout(56, 3.4, 6, 13.5, 5.7, 14.5);
		Geometry3D boltCutout = bolt.getCutout(csg, angularResolution/2);
		List<Geometry3D> boltCutouts = new ArrayList<>();
		boltCutouts.add(csg.translate3D(-15, 8, -28).transform(boltCutout));
		boltCutouts.add(csg.translate3D(-15, -8, -28).transform(boltCutout));
		boltCutouts.add(csg.translate3D(39, 8, -28).transform(boltCutout));
		boltCutouts.add(csg.translate3D(39, -8, -28).transform(boltCutout));
		servoTop = csg.difference3D(servoTop, boltCutouts);
		servoBottom = csg.difference3D(servoBottom, boltCutouts);
		bearingTop = csg.difference3D(bearingTop, boltCutouts);
		bearingBottom = csg.difference3D(bearingBottom, boltCutouts);

		//Servo motor
		MG996R mg996r = new MG996R(csg, angularResolution);
		Geometry3D servoCutout = mg996r.motorCutout(18, 20);
		servoCutout = csg.rotate3DZ(csg.degrees(180)).transform(servoCutout);
		servoCutout = csg.rotate3DX(csg.degrees(-90)).transform(servoCutout);
		servoCutout = csg.translate3DY(6).transform(servoCutout);
		servoTop = csg.difference3D(servoTop, servoCutout);
		servoBottom = csg.difference3D(servoBottom, servoCutout);

		//Ball bearing
		Geometry3D ballBearing = ballBearingCutout(csg, angularResolution);
		bearingTop = csg.difference3D(bearingTop, ballBearing);
		bearingBottom = csg.difference3D(bearingBottom, ballBearing);

		servoBottom = csg.translate3DY(-32).transform(servoBottom);
		bearingBottom = csg.translate3DY(32).transform(bearingBottom);
		Geometry3D res = csg.union3D(mount, servoBottom, bearingBottom);

		servoTop = csg.rotate3DX(csg.degrees(180)).transform(servoTop);
		servoTop = csg.translate3DY(-60).transform(servoTop);

		bearingTop = csg.rotate3DX(csg.degrees(180)).transform(bearingTop);
		bearingTop = csg.translate3DY(60).transform(bearingTop);
		res = csg.union3D(res, servoTop, bearingTop);
		return res;
	}

	private Geometry3D ballBearingCutout(JavaCSG csg, int angularResolution)
	{
		Geometry3D ballBearing = csg.cylinder3D(22, 7, angularResolution, false);
		Geometry3D axle = csg.cylinder3D(16, 30, angularResolution, false);
		axle = csg.translate3DZ(-5).transform(axle);
		ballBearing = csg.union3D(ballBearing, axle);
		ballBearing = csg.rotate3DX(csg.degrees(90)).transform(ballBearing);
		return ballBearing;
	}

	private Geometry3D mountBrick(JavaCSG csg, int angularResolution, Features features)
	{
		List<Geometry3D> cutouts = new ArrayList<>();

		// Cutout balls
		Geometry3D zPosBalls = features.ballCutoutGrid(6, 4, 0);
		cutouts.add(zPosBalls);
		Geometry3D xNegBalls = features.ballCutoutGrid(2, 4, 0);
		xNegBalls = csg.rotate3DY(csg.degrees(90)).transform(xNegBalls);
		cutouts.add(xNegBalls);
		Geometry3D xPosBalls = csg.translate3DX(88).transform(xNegBalls);
		cutouts.add(xPosBalls);
		Geometry3D yNegBalls = features.ballCutoutGrid(6, 2, 0);
		yNegBalls = csg.rotate3DX(csg.degrees(-90)).transform(yNegBalls);
		cutouts.add(yNegBalls);
		Geometry3D yPosBalls = csg.translate3DY(56).transform(yNegBalls);
		cutouts.add(yPosBalls);

		// Center spheres
		Geometry3D centerSphere = features.brickCenterSphereCutout();
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 5; x++)
			{
				Geometry3D sphere = csg.translate3D(12 + x * 16, 12 + y * 16, -12).transform(centerSphere);
				cutouts.add(sphere);
			}
		}

		// Top axles
		Geometry3D topAxle = features.brickAxleCutout(1);
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 5; x++)
			{
				Geometry3D axle = csg.translate3D(12 + x * 16, 12 + y * 16, -8).transform(topAxle);
				cutouts.add(axle);
			}
		}

		// X axles
		Geometry3D xAxle = features.brickAxleCutout(11);
		xAxle = csg.rotate3DY(csg.degrees(90)).transform(xAxle);
		for(int y = 0; y < 3; y++)
		{
			cutouts.add(csg.translate3D(0, 12 + y * 16, -12).transform(xAxle));
		}

		// Y axles
		Geometry3D yAxle = features.brickAxleCutout(7);
		yAxle = csg.rotate3DX(csg.degrees(-90)).transform(yAxle);
		for(int x = 0; x < 5; x++)
		{
			cutouts.add(csg.translate3D(12 + x * 16, 0, -12).transform(yAxle));
		}
		Geometry3D cutout = csg.union3D(cutouts);



		// Create brick
		Geometry3D brick = csg.box3D(88, 56, 24, false);
		brick = csg.translate3D(44, 28,-24).transform(brick);
		brick = csg.difference3D(brick, cutout);
		brick = csg.translate3D(-44,-28, 16).transform(brick);
		brick = csg.rotate3DZ(csg.degrees(90)).transform(brick);
		brick = csg.rotate3DY(csg.degrees(-90)).transform(brick);
		brick = csg.translate3DX(-28).transform(brick);
		return brick;
	}


	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createDefault();
		MG996RBrick brick = new MG996RBrick();
		Geometry3D geometry = brick.getGeometry(csg, 1, 128);
		csg.view(geometry);
	}
}
