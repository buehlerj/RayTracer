import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.Matrix;

public class Scene {
	private Camera camera;
	private Matrix ambient;
	private Matrix model;
	private ArrayList<Matrix> lights;
	private ArrayList<Matrix> spheres;
	
	public Scene(String inputFileScene) {
		camera = new Camera();
		camera.read(inputFileScene);
		lights = new ArrayList<Matrix>();
		spheres = new ArrayList<Matrix>();
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
					 Matrix light = new Matrix(new double[][] { { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() } });
					 lights.add(light);
					break;
				case "sphere":
					Matrix sphere = new Matrix(new double[][] { { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() } });
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

	public ArrayList<Matrix> getLights() {
		return lights;
	}

	public void setLights(ArrayList<Matrix> lights) {
		this.lights = lights;
	}

	public ArrayList<Matrix> getSphere() {
		return spheres;
	}

	public void setSphere(ArrayList<Matrix> spheres) {
		this.spheres = spheres;
	}

	public Matrix getModel() {
		return model;
	}

	public void setModel(Matrix model) {
		this.model = model;
	}
}
