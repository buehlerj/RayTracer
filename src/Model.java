import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Model {
	String header = "";
	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	ArrayList<Face> faces = new ArrayList<Face>();
	int number_of_vertices = 0;
	int number_of_faces = 0;

	Model() {
	}

	public boolean read(String input_file_name) {
		File input_file = new File(input_file_name);
		boolean reading_header = true;
		int vertex_count = 0;
		int face_count = 0;
		try {
			Scanner input = new Scanner(input_file);
			while (input.hasNext()) {
				if (reading_header) {
					String next = input.nextLine();
					header += next + "\n";
					if (next.equals("end_header"))
						reading_header = false;
					if (next.contains("element vertex"))
						number_of_vertices = Integer.valueOf(next.split("\\s+")[2]);
					if (next.contains("element face"))
						number_of_faces = Integer.valueOf(next.split("\\s+")[2]);
				} else {
					if (vertex_count < number_of_vertices) {
						Vertex input_vertex = new Vertex(input.nextDouble(), input.nextDouble(), input.nextDouble());
						vertices.add(input_vertex);
						vertex_count++;
					} else if (face_count < number_of_faces) {
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
					} else
						input.nextLine();
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + input_file_name);
			return false;
		}
		return true;
	}

	@SuppressWarnings("resource")
	public boolean write(String output_file_name, String name_modifier) {
		if (name_modifier.length() > 0)
			name_modifier = "_" + name_modifier;
		String[] file_split = output_file_name.split("\\.");
		String new_file_name = "";
		for (int i = 0; i < file_split.length - 1; i++) {
			new_file_name += file_split[i];
		}
		new_file_name += name_modifier + "." + file_split[file_split.length - 1];
		try {
			PrintWriter output = new PrintWriter(new_file_name);
			switch (file_split[file_split.length - 1]) {
			case "ply":
				output.print(header);
				for (Vertex vertex : vertices)
					output.println(vertex);
				for (Face face : faces)
					output.println(face);
				break;
			case "ppm":
				output.print("unimplemented ppm");
				break;
			default:
				return false;
			}
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Writing file: " + new_file_name);
		}
		return true;
	}

	public boolean center(String output_file_name) {
		Vertex average = getAverageVertex();
		for (Vertex vertex : vertices) {
			vertex.subtract(average);
		}
		return write(output_file_name, "centered");
	}

	public boolean whiten(String output_file_name) {
		Vertex scalar = getStandardDeviationVertex();
		for (Vertex vertex : vertices)
			vertex.divide(scalar);
		return write(output_file_name, "rounded");
	}

	public void printConsoleInfo(String command) {
		DecimalFormat df = new DecimalFormat("#.######");
		Vertex min_vertex = getMinVertex();
		Vertex max_vertex = getMaxVertex();
		Vertex standard_deviation = getStandardDeviationVertex();
		System.out.println("=== " + command);
		System.out.println(number_of_vertices + " vertices, " + number_of_faces + " polygons");
		System.out.println("Mean Vertex = " + getAverageVertex().toStringVertex());
		System.out.println("Bounding Box: " + df.format(min_vertex.getX()) + " <= x <= " + df.format(max_vertex.getX())
				+ ", " + df.format(min_vertex.getY()) + " <= y <= " + df.format(max_vertex.getY()) + ", "
				+ df.format(min_vertex.getZ()) + " <= z <= " + df.format(max_vertex.getZ()));
		System.out.println("Standard Deviations: x = " + df.format(standard_deviation.getX()) + ", y = "
				+ df.format(standard_deviation.getY()) + ", z = " + df.format((standard_deviation.getZ())));
	}

	private Vertex getAverageVertex() {
		Vertex average = new Vertex();
		Vertex divisor = new Vertex(number_of_vertices, number_of_vertices, number_of_vertices);
		for (Vertex vertex : vertices)
			average.add(vertex);
		average.divide(divisor);
		return average;
	}

	private Vertex getMinVertex() {
		ArrayList<Double> x_values = new ArrayList<Double>();
		ArrayList<Double> y_values = new ArrayList<Double>();
		ArrayList<Double> z_values = new ArrayList<Double>();
		for (Vertex v : vertices) {
			x_values.add(v.getX());
			y_values.add(v.getY());
			z_values.add(v.getZ());
		}
		Collections.sort(x_values);
		Collections.sort(y_values);
		Collections.sort(z_values);
		return new Vertex(x_values.get(0), y_values.get(0), z_values.get(0));
	}

	private Vertex getMaxVertex() {
		ArrayList<Double> x_values = new ArrayList<Double>();
		ArrayList<Double> y_values = new ArrayList<Double>();
		ArrayList<Double> z_values = new ArrayList<Double>();
		for (Vertex v : vertices) {
			x_values.add(v.getX());
			y_values.add(v.getY());
			z_values.add(v.getZ());
		}
		Collections.sort(x_values);
		Collections.sort(y_values);
		Collections.sort(z_values);
		return new Vertex(x_values.get(x_values.size() - 1), y_values.get(y_values.size() - 1),
				z_values.get(z_values.size() - 1));
	}

	private Vertex getStandardDeviationVertex() {
		Vertex average = getAverageVertex();
		ArrayList<Vertex> sd_vertices = new ArrayList<Vertex>();
		for (int i = 0; i < vertices.size(); i++) {
			double x = Math.pow((vertices.get(i).getX() - average.getX()), 2);
			double y = Math.pow((vertices.get(i).getY() - average.getY()), 2);
			double z = Math.pow((vertices.get(i).getZ() - average.getZ()), 2);
			sd_vertices.add(new Vertex(x, y, z));
		}
		Vertex standard_deviation = new Vertex();
		Vertex divisor = new Vertex(number_of_vertices, number_of_vertices, number_of_vertices);
		for (Vertex vertex : sd_vertices)
			standard_deviation.add(vertex);
		standard_deviation.divide(divisor);
		standard_deviation.squareRoot();
		return standard_deviation;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String new_header) {
		header = new_header;
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public void addVertex(Vertex vertex) {
		vertices.add(vertex);
	}

	public void removeVertex(Vertex vertex) {
		vertices.remove(vertex);
	}

	public ArrayList<Face> getFaces() {
		return faces;
	}

	public void addFace(Face face) {
		faces.add(face);
	}

	public void removeFace(Face face) {
		faces.remove(face);
	}

	public int getNumberOfVertices() {
		return number_of_vertices;
	}

	public void setNumberOfVertices(int new_number_of_vertices) {
		number_of_vertices = new_number_of_vertices;
	}

	public int getNumberOfFaces() {
		return number_of_faces;
	}

	public void setNumberOfFaces(int new_number_of_faces) {
		number_of_faces = new_number_of_faces;
	}
}
