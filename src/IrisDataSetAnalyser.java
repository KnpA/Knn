import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


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
		HashMap<Iris,Double> distances = new HashMap<Iris,Double>();
		ArrayList<Iris> results = new ArrayList<Iris>();
		int i;
		//get distances
		for(i=0;i<learningset.size();i++){			
			distances.put(learningset.get(i), iris.getDistance(learningset.get(i), 2));
		}
		//sort distances
		while(!distances.isEmpty()){
			Iterator<Entry<Iris, Double>> entries = distances.entrySet().iterator();
			Iris maxKey = null;
			double maxValue = 0;
			while(entries.hasNext()){
				Map.Entry<Iris,Double> entry = (Map.Entry<Iris,Double>) entries.next();
			    Iris key = (Iris)entry.getKey();
			    Double value = (Double)entry.getValue();
			    if(maxKey == null || maxValue < value){
			    	maxKey = key;
			    	maxValue = value;			    	
			    }
			    
			}
			results.add(maxKey);
			distances.remove(maxKey);			
		}
		HashMap<String,Double> neighbors = new HashMap<String,Double>();
		
		//get neighbors scores
		for(i=0;i<results.size();i++){		
			Iris r = results.get(i);
			if(neighbors.containsKey(r.type)){
				double d = neighbors.get(r.type);
				d++;
				System.out.println("Added neighbor "+ r.type);
				neighbors.put(r.type, d);				
			}else{
				neighbors.put(r.type, 1.0);
			}
		}
		
		Iterator<Entry<String, Double>> entries = neighbors.entrySet().iterator();
		String maxKey = null;
		double maxValue = 0;
		while(entries.hasNext()){
			Map.Entry<String,Double> entry = (Map.Entry<String,Double>) entries.next();
			String key = (String)entry.getKey();
		    Double value = (Double)entry.getValue();
		    if(maxKey == null || maxValue < value){
		    	maxKey = key;
		    	maxValue = value;			    	
		    }
		}
		
		return maxKey;
	}
	
	public static void TestKNNModel(ArrayList<Iris> learningset,ArrayList<Iris> testset, int N){
		
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Iris> dataset=IrisDataSetAnalyser.ReadFile("./datasets/iris.data");
		//IrisDataSetAnalyser.TestDistance(new Iris(3,2,5,2,"Jeej"), dataset);
		
		//System.out.println("Predicted :"+IrisDataSetAnalyser.QueryKNN(new Iris(3,2,5,2,"Jeej"), dataset, 3));
	}

}
