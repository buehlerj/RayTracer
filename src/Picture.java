import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Picture {
	int width;
	int height;
	ArrayList<Pixel> pixels = new ArrayList<Pixel>();

	Picture(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public ArrayList<Pixel> getPixels() {
		return pixels;
	}

	public void addToPixels(Pixel p) {
		pixels.add(p);
	}

	public void removeFromPixels(Pixel p) {
		pixels.remove(p);
	}

	public void setPixels(ArrayList<Pixel> newPixels) {
		pixels = newPixels;
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
			output.println(width + " " + height + " " + 255);
			output.print(toString());
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Writing file: " + new_file_name);
		}
		return true;
	}

	@Override
	public String toString() {
		String all_pixels_string = "";
		for (int i = 0; i < pixels.size(); i++) {
			all_pixels_string += pixels.get(i) + " ";
			if (i % width == width - 1)
				all_pixels_string += "\n";
		}
		return all_pixels_string;
	}
}
