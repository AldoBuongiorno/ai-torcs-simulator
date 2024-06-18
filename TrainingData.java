package scr;

public class TrainingData {
    //speed;trackPosition;trackEdgeSensor3;trackEdgeSensor6;trackEdgeSensor9;trackEdgeSensor12;trackEdgeSensor15;angleToTrackAxis;classLabel
    private double speed;
    private double trackPosition;
    private double trackEdgeSensor3;
    private double trackEdgeSensor6;
    private double trackEdgeSensor9;
    private double trackEdgeSensor12;
    private double trackEdgeSensor15;
    private double angleToTrackAxis;
    public int classLabel;
    
    //Costruttore standard
    public TrainingData(double speed, double trackPosition, double trackEdgeSensor3, double trackEdgeSensor6, 
        double trackEdgeSensor9, double trackEdgeSensor12, double trackEdgeSensor15, double angleToTrackAxis, int classLabel) {
        this.speed = speed;
        this.trackPosition = trackPosition;
        this.trackEdgeSensor3 = trackEdgeSensor3;
        this.trackEdgeSensor6 = trackEdgeSensor6;
        this.trackEdgeSensor9 = trackEdgeSensor9;
        this.trackEdgeSensor12 = trackEdgeSensor12;
        this.trackEdgeSensor15 = trackEdgeSensor15;
        this.angleToTrackAxis = angleToTrackAxis;
        this.classLabel = classLabel;
    }

    public TrainingData(double speed, double trackPosition, double trackEdgeSensor3, double trackEdgeSensor6, 
        double trackEdgeSensor9, double trackEdgeSensor12, double trackEdgeSensor15, double angleToTrackAxis) {
        this.speed = speed;
        this.trackPosition = trackPosition;
        this.trackEdgeSensor3 = trackEdgeSensor3;
        this.trackEdgeSensor6 = trackEdgeSensor6;
        this.trackEdgeSensor9 = trackEdgeSensor9;
        this.trackEdgeSensor12 = trackEdgeSensor12;
        this.trackEdgeSensor15 = trackEdgeSensor15;
        this.angleToTrackAxis = angleToTrackAxis;
        this.classLabel = -1;
    }

    /*
     * Costruttore funzionale alla lettura del dataset CSV
    */
     public TrainingData(String dataLine){
        String[] parts = dataLine.split(";");
        this.speed = Double.parseDouble(parts[0].trim());
        this.trackPosition = Double.parseDouble(parts[1].trim());
        this.trackEdgeSensor3 = Double.parseDouble(parts[2].trim());
        this.trackEdgeSensor6 = Double.parseDouble(parts[3].trim());
        this.trackEdgeSensor9 = Double.parseDouble(parts[4].trim());
        this.trackEdgeSensor12 = Double.parseDouble(parts[5].trim());
        this.trackEdgeSensor15 = Double.parseDouble(parts[6].trim());
        this.angleToTrackAxis = Double.parseDouble(parts[7].trim());
        this.classLabel = Integer.parseInt(parts[8].trim());
    }

    public int getClassLabel(){
        return this.classLabel;
    }

    /*
     * Questo metodo è utilizzato per calcolare la distanza euclidea tra due punti "trainingData"
     * 'this' e 'object', la quale si calcola sommando i quadrati delle differenze tra i valori corrispondenti di ciascun attributo,
     * e poi prendendo la radice quadrata del risultato.
    */ 
    public double distance(TrainingData object){
        return Math.sqrt(
            Math.pow(this.speed - object.speed, 2)
            + Math.pow(this.angleToTrackAxis - object.angleToTrackAxis, 2)
            + Math.pow(this.trackEdgeSensor3 - object.trackEdgeSensor3, 2)
            + Math.pow(this.trackEdgeSensor6 - object.trackEdgeSensor6, 2)
            + Math.pow(this.trackEdgeSensor9 - object.trackEdgeSensor9, 2)
            + Math.pow(this.trackEdgeSensor12 - object.trackEdgeSensor12, 2)
            + Math.pow(this.trackEdgeSensor15 - object.trackEdgeSensor15, 2)
            + Math.pow(this.trackPosition - object.trackPosition, 2)
        );
    }

    /*
     * Ritorna il valore di un attributo specifico in base ad un indice 
     * Usato nel KD-Tree per scegliere l'asse di divisione.
     */
    public double getData(int dataKey) {
        switch (dataKey) {
            case 0 -> {
                return speed;
            }
            case 1 -> {
                return trackPosition;
            }
            case 2 -> {
                return trackEdgeSensor3;
            }
            case 3 -> {
                return trackEdgeSensor6;
            }
            case 4 -> {
                return trackEdgeSensor9;
            }
            case 5 -> {
                return trackEdgeSensor12;
            }
            case 6 -> {
                return trackEdgeSensor15;
            }
            case 7 -> {
                return angleToTrackAxis;
            }
            case 8 -> {
                return classLabel;
            }
            default -> throw new IllegalArgumentException("Invalid data-key: " + dataKey);
        }
    }

    public void normalize(double[] minValues, double[] maxValues) {
        this.speed = normalizeValue(this.speed, minValues[0], maxValues[0]);
        this.trackPosition = normalizeValue(this.trackPosition, minValues[1], maxValues[1]);
        this.trackEdgeSensor3 = normalizeValue(this.trackEdgeSensor3, minValues[2], maxValues[2]);
        this.trackEdgeSensor6 = normalizeValue(this.trackEdgeSensor6, minValues[3], maxValues[3]);
        this.trackEdgeSensor9 = normalizeValue(this.trackEdgeSensor9, minValues[4], maxValues[4]);
        this.trackEdgeSensor12 = normalizeValue(this.trackEdgeSensor12, minValues[5], maxValues[5]);
        this.trackEdgeSensor15 = normalizeValue(this.trackEdgeSensor15, minValues[6], maxValues[6]);
        this.angleToTrackAxis = normalizeValue(this.angleToTrackAxis, minValues[7], maxValues[7]);
    }

    private double normalizeValue(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}
