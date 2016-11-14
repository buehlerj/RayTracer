import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.Matrix;

public class Scene {
	private Camera camera;
	private Matrix ambient;
	private Matrix model;
	private ArrayList<Light> lights;
	private ArrayList<Sphere> spheres;
	
	public Scene(String inputFileScene) {
		camera = new Camera();
		camera.read(inputFileScene);
		lights = new ArrayList<Light>();
		spheres = new ArrayList<Sphere>();
		File inputFile = new File(inputFileScene);
		try {
			Scanner input = new Scanner(inputFile);
			while (input.hasNext()) {
				String keyTerm = input.next();
				switch (keyTerm) {
				case "ambient":
					ambient = new Matrix(new double[][] { { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() } });
					break;
				case "light":
					Light light = new Light(input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble());
					 lights.add(light);
					break;
				case "sphere":
					Sphere sphere = new Sphere(input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble());
					spheres.add(sphere);
					break;
				case "model":
					break;
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + inputFileScene);
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Matrix getAmbient() {
		return ambient;
	}

	public void setAmbient(Matrix ambient) {
		this.ambient = ambient;
	}

	public ArrayList<Light> getLights() {
		return lights;
	}

	public void setLights(ArrayList<Light> lights) {
		this.lights = lights;
	}

	public ArrayList<Sphere> getSpheres() {
		return spheres;
	}

	public void setSphere(ArrayList<Sphere> spheres) {
		this.spheres = spheres;
	}

	public Matrix getModel() {
		return model;
	}

	public void setModel(Matrix model) {
		this.model = model;
	}
}
