import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.Matrix;

public class Scene {
	private Matrix ambient;
	private ArrayList<Light> lights;
	private ArrayList<Sphere> spheres;

	public Scene() {
		ambient = new Matrix(1, 3);
		lights = new ArrayList<Light>();
		spheres = new ArrayList<Sphere>();
	}

	public boolean read(String inputFileName) {
		ArrayList<Model> models = new ArrayList<>();
		File inputFile = new File(inputFileName);
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
					Model m = new Model();
					double a = input.nextDouble();
					double b = input.nextDouble();
					double c = input.nextDouble();
					double d = input.nextDouble();
					double e = input.nextDouble();
					double f = input.nextDouble();
					double g = input.nextDouble();
					m.read(input.next());
					models.add(m);
					break;
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
		try {
			Scanner input = new Scanner(inputFile);
			while (input.hasNext()) {
				String keyTerm = input.next();
				switch (keyTerm) {
				case "model":
					Model m = new Model();
					double a = input.nextDouble();
					double b = input.nextDouble();
					double c = input.nextDouble();
					double d = input.nextDouble();
					double e = input.nextDouble();
					double f = input.nextDouble();
					double g = input.nextDouble();
					m.read(input.next());
					models.add(m);
					break;
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + inputFileName);
			return null;
		}
		return models;
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

	public String toString() {
		String sceneString = "";
		sceneString += "Ambient: " + Utils.MatrixToStringOneLine(ambient) + "\n";
		for (Light l : lights) {
			sceneString += "Light:\n";
			sceneString += "   Coordinates: " + Utils.MatrixToStringOneLine(l.getCoordinates()) + "\n";
			sceneString += "   Color: " + Utils.MatrixToStringOneLine(l.getColor()) + "\n\n";
		}
		for (Sphere s : spheres) {
			sceneString += "Sphere:\n";
			sceneString += "   Coordinates: " + Utils.MatrixToStringOneLine(s.getCoordinates()) + "\n";
			sceneString += "   Radius: " + s.getRadius() + "\n";
			sceneString += "   Color: " + Utils.MatrixToStringOneLine(s.getColor()) + "\n\n";
		}
		return sceneString;
	}
}
