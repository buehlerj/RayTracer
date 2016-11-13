import java.util.ArrayList;
import java.util.Collections;

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
		for (int i = camera.getRes()[0] - 1; i >= 0; i--) {
			for (int j = 0; j < camera.getRes()[1]; j++) {
				setPixelPointsAndRays(j, i);
			}
		}

		Model m = models.get(0);
		ArrayList<Double> tValues = new ArrayList<Double>();
		double a1; double a2; double a3;
		double b1; double b2; double b3;
		double c1; double c2; double c3;
		double d1; double d2; double d3;
		Matrix pixel; Matrix D; Matrix M; Matrix y; Matrix x;
		double beta; double gamma; double t;
		for (int i = 0; i < rays.size(); i++) {
			tValues.clear();
			pixel = pixels.get(i);
			D = pixels.get(i).minus(camera.getEye());
			D = D.timesEquals(1 / D.normF());
			c1 = D.get(0, 0);
			c2 = D.get(1, 0);
			c3 = D.get(2, 0);
			for (Face f : m.getFaces()) {
				Vertex a = m.getVertices().get(f.getVertexIndices().get(0));
				Vertex b = m.getVertices().get(f.getVertexIndices().get(1));
				Vertex c = m.getVertices().get(f.getVertexIndices().get(2));
				a1 = a.getX() - b.getX();
				a2 = a.getY() - b.getY();
				a3 = a.getZ() - b.getZ();
				b1 = a.getX() - c.getX();
				b2 = a.getY() - c.getY();
				b3 = a.getZ() - c.getZ();
				d1 = a.getX() - pixel.get(0, 0);
				d2 = a.getY() - pixel.get(1, 0);
				d3 = a.getZ() - pixel.get(2, 0);
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
			if (tValues.isEmpty()) {
				distances.add(null);
			} else {
				distances.add(Collections.min(tValues));
			}
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
		double width = (double) camera.getRes()[0];
		double height = (double) camera.getRes()[1];
		double top = (double) camera.getBounds()[0];
		double right = (double) camera.getBounds()[1];
		double bottom = (double) camera.getBounds()[2];
		double left = (double) camera.getBounds()[3];
		double px = i / (width - 1) * (right - left) + left;
		double py = j / (height - 1) * (top - bottom) + bottom;
		Matrix pixpt = camera.getEye().plus(cameraW.times(camera.getD())).plus(cameraU.times(px))
				.plus(cameraV.times(py));
		return pixpt;
	}

	public Ray pixelRay(int i, int j) {
		double width = (double) camera.getRes()[0];
		double height = (double) camera.getRes()[1];
		double top = (double) camera.getBounds()[0];
		double right = (double) camera.getBounds()[1];
		double bottom = (double) camera.getBounds()[2];
		double left = (double) camera.getBounds()[3];
		double px = i / (width - 1) * (right - left) + left;
		double py = j / (height - 1) * (top - bottom) + bottom;
		Matrix pixpt = camera.getEye().plus(cameraW.times(camera.getD())).plus(cameraU.times(px))
				.plus(cameraV.times(py));
		Matrix ray = pixpt.minus(camera.getEye());
		ray = ray.timesEquals(1 / ray.normF());
		return new Ray(ray);
	}

	public void setPixelPointsAndRays(int i, int j) {
		double width = (double) camera.getRes()[0];
		double height = (double) camera.getRes()[1];
		double top = (double) camera.getBounds()[0];
		double right = (double) camera.getBounds()[1];
		double bottom = (double) camera.getBounds()[2];
		double left = (double) camera.getBounds()[3];
		double px = (double) i / (double) (width - 1) * (double) (right - left) + left;
		double py = (double) j / (double) (height - 1) * (double) (top - bottom) + bottom;
		Matrix pixpt = camera.getEye().plus(cameraW.times(camera.getD())).plus(cameraU.times(px))
				.plus(cameraV.times(py));
		Matrix ray = pixpt.minus(camera.getEye());
		ray = ray.timesEquals(1 / ray.normF());
		pixels.add(pixpt);
//		System.out.print(pixpt.get(0, 0) + ", ");
//		System.out.print(pixpt.get(1, 0) + ", ");
//		System.out.println(pixpt.get(2, 0));
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
