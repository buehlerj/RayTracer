import Jama.Matrix;

public class Sphere {
	Matrix cordinates;
	double radius;
	Matrix color;

	public Sphere(double x, double y, double z, double radius, double red, double green, double blue) {
		this.cordinates = new Matrix(new double[][] { { x }, { y }, { z } });
		this.radius = radius;
		this.color = new Matrix(new double[][] { { red }, { green }, { blue } });
	}

	public Matrix getCoridnates() {
		return cordinates;
	}

	public void setCordinates(Matrix cordinates) {
		this.cordinates = cordinates;
	}

	public void setX(double x) {
		cordinates.set(0, 0, x);
	}

	public void setY(double y) {
		cordinates.set(1, 0, y);
	}

	public void setZ(double z) {
		cordinates.set(2, 0, z);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Matrix getColor() {
		return color;
	}

	public void setColor(Matrix color) {
		this.color = color;
	}

	public String toString() {
		String sphere = "";
		sphere += Utils.MatrixToString(cordinates);
		sphere += radius + " ";
		sphere += color.get(0, 0) + " ";
		sphere += color.get(1, 0) + " ";
		sphere += color.get(2, 0);
		return sphere;
	}
}
