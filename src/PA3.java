import java.util.ArrayList;
import java.util.Arrays;

/*
C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\airplane_cam01.txt C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\airplane.ply C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\output\output1.ppm
C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\ellelltri_cam01.txt C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\ellelltri.ply C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\output\output1.ppm
D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\airplane_cam01.txt D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\airplane.ply D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\ellelltri.ply D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\output\output1.ppm
C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\test_cam.txt C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\pyramid.ply C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\ellelltri.ply C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\output\output1.ppm
 */

public class PA3 {

	public static void main(String[] args) {
		ArrayList<Pixel> pixels = new ArrayList<Pixel>();
		pixels.add(new Pixel(1, 1, 1));

		RayTracer rayTracer = new RayTracer();
		if (args.length < 3) {
			System.err.println("Incorrect number of argments: " + args.length);
			System.exit(-1);
		}

		if (!rayTracer.getCamera().read(args[0])) {
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
