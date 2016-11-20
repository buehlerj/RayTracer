
public class Pixel {
	public int r = 239;
	public int g = 239;
	public int b = 239;

	Pixel() {
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

	public String toString() {
		return r + " " + g + " " + b;
	}

	public boolean equals(Pixel a) {
		return r == a.r && g == a.g && b == a.b;
	}

	public void modify(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
