import Jama.Matrix;

public class Pixel {
	private int r = 0;
	private int g = 0;
	private int b = 0;
	private String type;
	private int index;

	Pixel() {
	}

	Pixel(Matrix color) {
		if (color.get(0, 0) > 1) {
			this.r = (int) color.get(0, 0);
			this.g = (int) color.get(1, 0);
			this.b = (int) color.get(2, 0);
		} else {
			this.r = (int) (color.get(0, 0) * 255);
			this.g = (int) (color.get(1, 0) * 255);
			this.b = (int) (color.get(2, 0) * 255);
		}
	}

	Pixel(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	Pixel(double r, double g, double b) {
		this.r = (int) Math.round(r);
		this.g = (int) Math.round(g);
		this.b = (int) Math.round(b);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean equals(Pixel a) {
		return r == a.r && g == a.g && b == a.b;
	}

	public void modify(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public String toString() {
		return r + " " + g + " " + b;
	}
}
