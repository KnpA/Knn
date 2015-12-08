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
				nbLines++
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
	
	public static void main(String[] args) {
		ArrayList<Iris> learningSet = IrisDataSetAnalyser.ReadFile("./datasets/iris_learning.data");
		ArrayList<Iris> testSet = IrisDataSetAnalyser.ReadFile("./datasets/iris_test.data");
		IrisDataSetAnalyser.TestDistance(new Iris(3,2,5,2,"Jeej"), learningSet);
	}

}
