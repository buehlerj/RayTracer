
public class Pixel {
	public int r;
	public int g;
	public int b;

	Pixel() {
		this.r = 239;
		this.g = 239;
		this.b = 239;
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

	public void modify(int new_r, int new_g, int new_b) {
		r = new_r;
		g = new_g;
		b = new_b;
	}
}
