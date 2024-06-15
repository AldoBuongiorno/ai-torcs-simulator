package scr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import scr.TrainingData;

public class NearestNeighbor {


    private List<TrainingData> trainingData;
    private KDTree kdtree;
    private int[] classCounts; // VERIFICA NEI COSTRUTTORI CHE QUESTO SIA CONFORME CON QUELLO CHE STAI IPOTIZZANDO!
    private String firstLineOfTheFile; // VERIFICA  NEI COSTRUTTORI CHE QUESTO SIA CONFORME CON QUELLO CHE STAI IPOTIZZANDO!
    


    public NearestNeighbor(String filename) {
        this.trainingData = new ArrayList<>();
        this.kdtree = null;
        this.classCounts = new int[6]; // Assuming classes are labeled 0-9
        this.firstLineOfTheFile = "speed;trackPosition;trackEdgeSensor3;trackEdgeSensor6;trackEdgeSensor9;trackEdgeSensor12;trackEdgeSensor15;angleToTrackAxis;classLabel";
        this.readPointsFromCSV(filename);
        
        //Stampa la distribuzione delle classi
        this.printClassDistribution();              
    }
    

    private void readPointsFromCSV(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("speed;trackPosition;trackEdgeSensor3;trackEdgeSensor6;trackEdgeSensor9;trackEdgeSensor12;trackEdgeSensor15;angleToTrackAxis;classLabel")) {
                    continue; // Skip header
                }
                // Aggiungo il campione richiamando il costruttore che prende come input la stringa letta
                trainingData.add(new TrainingData(line));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.kdtree = new KDTree(trainingData); // Inizializza il KDTree utilizzando i punti letti
    }

    public List<TrainingData> findKNearestNeighbors(TrainingData testPoint, int k) {
        return kdtree.kNearestNeighbors(testPoint, k);
    }

    public int classify(TrainingData testPoint, int k) {
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


