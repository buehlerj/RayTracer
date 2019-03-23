import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

public class Picture {
	int width;
	int height;
	Pixel[][] pixels;

	Picture(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new Pixel[height][width];
	}

	public void addToPixels(int i, int j, Pixel p) {
		pixels[j][i] = p;
	}

	public boolean write(String outputFileName, String nameModifier) {
		if (nameModifier.length() > 0) {
			nameModifier = "_" + nameModifier;
		}
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

	public void addStars() {
		int starSize = 0;
		int hue = 0;
		double random;
		if (height <= 720) {
			starSize = 1;
		} else if (height <= 1080) {
			starSize = 2;
		} else {
			starSize = 3;
		}
		for (int i = 0; i < height; i += starSize) {
			for (int j = 0; j < width; j += starSize) {
				random = Math.random() * 100;
				if (pixels[i][j].isEmpty() && random < 1) {
					random = Math.random();
					if (random >= .75) {
						hue = 255;
					} else if (random >= .5 && random < .75) {
						hue = 200;
					} else if (random >= .25 && random < .5) {
						hue = 128;
					} else {
						hue = 64;
					}
					for (int q = 0; q < starSize; q++) {
						for (int u = 0; u < starSize; u++) {
							pixels[i + q][j + u].modify(hue, hue, hue);
						}
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return Arrays.deepToString(pixels).replace("[", "").replace("]", "\n").replace(",", "");
	}
}
