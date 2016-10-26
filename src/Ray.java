
public class Ray {
	Vertex origin;
	Vertex D;
	double t;
	
	Ray(Vertex origin, Vertex D) {
		this.origin = origin;
		this.D = D;
		this.D.normalize();
	}

	public Vertex getOrigin() {
		return origin;
	}

	public void setOrigin(Vertex new_origin) {
		origin = new_origin;
	}

	public Vertex getD() {
		return D;
	}
	
	public void setD(Vertex new_D) {
		D = new_D;
	}
}
