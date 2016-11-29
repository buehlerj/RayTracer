import Jama.Matrix;

public class Ray {
	Matrix location;
	Matrix direction;

	Double bestT = null;
	Sphere bestSphere = null;
	Matrix bestPt = null;

	Ray() {
		location = new Matrix(1, 3);
		direction = new Matrix(1, 3);
	}

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

	public Double getBestT() {
		return bestT;
	}

	public void setBestT(Double bestT) {
		this.bestT = bestT;
	}

	public Sphere getBestSphere() {
		return bestSphere;
	}

	public void setBestSphere(Sphere bestSphere) {
		this.bestSphere = bestSphere;
	}

	public Matrix getBestPt() {
		return bestPt;
	}

	public void setBestPt(Matrix bestPt) {
		this.bestPt = bestPt;
	}

	public String toString() {
		return direction.get(0, 0) + ", " + direction.get(1, 0) + ", " + direction.get(2, 0);
	}
}
