import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.Matrix;

public class Scene {
	private Matrix ambient;
	private ArrayList<Light> lights;

	public Scene() {
		ambient = new Matrix(1, 3);
		lights = new ArrayList<>();
	}

	public Scene(Matrix ambient) {
		this.ambient = ambient;
		lights = new ArrayList<>();
	}

	public boolean read(String inputFileName) {
		File inputFile = new File(inputFileName);
		String keyTerm;
		try {
			Scanner input = new Scanner(inputFile);
			while (input.hasNextLine()) {
				keyTerm = input.next();
				switch (keyTerm) {
				case "ambient":
					ambient = new Matrix(new double[][] { { input.nextDouble() }, { input.nextDouble() }, { input.nextDouble() } });
					break;
				case "light":
					Light light = new Light(input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble());
					lights.add(light);
					break;
				default:
					keyTerm = input.nextLine();
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + inputFileName);
			return false;
		}
		return true;
	}

	public ArrayList<Model> readModels(String inputFileName) {
		ArrayList<Model> models = new ArrayList<>();
		File inputFile = new File(inputFileName);
		String keyTerm;
		try {
			Scanner input = new Scanner(inputFile);
			while (input.hasNext()) {
				keyTerm = input.next();
				switch (keyTerm) {
				case "model":
					Model m = new Model(input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble());
					m.read(input.next());
					m.rotateModel();
					models.add(m);
					break;
				default:
					keyTerm = input.nextLine();
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + inputFileName);
			return null;
		}
		return models;
	}

	public ArrayList<Sphere> readSpheres(String inputFileName) {
		ArrayList<Sphere> spheres = new ArrayList<>();
		File inputFile = new File(inputFileName);
		String keyTerm;
		try {
			Scanner input = new Scanner(inputFile);
			while (input.hasNext()) {
				keyTerm = input.next();
				switch (keyTerm) {
				case "sphere":
					Sphere sphere = new Sphere(input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextDouble());
					spheres.add(sphere);
					break;
				default:
					keyTerm = input.nextLine();
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + inputFileName);
			return null;
		}
		return spheres;
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

	public void addLight(Light light) {
		lights.add(light);
	}

	public void removeLight(Light light) {
		lights.remove(light);
	}

	public String toString() {
		String sceneString = "Ambient: " + Utils.MatrixToStringOneLine(ambient) + "\n";
		for (Light l : lights) {
			sceneString += "Light:\n";
			sceneString += "   Coordinates: " + Utils.MatrixToStringOneLine(l.getCoordinates()) + "\n";
			sceneString += "   Color: " + Utils.MatrixToStringOneLine(l.getColor()) + "\n\n";
		}
		return sceneString;
	}
}
