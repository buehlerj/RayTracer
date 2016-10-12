import java.text.DecimalFormat;

public class Vector {
	private double x = 0.0;
	private double y = 0.0;
	private double z = 0.0;

	Vector() {
	}

	Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void add(Vector other_Vector) {
		x += other_Vector.x;
		y += other_Vector.y;
		z += other_Vector.z;
	}

	public void subtract(Vector other_Vector) {
		x -= other_Vector.x;
		y -= other_Vector.y;
		z -= other_Vector.z;
	}

	public void divide(Vector other_Vector) {
		x /= other_Vector.x;
		y /= other_Vector.y;
		z /= other_Vector.z;
	}

	public void scale(Vector other_Vector) {
		x *= other_Vector.x;
		y *= other_Vector.y;
		z *= other_Vector.z;
	}

	public void squareRoot() {
		x = Math.sqrt(x);
		y = Math.sqrt(y);
		z = Math.sqrt(z);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat("#.######");
		return df.format(x) + " " + df.format(y) + " " + df.format(z);
	}

	public String toStringVector() {
		DecimalFormat df = new DecimalFormat("#.######");
		return "(" + df.format(x) + ", " + df.format(y) + ", " + df.format(z) + ")";
	}
}
