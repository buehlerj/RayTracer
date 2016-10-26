import java.util.ArrayList;

public class RayTracer {
	private Camera camera = new Camera();
	private ArrayList<Model> models = new ArrayList<Model>();
	private Vertex topLeftCorner;
	private Vertex topRightCorner;
	private Vertex bottomRightCorner;
	private Vertex bottomLeftCorner;

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera new_camera) {
		camera = new_camera;
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

	public Picture capturePicture() {
		Picture photo = new Picture(camera.getRes()[0], camera.getRes()[1]);
		Vertex center_of_near_clipping_plane;
		int[] bounds;
		Vertex right;
		Vertex left;
		Vertex up;
		Vertex down;

		// Make the eye the center of the image
		for (Model m : models) {
			for (Vertex v : m.getVertices())
				v.subtract(camera.getEye());
		}
		camera.getLook().subtract(camera.getEye());

		// Near Clipping Plane
		center_of_near_clipping_plane = new Vertex(camera.getLook());
		center_of_near_clipping_plane.normalize();
		center_of_near_clipping_plane.scale(camera.getD());
		bounds = camera.getBounds();

		right = Vertex.crossProduct(center_of_near_clipping_plane, camera.getUp());
		right.normalize();
		left = new Vertex(right);
		right.scale(bounds[1]);
		left.scale(bounds[3]);
		up = Vertex.crossProduct(right, center_of_near_clipping_plane);
		up.normalize();
		down = new Vertex(up);
		up.scale(bounds[0]);
		down.scale(bounds[2]);

		bottomRightCorner = new Vertex(up);
		bottomRightCorner.add(left);
		bottomLeftCorner = new Vertex(up);
		bottomLeftCorner.add(right);
		topRightCorner = new Vertex(down);
		topRightCorner.add(left);
		topLeftCorner = new Vertex(down);
		topLeftCorner.add(right);

		bottomRightCorner.add(center_of_near_clipping_plane);
		bottomLeftCorner.add(center_of_near_clipping_plane);
		topRightCorner.add(center_of_near_clipping_plane);
		topLeftCorner.add(center_of_near_clipping_plane);

		// Relative Depth of Each Pixel
		getRelativeDepth(photo);

		// Re-center everything
		for (Model m : models) {
			for (Vertex v : m.getVertices())
				v.add(camera.getEye());
		}
		camera.getLook().add(camera.getEye());
		return photo;
	}

	public void getRelativeDepth(Picture photo) {
		Vector leftOfNCP = new Vector(topLeftCorner, bottomLeftCorner);
		Vector rightOfNCP = new Vector(topRightCorner, bottomRightCorner);
		Vertex currentLeftHeight;
		Vertex currentRightHeight;
		Vector currentVector;
		Vertex currentPoint;
		Ray ray;
		double tmin = 255;
		double tmax = 0;
		double distance;

		// Get Distance Specs
		for (int j = 0; j < camera.getRes()[1]; j++) {
			currentLeftHeight = new Vertex(leftOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentRightHeight = new Vertex(rightOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentVector = new Vector(currentLeftHeight, currentRightHeight);
			for (int i = 0; i < camera.getRes()[0]; i++) {
				currentPoint = new Vertex(currentVector.getPoint((double) i / camera.getRes()[0]));
				ray = new Ray(camera.getEye(), currentPoint);
				distance = getDistance(ray);
				if (distance > tmax)
					tmax = distance;
				if (distance < tmin)
					tmin = distance;
			}
		}

		// Set Each Pixel's RGB Value
		double ratio;
		double r;
		double g;
		double b;
		Pixel currentPixel;
		for (int j = 0; j < camera.getRes()[1]; j++) {
			currentLeftHeight = new Vertex(leftOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentRightHeight = new Vertex(rightOfNCP.getPoint((double) j / camera.getRes()[1]));
			currentVector = new Vector(currentLeftHeight, currentRightHeight);
			for (int i = 0; i < camera.getRes()[0]; i++) {
				currentPoint = new Vertex(currentVector.getPoint((double) i / camera.getRes()[0]));
				ray = new Ray(camera.getEye(), currentPoint);
				distance = getDistance(ray);
				ratio = 2 * (distance - tmin) / (tmax - tmin);
				r = Math.max(0, 255 * (1 - ratio));
				b = Math.max(0, 255 * (ratio - 1));
				g = 255 - b - r;
				currentPixel = new Pixel(r, g, b);
				photo.addToPixels(currentPixel);
			}
		}
	}

	public double getDistance(Ray ray) {
		double distance = 0;
		double currentDistance = 0;
		for (Model m : models) {
			if (currentDistance < distance)
				distance = currentDistance;
		}
		Model m = models.get(1);
		
		return distance;
	}

	public Model aggregateModels(RayTracer rt) {
		Model complete = new Model();
		int total_faces = 0;
		int total_vertices = 0;
		for (Model m : rt.models) {
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
}
