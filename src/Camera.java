import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Camera {
	private Vertex eye;
	private Vertex look;
	private Vertex up;
	private int d;
	private int boundu;
	private int boundr;
	private int boundd;
	private int boundl;
	private int resx;
	private int resy;

	Camera() {
		
	}

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
						boundu = input.nextInt();
						boundr = input.nextInt();
						boundd = input.nextInt();
						boundl = input.nextInt();
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
		} catch (FileNotFoundException e) {System.err.println("Problem Read file: " + input_file_name);return false;}
		return true;		
	}

	public Vertex getEye() {
		return eye;
	}

	public Vertex getLook() {
		return look;
	}

	public Vertex getUp() {
		return up;
	}

	public int getD() {
		return d;
	}

	public int[] getBounds() {
		return new int[]{boundu, boundr, boundd, boundl};
	}

	public int[] getRes() {
		return new int[]{resx, resy};
	}
}
