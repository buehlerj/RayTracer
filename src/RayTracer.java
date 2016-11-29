import java.util.ArrayList;

import Jama.Matrix;

public class RayTracer {
	private Camera camera;
	private Scene scene;
	private ArrayList<Model> models;
	private ArrayList<Sphere> spheres;
	private Ray[][] rays;
	private Double[][] distances;
	private Material[][] materialPixels;
	private ArrayList<Material> materials;
	private final int recursionLevel = 6;
	private final int PhongConstant = 16;
	private final Matrix Kr = new Matrix(new double[][] { {.9}, {.9}, {.9} });

	public RayTracer() {
		camera = new Camera();
		scene = new Scene();
		models = new ArrayList<>();
		spheres = new ArrayList<>();
		materials = new ArrayList<>();
	}

	public boolean setupCamera(String inputFileName) {
		boolean cameraRead = camera.read(inputFileName);
		rays = new Ray[camera.getRes()[0]][camera.getRes()[1]];
		distances = new Double[camera.getRes()[0]][camera.getRes()[1]];
		materialPixels = new Material[camera.getRes()[0]][camera.getRes()[1]];
		return cameraRead;
	}

	public boolean setupScene(String inputFileName) {
		boolean sceneAmbient = scene.read(inputFileName);
		models = scene.readModels(inputFileName);
		spheres = scene.readSpheres(inputFileName);
		ArrayList<Material> newMaterials;
		for (Model m : models) {
			newMaterials = m.readMaterials();
			for (Material mat : newMaterials) {
				addMaterial(mat);
			}
		}
		return sceneAmbient;
	}

	public Picture capturePicture() {
		Picture photo = new Picture(camera.getRes()[0], camera.getRes()[1]);

		// Add all Rays
		for (int j = camera.getRes()[1] - 1; j >= 0; j--) {
			for (int i = 0; i < camera.getRes()[0]; i++) {
				rays[i][j] = rayPt(i, j);
			}
		}

		Matrix rgb;
		Matrix currentPixelValues;
		Ray ray;
		int jIndex;
		for (int i = 0; i < camera.getRes()[0]; i++) {
			for (int j = 0; j < camera.getRes()[1]; j++) {
				jIndex = camera.getRes()[1] - j - 1;
				ray = rays[i][jIndex];
				rgb = new Matrix(3, 1);
				currentPixelValues = rayTrace(i, jIndex, ray, rgb, scene.getAmbient(), recursionLevel);
				photo.addToPixels(i, j, new Pixel(currentPixelValues));

				// TODO: Incorporate Models too
			}
		}
		return photo;
	}

	public Matrix rayTrace(int i, int j, Ray ray, Matrix accum, Matrix refatt, int level) {
		rayFind(ray, i, j);

		Matrix snrm; Matrix color; Matrix N;

		if (ray.getBestT() != null) {
			N = Utils.pairwiseMinus(ray.getBestPt(), ray.getBestSphere().getCoordinates());
			N = N.timesEquals(1 / N.normF());
			snrm = ray.getBestPt().minus(ray.getBestSphere().getCoordinates());
			snrm.timesEquals(1 / snrm.normF());
			Material currentMaterial = ray.getBestSphere().getMaterial();
			color = Utils.pairwiseProduct(scene.getAmbient(), currentMaterial.getKa());
			for (Light l : scene.getLights()) {
				Matrix emL = l.getColor();
				Matrix toL = Utils.pairwiseMinus(l.getCoordinates(), ray.getBestPt());
				toL.timesEquals(1 / toL.normF());
				double NdotL = Utils.dotProduct(snrm, toL);
				if (NdotL > 0.0) {
					color.plusEquals(Utils.pairwiseProduct(currentMaterial.getKd(), emL.times(Utils.dotProduct(snrm,  toL))));
					Matrix toC = rays[i][j].getLocation().minus(ray.getBestPt());
					toC.timesEquals(1 / toC.normF());
					Matrix spR = snrm.times(2 * Utils.dotProduct(snrm, toL)).minus(toL);
					color.plusEquals(Utils.pairwiseProduct(currentMaterial.getKs(), emL.times(Math.pow(Utils.dotProduct(toC, spR), PhongConstant))));
				}
			}
			accum = Utils.parwisePlus(accum, Utils.pairwiseProduct(refatt, color));
			if (level > 0) {
				Matrix Uinv = ray.getDirection().times(-1);
				Matrix refR = Utils.pairwiseMinus(N.times(2 * Utils.dotProduct(N, Uinv)), Uinv);
				rayTrace(i, j, new Ray(ray.getBestPt(), refR), accum, Utils.pairwiseProduct(Kr, refatt), level - 1);
			}
		}
		return accum;
	}

	public void rayFind(Ray r, int i, int j) {
		for (Sphere s : spheres) {
			sphereTest(r, s, i, j);
		}
	}

	public void sphereTest(Ray ray, Sphere sphere, int i, int j) {
		Matrix pt = null;
		Matrix Tv = sphere.getCoordinates().minus(ray.getLocation());
		double v = Utils.dotProduct(Tv, ray.getDirection());
		double csq = Utils.dotProduct(Tv, Tv);
		double disc = Math.pow(sphere.getRadius(), 2) - (csq - Math.pow(v, 2));
		if (disc > 0) {
			double d = Math.sqrt(disc);
			double tval = v - d;
			if (ray.getBestT() == null || tval < ray.getBestT()) {
				pt = ray.getLocation().plus(ray.getDirection().times(tval));
				ray.setBestT(d);
				ray.setBestSphere(sphere);
				ray.setBestPt(pt);
			}
		}
	}

