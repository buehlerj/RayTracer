import java.util.Arrays;

import Jama.Matrix;

public class CS410BuehlerJeff {

	public static void main(String[] args) {
		PA5(args);
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

	public static void setupScene(RayTracer rayTracer) {
		Matrix eye = new Matrix(new double[][] { {0}, {-550}, {180} });
		Matrix look = new Matrix(new double[][] { {0}, {0}, {0} });
		Matrix up = new Matrix(new double[][] { {0}, {0}, {1} });
		double d = -1.7;
		double boundL = -1;
		double boundB = -.5625;
		double boundR = 1;
		double boundT = .5625;
		int resx = 1280;
		int resy = 720;

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
}
