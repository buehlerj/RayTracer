import Jama.Matrix;

public class Sphere {
	public static int counter = 0;
	int index;
	Matrix coordinates;
	double radius;
	Matrix color;
	Material material;

	public Sphere(double x, double y, double z, double radius, double red, double green, double blue, Matrix ambient) {
		this.coordinates = new Matrix(new double[][] { { x }, { y }, { z } });
		this.radius = radius;
		this.color = new Matrix(new double[][] { { red }, { green }, { blue } });
		index = counter;
		counter++;
		material = new Material("Sphere " + Integer.toString(index), ambient);
	}

	public Matrix getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Matrix coordinates) {
		this.coordinates = coordinates;
	}

	public double getX() {
		return coordinates.get(0, 0);
	}

	public void setX(double x) {
		coordinates.set(0, 0, x);
	}

	public double getY() {
		return coordinates.get(1, 0);
	}

	public void setY(double y) {
		coordinates.set(1, 0, y);
	}

	public double getZ() {
		return coordinates.get(2, 0);
	}

	public void setZ(double z) {
		coordinates.set(2, 0, z);
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

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String toString() {
		String sphere = "";
		sphere += Utils.MatrixToString(coordinates);
		sphere += radius + " ";
		sphere += color.get(0, 0) + " ";
		sphere += color.get(1, 0) + " ";
		sphere += color.get(2, 0);
		return sphere;
	}
}
