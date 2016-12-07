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
		if (color != null) {
			this.r = Math.min(255, (int) (color.get(0, 0) * 255));
			this.g = Math.min(255, (int) (color.get(1, 0) * 255));
			this.b = Math.min(255, (int) (color.get(2, 0) * 255));
		}
	}

	Pixel(int r, int g, int b) {
		if (r > 1 && g > 1 && b > 1) {
		this.r = Math.min(255, r);
		this.g = Math.min(255, g);
		this.b = Math.min(255, b);
		} else {
			this.r = Math.min(255, r * 255);
			this.g = Math.min(255, g * 255);
			this.b = Math.min(255, b * 255);
		}
	}

	Pixel(double r, double g, double b) {
		if (r > 1 && g > 1 && b > 1) {
			this.r = Math.min(255, (int) Math.round(r));
			this.g = Math.min(255, (int) Math.round(g));
			this.b = Math.min(255, (int) Math.round(b));
		} else {
			this.r = Math.min(255, (int) Math.round(r * 255));
			this.g = Math.min(255, (int) Math.round(g * 255));
			this.b = Math.min(255, (int) Math.round(b * 255));
		}
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

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

	public boolean equals(Pixel a) {
		return r == a.r && g == a.g && b == a.b;
	}

	public boolean isEmpty() {
		return r == 0 && g == 0 && g == 0;
	}

	public void modify(int r, int g, int b) {
		this.r = Math.min(255, r);
		this.g = Math.min(255, g);
		this.b = Math.min(255, b);
	}

	public String toString() {
		return r + " " + g + " " + b;
	}
}
