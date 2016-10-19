import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Picture {
	int x;
	int y;
	ArrayList<Pixel> pixels;

	Picture(int x, int y) {
		this.x = x;
		this.y = y;
		pixels = new ArrayList<Pixel>();
		for (int i = 0; i < x * y; i++) {
			pixels.add(new Pixel());
		}
	}

	public boolean write(String output_file_name, String name_modifier) {
		if (name_modifier.length() > 0)
			name_modifier = "_" + name_modifier;
		String[] file_split = output_file_name.split("\\.");
		String new_file_name = "";
		for (int i = 0; i < file_split.length - 1; i++) {
			new_file_name += file_split[i];
		}
		new_file_name += name_modifier + "." + file_split[file_split.length - 1];
		try {
			PrintWriter output = new PrintWriter(new_file_name);
			output.println("P3");
			output.println(x + " " + y + " " + 255);
			output.print(toString());
			output.close();
		} catch (FileNotFoundException e) {System.err.println("Problem Writing file: " + new_file_name);}
		return true;
	}

	@Override
	public String toString() {
		String all_pixels_string = "";
		for (int i = 0; i < x * y; i++) {
			all_pixels_string += pixels.get(i) + " ";
			if (i % y == y - 1)
				all_pixels_string += "\n";
		}
		return all_pixels_string;
	}
}
