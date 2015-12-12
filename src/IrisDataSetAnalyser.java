import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class IrisDataSetAnalyser {
	private static final boolean VERBOSE = true;
	private static final boolean DEBUG = false;

	/**
	 * Lecture d'un fichier de données au format CSV
	 * @param csvFile Nom du fichier à parcourir
	 * @return Liste d'Iris contenus dans le fichier
	 */
	public static ArrayList<Iris> ReadFile(String csvFile) {
		ArrayList<Iris> result = new ArrayList<Iris>();
		BufferedReader br = null;
		String line = "";
		// use comma as separator
		String cvsSplitBy = ",";
		int nbLines = 0;
		try {
			if(VERBOSE)
				System.out.println("*** Reading file "+csvFile+" ***");
			br = new BufferedReader(new FileReader(csvFile));
			// read file line by line
			while ((line = br.readLine()) != null && !line.equals("")) {
				String[] data = line.split(cvsSplitBy);
				Iris iris =new Iris(
						Float.parseFloat(data[0]),
						Float.parseFloat(data[1]),
						Float.parseFloat(data[2]),
						Float.parseFloat(data[3]),
						data[4]);
				if(DEBUG)
					System.out.println(iris);
				result.add(iris);
				nbLines++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(VERBOSE)
			System.out.println("*** Done (Total: "+nbLines+" lines) ***\n");	  
		return result;
	}
	
	/**
	 * Obtient les distances entre un Iris requête et les iris d'une collection
	 * @param iris
	 * @param dataset
	 * @param L Type de distance : 1 pour manhattan, 2 pour euclidien ...
	 * @return
	 */
	public static HashMap<Iris,Double> GetDistances(Iris iris, ArrayList<Iris> dataset, int L){
		HashMap<Iris,Double> distances = new HashMap<Iris,Double>();
		int i;
		// get distances
		for(i=0;i<dataset.size();i++) {			
			distances.put(dataset.get(i), iris.getDistance(dataset.get(i), L));
		}
		
		return distances;
	}
	/**
	 * Trie la map des distances
	 * @param distances
	 * @return
	 */
	public static ArrayList<Iris> SortDistances(HashMap<Iris,Double> distances){
		ArrayList<Iris> results = new ArrayList<Iris>();
		// sort distances
		while(!distances.isEmpty()) {
			Iterator<Entry<Iris, Double>> entries = distances.entrySet().iterator();
			Iris minKey = null;
			double minValue = 0;
			
			while(entries.hasNext()) {
				Map.Entry<Iris,Double> entry = (Map.Entry<Iris,Double>) entries.next();
			    Iris key = (Iris)entry.getKey();
			    Double value = (Double)entry.getValue();
			    if(minKey == null || minValue > value) {
			    	minKey = key;
			    	minValue = value;			    	
			    }
			}
			
			results.add(minKey);
			distances.remove(minKey);			
		}
		
		return results;
	}
	
	/**
	 * Query the model and try to predic iris type using the N nearest neighbors
	 * @param iris 
	 * @param learningset
	 * @param N Nombre de plus proches voisins considérés
	 * @param L Type de distance : 1 pour manhattan, 2 pour euclidien ...
	 * @return predicted type
	 */
	public static String QueryKNN(Iris iris, ArrayList<Iris> learningset, int N, int L) {
		HashMap<Iris,Double> distances = IrisDataSetAnalyser.GetDistances(iris, learningset, L);
		ArrayList<Iris> results = IrisDataSetAnalyser.SortDistances(distances);
		
		HashMap<String,Double> neighbors = new HashMap<String,Double>();
		
		// get neighbors scores
		int i;
		for(i=0;i<Math.min(N, results.size());i++) {		
			Iris r = results.get(i);
			if(neighbors.containsKey(r.type)) {
				double d = neighbors.get(r.type);
				d++;
				//System.out.println("Added neighbor score "+r.type+" to "+ d);
				neighbors.put(r.type, d);				
			} else {
				neighbors.put(r.type, 1.0);
			}
		}
		// get max type score
		Iterator<Entry<String, Double>> entries = neighbors.entrySet().iterator();
		String maxKey = null;
		double maxValue = 0;
		
		while(entries.hasNext()) {
			Map.Entry<String,Double> entry = (Map.Entry<String,Double>) entries.next();
			String key = (String)entry.getKey();
		    Double value = (Double)entry.getValue();
		    if(maxKey == null || maxValue < value) {
		    	maxKey = key;
		    	maxValue = value;			    	
		    }
		}
		
		return maxKey;
	}
	/**
	 * Query the model and try to predic iris type using the N nearest neighbors weighted by order
	 * @param iris 
	 * @param learningset
	 * @param N Nombre de plus proches voisins considérés
	 * @return predicted type
	 */
	public static String QueryWeightedKNN(Iris iris, ArrayList<Iris> learningset, int N) {
		HashMap<Iris,Double> distances = IrisDataSetAnalyser.GetDistances(iris, learningset,2);
		ArrayList<Iris> results = IrisDataSetAnalyser.SortDistances(distances);
		
		HashMap<String,Double> neighbors = new HashMap<String,Double>();
		
		// get neighbors scores
		int i;
		for(i=0;i<Math.min(N, results.size());i++) {		
			Iris r = results.get(i);
			if(neighbors.containsKey(r.type)) {
				double d = neighbors.get(r.type);
				d+=1/(double)(i+2);
				//System.out.println("Added neighbor score "+r.type+" to "+ d);
				neighbors.put(r.type, d);				
			} else {
				neighbors.put(r.type, 1/(double)(i+2));
			}
		}
		
		// get max type score
		Iterator<Entry<String, Double>> entries = neighbors.entrySet().iterator();
		String maxKey = null;
		double maxValue = 0;
		
		while(entries.hasNext()) {
			Map.Entry<String,Double> entry = (Map.Entry<String,Double>) entries.next();
			String key = (String)entry.getKey();
		    Double value = (Double)entry.getValue();
		    if(maxKey == null || maxValue < value) {
		    	maxKey = key;
		    	maxValue = value;			    	
		    }
		}
		
		return maxKey;
	}
	
	/**
	 * Boucle de prédiction sur tout le Test Set
	 * @param learningset Learning Set données apprises
	 * @param testset Test Set à observer
	 * @param N Nombre de voisins proches à comparer 
	 */
	public static void TestKNNModel(ArrayList<Iris> learningset, ArrayList<Iris> testset, int N, int L) {
		int nbTest = 0;
		int nbGoodPrediction = 0;
			
		for(Iris iris : testset) {
			
			String result = QueryKNN(iris, learningset, N, L);
			//String result = QueryWeightedKNN(iris, learningset, N);
			if(TestPrediction(iris, result)) {
				nbGoodPrediction++;
			}
			nbTest++;
		}
		
		float accuracy = (float) (((float)nbGoodPrediction/nbTest)*100.0);
		
		System.out.println("Accuracy: "+String.format("%.2f", accuracy)+"% for K="+N);
	}
	
	/**
	 * Retourne le résultat d'une prédiction avec la valeur réelle de l'Iris
	 * @param iris Iris à comparer
	 * @param typePredicted Type de l'Iris prédit
	 * @return true si la prédiction est juste, false sinon 
	 */
	public static boolean TestPrediction(Iris iris, String typePredicted) {
		//System.out.println("Predicted type: " + typePredicted + ", Real type: " + iris.type + ", Prediction is " + iris.type.equals(typePredicted));
		return iris.type.equals(typePredicted);
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
	
	/**
	 * Data Quality Report on Continuous Features
	 * @param dataSet Le Data Set à étudier
	 * @param features Le nom des Features
	 */
	public static void DataQualityReport(ArrayList<Iris> dataSet, ArrayList<String> features) {
		System.out.println("*** Data Quality Report ***\n");
		System.out.println("Features\tCount\t%Miss.\tCard.\tMin.\t1st Qrt.\tMean\tMedian\t3rd Qrt.\tMax\tStd Dev.");
		
		// Init DQR
		NumberFormat fmt = new DecimalFormat("#0.00");  
		HashMap<Integer,ArrayList<Float>> map = new HashMap<>();
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
	
	/**
	 * Test avec un Learning Set contenant des valeurs choisies aléatoirement
	 * @param LearningSetPercentage Taille du Learning Set en pourcentage
	 * @param maxK K voisins maximum voulu
	 */
	public static void RandomTest(int LearningSetPercentage, int maxK, int L) {
		ArrayList<Iris> learningSet = new ArrayList<Iris>();
		ArrayList<Iris> testSet = new ArrayList<Iris>();
		ArrayList<Iris> dataSet = IrisDataSetAnalyser.ReadFile("./datasets/iris.data");
		
		int TOTAL_SIZE = 150;
		int learningSize = TOTAL_SIZE*LearningSetPercentage/100;
		int currentSize = TOTAL_SIZE;
		
		if(VERBOSE)
			System.out.println("LearningSet percentage = "+LearningSetPercentage+"%");
		
		for(currentSize=TOTAL_SIZE; currentSize > 0; currentSize--) {
			int index = ThreadLocalRandom.current().nextInt(0, currentSize);
			Iris i = dataSet.remove(index);
			if(learningSet.size() < learningSize) {
				learningSet.add(i);
			} else {
				testSet.add(i);
			}
		}
		
		if(VERBOSE) {
			System.out.println("LearningSet size = "+learningSet.size());
			System.out.println("TestSet size = "+testSet.size()+"\n");
			System.out.println("*** Executing Random TestKNNModel: ***\n");
		}
		int k = 1;

		if(maxK < 1 || maxK > learningSet.size())
			maxK = learningSet.size();
		
		for(k=1;k<=maxK;k++){
			TestKNNModel(learningSet,testSet,k, L);			
		}
		if(VERBOSE)
			System.out.println("\n*** End of Random TestKNNModel ***\n");
	}

	/**
	 * Test avec un Learning Set contenant des valeurs choisies linéairement par un pourcentage
	 * @param LearningSetPercentage Taille du Learning Set en pourcentage
	 * @param maxK K voisins maximum voulu
	 */
	public static void FixedTest(int LearningSetPercentage, int maxK, int L) {
		ArrayList<Iris> learningSet = new ArrayList<Iris>();
		ArrayList<Iris> testSet = new ArrayList<Iris>();
		ArrayList<Iris> dataSet = IrisDataSetAnalyser.ReadFile("./datasets/iris.data");
		
		int NB_CLASSES = 3;
		int TOTAL_SIZE = 150;
		int CLASS_SIZE = TOTAL_SIZE/NB_CLASSES;
		int learningSize = CLASS_SIZE*NB_CLASSES*LearningSetPercentage/100;
		
		if(VERBOSE)
			System.out.println("LearningSet percentage = "+LearningSetPercentage+"%");
		
		for(int i=0; i < CLASS_SIZE; i++) {
			Iris iris1 = dataSet.get(i);
			Iris iris2 = dataSet.get(i+CLASS_SIZE);
			Iris iris3 = dataSet.get(i+2*CLASS_SIZE);
			if(learningSet.size() < learningSize) {
				learningSet.add(iris1);
				learningSet.add(iris2);
				learningSet.add(iris3);
			} else {
				testSet.add(iris1);
				testSet.add(iris2);
				testSet.add(iris3);
			}
		}
		
		if(VERBOSE) {
			System.out.println("LearningSet size = "+learningSet.size());
			System.out.println("TestSet size = "+testSet.size()+"\n");
			System.out.println("*** Executing Fixed TestKNNModel: ***\n");
		}
		int k = 1;

		if(maxK < 1 || maxK > learningSet.size())
			maxK = learningSet.size();
		
		for(k=1;k<=maxK;k++){
			TestKNNModel(learningSet,testSet,k,L);			
		}
		if(VERBOSE)
			System.out.println("\n*** End of Fixed TestKNNModel ***\n");
	}
	
	/**
	 * Main
	 * @param args Arguments
	 */
	public static void main(String[] args) {
		ArrayList<Iris> dataSet = IrisDataSetAnalyser.ReadFile("./datasets/iris.data");
		ArrayList<String> features = new ArrayList<>();
		features.add("sepal_length");
		features.add("sepal_width");
		features.add("petal_length");
		features.add("petal_width");
		
		DataQualityReport(dataSet, features);
		
		// Parameters
		int maxK = 50;
		int distanceType = 2;

		// Define Percentage of Learning Data Set, Max K-NN and see Accuracy for each K
        for(int p=10; p<100; p+=10) {
        	//RandomTest(p,maxK,distanceType);
        }

		// Example with a defined Learning Set of 20% and a Max K-NN as parameter
        for(int p=10; p<100; p+=10) {
        	FixedTest(p,maxK,distanceType);
        }
	}
}
