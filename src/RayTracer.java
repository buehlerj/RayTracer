import java.util.ArrayList;

import Jama.Matrix;

public class RayTracer {
	private Camera camera;
	private Scene scene;
	private ArrayList<Model> models;
	private Ray[][] rays;
	private Double[][] distances;
	private ArrayList<Material> materials;

	public RayTracer() {
		camera = new Camera();
		scene = new Scene();
		models = new ArrayList<>();
		materials = new ArrayList<>();
	}

	public boolean setupCamera(String inputFileName) {
		boolean cameraRead = camera.read(inputFileName);
		rays = new Ray[camera.getRes()[0]][camera.getRes()[1]];
		distances = new Double[camera.getRes()[0]][camera.getRes()[1]];
		return cameraRead;
	}

	public boolean setupScene(String inputFileName) {
		models = scene.read(inputFileName);
		ArrayList<Material> newMaterials;
		for (Model m : models) {
			newMaterials = m.readMaterials();
			for (Material mat : newMaterials) {
				addMaterial(mat);
			}
		}
		return true;
	}

	public Picture capturePicture() {
		Picture photo = new Picture(camera.getRes()[0], camera.getRes()[1]);

		// Add all Rays
		for (int j = camera.getRes()[1] - 1; j >= 0; j--) {
			for (int i = 0; i < camera.getRes()[0]; i++) {
				rays[i][j] = rayPt(i, j);
			}
		}

//		ArrayList<Double> tValues = new ArrayList<Double>();
		double currentT;
		Vertex aVertex; Vertex bVertex; Vertex cVertex;
		double a1; double a2; double a3;
		double b1; double b2; double b3;
		double c1; double c2; double c3;
		double d1; double d2; double d3;
		Matrix rayOrigin; Matrix rayDirection; Matrix D; Matrix M; Matrix y; Matrix x;
		double beta; double gamma; double t;
		for (int j = 0; j < camera.getRes()[1]; j++) {
			for (int i = 0; i < camera.getRes()[0]; i++) {
				currentT = -1;
				rayOrigin = rays[i][j].getLocation();
				rayDirection = rays[i][j].getDirection();
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
							if (currentT == -1)
								currentT = t;
							else if (t < currentT)
								currentT = t;
						}
					}
				}

				// Ray Trace on all Sphere Models
				Matrix Cv; Matrix Lv; Matrix Uv; Matrix Tv;
				double v; double bsq; double disc;
				for (Sphere s : scene.getSpheres()) {
					Cv = s.getCoordinates();
					Lv = rayOrigin;
					Uv = rayDirection;
					Tv = Cv.minus(Lv);
					v = Utils.dotProduct(Tv, Uv);
					bsq = Utils.dotProduct(Tv, Tv);
					disc = Math.pow(s.getRadius(), 2) - (bsq - Math.pow(v, 2));
					if (disc > 0) {
						if (currentT == -1)
							currentT = disc;
						else if (disc < currentT)
							currentT = disc;
					}
				}

				// Compare all distance values
				if (currentT == -1) {
					distances[i][j] = null;
				} else {
					distances[i][j] = currentT;
				}
			}
		}

		double min = getMin(distances);
		double max = getMax(distances);
		Double distance;
		int jIndex;
		for (int j = 0; j < distances[0].length; j++) {
			for (int i = 0; i < distances.length; i++) {
				jIndex = distances[0].length - j - 1;
				distance = distances[i][j];
				if (distance == null) {
					photo.addToPixels(i, jIndex, new Pixel());
				} else {
					double ratio = 2 * (distance - min) / (max - min);
					double r = Math.max(0, 255 * (1 - ratio));
					double b = Math.max(0, 255 * (ratio - 1));
					double g = 255 - b - r;
					photo.addToPixels(i, jIndex, new Pixel(r, g, b));
				}
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
		//		Matrix ray = point.minus(camera.getCameraV());
		//		ray = ray.timesEquals(1 / ray.normF());
		return new Ray(point, camera.getEye().plus(point));
	}

	public double getMin(Double[][] list) {
		double min = 100000000;
		for (int j = 0; j < list[0].length; j++) {
			for (int i = 0; i < list.length; i++) {
				Double d = list[i][j];
				if (d != null && d < min)
					min = d;
			}
		}
		return min;
	}

	public double getMax(Double[][] list) {
		double max = -1;
		for (int j = 0; j < list[0].length; j++) {
			for (int i = 0; i < list.length; i++) {
				Double d = list[i][j];
				if (d != null && d > max)
					max = d;
			}
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

	public ArrayList<Material> getMaterials() {
		return materials;
	}

	public void addMaterial(Material material) {
		materials.add(material);
	}

	public Material getMaterial(String materialName) {
		for (Material m : materials) {
			if (m.getName().equals(materialName))
				return m;
		}
		return null;
	}
}
