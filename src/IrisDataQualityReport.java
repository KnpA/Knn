import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class IrisDataQualityReport {

	HashMap<Integer,ArrayList<Float>> map;
	NumberFormat fmt = new DecimalFormat("#0.00");

	/**
	 * Data Quality Report on Continuous Features
	 * @param dataSet Le Data Set à étudier
	 * @param features Le nom des Features
	 */
	public IrisDataQualityReport(ArrayList<Iris> dataSet, ArrayList<String> features) {
		System.out.println("*** Data Quality Report ***\n");
		System.out.println("Features\tCount\t%Miss.\tCard.\tMin.\t1st Qrt.\tMean\tMedian\t3rd Qrt.\tMax\tStd Dev.");
		
		// Init DQR
		map = new HashMap<>();
		for(int i=0; i<features.size(); i++) {
			map.put(i, new ArrayList<Float>());
		}
		
		// For each line :
		for(Iris iris : dataSet) {
			map.get(0).add(iris.sepal_length);
			map.get(1).add(iris.sepal_width);
			map.get(2).add(iris.petal_length);
			map.get(3).add(iris.petal_width);
		}
		
		// For each feature calculate data		
		int total = dataSet.size();
		int i = 0;
		for(String feature : features) {
			double median = Median(map.get(i));
			double mean = Mean(map.get(i));
			double min = Min(map.get(i));
			double max = Max(map.get(i));
			double card = Card(map.get(i));
			int miss = total - map.get(i).size();
			double first_qrt = Quartile(map.get(i),1);
			double third_qrt = Quartile(map.get(i),3);
			double std_dev = StdDev(map.get(i));
			System.out.println(feature+"\t"+map.get(i).size()+"\t"+miss+"\t"+fmt.format(card)+"\t"+fmt.format(min)+"\t"+fmt.format(first_qrt)+"\t\t"+fmt.format(mean)+"\t"+fmt.format(median)+"\t"+fmt.format(third_qrt)+"\t\t"+fmt.format(max)+"\t"+fmt.format(std_dev));
			i++;
		}
		System.out.println("\n*** End of Data Quality Report ***\n");
	}
	
	public static double Median(ArrayList<Float> values)
	{
	    Collections.sort(values);
	 
	    if (values.size() % 2 == 1)
		return (double) values.get((values.size()+1)/2-1);
	    else
	    {
		double lower = (double) values.get(values.size()/2-1);
		double upper = (double) values.get(values.size()/2);
	 
		return (lower + upper) / 2.0;
	    }	
	}
	
	public static double Mean(ArrayList<Float> values)
	{
		double sum = 0;
		for(Float f : values) {
			sum += f;
		}
		return sum/values.size();
	}
	
	public static double Min(ArrayList<Float> values)
	{
		double min = Float.MAX_VALUE;
		for(Float f : values) {
			if(f < min)
				min = f;
		}
		return min;
	}
	
	public static double Max(ArrayList<Float> values)
	{
		double max = Float.MIN_VALUE;
		for(Float f : values) {
			if(f > max)
				max = f;
		}
		return max;
	}
	
	public static double Card(ArrayList<Float> values)
	{
		double card = 0;
		float last = Float.MIN_VALUE;
		Collections.sort(values);
		for(Float f : values) {
			if(last != f)
				card++;
			last = f;
		}
		return card;
	}
	
	public static double StdDev(ArrayList<Float> values)
	{
		double mean = Mean(values);
        double temp = 0;
        for(float a : values) {
            temp += (mean-a)*(mean-a);
        }
		double variance = temp/values.size();
		return Math.sqrt(variance);
	}
	
	public static double Quartile(ArrayList<Float> values, int quartile)
	{
		Collections.sort(values);
		
		return values.get(values.size()*quartile/4);
	}

}
