
public class Iris {
	
	// Attributs
	public float sepal_length;
	public float sepal_width;
	public float petal_length;
	public float petal_width;
	
	// Resultat
	public String type;
	
	/**
	 * Construteur d'Iris
	 * @param sl Longueur du sepal
	 * @param sw Largeur du sepal
	 * @param pl Longueur du petal
	 * @param pw Largeur du petal
	 * @param t  Type de l'Iris
	 */
	public Iris(float sl, float sw, float pl, float pw, String t) {
		sepal_length = sl;
		sepal_width = sw;
		petal_length = pl;
		petal_width = pw;	
		type = t;
	}
	
	/**
	 * Méthode d'affichage d'un Iris
	 */
	public String toString() {
		return type+" : [sepal length : "+sepal_length+", sepal width : "+sepal_width+", petal length : "+petal_length+", petal_width : "+petal_width+"]";
	}
	
	/**
	 * Méthode retournant la distance entre deux Iris
	 * @param other Iris à comparer
	 * @param power Type de distance voulue (1 = Manhattan, 2 = Euclidienne, ...)
	 * @return La distance entre deux Iris
	 */
	public double getDistance(Iris other, float power) {
		double dist;
		
		dist =  Math.pow(Math.abs(this.sepal_length - other.sepal_length),power) +
				Math.pow(Math.abs(this.sepal_width - other.sepal_width),power) +
				Math.pow(Math.abs(this.petal_length - other.petal_length),power) +
				Math.pow(Math.abs(this.petal_width - other.petal_width),power);
		dist = Math.pow(dist, 1.0/power);
		
		return dist;		
	}
	
	public double getDistanceSepalLength(Iris other, float power) {
		return Math.pow(Math.pow(Math.abs(this.sepal_length - other.sepal_length), power), 1.0/power);
	}
	
	public double getDistanceSepalWidth(Iris other, float power) {
		return Math.pow(Math.pow(Math.abs(this.sepal_width - other.sepal_width), power), 1.0/power);
	}
	
	public double getDistancePetalLength(Iris other, float power) {
		return Math.pow(Math.pow(Math.abs(this.petal_length - other.petal_length), power), 1.0/power);
	}
	
	public double getDistancePetalWidth(Iris other, float power) {
		return Math.pow(Math.pow(Math.abs(this.petal_width - other.petal_width), power), 1.0/power);
	}
}
