import java.util.ArrayList;
import java.util.Collections;

import Jama.Matrix;

public class RayTracer {
	private Camera camera = new Camera();
	private ArrayList<Model> models = new ArrayList<Model>();
	private Vertex topLeftCorner;
	private Vertex topRightCorner;
	private Vertex bottomRightCorner;
	private Vertex bottomLeftCorner;
	private ArrayList<Ray> rays = new ArrayList<>();
	private ArrayList<Double> distances = new ArrayList<>();

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

		// Get all Rays
		for (int j = 0; j < camera.getRes()[1]; j++) {
			currentLeftHeight = new Vertex(leftOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentRightHeight = new Vertex(rightOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentVector = new Vector(currentLeftHeight, currentRightHeight);
			for (int i = 0; i < camera.getRes()[0]; i++) {
				currentPoint = new Vertex(currentVector.getPoint((double) i / camera.getRes()[0]));
				rays.add(new Ray(new Vertex(), currentPoint));
			}
		}
		System.out.println(rays.get(0).getDirection());
		CramersRule();

		// Set Each Pixel's RGB Value
		double tmin = getMin(distances);
		double tmax = Collections.max(distances);
		double distance;
		double ratio;
		double r;
		double g;
		double b;

		for (int i = 0; i < rays.size(); i++) {
			distance = distances.get(i);
			if (distance == -1)
				photo.addToPixels(new Pixel());
			else {
				ratio = 2 * (distance - tmin) / (tmax - tmin);
				r = Math.max(0, 255 * (1 - ratio));
				b = Math.max(0, 255 * (ratio - 1));
				g = 255 - b - r;
				photo.addToPixels(new Pixel(r, g, b));
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
		ArrayList<Double> betas = new ArrayList<Double>();
		ArrayList<Double> gammas = new ArrayList<Double>();
		Matrix coefficient;
		Matrix xMatrix;
		Matrix yMatrix;
		Matrix zMatrix;
		double[][] empty = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };

		for (Face f : currentModel.getFaces()) {
			if (f.getNumberOfVertices() > 3)
				System.err.println("ERROR: Face has more than 3 vertices");
			else {
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
		}

		boolean hitFace = false;
		for (Ray r : rays) {
			hitFace = false;
			for (int faceIndex = 0; currentModel.getFaces().size() < faceIndex; faceIndex++) {
				Vertex currentA = a.get(faceIndex);
				Vertex currentB = b.get(faceIndex);
				Vertex currentC = c.get(faceIndex);
				if (hitFace == true)
					break;
				double a1 = currentA.getX() - currentB.getX();
				double a2 = currentA.getY() - currentB.getY();
				double a3 = currentA.getZ() - currentB.getZ();
				double b1 = currentA.getX() - currentC.getX();
				double b2 = currentA.getY() - currentC.getY();
				double b3 = currentA.getZ() - currentC.getZ();
				double c1 = r.getDirection().getX();
				double c2 = r.getDirection().getY();
				double c3 = r.getDirection().getZ();
				double[][] coefficientValues = new double[][] { { a1, b1, c1 }, { a2, b2, c2 }, { a3, b3, c3 } };
				double[][] xValues = new double[][] {
					{ currentA.getX() - r.getOrigin().getX(), b1, c1 },
					{ currentA.getY() - r.getOrigin().getY(), b2, c2 },
					{ currentA.getZ() - r.getOrigin().getZ(), b3, c3 }
				};
				double[][] yValues = new double[][] {
					{ a1, currentA.getX() - r.getOrigin().getX(), c1 },
					{ a2, currentA.getY() - r.getOrigin().getY(), c2 },
					{ a3, currentA.getZ() - r.getOrigin().getZ(), c3 }
				};
				double[][] zValues = new double[][] {
					{ a1, b1, currentA.getX() - r.getOrigin().getX() },
					{ a2, b2, currentA.getY() - r.getOrigin().getY() },
					{ a3, b3, currentA.getZ() - r.getOrigin().getZ() }
				};
				coefficient = new Matrix(coefficientValues);
				xMatrix = new Matrix(xValues);
				yMatrix = new Matrix(yValues);
				zMatrix = new Matrix(zValues);
				double determinatnCoefficient = coefficient.det();
				double beta = xMatrix.det() / determinatnCoefficient;
				double gamma = yMatrix.det() / determinatnCoefficient;
				double t = zMatrix.det() / determinatnCoefficient;
				if (beta >= 0 && gamma >= 0 && (beta + gamma) >= 1 && t > 0) {
					System.out.println("Hit");
					coefficients.add(coefficient);
					betas.add(beta);
					gammas.add(gamma);
					distances.add(t);
					hitFace = true;
					break;
				}
			}
			if (hitFace == false) {
				coefficients.add(new Matrix(empty));
				betas.add(-1.0);
				gammas.add(-1.0);
				distances.add(-1.0);
			}
		}
		System.out.println("Resolution: " + camera.getRes()[0] + ", " + camera.getRes()[1]);
		System.out.println("Rays: " + rays.size());
		System.out.println("Coefficients: " + coefficients.size());
		System.out.println("Betas: " + betas);
		System.out.println("Gammas: " + gammas);
		System.out.println("tValues: " + distances + "\n\n\n");

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

	public double getMin(ArrayList<Double> list) {
		double min = -1;
		for (double d : list) {
			if (d != -1) {
				min = d;
				break;
			}
		}
		if (min == -1)
			return 0;
		for (double d : list) {
			if (d != -1 && d < min)
				min = d;
		}
		return min;
	}
}
