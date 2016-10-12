import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Model {
	static String header = "";
	static ArrayList<Vector> vertices = new ArrayList<Vector>();
	static ArrayList<Face> faces = new ArrayList<Face>();
	static int number_of_vertices = 0;
	static int number_of_faces = 0;

	Model() {}

	public boolean read(String input_file_name) {
		File input_file = new File(input_file_name);
		boolean reading_header = true;
		int vector_count = 0;
		int face_count = 0;
		try {
			Scanner input = new Scanner(input_file );
			while (input.hasNext()) {
				if (reading_header) {
					String next = input.nextLine();
					header += next + "\n";
					if (next.equals("end_header"))
						reading_header = false;
					if (next.contains("element vector"))
						number_of_vertices = Integer.valueOf(next.split("\\s+")[2]);
					if (next.contains("element face"))
						number_of_faces = Integer.valueOf(next.split("\\s+")[2]);
				}
				else {
					if (vector_count < number_of_vertices) {
						Vector input_vector = new Vector(input.nextDouble(), input.nextDouble(), input.nextDouble());
						vertices.add(input_vector);
						vector_count++;
					}
					else if (face_count < number_of_faces) {
						String line = input.nextLine();
						if (!line.isEmpty()) {
							ArrayList<Integer> face_vertices = new ArrayList<Integer>();
							for (String s : line.split("\\s"))
								face_vertices.add(Integer.valueOf(s));
							if (!face_vertices.isEmpty()) {
								Face input_face = new Face(face_vertices);
								faces.add(input_face);
								face_count++;
							}
						}
					}
					else
						input.nextLine();
				}
			}
			input.close();
		} catch (FileNotFoundException e) {System.err.println("Problem Read file: " + input_file_name);return false;}
		return true;
	}

	public boolean write(String output_file_name, String name_modifier) {
		String[] file_split = output_file_name.split("\\.");
		String new_file_name = "";
		for (int i = 0; i < file_split.length - 1; i++) {
			new_file_name += file_split[i];
		}
		new_file_name += "_" + name_modifier + "." + file_split[file_split.length - 1];
		try {
			PrintWriter output = new PrintWriter(new_file_name);
			output.print(header);
			for (Vector vector : vertices)
				output.println(vector);
			for (Face face : faces)
				output.println(face);
			output.close();
		} catch (FileNotFoundException e) {System.err.println("Problem Writing file: " + new_file_name);}
		return true;
	}

	public boolean center(String output_file_name) {
		Vector average = getAverageVector();
		for (Vector vector : vertices) {
			vector.subtract(average);
		}
		return write(output_file_name, "centered");
	}

	public boolean whiten(String output_file_name) {
		Vector scalar = getStandardDeviationVector();
		for (Vector vector : vertices)
			vector.divide(scalar);
		return write(output_file_name, "rounded");
	}

	public void printConsoleInfo(String command) {
		DecimalFormat df = new DecimalFormat("#.######");
		Vector min_vector = getMinVector();
		Vector max_vector = getMaxVector();
		Vector standard_deviation = getStandardDeviationVector();
		System.out.println("=== " + command);
		System.out.println(number_of_vertices + " vertices, " + number_of_faces + " polygons");
		System.out.println("Mean Vector = " + getAverageVector().toStringVector());
		System.out.println("Bounding Box: " + 
				df.format(min_vector.getX()) + " <= x <= " + df.format(max_vector.getX()) + ", " +
				df.format(min_vector.getY()) + " <= y <= " + df.format(max_vector.getY()) + ", " + 
				df.format(min_vector.getZ()) + " <= z <= " + df.format(max_vector.getZ()));
		System.out.println("Standard Deviations: x = " + df.format(standard_deviation.getX()) + ", y = " + df.format(standard_deviation.getY()) + ", z = " + df.format((standard_deviation.getZ())));
	}

	private Vector getAverageVector() {
		Vector average = new Vector();
		Vector divisor = new Vector(number_of_vertices, number_of_vertices, number_of_vertices);
		for (Vector vector: vertices)
			average.add(vector);
		average.divide(divisor);
		return average;
	}

	private Vector getMinVector() {
		ArrayList<Double> x_values = new ArrayList<Double>();
		ArrayList<Double> y_values = new ArrayList<Double>();
		ArrayList<Double> z_values = new ArrayList<Double>();
		for (Vector v : vertices) {
			x_values.add(v.getX());
			y_values.add(v.getY());
			z_values.add(v.getZ());
		}
		Collections.sort(x_values);
		Collections.sort(y_values);
		Collections.sort(z_values);
		return new Vector(x_values.get(0), y_values.get(0), z_values.get(0));
	}

	private Vector getMaxVector() {
		ArrayList<Double> x_values = new ArrayList<Double>();
		ArrayList<Double> y_values = new ArrayList<Double>();
		ArrayList<Double> z_values = new ArrayList<Double>();
		for (Vector v : vertices) {
			x_values.add(v.getX());
			y_values.add(v.getY());
			z_values.add(v.getZ());
		}
		Collections.sort(x_values);
		Collections.sort(y_values);
		Collections.sort(z_values);
		return new Vector(x_values.get(x_values.size() - 1), y_values.get(y_values.size() - 1), z_values.get(z_values.size() - 1));
	}

	private Vector getStandardDeviationVector() {
		Vector average = getAverageVector();
		ArrayList<Vector> sd_vertices = new ArrayList<Vector>();
		for (int i = 0; i < vertices.size(); i++) {
			double x = Math.pow((vertices.get(i).getX() - average.getX()), 2);
			double y = Math.pow((vertices.get(i).getY() - average.getY()), 2);
			double z = Math.pow((vertices.get(i).getZ() - average.getZ()), 2);
			sd_vertices.add(new Vector(x, y, z));
		}
		Vector standard_deviation = new Vector();
		Vector divisor = new Vector(number_of_vertices, number_of_vertices, number_of_vertices);
		for (Vector vector: sd_vertices)
			standard_deviation.add(vector);
		standard_deviation.divide(divisor);
		standard_deviation.squareRoot();
		return standard_deviation;
	}
}
