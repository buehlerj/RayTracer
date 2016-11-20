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

	public boolean write(String outputFileName, String nameModifier) {
		if (nameModifier.length() > 0)
			nameModifier = "_" + nameModifier;
		String[] fileSplit = outputFileName.split("\\.");
		String newFileName = "";
		for (int i = 0; i < fileSplit.length - 1; i++) {
			newFileName += fileSplit[i];
		}
		newFileName += nameModifier + "." + fileSplit[fileSplit.length - 1];
		try {
			PrintWriter output = new PrintWriter(newFileName);
			output.println("P3");
			output.println(width + " " + height + " " + 255);
			output.print(toString());
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Writing file: " + newFileName);
		}
		return true;
	}

	@Override
	public String toString() {
		String allPixelsString = "";
		for (int i = 0; i < pixels.size(); i++) {
			allPixelsString += pixels.get(i) + " ";
			if (i % width == width - 1)
				allPixelsString += "\n";
		}
		return allPixelsString;
	}
}
