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

	public static ArrayList<Iris> ReadFile(String csvFile) {
		ArrayList<Iris> result = new ArrayList<Iris>();
		BufferedReader br = null;
		String line = "";
		// use comma as separator
		String cvsSplitBy = ",";
		int nbLines = 0;
		try {
			System.out.println("*** Reading file "+csvFile+" ***");
			br = new BufferedReader(new FileReader(csvFile));
			//read file line by line
			while ((line = br.readLine()) != null && !line.equals("")) {
				String[] data = line.split(cvsSplitBy);
				Iris iris =new Iris(
						Float.parseFloat(data[0]),
						Float.parseFloat(data[1]),
						Float.parseFloat(data[2]),
						Float.parseFloat(data[3]),
						data[4]);
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
		
		System.out.println("*** Done (Total: "+nbLines+" lines) ***\n");	  
		return result;
	}
	
	public static void TestDistance(Iris iris, ArrayList<Iris> dataset) {
		int i;
		for(i=0;i<dataset.size();i++) {
			System.out.println("--");
			System.out.println(iris.getDistance(dataset.get(i), 2));
			System.out.println(iris.getDistance(dataset.get(i), 1));			
		}
	}
	
	public static String QueryKNN(Iris iris, ArrayList<Iris> learningset, int N) {
		HashMap<Iris,Double> distances = new HashMap<Iris,Double>();
		ArrayList<Iris> results = new ArrayList<Iris>();
		int i;
		//get distances
		for(i=0;i<learningset.size();i++) {			
			distances.put(learningset.get(i), iris.getDistance(learningset.get(i), 2));
		}
		//sort distances
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
		HashMap<String,Double> neighbors = new HashMap<String,Double>();
		
		//get neighbors scores
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
		
		//get max type score
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
	
	public static void TestKNNModel(ArrayList<Iris> learningset, ArrayList<Iris> testset, int N) {
		int nbTest = 0;
		int nbGoodPrediction = 0;
		
		for(Iris iris : testset) {
			String result = QueryKNN(iris, learningset, N);
			if(TestPrediction(iris, result)) {
				nbGoodPrediction++;
			}
			nbTest++;
		}
		
		float accuracy = (float) (((float)nbGoodPrediction/nbTest)*100.0);
		System.out.println("Accuracy: "+String.format("%.2f", accuracy)+"% for N="+N);
	}
	
	public static boolean TestPrediction(Iris iris, String typePredicted) {
		//System.out.println("Predicted type: " + typePredicted + ", Real type: " + iris.type + ", Prediction is " + iris.type.equals(typePredicted));
		return iris.type.equals(typePredicted);
	}
	
	public static void RandomTest(int LearningSetPercentage) {
		ArrayList<Iris> learningSet = new ArrayList<Iris>();
		ArrayList<Iris> testSet = new ArrayList<Iris>();
		ArrayList<Iris> dataSet = IrisDataSetAnalyser.ReadFile("./datasets/iris.data");
		
		int TOTAL_SIZE = 150;
		int learningSize = TOTAL_SIZE*LearningSetPercentage/100;
		int currentSize = TOTAL_SIZE;
		
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
		
		System.out.println("LearningSet size = "+learningSet.size());
		System.out.println("TestSet size = "+testSet.size()+"\n");
		
		System.out.println("*** Executing TestKNNModel: ***\n");
		int k = 1;

		for(k=1;k<=learningSize;k++){
			TestKNNModel(learningSet,testSet,k);			
		}
		System.out.println("\n*** End of TestKNNModel ***\n");
	}
	
	public static void FixedTest() {
		ArrayList<Iris> learningSet = IrisDataSetAnalyser.ReadFile("./datasets/iris_learning.data");
		ArrayList<Iris> testSet = IrisDataSetAnalyser.ReadFile("./datasets/iris_test.data");
		
		System.out.println("LearningSet size = "+learningSet.size());
		System.out.println("TestSet size = "+testSet.size()+"\n");
		
		System.out.println("*** Executing TestKNNModel: ***\n");
		int k = 1;

		for(k=1;k<=learningSet.size();k++){
			TestKNNModel(learningSet,testSet,k);			
		}
		System.out.println("\n*** End of TestKNNModel ***\n");
	}
	
	public static void main(String[] args) {		
		// Define Percentage of Learning Data Set and see Accuracy
		RandomTest(20);
		// Example with a defined Learning Set of 20%
		//FixedTest();
	}
}
