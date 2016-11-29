import Jama.Matrix;

public class Ray {
	Matrix location;
	Matrix direction;

	Double bestTSphere = null;
	Sphere bestSphere = null;
	Matrix bestPtSphere = null;

	Double bestTModel = null;
	Model bestModel = null;
	Face bestFace = null;
	Matrix bestPtModel = null;

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

	public Double getBestTSphere() {
		return bestTSphere;
	}

	public void setBestTSphere(Double bestT) {
		this.bestTSphere = bestT;
	}

	public Sphere getBestSphere() {
		return bestSphere;
	}

	public void setBestSphere(Sphere bestSphere) {
		this.bestSphere = bestSphere;
	}

	public Matrix getBestPtSphere() {
		return bestPtSphere;
	}

	public void setBestPtSphere(Matrix bestPt) {
		this.bestPtSphere = bestPt;
	}

	public Double getBestTModel() {
		return bestTModel;
	}

	public void setBestTModel(Double bestTModel) {
		this.bestTModel = bestTModel;
	}

	public Model getBestModel() {
		return bestModel;
	}

	public void setBestModel(Model bestModel) {
		this.bestModel = bestModel;
	}

	public Face getBestFace() {
		return bestFace;
	}

	public void setBestFace(Face bestFace) {
		this.bestFace = bestFace;
	}

	public Matrix getBestPtModel() {
		return bestPtModel;
	}

	public void setBestPtModel(Matrix bestPtModel) {
		this.bestPtModel = bestPtModel;
	}

	public String toString() {
		return direction.get(0, 0) + ", " + direction.get(1, 0) + ", " + direction.get(2, 0);
	}
}
