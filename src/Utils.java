import Jama.Matrix;

public class Utils {
	public static String MatrixToString(Matrix m) {
		String matrix = "";
		for (int j = 0; j < m.getRowDimension(); j++) {
			for (int i = 0; i < m.getColumnDimension(); i++) {
				matrix += m.get(j, i) + " ";
			}
			matrix += "\n";
		}
		return matrix;
	}

	public static String MatrixToStringOneLine(Matrix m) {
		String matrix = "";
		for (int j = 0; j < m.getRowDimension() - 1; j++) {
			for (int i = 0; i < m.getColumnDimension(); i++) {
				matrix += m.get(j, i) + " ";
			}
			matrix += ", ";
		}
		for (int i = 0; i < m.getColumnDimension(); i++) {
			matrix += m.get(m.getRowDimension() - 1, i) + " ";
		}
		return matrix;
	}

	public static double dotProduct(Matrix m1, Matrix m2) {
		return m1.get(0, 0) * m2.get(0, 0) + m1.get(1, 0) * m2.get(1, 0) + m1.get(2, 0) * m2.get(2, 0);
	}

	public static Matrix pairwiseProduct(Matrix m1, Matrix m2) {
		double a = m1.get(0, 0) * m2.get(0, 0);
		double b = m1.get(1, 0) * m2.get(1, 0);
		double c = m1.get(2, 0) * m2.get(2, 0);
		return new Matrix(new double[][] { {a}, {b}, {c} });
	}

	public static Matrix pairwiseMinus(Matrix m1, Matrix m2) {
		double a = m1.get(0, 0) - m2.get(0, 0);
		double b = m1.get(1, 0) - m2.get(1, 0);
		double c = m1.get(2, 0) - m2.get(2, 0);
		return new Matrix(new double[][] { {a}, {b}, {c} });
	}
}