	public Matrix raySphereRGB(Ray r, Sphere s, int i, int j) {
		Matrix currentPt = raySphereTest(rays[i][j], s, i, j);
		Matrix snrm; Matrix color;
		if (currentPt != null) {
			snrm = currentPt.minus(s.getCoordinates());
			snrm.timesEquals(1 / snrm.normF());
			Material currentMaterial = s.getMaterial();
			color = Utils.pairwiseProduct(scene.getAmbient(), currentMaterial.getKa());
			for (Light l : scene.getLights()) {
				Matrix ptL = l.getCoordinates();
				Matrix emL = l.getColor();
				Matrix toL = Utils.pairwiseMinus(ptL, currentPt);
				toL.timesEquals(1 / toL.normF());
				if (Utils.dotProduct(snrm, toL) > 0.0) {
					color.plusEquals(Utils.pairwiseProduct(currentMaterial.getKd(), emL.times(Utils.dotProduct(snrm,  toL))));
					Matrix toC = rays[i][j].getLocation().minus(currentPt);
					toC.timesEquals(1 / toC.normF());
					Matrix spR = snrm.times(2 * Utils.dotProduct(snrm, toL)).minus(toL);
					color.plusEquals(Utils.pairwiseProduct(currentMaterial.getKs(), emL.times(Math.pow(Utils.dotProduct(toC, spR), PhongConstant))));
				}
			}
			r.setBestPt(r.getLocation().plus(r.getDirection().times(distances[i][j])));
			return color;
		}
		return null;
	}

	public Matrix rayModelTest(Ray ray, Model model, int i, int j) {
		double t;
		Matrix pt = null;

		if (model != null) {
			Matrix D; Matrix M; Matrix y; Matrix x;
			double beta; double gamma;
			Vertex aVertex; Vertex bVertex; Vertex cVertex;
			double a1; double a2; double a3;
			double b1; double b2; double b3;
			double c1; double c2; double c3;
			double d1; double d2; double d3;
			D = ray.getLocation().minus(camera.getEye());
			D = D.timesEquals(1 / D.normF());
			c1 = D.get(0, 0);
			c2 = D.get(1, 0);
			c3 = D.get(2, 0);
			// Ray Trace on all Polygonal Models
			for (Face f : model.getFaces()) {
				aVertex = model.getVertices().get(f.getVertexIndices().get(0));
				bVertex = model.getVertices().get(f.getVertexIndices().get(1));
				cVertex = model.getVertices().get(f.getVertexIndices().get(2));
				a1 = aVertex.getX() - bVertex.getX();
				a2 = aVertex.getY() - bVertex.getY();
				a3 = aVertex.getZ() - bVertex.getZ();
				b1 = aVertex.getX() - cVertex.getX();
				b2 = aVertex.getY() - cVertex.getY();
				b3 = aVertex.getZ() - cVertex.getZ();
				d1 = aVertex.getX() - ray.getLocation().get(0, 0);
				d2 = aVertex.getY() - ray.getLocation().get(1, 0);
				d3 = aVertex.getZ() - ray.getLocation().get(2, 0);
				M = new Matrix(new double[][] { { a1, b1, c1 }, { a2, b2, c2 }, { a3, b3, c3 } });
				y = new Matrix(new double[][] { { d1 }, { d2 }, { d3 } });
				x = M.solve(y);

				beta = x.get(0, 0);
				gamma = x.get(1, 0);
				t = x.get(2, 0);
				if (beta >= 0 && gamma >= 0 && (beta + gamma) <= 1 && t > 0) {
					if (distances[i][j] == null || t < distances[i][j]) {
						distances[i][j] = t;
						materialPixels[i][j] = getMaterial(model.getMaterialName());
					}
				}
			}
		}

		return pt;
	}

	public Matrix raySphereTest(Ray ray, Sphere sphere, int i, int j) {
		double t;
		Matrix pt = null;

		// Ray Trace on all Sphere Models
		double v; double d; double csq; double disc;
		Matrix Tv;
		Tv = sphere.getCoordinates().minus(ray.getLocation());
		v = Utils.dotProduct(Tv, ray.getDirection());
		csq = Utils.dotProduct(Tv, Tv);
		disc = Math.pow(sphere.getRadius(), 2) - (csq - Math.pow(v, 2));
		if (disc >= 0) {
			d = Math.sqrt(disc);
			t = v - d;
			distances[i][j] = t;
			pt = ray.getLocation().plus(ray.getDirection().times(t));
		}

		return pt;
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
		Matrix ray = point.minus(camera.getEye());
		ray.timesEquals(1 / ray.normF());
		return new Ray(point, ray);
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

	public ArrayList<Sphere> getSpheres() {
		return spheres;
	}

	public void addSphere(Sphere s) {
		spheres.add(s);
	}

	public void removeSphere(Sphere s) {
		spheres.remove(s);
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

	public int getRecursionLevel() {
		return recursionLevel;
	}

	public String toString() {
		String rayTracerString = "";
		rayTracerString += camera + "\n";
		rayTracerString += scene + "\n";
		for (Sphere s : spheres) {
			rayTracerString += "Sphere:\n";
			rayTracerString += "   Coordinates: " + Utils.MatrixToStringOneLine(s.getCoordinates()) + "\n";
			rayTracerString += "   Radius: " + s.getRadius() + "\n";
			rayTracerString += "   Color: " + Utils.MatrixToStringOneLine(s.getColor()) + "\n\n";
		}
		return rayTracerString;
	}
}
