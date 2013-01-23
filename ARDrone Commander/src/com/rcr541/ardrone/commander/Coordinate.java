package com.rcr541.ardrone.commander;

public class Coordinate {

	public int lat[] = new int[3];
	public char latdir;
	public int lon[] = new int[3];
	public char londir;
	public int latE6 = 0;
	public int lonE6 = 0;

	public Coordinate() {

	}

	public Coordinate(int x, int y) {
		latE6 = x;
		lonE6 = y;
	}

	public Coordinate(int d1, int m1, int s1, char z1, int d2, int m2, int s2,
			char z2) {
		lat[0] = d1;
		lat[1] = m1;
		lat[2] = s1;
		latdir = z1;
		lon[0] = d2;
		lon[1] = m2;
		lon[2] = s2;
		londir = z2;
	}

	public int coordtoE6(int d, int m, int s, char z) {
		int x = d;
		x += ((m * 60 + s) / 3600);

		if (z == 'N' || z == 'E') {
			return x;
		} else {
			return (-1 * x);
		}

	}

	public void E6tolat(int x) {
		if (x > 0)
			latdir = 'N';
		else
			latdir = 'S';
		double z = (double) x;
		z = z / 1E6;
		lat[0] = (int) z;
		double temp = (z - (int) z);
		temp = temp * 60;
		lat[1] = (int) temp;
		temp = temp - (int) temp;
		temp = temp * 60;
		lat[2] = (int) temp;

	}

	public void E6tolon(int x) {
		if (x > 0)
			londir = 'E';
		else
			londir = 'W';
		double z = (double) x;
		z = z / 1E6;
		lon[0] = (int) z;
		double temp = (z - (int) z);
		temp = temp * 60;
		lon[1] = (int) temp;
		temp = temp - (int) temp;
		temp = temp * 60;
		lon[2] = (int) temp;

	}
}
