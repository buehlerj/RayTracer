import java.text.DecimalFormat;

public class Pixel {
	public double r = 239;
	public double g = 239;
	public double b = 239;

	Pixel() {
	}

	Pixel(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat("#.######");
		return df.format(r) + " " + df.format(g) + " " + df.format(b);
	}

	public void modify(double new_r, double new_g, double new_b) {
		r = new_r;
		g = new_g;
		b = new_b;
	}
}
