import Jama.Matrix;

public class Light {
	Matrix coordinates;
	Matrix color;

	public Light(double x, double y, double z, double w, double red, double green, double blue) {
		this.coordinates = new Matrix(new double[][] { { x }, { y }, { z }, { w } });
		this.color = new Matrix(new double[][] { { red }, { green }, { blue } });
	}

	public Matrix getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Matrix coordinates) {
		this.coordinates = coordinates;
	}

	public void setX(double x) {
		coordinates.set(0, 0, x);
	}

	public void setY(double y) {
		coordinates.set(1, 0, y);
	}

	public void setZ(double z) {
		coordinates.set(2, 0, z);
	}

	public void setW(double w) {
		coordinates.set(3, 0, w);
	}

	public Matrix getColor() {
		return color;
	}

	public void setColor(Matrix color) {
		this.color = color;
	}

	public String toString() {
		String light = "";
		light += Utils.MatrixToString(coordinates);
		light += color.get(0, 0) + " ";
		light += color.get(1, 0) + " ";
		light += color.get(2, 0);
		return light;
	}
}
