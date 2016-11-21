import java.util.ArrayList;
import java.util.Collections;

import Jama.Matrix;

public class RayTracer {
	private Camera camera;
	private Scene scene;
	private ArrayList<Model> models;
	private ArrayList<Ray> rays;
	private ArrayList<Double> distances;

	public RayTracer() {
		camera = new Camera();
		scene = new Scene();
		models = new ArrayList<Model>();
		rays = new ArrayList<Ray>();
		distances = new ArrayList<>();
	}

	public boolean setupCamera(String inputFileName) {
		return camera.read(inputFileName);
	}

	public boolean setupScene(String inputFileName) {
		return scene.read(inputFileName);
	}

	public Picture capturePicture() {
		Picture photo = new Picture(camera.getRes()[0], camera.getRes()[1]);

		// Add all Rays
		for (int j = camera.getRes()[1] - 1; j >= 0; j--) {
			for (int i = 0; i < camera.getRes()[0]; i++) {
				rays.add(rayPt(i, j));
			}
		}

		ArrayList<Double> tValues = new ArrayList<Double>();
		Vertex aVertex; Vertex bVertex; Vertex cVertex;
		double a1; double a2; double a3;
		double b1; double b2; double b3;
		double c1; double c2; double c3;
		double d1; double d2; double d3;
		Matrix rayOrigin; Matrix rayDirection; Matrix D; Matrix M; Matrix y; Matrix x;
		double beta; double gamma; double t;
		for (int i = 0; i < rays.size(); i++) {
			tValues.clear();
			rayOrigin = rays.get(i).getLocation();
			rayDirection = rays.get(i).getDirection();
			D = rayOrigin.minus(camera.getEye());
			D = D.timesEquals(1 / D.normF());
			c1 = D.get(0, 0);
			c2 = D.get(1, 0);
			c3 = D.get(2, 0);
			// Ray Trace on all Polygonal Models
			for (Model m : models) {
				for (Face f : m.getFaces()) {
					aVertex = m.getVertices().get(f.getVertexIndices().get(0));
					bVertex = m.getVertices().get(f.getVertexIndices().get(1));
					cVertex = m.getVertices().get(f.getVertexIndices().get(2));
					a1 = aVertex.getX() - bVertex.getX();
					a2 = aVertex.getY() - bVertex.getY();
					a3 = aVertex.getZ() - bVertex.getZ();
					b1 = aVertex.getX() - cVertex.getX();
					b2 = aVertex.getY() - cVertex.getY();
					b3 = aVertex.getZ() - cVertex.getZ();
					d1 = aVertex.getX() - rayOrigin.get(0, 0);
					d2 = aVertex.getY() - rayOrigin.get(1, 0);
					d3 = aVertex.getZ() - rayOrigin.get(2, 0);
					M = new Matrix(new double[][] { { a1, b1, c1 }, { a2, b2, c2 }, { a3, b3, c3 } });
					y = new Matrix(new double[][] { { d1 }, { d2 }, { d3 } });
					x = M.solve(y);

					beta = x.get(0, 0);
					gamma = x.get(1, 0);
					t = x.get(2, 0);
					if (beta >= 0 && gamma >= 0 && (beta + gamma) <= 1 && t > 0) {
						tValues.add(t);
					}
				}
			}

			// Ray Trace on all Sphere Models
			Matrix rayToCenter;
			double v; double bsq; double disc;
			for (Sphere s : scene.getSpheres()) {
				rayToCenter = new Matrix(new double[][] {
					{s.getX() - rayOrigin.get(0, 0)},
					{s.getY() - rayOrigin.get(1, 0)},
					{s.getZ() - rayOrigin.get(2, 0)}
				});
				v = Utils.dotProduct(rayToCenter, rayDirection);
				bsq = Utils.dotProduct(rayToCenter, rayToCenter);
				disc = Math.pow(s.getRadius(), 2) - (bsq - Math.pow(v, 2));
				if (disc > 0)
					tValues.add(disc);
			}

			// Compare all distance values
			if (tValues.isEmpty()) {
				distances.add(null);
			} else {
				distances.add(Collections.min(tValues));
			}
		}

		double min = getMin(distances);
		double max = getMax(distances);
		for (Double distance : distances) {
			if (distance == null) {
				photo.addToPixels(new Pixel());
			} else {
				double ratio = 2 * (distance - min) / (max - min);
				double r = Math.max(0, 255 * (1 - ratio));
				double b = Math.max(0, 255 * (ratio - 1));
				double g = 255 - b - r;
				photo.addToPixels(new Pixel(r, g, b));
			}
		}
		return photo;
	}

	public Picture captureLightedPicture() {
		Picture photo = capturePicture();
		
		return photo;
	}

	public Matrix pixelPt(int i, int j) {
		double width = camera.getRes()[0];
		double height = camera.getRes()[1];
		double left = camera.getBounds()[0];
		double bottom = camera.getBounds()[1];
		double right = camera.getBounds()[2];
		double top = camera.getBounds()[3];
		double px = i / (width - 1) * (right - left) + left;
		double py = j / (height - 1) * (top - bottom) + bottom;
		Matrix pixpt = camera.getEye().plus(camera.getCameraW().times(camera.getD()))
				.plus(camera.getCameraU().times(px)).plus(camera.getCameraV().times(py));
		return pixpt;
	}

	public Ray rayPt(int i, int j) {
		Matrix point = pixelPt(i, j);
		Matrix ray = point.minus(camera.getCameraV());
		ray = ray.timesEquals(1 / ray.normF());
		return new Ray(point, point.plus(ray.times(camera.getD())));
	}

	public double getMin(ArrayList<Double> list) {
		double min = 100000000;
		for (Double d : list) {
			if (d != null && d < min)
				min = d;
		}
		return min;
	}

	public double getMax(ArrayList<Double> list) {
		double max = -1;
		for (Double d : list) {
			if (d != null && d > max)
				max = d;
		}
		return max;
	}

	// GETTERS - SETTERS
	public Camera getCamera() {
		return camera;
	}

	public Scene getScene() {
		return scene;
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
}
