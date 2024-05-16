package org.abstractica.smartcubes.base;

import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;

public interface Part
{
	default String getName()
	{
		return this.getClass().getSimpleName();
	}

	Geometry3D getGeometry(JavaCSG csg, double scale, int angularResolution);
}
