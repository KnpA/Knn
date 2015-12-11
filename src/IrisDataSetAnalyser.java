import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
	
	
	public static HashMap<Iris,Double> GetDistances(Iris iris, ArrayList<Iris> dataset){
		HashMap<Iris,Double> distances = new HashMap<Iris,Double>();
		int i;
		// get distances
		for(i=0;i<dataset.size();i++) {			
			distances.put(dataset.get(i), iris.getDistance(dataset.get(i), 2));
		}
		
		return distances;
	}
	
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
	 * @return
	 */
	public static String QueryKNN(Iris iris, ArrayList<Iris> learningset, int N) {
		HashMap<Iris,Double> distances = IrisDataSetAnalyser.GetDistances(iris, learningset);
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
	 * @return
	 */
	public static String QueryWeightedKNN(Iris iris, ArrayList<Iris> learningset, int N) {
		HashMap<Iris,Double> distances = IrisDataSetAnalyser.GetDistances(iris, learningset);
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
	public static void TestKNNModel(ArrayList<Iris> learningset, ArrayList<Iris> testset, int N) {
		int nbTest = 0;
		int nbGoodPrediction = 0;
			
		for(Iris iris : testset) {
			//String result = QueryKNN(iris, learningset, N);
			String result = QueryWeightedKNN(iris, learningset, N);
			if(TestPrediction(iris, result)) {
				nbGoodPrediction++;
			}
			nbTest++;
		}
		
		float accuracy = (float) (((float)nbGoodPrediction/nbTest)*100.0);
		
		System.out.println("Accuracy: "+String.format("%.2f", accuracy)+"% for N="+N);
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
	
	/**
	 * Test avec un Learning Set contenant des valeurs choisies aléatoirement
	 * @param LearningSetPercentage Taille du Learning Set en pourcentage
	 * @param maxK K voisins maximum voulu
	 */
	public static void RandomTest(int LearningSetPercentage, int maxK) {
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
			TestKNNModel(learningSet,testSet,k);			
		}
		if(VERBOSE)
			System.out.println("\n*** End of Random TestKNNModel ***\n");
	}
	
	/**
	 * Test sur un Learning Set enregistré en dur
	 * @param maxK K maximum voulu de voisins
	 */
	public static void FixedTest(int maxK) {
		ArrayList<Iris> learningSet = IrisDataSetAnalyser.ReadFile("./datasets/iris_learning.data");
		ArrayList<Iris> testSet = IrisDataSetAnalyser.ReadFile("./datasets/iris_test.data");
		
		if(VERBOSE) {
			System.out.println("LearningSet size = "+learningSet.size());
			System.out.println("TestSet size = "+testSet.size()+"\n");	
			System.out.println("*** Executing Fixed TestKNNModel: ***\n");
		}
		int k = 1;
		
		if(maxK < 1 || maxK > learningSet.size())
			maxK = learningSet.size();

		for(k=1;k<=maxK;k++){
			TestKNNModel(learningSet,testSet,k);			
		}
		
		if(VERBOSE)
			System.out.println("\n*** End of Fixed TestKNNModel ***\n");
	}
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {		
		// Define Percentage of Learning Data Set, Max K-NN and see Accuracy for each K
		RandomTest(70,-1);
		// Example with a defined Learning Set of 20% and a Max K-NN as parameter
		//FixedTest(10);
	}
}
