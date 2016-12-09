import java.util.ArrayList;
import java.util.Arrays;

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

	public boolean setupCamera(Camera camera) {
		this.camera = camera;
		rays = new Ray[camera.getRes()[0]][camera.getRes()[1]];
		distances = new Double[camera.getRes()[0]][camera.getRes()[1]];
		materialPixels = new Material[camera.getRes()[0]][camera.getRes()[1]];
		return true;
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

	public boolean setupScene(Scene scene) {
		this.scene = scene;
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

		Matrix rgb;
		Matrix currentPixelValues;
		Ray ray;
		int jIndex;
		for (int i = 0; i < camera.getRes()[0]; i++) {
			for (int j = 0; j < camera.getRes()[1]; j++) {
				jIndex = camera.getRes()[1] - j - 1;
				ray = rays[i][jIndex];
				rgb = new Matrix(3, 1);
				currentPixelValues = rayTraceSpheres(i, jIndex, ray, rgb, scene.getAmbient(), recursionLevel);
				currentPixelValues.set(0, 0, Math.min(255, currentPixelValues.get(0, 0)));
				currentPixelValues.set(1, 0, Math.min(255, currentPixelValues.get(1, 0)));
				currentPixelValues.set(2, 0, Math.min(255, currentPixelValues.get(2, 0)));
				photo.addToPixels(i, j, new Pixel(currentPixelValues));

				for (Model m : models) {
					rayModelTest(ray, m, i, j);
				}

				if (ray.getBestTModel() != null && (ray.getBestTSphere() == null || ray.getBestTModel() < ray.getBestTSphere())) {
					rgb = new Matrix(3, 1);
					currentPixelValues = rayTraceModels(i, j, ray, rgb, scene.getAmbient(), recursionLevel);
					currentPixelValues.set(0, 0, Math.min(255, currentPixelValues.get(0, 0)));
					currentPixelValues.set(1, 0, Math.min(255, currentPixelValues.get(1, 0)));
					currentPixelValues.set(2, 0, Math.min(255, currentPixelValues.get(2, 0)));
					photo.addToPixels(i, j, new Pixel(currentPixelValues));
				}
			}
		}
		return photo;
	}

	public Matrix rayTraceModels(int i, int j, Ray ray, Matrix accum, Matrix refatt, int level) {
		Material currentMaterial;
		Matrix snrm; Matrix color;
		Matrix emL; Matrix toL;
		Matrix toC; Matrix spR;
		double NdotL;
		Face bestFace = ray.getBestFace();
		Model bestModel = ray.getBestModel();
		if (materialPixels[i][j] != null) {
			Vertex vertex1 = bestModel.getVertices().get(bestFace.getVertexIndices().get(0));
			Vertex vertex2 = bestModel.getVertices().get(bestFace.getVertexIndices().get(1));
			Matrix v1 = new Matrix(new double[][] { {vertex1.getX()}, {vertex1.getY()}, {vertex1.getZ()} });
			Matrix v2 = new Matrix(new double[][] { {vertex2.getX()}, {vertex2.getY()}, {vertex2.getZ()} });
			snrm = Vertex.crossProduct(v1, v2);
			snrm.timesEquals(1 / snrm.normF());
			currentMaterial = materialPixels[i][j];
			color = Utils.pairwiseProduct(scene.getAmbient(), currentMaterial.getKa());
			for (Light l : scene.getLights()) {
				emL = l.getColor();
				toL = Utils.pairwiseMinus(l.getCoordinates(), ray.getBestPtModel());
				toL.timesEquals(1 / toL.normF());
				NdotL = Utils.dotProduct(snrm,  toL);
				if (NdotL > 0.0) {
					color.plusEquals(Utils.pairwiseProduct(currentMaterial.getKd(), emL.times(Utils.dotProduct(snrm,  toL))));
					toC = rays[i][j].getLocation().minus(ray.getBestPtModel());
					toC.timesEquals(1 / toC.normF());
					spR = snrm.times(2 * Utils.dotProduct(snrm, toL)).minus(toL);
					color.plusEquals(Utils.pairwiseProduct(currentMaterial.getKs(), emL.times(Math.pow(Utils.dotProduct(toC, spR), PhongConstant))));
				}
			}
			accum = Utils.pairwisePlus(accum, Utils.pairwiseProduct(refatt,  color));
		}
		return accum;
	}

	public Matrix rayTraceSpheres(int i, int j, Ray ray, Matrix accum, Matrix refatt, int level) {
		Material currentMaterial;
		Matrix snrm; Matrix color; Matrix N;
		Matrix emL; Matrix toL;
		Matrix toC; Matrix spR;
		Matrix Uinv;
		Matrix refR;
		double NdotL;

		rayFindSpheres(ray, i, j);

		if (ray.getBestTSphere() != null) {
			N = Utils.pairwiseMinus(ray.getBestPtSphere(), ray.getBestSphere().getCoordinates());
			N = N.timesEquals(1 / N.normF());
			snrm = ray.getBestPtSphere().minus(ray.getBestSphere().getCoordinates());
			snrm.timesEquals(1 / snrm.normF());
			currentMaterial = ray.getBestSphere().getMaterial();
			color = Utils.pairwiseProduct(scene.getAmbient(), currentMaterial.getKa());
			for (Light l : scene.getLights()) {
				emL = l.getColor();
				toL = Utils.pairwiseMinus(l.getCoordinates(), ray.getBestPtSphere());
				toL.timesEquals(1 / toL.normF());
				NdotL = Utils.dotProduct(snrm, toL);
				if (NdotL > 0.0) {
					color.plusEquals(Utils.pairwiseProduct(currentMaterial.getKd(), emL.times(Utils.dotProduct(snrm,  toL))));
					toC = rays[i][j].getLocation().minus(ray.getBestPtSphere());
					toC.timesEquals(1 / toC.normF());
					spR = snrm.times(2 * Utils.dotProduct(snrm, toL)).minus(toL);
					color.plusEquals(Utils.pairwiseProduct(currentMaterial.getKs(), emL.times(Math.pow(Utils.dotProduct(toC, spR), PhongConstant))));
				}
			}
			accum = Utils.pairwisePlus(accum, Utils.pairwiseProduct(refatt, color));
			if (level > 0) {
				Uinv = ray.getDirection().times(-1);
				refR = Utils.pairwiseMinus(N.times(2 * Utils.dotProduct(N, Uinv)), Uinv);
				rayTraceSpheres(i, j, new Ray(ray.getBestPtSphere(), refR), accum, Utils.pairwiseProduct(Kr, refatt), level - 1);
			}
		}
		return accum;
	}

	public void rayFindSpheres(Ray r, int i, int j) {
		for (Sphere s : spheres) {
			testSphere(r, s, i, j);
		}
	}

	public void testSphere(Ray ray, Sphere sphere, int i, int j) {
		Matrix pt = null;
		Matrix Tv = sphere.getCoordinates().minus(ray.getLocation());
		double v = Utils.dotProduct(Tv, ray.getDirection());
		double csq = Utils.dotProduct(Tv, Tv);
		double disc = Math.pow(sphere.getRadius(), 2) - (csq - Math.pow(v, 2));
		if (disc > 0) {
			double d = Math.sqrt(disc);
			double tval = v - d;
			if (ray.getBestTSphere() == null || tval > ray.getBestTSphere()) {
				pt = ray.getLocation().plus(ray.getDirection().times(tval));
				ray.setBestTSphere(d);
				ray.setBestSphere(sphere);
				ray.setBestPtSphere(pt);
			}
		}
	}

	public Matrix rayRGBSphere(Ray r, Sphere s, int i, int j) {
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
			r.setBestPtSphere(r.getLocation().plus(r.getDirection().times(distances[i][j])));
			return color;
		}
		return null;
	}

	public void rayModelTest(Ray ray, Model model, int i, int j) {
		double t;

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
					if (ray.getBestTModel() == null || t < ray.getBestTModel()) {
						ray.setBestTModel(t);
						ray.setBestFace(f);
						ray.setBestModel(model);
						ray.setBestPtModel(ray.getLocation().plus(ray.getDirection().times(t)));
						materialPixels[i][j] = getMaterial(model.getMaterialName());
					}
				}
			}
		}
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

	public static void PA3(String[] args) {
		RayTracer rayTracer = new RayTracer();
		if (args.length < 3) {
			System.err.println("Incorrect number of argments: " + args.length);
			System.exit(-1);
		}

		if (!rayTracer.setupCamera(args[0])) {
			System.exit(-1);
		}

		for (String model_file_name : Arrays.copyOfRange(args, 1, args.length - 1)) {
			Model model = new Model();
			if (!model.read(model_file_name)) {
				System.exit(-1);
			}
			rayTracer.addModel(model);
		}

		Picture picture = rayTracer.capturePicture();
		picture.write(args[args.length - 1], "");
		System.out.println(rayTracer.getCamera());
		System.out.println("Wrote to: " + args[args.length - 1]);
		System.out.println("\n\n\n\n----------------- EXIT -----------------");
	}

	public static void PA4(String[] args) {
		RayTracer rayTracer = new RayTracer();
		if (args.length != 2) {
			System.err.println("Incorrect number of argments: " + args.length);
			System.exit(-1);
		}

		if (!rayTracer.setupCamera(args[0])) {
			System.exit(-1);
		}
		if (!rayTracer.setupScene(args[0])) {
			System.exit(-1);
		}

		Picture image = rayTracer.capturePicture();
		System.out.println("Image Read, writing file.");
		image.write(args[args.length - 1], "");

		System.out.println("\n\n\n\n----------------- STATS -----------------\n");
		System.out.println(rayTracer);
		System.out.println("Wrote to: " + args[args.length - 1]);
		System.out.println("\n\n\n\n----------------- EXIT -----------------");
	}

	public static void PA5(String[] args) {
		int argsLength = args.length;
		RayTracer rayTracer = new RayTracer();

		if (argsLength == 0) {
			setupScene(rayTracer);
		} else if (argsLength == 1 || argsLength == 2) {
			if (!rayTracer.setupCamera(args[0])) {
				System.exit(-1);
			}
			if (!rayTracer.setupScene(args[0])) {
				System.exit(-1);
			}
		} else {
			System.err.println("Incorrect number of argments: " + argsLength);
			System.exit(-1);
		}

		Picture image = rayTracer.capturePicture();

		if (argsLength == 0) {
			image.addStars();
		}

		System.out.println("Image Read, writing file.");
		String outputFileName = "masterwork.ppm";

		if (argsLength == 2) {
			outputFileName = args[1];
		}

		image.write(outputFileName, "");

		System.out.println("\n\n\n\n----------------- STATS -----------------\n");
		System.out.println(rayTracer);
		System.out.println("Wrote to: " + outputFileName);
		System.out.println("\n\n\n\n----------------- EXIT -----------------");
	}

	public static void setupScene(RayTracer rayTracer) {
		Matrix eye = new Matrix(new double[][] { {0}, {-550}, {180} });
		Matrix look = new Matrix(new double[][] { {0}, {0}, {0} });
		Matrix up = new Matrix(new double[][] { {0}, {0}, {1} });
		double d = -1.7;
		double boundL = -1;
		double boundB = -.5625;
		double boundR = 1;
		double boundT = .5625;
		int resx = 1920;
		int resy = 1080;
//		resx = 3840;
//		resy = 2160;

		Camera camera = new Camera(eye, look, up, d, boundL, boundB, boundR, boundT, resx, resy);
		rayTracer.setupCamera(camera);

		Matrix ambient = new Matrix(new double[][] { {.5}, {.5}, {.5} });
		Light l1 = new Light(0, 0, 0, 1, 1, 1, 1);
		Scene scene = new Scene(ambient);
		scene.addLight(l1);
		rayTracer.setupScene(scene);

		Sphere sun = new Sphere(0, 0, 0, 40, 5.208, 3.7884, 0.391125); 
		Sphere mercury = new Sphere(50, 0, 0, 3, 0.6627, 0.6627, 0.6627);
		Sphere venus = new Sphere(11, -64, 0, 5, 0.8784, 0.6118, 0.1569);
		Sphere earth = new Sphere(-69, -40, 0, 5, 0.3020, 0.3294, 0.5);
		Sphere mars = new Sphere(48, -82, 0, 4, 0.7569, 0.2667, 0.0549);
		Sphere jupiter = new Sphere(-113, -65, 0, 20, 0.7020, 0.5294, 0.4157);
		Sphere saturn = new Sphere(169, 62, 0, 18, 0.8941, 0.7725, 0.58824);
		Sphere uranus = new Sphere(-191, 110, 0, 12, 0.6549, 0.7647, 0.85098);
		Sphere neptune = new Sphere(235, -86, 0, 11.5, 0.2196, 0.6275, 0.99608);
		Sphere pluto = new Sphere(-286, 50, 0, 3, 0.8353, 0.6824, 0.5412);

		rayTracer.addSphere(sun);
		rayTracer.addSphere(mercury);
		rayTracer.addSphere(venus);
		rayTracer.addSphere(earth);
		rayTracer.addSphere(mars);
		rayTracer.addSphere(jupiter);
		rayTracer.addSphere(saturn);
		rayTracer.addSphere(uranus);
		rayTracer.addSphere(neptune);
		rayTracer.addSphere(pluto);
	}

	public static void main(String[] args) {
		PA5(args);
	}
}
