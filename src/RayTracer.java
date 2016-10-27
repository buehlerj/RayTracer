import java.util.ArrayList;

import Jama.Matrix;

public class RayTracer {
	private Camera camera = new Camera();
	private ArrayList<Model> models = new ArrayList<Model>();
	private Vertex topLeftCorner;
	private Vertex topRightCorner;
	private Vertex bottomRightCorner;
	private Vertex bottomLeftCorner;

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera new_camera) {
		camera = new_camera;
	}

	public ArrayList<Model> getModels() {
		return models;
	}

	public void addModel(Model m) {
		models.add(m);
	}

	public void removeModel(Model m) {
		models.remove(m);
	}

	public Picture capturePicture() {
		Picture photo = new Picture(camera.getRes()[0], camera.getRes()[1]);
		Vertex center_of_near_clipping_plane;
		int[] bounds;
		Vertex right;
		Vertex left;
		Vertex up;
		Vertex down;

		// Make the eye the center of the image
		for (Model m : models) {
			for (Vertex v : m.getVertices())
				v.subtract(camera.getEye());
		}
		camera.getLook().subtract(camera.getEye());

		// Near Clipping Plane
		center_of_near_clipping_plane = new Vertex(camera.getLook());
		center_of_near_clipping_plane.normalize();
		center_of_near_clipping_plane.scale(camera.getD());
		bounds = camera.getBounds();

		right = Vertex.crossProduct(center_of_near_clipping_plane, camera.getUp());
		right.normalize();
		left = new Vertex(right);
		right.scale(bounds[1]);
		left.scale(bounds[3]);
		up = Vertex.crossProduct(right, center_of_near_clipping_plane);
		up.normalize();
		down = new Vertex(up);
		up.scale(bounds[0]);
		down.scale(bounds[2]);

		bottomRightCorner = new Vertex(up);
		bottomRightCorner.add(left);
		bottomLeftCorner = new Vertex(up);
		bottomLeftCorner.add(right);
		topRightCorner = new Vertex(down);
		topRightCorner.add(left);
		topLeftCorner = new Vertex(down);
		topLeftCorner.add(right);

		bottomRightCorner.add(center_of_near_clipping_plane);
		bottomLeftCorner.add(center_of_near_clipping_plane);
		topRightCorner.add(center_of_near_clipping_plane);
		topLeftCorner.add(center_of_near_clipping_plane);

		// Relative Depth of Each Pixel
		getRelativeDepth(photo);

		// Re-center everything
		for (Model m : models) {
			for (Vertex v : m.getVertices())
				v.add(camera.getEye());
		}
		camera.getLook().add(camera.getEye());
		return photo;
	}

	public void getRelativeDepth(Picture photo) {
		Vector leftOfNCP = new Vector(topLeftCorner, bottomLeftCorner);
		Vector rightOfNCP = new Vector(topRightCorner, bottomRightCorner);
		Vertex currentLeftHeight;
		Vertex currentRightHeight;
		Vector currentVector;
		Vertex currentPoint;
		Ray ray;
		double tmin = 255;
		double tmax = 0;
		double distance;

		// Get Distance Specs
		for (int j = 0; j < camera.getRes()[1]; j++) {
			currentLeftHeight = new Vertex(leftOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentRightHeight = new Vertex(rightOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentVector = new Vector(currentLeftHeight, currentRightHeight);
			for (int i = 0; i < camera.getRes()[0]; i++) {
				currentPoint = new Vertex(currentVector.getPoint((double) i / camera.getRes()[0]));
				ray = new Ray(new Vertex(), currentPoint);
				distance = getDistance(ray);
				if (distance > tmax)
					tmax = distance;
				if (distance < tmin)
					tmin = distance;
			}
		}

		// Set Each Pixel's RGB Value
		double ratio;
		double r;
		double g;
		double b;
		Pixel currentPixel;
		for (int j = 0; j < camera.getRes()[1]; j++) {
			currentLeftHeight = new Vertex(leftOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentRightHeight = new Vertex(rightOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentVector = new Vector(currentLeftHeight, currentRightHeight);
			for (int i = 0; i < camera.getRes()[0]; i++) {
				currentPoint = new Vertex(currentVector.getPoint((double) i / camera.getRes()[0]));
				ray = new Ray(new Vertex(), currentPoint);
				distance = getDistance(ray);
				ratio = 2 * (distance - tmin) / (tmax - tmin);
				r = Math.max(0, 255 * (1 - ratio));
				b = Math.max(0, 255 * (ratio - 1));
				g = 255 - b - r;
				currentPixel = new Pixel(r, g, b);
				photo.addToPixels(currentPixel);
			}
		}
	}

	public double getDistance(Ray ray) {
		double distance = 0;
		Model currentModel = models.get(0);
		ArrayList<Matrix> coefficients = new ArrayList<Matrix>();
		ArrayList<Matrix> xMatrices = new ArrayList<Matrix>();
		ArrayList<Matrix> yMatrices = new ArrayList<Matrix>();
		ArrayList<Matrix> zMatrices = new ArrayList<Matrix>();
		ArrayList<Double> x;
		ArrayList<Double> y;
		ArrayList<Double> z;

		for (Face f : currentModel.getFaces()) {
			x = new ArrayList<Double>();
			y = new ArrayList<Double>();
			z = new ArrayList<Double>();
			for (int i : f.getVertexIndices()) {
				Vertex currentVertex = currentModel.getVertices().get(i);
				x.add(currentVertex.getX());
				y.add(currentVertex.getY());
				z.add(currentVertex.getZ());
			}
			// a: 0
			// b: 1
			// c: 2
			double a1 = x.get(0) - x.get(1);
			double a2 = y.get(0) - y.get(1);
			double a3 = z.get(0) - z.get(1);
			double b1 = x.get(0) - x.get(2);
			double b2 = y.get(0) - y.get(2);
			double b3 = z.get(0) - z.get(2);
			double[][] coefficientValues = new double[][] { { a1, b1, 0 }, { a2, b2, 0 }, { a3, b3, 0 } };
			coefficients.add(new Matrix(coefficientValues));
			double[][] xValues = new double[][] { { 0, b1, 0 }, { 0, b2, 0 }, { 0, b3, 0 } };
			xMatrices.add(new Matrix(xValues));
			double[][] yValues = new double[][] { { a1, 0, 0 }, { a2, 0, 0 }, { a3, 0, 0 } };
			yMatrices.add(new Matrix(yValues));
			double[][] zValues = new double[][] { { a1, b1, 0 }, { a2, b2, 0 }, { a3, b3, 0 } };
			zMatrices.add(new Matrix(zValues));
		}

		return distance;
	}

	public void determinant() {

	}

	public Model aggregateModels(RayTracer rt) {
		Model complete = new Model();
		int total_faces = 0;
		int total_vertices = 0;
		for (Model m : rt.models) {
			total_faces += m.getNumberOfFaces();
			total_vertices += m.getNumberOfVertices();
			for (Vertex v : m.getVertices())
				complete.addVertex(v);
			for (Face f : m.getFaces())
				complete.addFace(f);
		}
		complete.setNumberOfFaces(total_faces);
		complete.setNumberOfVertices(total_vertices);
		complete.setDefaultHeader();
		return complete;
	}
}
