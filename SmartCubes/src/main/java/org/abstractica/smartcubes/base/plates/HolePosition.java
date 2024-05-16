package org.abstractica.smartcubes.base.plates;

public class HolePosition
{
	public final int x;
	public final int y;

	public HolePosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HolePosition that = (HolePosition) o;

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
