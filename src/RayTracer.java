import java.util.ArrayList;

import Jama.Matrix;

public class RayTracer {
	private Camera camera = new Camera();
	private ArrayList<Model> models = new ArrayList<Model>();
	private Vertex topLeftCorner;
	private Vertex topRightCorner;
	private Vertex bottomRightCorner;
	private Vertex bottomLeftCorner;
	private ArrayList<Ray> rays = new ArrayList<>();

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

		// Get all Rays
		for (int j = 0; j < camera.getRes()[1]; j++) {
			currentLeftHeight = new Vertex(leftOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentRightHeight = new Vertex(rightOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentVector = new Vector(currentLeftHeight, currentRightHeight);
			for (int i = 0; i < camera.getRes()[0]; i++) {
				currentPoint = new Vertex(currentVector.getPoint((double) i / camera.getRes()[0]));
				ray = new Ray(new Vertex(), currentPoint);
				rays.add(ray);
			}
		}
		CramersRule();

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

	public void CramersRule() {
		Model currentModel = models.get(0);
		// Indexed by Face.
		// a: first vertex (0)
		// b: second vertex (1)
		// c: third vertex (2)
		ArrayList<Vertex> a = new ArrayList<Vertex>();
		ArrayList<Vertex> b = new ArrayList<Vertex>();
		ArrayList<Vertex> c = new ArrayList<Vertex>();
		ArrayList<Matrix> coefficients = new ArrayList<Matrix>();
		ArrayList<Matrix> betas = new ArrayList<Matrix>();
		ArrayList<Matrix> gammas = new ArrayList<Matrix>();
		ArrayList<Matrix> tValues = new ArrayList<Matrix>();
		Matrix coefficient;
		Matrix xMatrix;
		Matrix yMatrix;
		Matrix zMatrix;
		double[][] empty = new double[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

		for (Face f : currentModel.getFaces()) {
			if (f.getNumberOfVertices() > 3)
				System.err.println("ERROR: Face has more than 3 vertices");
			// For all vertices on this face
			int vertexIndex = 0;
			for (int i : f.getVertexIndices()) {
				Vertex currentVertex = currentModel.getVertices().get(i);
				if (vertexIndex == 0)
					a.add(currentVertex);
				else if (vertexIndex == 1)
					b.add(currentVertex);
				else if (vertexIndex == 2)
					c.add(currentVertex);
				vertexIndex++;
			}
		}

		int rayIndex = 0;
		boolean hitFace = false;
		for (Ray r : rays) {
			hitFace = false;
			int faceIndex = 0;
			for (Face f : currentModel.getFaces()) {
				if (hitFace == true)
					break;
				double a1 = a.get(faceIndex).getX() - b.get(faceIndex).getX();
				double a2 = a.get(faceIndex).getY() - b.get(faceIndex).getY();
				double a3 = a.get(faceIndex).getZ() - b.get(faceIndex).getZ();
				double b1 = a.get(faceIndex).getX() - c.get(faceIndex).getX();
				double b2 = a.get(faceIndex).getY() - c.get(faceIndex).getY();
				double b3 = a.get(faceIndex).getZ() - c.get(faceIndex).getZ();
				double c1 = r.getD().getX();
				double c2 = r.getD().getY();
				double c3 = r.getD().getZ();
				double[][] coefficientValues = new double[][]{
					{a1, b1, c1}, {a2, b2, c2}, {a3, b3, c3}
				};
				double[][] xValues = new double[][]{
					{a.get(faceIndex).getX() - r.getOrigin().getX(), b1, c1},
					{a.get(faceIndex).getY() - r.getOrigin().getY(), b2, c2},
					{a.get(faceIndex).getZ() - r.getOrigin().getZ(), b3, c3}
				};
				double[][] yValues = new double[][]{
					{a1, a.get(faceIndex).getX() - r.getOrigin().getX(), c1},
					{a2, a.get(faceIndex).getY() - r.getOrigin().getY(), c2},
					{a3, a.get(faceIndex).getZ() - r.getOrigin().getZ(), c3}
				};
				double[][] zValues = new double[][]{
					{a1, b1, a.get(faceIndex).getX() - r.getOrigin().getX()},
					{a2, b2, a.get(faceIndex).getY() - r.getOrigin().getY()},
					{a3, b3, a.get(faceIndex).getZ() - r.getOrigin().getZ()}
				};
				coefficient = new Matrix(coefficientValues);
				xMatrix = new Matrix(xValues);
				yMatrix = new Matrix(yValues);
				zMatrix = new Matrix(zValues);
				double determinatnCoefficient = coefficient.det();
				double beta = xMatrix.det() / determinatnCoefficient;
				double gamma = yMatrix.det() / determinatnCoefficient;
				double t = zMatrix.det() / determinatnCoefficient;
//				System.out.println(beta + ":" + gamma + ":" + t);
				if (beta >= 0 && gamma >= 0 && (beta + gamma) >= 1 && t > 0) {
					coefficients.add(coefficient);
					betas.add(xMatrix);
					gammas.add(yMatrix);
					tValues.add(zMatrix);
					hitFace = true;
					break;
				}
				faceIndex++;
			}
			if (hitFace == false) {
				coefficients.add(new Matrix(empty));
				betas.add(new Matrix(empty));
				gammas.add(new Matrix(empty));
				tValues.add(new Matrix(empty));
			}
			rayIndex++;
		}
		System.out.println("Resolution: " + camera.getRes()[0] + ", " + camera.getRes()[1]);
		System.out.println("Rays: " + rays.size());
		System.out.println("Coefficients: " + coefficients.size());
		System.out.println("Betas: " + betas.size());
		System.out.println("Gammas: " + gammas.size());
		System.out.println("tValues: " + tValues.size() + "\n\n\n");
	}

	public double getDistance(Ray ray) {
		return 0;
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
