import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IrisDataSetAnalyser {

	public static ArrayList<Iris> ReadFile(String csvFile){
		ArrayList<Iris> result = new ArrayList<Iris>();
		BufferedReader br = null;
		String line = "";
		// use comma as separator
		String cvsSplitBy = ",";
		int nbLines = 0;
		try {
			System.out.println("*** Reading file "+csvFile+" ***");
			br = new BufferedReader(new FileReader(csvFile));
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
		
		return "Iris-virginica";
	}
	
	public static void TestKNNModel(ArrayList<Iris> learningset, ArrayList<Iris> testset, int N) {
		int nbTest = 0;
		int nbGoodPrediction = 0;
		System.out.println("Executing TestKNNModel:\n");
		for(Iris iris : testset) {
			String result = QueryKNN(iris, learningset, N);
			if(TestPrediction(iris, result)) {
				nbGoodPrediction++;
			}
			nbTest++;
		}
		float accuracy = (float) (((float)nbGoodPrediction/nbTest)*100.0);
		System.out.println("Accuracy: "+accuracy+"%");
	}
	
	public static boolean TestPrediction(Iris iris, String typePredicted){
		System.out.println("Predicted type: " + typePredicted + ", Real type: " + iris.type + ", Prediction is " + iris.type.equals(typePredicted) + "\n");
		return iris.type.equals(typePredicted);
	}
	
	public static void main(String[] args) {
		ArrayList<Iris> learningSet = IrisDataSetAnalyser.ReadFile("./datasets/iris_learning.data");
		ArrayList<Iris> testSet = IrisDataSetAnalyser.ReadFile("./datasets/iris_test.data");
		//IrisDataSetAnalyser.TestDistance(new Iris(3,2,5,2,"Jeej"), learningSet);
		TestKNNModel(learningSet,testSet,1);
	}

}
