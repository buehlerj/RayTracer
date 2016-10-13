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

	public void scale(Vertex other_vector) {
		x *= other_vector.x;
		y *= other_vector.y;
		z *= other_vector.z;
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

	public String toStringVertex() {
		DecimalFormat df = new DecimalFormat("#.######");
		return "(" + df.format(x) + ", " + df.format(y) + ", " + df.format(z) + ")";
	}
}
