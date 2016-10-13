import java.util.ArrayList;
import java.util.Arrays;

/*
C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\airplane_cam01.txt                                                                                                                                     C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\airplane.ply           C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\ellelltri.ply           C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\output\output1.ply
 */


public class RayTracer {

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Incorrect number of argments: " + args.length);
		}
		Camera camera = new Camera();
		if (!camera.read(args[0])) {
			System.exit(-1);
		}
		ArrayList<Model> models = new ArrayList<Model>();
		for (String model_file_name : Arrays.copyOfRange(args, 1, args.length - 1)) {
			Model model = new Model();
			if (!model.read(model_file_name)) {
				System.exit(-1);
			}
			models.add(model);
		}
		models.get(0).write(args[args.length - 1], "test");
	}
}
