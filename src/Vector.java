
public class Vector {
	Vertex start;
	Vertex end;
	
	Vector (Vertex start, Vertex end) {
		this.start = start;
		this.end = end;
	}

	public double length () {
		Vertex centered = end.minus(start);
		return centered.length();
	}

	public Vertex getPoint (double distance) {
		Vertex point = end.minus(start);
		point.normalize();
		point.scale(distance * length());
		point.add(start);
		return point;
	}
}
