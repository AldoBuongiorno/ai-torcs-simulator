package scr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearestNeighbor {

    private final List<TrainingData> trainingData;
    private KDTree kdtree;
    private final int[] classCounts;
    private final String firstLineOfTheFile;

    public NearestNeighbor(String filename) {
        this.trainingData = new ArrayList<>();
        this.kdtree = null;
        this.firstLineOfTheFile = "speed;trackPosition;trackEdgeSensor4;trackEdgeSensor6;trackEdgeSensor8;trackEdgeSensor9;"
			+ "trackEdgeSensor10;trackEdgeSensor12;trackEdgeSensor14;angleToTrackAxis;classLabel";
        this.classCounts = new int[7];              //le classi sono 7
        this.readPointsFromCSV(filename);
        this.printClassDistribution();              //Stampa la distribuzione delle classi
    }

    private void readPointsFromCSV(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(firstLineOfTheFile)) {
                    continue; // Skip header
                }
                // Aggiungo il campione richiamando il costruttore che prende come input la stringa letta
                try{
                    //per bypassare errori di formattazione nel training set
                    TrainingData data = new TrainingData(line);
                    trainingData.add(data);
                }catch(NumberFormatException ex){
                    System.out.println(line);
                }
                
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.kdtree = new KDTree(trainingData); // Inizializza il KDTree utilizzando i punti letti
    }
 
    public List<TrainingData> findKNearestNeighbors(TrainingData testPoint, int k) {
        //kdtree.printTree();
        return kdtree.kNearestNeighbors(testPoint, k);
    }

    public int classify(TrainingData testPoint, int k) {

        Arrays.fill(classCounts, 0); // Azzera i conteggi delle classi
        List<TrainingData> kNearestNeighbors = findKNearestNeighbors(testPoint, k);

        // Count the occurrences of each class in the k nearest neighbors
        for (TrainingData neighbor : kNearestNeighbors) {
            classCounts[neighbor.classLabel]++;
        }

        // Find the class with the maximum count
        int maxCount = -1;
        int predictedClass = -1;
        for (int i = 0; i < classCounts.length; i++) {
            if (classCounts[i] > maxCount) {
                maxCount = classCounts[i];
                predictedClass = i;
            }
        }

        return predictedClass;
    }

       
    public List<TrainingData> getTrainingData() {
        return trainingData;
    }


    public void printClassDistribution() {
        for (TrainingData data : trainingData) {
            classCounts[data.classLabel]++;
        }
        for (int i = 0; i < classCounts.length; i++) {
            System.out.println("Class " + i + ": " + classCounts[i]);
        }
    }

}