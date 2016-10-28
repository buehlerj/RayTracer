import java.util.ArrayList;

import Jama.Matrix;

public class RayTracer {
	private Camera camera;
	private ArrayList<Model> models;
	private ArrayList<Ray> rays;
	private ArrayList<Matrix> pixels;
	private ArrayList<Double> distances;
	private Matrix cameraW;
	private Matrix cameraU;
	private Matrix cameraV;

	public RayTracer() {
		camera = new Camera();
		models = new ArrayList<Model>();
		rays = new ArrayList<>();
		pixels = new ArrayList<>();
		distances = new ArrayList<>();
		cameraW = new Matrix(3, 1);
		cameraU = new Matrix(3, 1);
		cameraV = new Matrix(3, 1);
	}

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

		cameraW = camera.getEye().minus(camera.getLook());
		cameraW = cameraW.timesEquals(1 / cameraW.normF());
		cameraU = Vertex.crossProduct(camera.getUp(), cameraW);
		cameraU = cameraU.timesEquals(1 / cameraU.normF());
		cameraV = Vertex.crossProduct(cameraW, cameraU);

		// Add all Rays
		for (int i = 0; i < camera.getRes()[0]; i++) {
			for (int j = 0; j < camera.getRes()[1]; j++) {
				setPixelPointsAndRays(i, j);
			}
		}

		Model m = models.get(0);
		boolean hitFace;
		for (int i = 0; i < rays.size(); i++) {
			hitFace = false;
			for (Face f : m.getFaces()) {
				Vertex a = m.getVertices().get(f.getVertexIndices().get(0));
				Vertex b = m.getVertices().get(f.getVertexIndices().get(1));
				Vertex c = m.getVertices().get(f.getVertexIndices().get(2));
				Matrix pixel = pixels.get(i);
				Matrix D = pixels.get(i).minus(camera.getEye());
				D = D.timesEquals(1 / D.normF());
				double a1 = a.getX() - b.getX();
				double a2 = a.getY() - b.getY();
				double a3 = a.getZ() - b.getZ();
				double b1 = a.getX() - c.getX();
				double b2 = a.getY() - c.getY();
				double b3 = a.getZ() - c.getZ();
				double c1 = D.get(0, 0);
				double c2 = D.get(1, 0);
				double c3 = D.get(2, 0);
				double d1 = a.getX() - pixel.get(0, 0);
				double d2 = a.getY() - pixel.get(1, 0);
				double d3 = a.getZ() - pixel.get(2, 0);
				Matrix M = new Matrix(new double[][] { { a1, b1, c1 }, { a2, b2, c2 }, { a3, b3, c3 } });
				Matrix y = new Matrix(new double[][] { { d1 }, { d2 }, { d3 } });
				Matrix x = M.solve(y);
				double beta = x.get(0, 0);
				double gamma = x.get(1, 0);
				double t = x.get(2, 0);
				if (beta >= 0 && gamma >= 0 && (beta + gamma) <= 1 && t > 0) {
					distances.add(t);
					hitFace = true;
					break;
				}
			}
			if (hitFace == false)
				distances.add(null);
		}

		double min = getMin(distances);
		double max = getMax(distances);
		for (int i = 0; i < distances.size(); i++) {
			if (distances.get(i) == null) {
				photo.addToPixels(new Pixel());
			} else {
				double ratio = 2 * (distances.get(i) - min) / (max - min);
				double r = Math.max(0, 255 * (1 - ratio));
				double b = Math.max(0, 255 * (ratio - 1));
				double g = 255 - b - r;
				photo.addToPixels(new Pixel(r, g, b));
			}
		}
		return photo;
	}

	public Matrix pixelPt(int i, int j) {
		double width = camera.getRes()[0];
		double height = camera.getRes()[1];
		double top = camera.getBounds()[0];
		double right = camera.getBounds()[1];
		double bottom = camera.getBounds()[2];
		double left = camera.getBounds()[3];
		double px = i / (width - 1) * (right - left) + left;
		double py = j / (height - 1) * (top - bottom) + bottom;
		Matrix pixpt = camera.getEye().plus(cameraW.times(camera.getD())).plus(cameraU.times(px)).plus(cameraV.times(py));
		return pixpt;
	}

	public Ray pixelRay(int i, int j) {
		double width = camera.getRes()[0];
		double height = camera.getRes()[1];
		double top = camera.getBounds()[0];
		double right = camera.getBounds()[1];
		double bottom = camera.getBounds()[2];
		double left = camera.getBounds()[3];
		double px = i / (width - 1) * (right - left) + left;
		double py = j / (height - 1) * (top - bottom) + bottom;
		Matrix pixpt = camera.getEye().plus(cameraW.times(camera.getD())).plus(cameraU.times(px)).plus(cameraV.times(py));
		Matrix ray = pixpt.minus(camera.getEye());
		ray = ray.timesEquals(1 / ray.normF());
		return new Ray(ray);
	}

	public void setPixelPointsAndRays(int i, int j) {
		double width = camera.getRes()[0];
		double height = camera.getRes()[1];
		double top = camera.getBounds()[0];
		double right = camera.getBounds()[1];
		double bottom = camera.getBounds()[2];
		double left = camera.getBounds()[3];
		double px = (double) i / (double) (width - 1) * (double) (right - left) + left;
		double py = (double) j / (double) (height - 1) * (double) (top - bottom) + bottom;
		Matrix pixpt = camera.getEye().plus(cameraW.times(camera.getD())).plus(cameraU.times(px)).plus(cameraV.times(py));
		Matrix ray = pixpt.minus(camera.getEye());
		ray = ray.timesEquals(1 / ray.normF());
		pixels.add(pixpt);
		rays.add(new Ray(ray));
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
}
