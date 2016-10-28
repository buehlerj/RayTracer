import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Jama.Matrix;

public class Camera {
	private Matrix eye;
	private Matrix look;
	private Matrix up;
	private double d;
	private int boundU;
	private int boundR;
	private int boundD;
	private int boundL;
	private int resx;
	private int resy;

	public boolean read(String input_file_name) {
		File input_file = new File(input_file_name);
		try {
			Scanner input = new Scanner(input_file);
			while (input.hasNext()) {
				String key_term = input.next();
				switch (key_term) {
				case "eye":
					eye = new Matrix(new double[][] { { input.nextInt() }, { input.nextInt() }, { input.nextInt() } });
					break;
				case "look":
					look = new Matrix(new double[][] { { input.nextInt() }, { input.nextInt() }, { input.nextInt() } });
					break;
				case "up":
					up = new Matrix(new double[][] { { input.nextInt() }, { input.nextInt() }, { input.nextInt() } });
					break;
				case "d":
					d = input.nextInt() * -1;
					break;
				case "bounds":
					boundD = input.nextInt();
					boundR = input.nextInt();
					boundU = input.nextInt();
					boundL = input.nextInt();
					break;
				case "res":
					resx = input.nextInt();
					resy = input.nextInt();
					break;
				default:
					System.err.println("Error with Camera file format.");
					return false;
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + input_file_name);
			return false;
		}
		return true;
	}

	public Matrix getEye() {
		return eye;
	}

	public void setEye(Matrix new_eye) {
		eye = new_eye;
	}

	public Matrix getLook() {
		return look;
	}

	public void setLook(Matrix new_look) {
		look = new_look;
	}

	public Matrix getUp() {
		return up;
	}

	public void setUp(Matrix new_up) {
		up = new_up;
	}

	public double getD() {
		return d;
	}

	public int[] getBounds() {
		return new int[] { boundU, boundR, boundD, boundL };
	}

	public int[] getRes() {
		return new int[] { resx, resy };
	}

	public String toString() {
		String camera = "";
		camera += "Eye: " + eye.get(0, 0) + ", " + eye.get(1, 0) + ", " + eye.get(2, 0);
		camera += "\nLook: " + look.get(0, 0) + ", " + look.get(1, 0) + ", " + look.get(2, 0);
		camera += "\nUp: " + up.get(0, 0) + ", " + up.get(1, 0) + ", " + up.get(2, 0);
		camera += "\nD: " + d;
		camera += "\nUp: " + boundU + ", Right: " + boundR + ", Down: " + boundD + ", Left: " + boundL;
		camera += "\nResX: " + resx + ", ResY: " + resy;
		return camera;
	}
}
