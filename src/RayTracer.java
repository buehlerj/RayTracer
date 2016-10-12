import java.util.Arrays;

public class RayTracer {

	public static void main(String[] args) {
		if (args.length < 3) { System.err.println("Incorrect number of argments: " + args.length); }
		for (String model_file_name : Arrays.copyOfRange(args, 1, args.length - 1)) {
			Model model = new Model();
			if (!model.read(model_file_name)) { System.exit(-1); }
//			model.write(args[4], "");
		}		
	}

}
