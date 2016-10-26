import java.util.Arrays;

/*
C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\airplane_cam01.txt                                                                                                                                     C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\airplane.ply           C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\ellelltri.ply           C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\output\output1.ply
D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\airplane_cam01.txt     D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\airplane.ply              D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\ellelltri.ply              D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\output\output1.ply
 */

public class PA3 {

	public static void main(String[] args) {
		RayTracer ray_tracer = new RayTracer();
		if (args.length < 3) {
			System.err.println("Incorrect number of argments: " + args.length);
			System.exit(-1);
		}

		if (!ray_tracer.getCamera().read(args[0])) {
			System.exit(-1);
		}

		for (String model_file_name : Arrays.copyOfRange(args, 1, args.length - 1)) {
			Model model = new Model();
			if (!model.read(model_file_name)) {
				System.exit(-1);
			}
			ray_tracer.addModel(model);
		}

		Picture picture = ray_tracer.capturePicture();
		picture.write(args[args.length - 1], "airplane");
		System.out.println("\n\n\n\n----------------- EXIT -----------------");
	}

}
