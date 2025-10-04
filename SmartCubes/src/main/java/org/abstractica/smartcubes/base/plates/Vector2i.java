package org.abstractica.smartcubes.base.plates;

public class Vector2i
{
	public final int x;
	public final int y;

	public Vector2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Vector2i that = (Vector2i) o;

		if (x != that.x) return false;
		return y == that.y;
	}

	@Override
	public int hashCode()
	{
		int result = x;
		result = 31 * result + y;
		return result;
	}
}
