import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Camera {
	private Vertex eye;
	private Vertex look;
	private Vertex up;
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
					eye = new Vertex(input.nextInt(), input.nextInt(), input.nextInt());
					break;
				case "look":
					look = new Vertex(input.nextInt(), input.nextInt(), input.nextInt());
					break;
				case "up":
					up = new Vertex(input.nextInt(), input.nextInt(), input.nextInt());
					break;
				case "d":
					d = input.nextInt();
					break;
				case "bounds":
					boundD = input.nextInt();
					boundL = input.nextInt();
					boundU = input.nextInt();
					boundR = input.nextInt();
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

	public Vertex getEye() {
		return eye;
	}

	public void setEye(Vertex new_eye) {
		eye = new_eye;
	}

	public Vertex getLook() {
		return look;
	}

	public void setLook(Vertex new_look) {
		look = new_look;
	}

	public Vertex getUp() {
		return up;
	}

	public void setUp(Vertex new_up) {
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
}
