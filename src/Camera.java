import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Jama.Matrix;

public class Camera {
	private Matrix eye;
	private Matrix look;
	private Matrix up;
	private double d;
	private int boundL;
	private int boundB;
	private int boundR;
	private int boundT;
	private int resx;
	private int resy;
	private Matrix cameraW;
	private Matrix cameraU;
	private Matrix cameraV;

	public Camera() {
		eye = new Matrix(1, 3);
		look = new Matrix(1, 3);
		up = new Matrix(1, 3);
		cameraW = new Matrix(1, 3);
		cameraU = new Matrix(1, 3);
		cameraV = new Matrix(1, 3);
	}

	public boolean read(String inputFileName) {
		File inputFile = new File(inputFileName);
		try {
			Scanner input = new Scanner(inputFile);
			while (input.hasNext()) {
				String keyTerm = input.next();
				switch (keyTerm) {
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
					boundL = input.nextInt();
					boundB = input.nextInt();
					boundR = input.nextInt();
					boundT = input.nextInt();
					break;
				case "res":
					resx = input.nextInt();
					resy = input.nextInt();
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
		cameraW = eye.minus(look);
		cameraU = Vertex.crossProduct(up, cameraW);
		cameraW = cameraW.timesEquals(1 / cameraW.normF());
		cameraU = cameraU.timesEquals(1 / cameraU.normF());
		cameraV = Vertex.crossProduct(cameraW, cameraU);
		return true;
	}

	public Matrix getCameraW() {
		return cameraW;
	}

	public void setCameraW(Matrix cameraW) {
		this.cameraW = cameraW;
	}

	public Matrix getCameraU() {
		return cameraU;
	}

	public void setCameraU(Matrix cameraU) {
		this.cameraU = cameraU;
	}

	public Matrix getCameraV() {
		return cameraV;
	}

	public void setCameraV(Matrix cameraV) {
		this.cameraV = cameraV;
	}

	public Matrix getEye() {
		return eye;
	}

	public void setEye(Matrix newEye) {
		eye = newEye;
	}

	public Matrix getLook() {
		return look;
	}

	public void setLook(Matrix newLook) {
		look = newLook;
	}

	public Matrix getUp() {
		return up;
	}

	public void setUp(Matrix newUp) {
		up = newUp;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public int[] getBounds() {
		return new int[] { boundL, boundB, boundR, boundT };
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
		camera += "\nUp: " + boundT + ", Right: " + boundR + ", Down: " + boundB + ", Left: " + boundL;
		camera += "\nResX: " + resx + ", ResY: " + resy;
		return camera;
	}
}
