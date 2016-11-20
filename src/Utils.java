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
}
