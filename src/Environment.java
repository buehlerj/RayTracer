import java.util.ArrayList;

public class Environment {
	Camera camera;
	ArrayList<Model> models;

	Environment(Camera camera, ArrayList<Model> models) {
		this.camera = camera;
		this.models = models;
	}

	public void addModel(Model model) {
		models.add(model);
	}

	public void addCamera(Camera new_camera) {
		camera = new_camera;
	}

	public Picture capturePicture() {
		Picture photo = new Picture(camera.getRes()[0], camera.getRes()[1]);
		Vector center_of_near_clipping_plane = new Vector(camera.getEye().getX() + camera.getLook().getX(), camera.getEye().getY() + camera.getLook().getY(), camera.getEye().getZ() + camera.getLook().getZ());
		
		return photo;
	}
}
