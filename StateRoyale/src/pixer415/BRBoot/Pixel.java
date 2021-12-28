package pixer415.BRBoot;

import java.awt.Color;

// Class for a pixel.
public class Pixel {
	private int x = 0;
	private int y = 0;
	private Color geo = null;
	private Color elv = null;
	private double geoSim = 0.0;
	private double elvSim = 0.0;
	private int water = 0;
	private boolean isBorder = false;
	private boolean isBorderWithNull = false;
	private boolean isBorderWithOL = false;
	private Color deef = new Color(128, 128, 128);
	private int region = -1;
	public Pixel() {
		//nothing
	}
	public Pixel(String line) {
		String[] elements = line.split("\",\"");
		setX(Integer.parseInt(elements[0].split("x")[0]));
		setY(Integer.parseInt(elements[0].split("x")[1]));
		setGeo(Integer.parseInt(elements[1]));
		setElv(Integer.parseInt(elements[2]));
		setWater(Integer.parseInt(elements[3]));
	}
	public Pixel(int x, int y, int geo, int elv) {
		setX(x);
		setY(y);
		setGeo(geo);
		setElv(elv);
		setWater(0);
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Color getGeo() {
		return geo;
	}
	public void setGeo(int geo) {
		this.geo = new Color(geo);
		setGeoSim(findSim(this.geo, deef));
	}
	public Color getElv() {
		return elv;
	}
	public void setElv(int elv) {
		this.elv = new Color(elv);
		setElvSim(findSim(this.elv, deef));
	}
	public double getGeoSim() {
		return geoSim;
	}
	private void setGeoSim(double geoSim) {
		this.geoSim = geoSim;
	}
	public double getElvSim() {
		return elvSim;
	}
	private void setElvSim(double elvSim) {
		this.elvSim = elvSim;
	}
	public static double findSim(Color one, Color two) {
		int diff = Math.abs(two.getRed() - one.getRed()) + Math.abs(two.getGreen() - one.getGreen()) + Math.abs(two.getBlue() - one.getBlue());
		double p = (double)diff / 755.0;
	    return p;
	}
	public int getWater() {
		return water;
	}
	public void setWater(int water) {
		this.water = water;
	}
	public boolean isBorder() {
		return isBorder;
	}
	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}
	public boolean isBorderWithNull() {
		return isBorderWithNull;
	}
	public void setBorderWithNull(boolean isBorderWithNull) {
		this.isBorderWithNull = isBorderWithNull;
	}
	public int getRegion() {
		return region;
	}
	public void setRegion(int region) {
		this.region = region;
	}
	public boolean isBorderWithOL() {
		return isBorderWithOL;
	}
	public void setBorderWithOL(boolean isBorderWithOL) {
		this.isBorderWithOL = isBorderWithOL;
	}
	
}
