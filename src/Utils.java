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
}
