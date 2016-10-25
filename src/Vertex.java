import java.text.DecimalFormat;

public class Vertex {
	private double x = 0.0;
	private double y = 0.0;
	private double z = 0.0;

	Vertex() {
	}

	Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	Vertex(Vertex old_v) {
		this.x = old_v.getX();
		this.y = old_v.getY();
		this.z = old_v.getZ();
	}

	public void add(Vertex other_vector) {
		x += other_vector.x;
		y += other_vector.y;
		z += other_vector.z;
	}

	public void subtract(Vertex other_vector) {
		x -= other_vector.x;
		y -= other_vector.y;
		z -= other_vector.z;
	}

	public void divide(Vertex other_vector) {
		x /= other_vector.x;
		y /= other_vector.y;
		z /= other_vector.z;
	}

	public Vertex divided_by(Vertex other_vector) {
		return new Vertex(x / other_vector.getX(), y / other_vector.getY(), z / other_vector.getZ());
	}

	public void scale(Vertex other_vector) {
		x *= other_vector.x;
		y *= other_vector.y;
		z *= other_vector.z;
	}

	public void scale(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	public Vertex newScale(Vertex other_vector) {
		return new Vertex(x * other_vector.getX(), y * other_vector.getY(), z * other_vector.getZ());
	}

	public Vertex newscale(double scalar) {
		return new Vertex(x * scalar, y * scalar, z * scalar);
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

	public static double dotProduct(Vertex a, Vertex b) {
		return (a.getX() * b.getX()) + (a.getY() * b.getY()) + (a.getZ() * b.getZ());
	}

	public static Vertex crossProduct(Vertex a, Vertex b) {
		double i = (a.getY() * b.getZ()) - (a.getZ() * b.getY());
		double j = (a.getX() * b.getZ()) - (a.getZ() * b.getX());
		double k = (a.getX() * b.getY()) - (a.getY() * b.getX());
		return new Vertex(i, -j, k);
	}

	public Vertex unit() {
		double length = length();
		return new Vertex(x / length, y / length, z / length);
	}

	public void normalize() {
		Vertex unit_vector = unit();
		x = unit_vector.getX();
		y = unit_vector.getY();
		z = unit_vector.getZ();
	}

	public double length() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat("#.######");
		return df.format(x) + " " + df.format(y) + " " + df.format(z);
	}

	public String toStringVertex() {
		DecimalFormat df = new DecimalFormat("#.######");
		return "(" + df.format(x) + ", " + df.format(y) + ", " + df.format(z) + ")";
	}
}
