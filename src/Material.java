import Jama.Matrix;

public class Material {
	String name = "";
	Model model;
	double Ns = 0;
	Matrix Ka = new Matrix(new double[][] { {.9}, {.9}, {.9} });
	Matrix Kd = new Matrix(new double[][] { {.9}, {.9}, {.9} });
	Matrix Ks = new Matrix(new double[][] { {.9}, {.9}, {.9} });
	double d = 0;
	double illum;

	Material(String name, Model model) {
		this.name = name;
		this.model = model;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public double getNs() {
		return Ns;
	}

	public void setNs(double ns) {
		Ns = ns;
	}

	public Matrix getKa() {
		return Ka;
	}

	public void setKa(double r, double g, double b) {
		Ka = new Matrix(new double[][] { {r}, {g}, {b} });
	}

	public Matrix getKd() {
		return Kd;
	}

	public void setKd(double r, double g, double b) {
		Kd = new Matrix(new double[][] { {r}, {g}, {b} });
	}

	public Matrix getKs() {
		return Ks;
	}

	public void setKs(double r, double g, double b) {
		Ks = new Matrix(new double[][] { {r}, {g}, {b} });
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getIllum() {
		return illum;
	}

	public void setIllum(double illum) {
		this.illum = illum;
	}

	public String toString() {
		String materialString = name + "\n";
		materialString += "Ns: " + Ns + "\n";
		materialString += "Ka: " + Utils.MatrixToStringOneLine(Ka) + "\n";
		materialString += "Kd: " + Utils.MatrixToStringOneLine(Kd) + "\n";
		materialString += "Ks: " + Utils.MatrixToStringOneLine(Ks) + "\n";
		materialString += "d: " + d + "\n";
		materialString += "illum: " + illum;
		return materialString;
	}
}
