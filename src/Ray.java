import Jama.Matrix;

public class Ray {
	Matrix location;
	Matrix direction;

	Ray(Matrix location, Matrix direction) {
		this.location = location;
		this.direction = direction;
	}

	public Matrix getLocation() {
		return location;
	}

	public void setLocation(Matrix location) {
		this.location = location;
	}

	public Matrix getDirection() {
		return direction;
	}

	public void setDirection(Matrix direction) {
		this.direction = direction;
	}

	public String toString() {
		return direction.get(0, 0) + ", " + direction.get(1, 0) + ", " + direction.get(2, 0);
	}
}
