package org.abstractica.sourced.motors.dc;


import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class TTMotor
{
	private final JavaCSG csg;
	private static final double CORNER_D = 8;
	private static final double WIDTH = 18.7;
	private static final double HEIGHT = 22.3;
	private static final double CYLINDER_WIDTH_ADJUST = 1.4;
	private static final double AXLE_TO_FRONT = 11.2;
	private static final double AXLE_TO_BACK = 26.1;
	private static final double MOTOR_HOLDER_LENGTH = 30;
	private static final double MOTOR_SIDE_ROOM = 3;
	private static final double AXLE_TO_SIDE_KNOB = 11;
	private static final double SIDE_KNOB_HEIGHT = 2;
	private static final double SIDE_KNOB_DIAMETER = 5;
	private final double adjustWidth;
	private final double adjustHeight;


	private final boolean doubleAxle;

	public TTMotor(JavaCSG csg,
	               boolean doubleAxle,
	               double adjustWidth,
	               double adjustHeight)
	{
		this.csg = csg;
		this.adjustWidth = adjustWidth;
		this.adjustHeight = adjustHeight;
		this.doubleAxle = doubleAxle;
	}

	public Geometry3D getAxleCutout()
	{
		Geometry3D cyl = csg.cylinder3D(5.35, 8, 1024, true);
		Geometry3D box = csg.box3D(3.8, 6, 10, true);
		Geometry3D axleCutout = csg.intersection3D(cyl, box);
		Geometry3D res = csg.translate3D(0, 0, 3.999).transform(axleCutout);
		return csg.cache(res);
	}

	public Geometry3D getAxle()
	{
		Geometry3D cyl = csg.cylinder3D(5.35, 7.8, 1024, true);
		Geometry3D box = csg.box3D(3.8, 6, 10, true);
		Geometry3D axleCutout = csg.intersection3D(cyl, box);
		Geometry3D res = csg.translate3D(0, 0, 3.89).transform(axleCutout);
		return csg.cache(res);
	}

	public Geometry3D getCutout()
	{
		Geometry3D res = csg.union3D
			(
				gearBox(),
				gearFrontKnob(),
				gearSideKnob(),
				axle(),
				motorCylinder(),
				motorSideRoom()
			);
		return csg.cache(res);
	}

	private Geometry3D gearBox()
	{
		double adjXY = 0;
		double adjZ = 0;
		Geometry3D corner = csg.cylinder3D(CORNER_D, WIDTH + 2 * adjXY, 64, true);
		corner = csg.rotate3DY(csg.degrees(90)).transform(corner);

		double cornerTY = AXLE_TO_FRONT - 0.5 * CORNER_D + adjXY;
		double cornerTZ = 0.5 * (HEIGHT - CORNER_D) + adjZ;

		Geometry3D tCA = csg.translate3D(0, cornerTY, cornerTZ).transform(corner);
		Geometry3D tCB = csg.translate3D(0, cornerTY, -cornerTZ).transform(corner);

		Geometry3D back = csg.box3D(WIDTH + 2 * adjXY, 2, HEIGHT + 2 * adjZ, true);
		double backTY = -AXLE_TO_BACK + 1 - adjXY;
		Geometry3D tB = csg.translate3D(0, backTY, 0).transform(back);
		return csg.hull3D(tCA, tCB, tB);
	}

	private Geometry3D axle()
	{
		Geometry3D cyl;
		if (doubleAxle)
		{
			cyl = csg.cylinder3D(16, 40, 128, true);
		} else
		{
			cyl = csg.cylinder3D(16, 20, 128, false);
		}
		return csg.rotate3DY(csg.degrees(90)).transform(cyl);
	}

	private Geometry3D motorCylinder()
	{
		Geometry3D cyl = csg.cylinder3D(
			HEIGHT + 0.8 + adjustHeight,
			MOTOR_HOLDER_LENGTH, 128, true);
		cyl = csg.rotate3DX(csg.degrees(90)).transform(cyl);
		Geometry3D box = csg.box3D(
			WIDTH -CYLINDER_WIDTH_ADJUST + adjustWidth,
			MOTOR_HOLDER_LENGTH,
			HEIGHT + adjustHeight, true);
		box = csg.translate3DX(-0.5*CYLINDER_WIDTH_ADJUST).transform(box);
		Geometry3D intersect = csg.intersection3D(cyl, box);
		return csg.translate3D
			(
				0,
				-0.5 * MOTOR_HOLDER_LENGTH - AXLE_TO_BACK + 0.001,
				0
			).transform(intersect);
	}

	private Geometry3D motorSideRoom()
	{
		Geometry3D box = csg.box3D
			(
				WIDTH + adjustWidth + 2 * MOTOR_SIDE_ROOM,
				MOTOR_HOLDER_LENGTH - 3,
				14,
				true
			);
		return csg.translate3D(
			0,
			-0.5 * (MOTOR_HOLDER_LENGTH - 3) - AXLE_TO_BACK - 3,
			0).transform(box);
	}

	private Geometry3D gearFrontKnob()
	{
		return csg.translate3D
			(
				-0.5*CYLINDER_WIDTH_ADJUST,
				0.5 * 6.5 - 1 + AXLE_TO_FRONT,
				0
			).transform(csg.box3D(3, 5, 5.5, true));
	}

	private Geometry3D gearSideKnob()
	{
		return csg.translate3D
			(
				0.5 * (WIDTH + SIDE_KNOB_HEIGHT + 1) - 1,
				-AXLE_TO_SIDE_KNOB,
				0
			).transform
			(
				csg.rotate3DY(csg.degrees(90)).transform
					(
						csg.cylinder3D
							(
								SIDE_KNOB_DIAMETER,
								SIDE_KNOB_HEIGHT + 1,
								32,
								true
							)
					)
			);
	}

	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createNoCaching();
		TTMotor motor = new TTMotor(csg, true, 0, 0.8);
		Geometry3D cutout = motor.getCutout();
		cutout = csg.translate3DY(16).transform(cutout);
		Geometry3D box1 = csg.box3D(24, 88, 12, false);
		box1 = csg.translate3DZ(-12).transform(box1);
		Geometry3D res1 = csg.difference3D(box1, cutout);
		res1 = csg.translate3DX(20).transform(res1);
		Geometry3D box2 = csg.box3D(24, 88, 12, false);
		Geometry3D res2 = csg.difference3D(box2, cutout);
		res2 = csg.rotate3DY(csg.degrees(180)).transform(res2);
		res2 = csg.translate3DX(-20).transform(res2);
		Geometry3D res = csg.union3D(res1, res2);
		csg.view(res);
	}
}
