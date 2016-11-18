import Jama.Matrix;

public class Light {
	Matrix cordinates;
	Matrix color;

	public Light(double x, double y, double z, double w, double red, double green, double blue) {
		this.cordinates = new Matrix(new double[][] { { x }, { y }, { z }, { w } });
		this.color = new Matrix(new double[][] { { red }, { green }, { blue } });
	}

	public Matrix getCordinates() {
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

	public void setW(double w) {
		cordinates.set(3, 0, w);
	}

	public Matrix getColor() {
		return color;
	}

	public void setColor(Matrix color) {
		this.color = color;
	}

	public String toString() {
		String light = "";
		light += Utils.MatrixToString(cordinates);
		light += color.get(0, 0) + " ";
		light += color.get(1, 0) + " ";
		light += color.get(2, 0);
		return light;
	}
}
