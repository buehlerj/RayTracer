import Jama.Matrix;

public class Light {
	double x;
	double y;
	double z;
	double w;
	Matrix color;

	public Light(double x, double y, double z, double w, double red, double green, double blue) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
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

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public Matrix getColor() {
		return color;
	}

	public void setColor(Matrix color) {
		this.color = color;
	}

	public String toString() {
		String light = "";
		light += x + " ";
		light += y + " ";
		light += z + " ";
		light += w + " ";
		light += color.get(0, 0) + " ";
		light += color.get(1, 0) + " ";
		light += color.get(2, 0);
		return light;
	}
}
