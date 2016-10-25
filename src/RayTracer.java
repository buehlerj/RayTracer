import java.util.ArrayList;
import java.util.Arrays;

import org.omg.Messaging.SyncScopeHelper;

/*
C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\airplane_cam01.txt                                                                                                                                     C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\airplane.ply           C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\original\ellelltri.ply           C:\Users\Jeffrey\Documents\workspace\Java\CS410\RayTracer\Models\output\output1.ply
D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\airplane_cam01.txt     D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\airplane.ply              D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\original\ellelltri.ply              D:\jeffs\Documents\workspace\java\CS410\RayTracer-Java\Models\output\output1.ply
 */

public class RayTracer {
	private Camera camera;
	private static ArrayList<Model> models = new ArrayList<Model>();

	public Picture capturePicture() {
		// Make the eye the center of the image
		for (Model m : models) {
			for (Vertex v : m.getVertices())
				v.subtract(camera.getEye());
		}
		camera.getLook().subtract(camera.getEye());

		Picture photo = new Picture(camera.getRes()[0], camera.getRes()[1]);
		Vertex center_of_near_clipping_plane = new Vertex(camera.getLook());
		center_of_near_clipping_plane.normalize();
		center_of_near_clipping_plane.scale(camera.getD());
		int[] bounds = camera.getBounds();
		Vertex right = Vertex.crossProduct(center_of_near_clipping_plane, camera.getUp()).unit();
		Vertex left = Vertex.crossProduct(camera.getUp(), center_of_near_clipping_plane).unit();
		Vertex up = Vertex.crossProduct(right, center_of_near_clipping_plane).unit();
		Vertex down = Vertex.crossProduct(left, center_of_near_clipping_plane).unit();
		right.scale(bounds[1]);
		left.scale(bounds[3]);
		up.scale(bounds[0]);
		down.scale(bounds[2]);
		System.out.println(up);
		System.out.println(down);
		System.out.println(left);
		System.out.println(right);

		// Re-center everything
		for (Model m : models) {
			for (Vertex v : m.getVertices())
				v.add(camera.getEye());
		}
		camera.getLook().add(camera.getEye());
		return photo;
	}

	public static Model aggregateModels() {
		Model complete = new Model();
		int total_faces = 0;
		int total_vertices = 0;
		for (Model m : models) {
			total_faces += m.getNumberOfFaces();
			total_vertices += m.getNumberOfVertices();
			for (Vertex v : m.getVertices())
				complete.addVertex(v);
			for (Face f : m.getFaces())
				complete.addFace(f);
		}
		complete.setNumberOfFaces(total_faces);
		complete.setNumberOfVertices(total_vertices);
		complete.setHeader("ply\nformat ascii 1.0\nelement vertex " + total_vertices
				+ "\nproperty float32 x\nproperty float32 y\nproperty float32 z\nelement face " + total_faces
				+ "\nproperty list uint8 int32 vertex_indices\nend_header\n");
		return complete;
	}

	public static void main(String[] args) {
		RayTracer ray_tracer = new RayTracer();
		if (args.length < 3) {
			System.err.println("Incorrect number of argments: " + args.length);
			System.exit(-1);
		}

		ray_tracer.camera = new Camera();
		if (!ray_tracer.camera.read(args[0])) {
			System.exit(-1);
		}

		for (String model_file_name : Arrays.copyOfRange(args, 1, args.length - 1)) {
			Model model = new Model();
			if (!model.read(model_file_name)) {
				System.exit(-1);
			}
			ray_tracer.models.add(model);
		}

		Picture picture = ray_tracer.capturePicture();
		picture.write(args[args.length - 1], "a");
		System.out.println("\n\n\n\n\n----------------- EXIT -----------------");
	}
}
