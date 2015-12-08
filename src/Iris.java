
public class Iris {
	
	public float sepal_length,
		sepal_width,
		petal_length,
		petal_width;
	
	public String type;
	
	public Iris(float sl, float sw, float pl, float pw, String t){
		sepal_length =sl;
		sepal_width = sw;
		petal_length = pl;
		petal_width = pw;	
		type = t;
	}
	
	public String toString(){
		return type+" : [sepal length : "+sepal_length+", sepal width : "+sepal_width+", petal length : "+petal_length+", petal_width : "+petal_width+"]";
	}
	
	public double getDistance(Iris other, float power){
		double dist;
		
		dist =  Math.pow(Math.abs(this.sepal_length - other.sepal_length),power) +
				Math.pow(Math.abs(this.sepal_width - other.sepal_width),power) +
				Math.pow(Math.abs(this.petal_length - other.petal_length),power) +
				Math.pow(Math.abs(this.petal_width - other.petal_width),power);
		dist = Math.pow(dist, 1.0/power);
		
		return dist;
		
	}
	
}
