import java.util.Arrays;

public class CS410BuehlerJeff {

	public static void main(String[] args) {
		PA3(args);
	}

	public static void PA4(String[] args) {
		RayTracer rayTracer = new RayTracer();
		if (args.length != 2) {
			System.err.println("Incorrect number of argments: " + args.length);
			System.exit(-1);
		}

		rayTracer.setupCamera(args[0]);
		rayTracer.setupScene(args[0]);

		for (String model_file_name : Arrays.copyOfRange(args, 1, args.length - 1)) {
			Model model = new Model();
			if (!model.read(model_file_name)) {
				System.exit(-1);
			}
			rayTracer.addModel(model);
		}
		Picture image = rayTracer.capturePicture();
		image.write(args[args.length - 1], "");

		System.out.println(rayTracer.getCamera());
		System.out.println(rayTracer.getScene());
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
}
