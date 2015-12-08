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

		try {
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
		
		System.out.println("Done");	  
		return result;
	
	}
	
	public static void TestDistance(Iris iris, ArrayList<Iris> dataset){
		int i;
		for(i=0;i<dataset.size();i++){
			System.out.println("--");
			System.out.println(iris.getDistance(dataset.get(i), 2));
			System.out.println(iris.getDistance(dataset.get(i), 1));			
		}
	}
	
	public static String QueryKNN(Iris iris, ArrayList<Iris> learningset, int N){
		
		return "Iris Type Placeholder";
	}
	
	public static void TestKNNModel(ArrayList<Iris> learningset,ArrayList<Iris> testset, int N){
		
		
	}
	
	public static Boolean TestPrediction(Iris iris, String typePredicted){
		System.out.println("Predicted type:" + typePredicted);
		System.out.println("Real iris type:" + iris.type);
		System.out.println("Prediction is" + iris.type.equals(typePredicted));
		return iris.type.equals(typePredicted);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Iris> dataset=IrisDataSetAnalyser.ReadFile("./datasets/iris.data");
		IrisDataSetAnalyser.TestDistance(new Iris(3,2,5,2,"Jeej"), dataset);
	}

}
