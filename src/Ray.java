import Jama.Matrix;

public class Ray {
	Matrix direction;

	Ray(Matrix direction) {
		this.direction = direction;
	}

	public Matrix getDirection() {
		return direction;
	}

	public void setDirection(Matrix new_D) {
		direction = new_D;
	}

	public String toString() {
		return direction.get(0, 0) + ", " + direction.get(1, 0) + ", " + direction.get(2, 0);
	}
}
