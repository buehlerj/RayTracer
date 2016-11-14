import Jama.Matrix;

public class Sphere {
	double x;
	double y;
	double z;
	double radius;
	Matrix color;

	public Sphere(double x, double y, double z, double radius, double red, double green, double blue) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.color = new Matrix(new double[][] { {red}, {green}, {blue} });
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
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
		sphere += x + " ";
		sphere += y + " ";
		sphere += z + " ";
		sphere += radius + " ";
		sphere += color.get(0, 0) + " ";
		sphere += color.get(1, 0) + " ";
		sphere += color.get(2, 0);
		return sphere;
	}
}
