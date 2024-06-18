package scr;

public class TrainingData {
    //speed;trackPosition;trackEdgeSensor3;trackEdgeSensor6;trackEdgeSensor9;trackEdgeSensor12;trackEdgeSensor15;angleToTrackAxis;classLabel
    private double speed;
    private double trackPosition;
    private double trackEdgeSensor4;
    private double trackEdgeSensor6;
    private double trackEdgeSensor8;
    private double trackEdgeSensor9;
    private double trackEdgeSensor10;
    private double trackEdgeSensor12;
    private double trackEdgeSensor14;
    private double angleToTrackAxis;
    public int classLabel;
    
    //Costruttore standard
    public TrainingData(double speed, double trackPosition, double trackEdgeSensor4, double trackEdgeSensor6, 
        double trackEdgeSensor8, double trackEdgeSensor9, double trackEdgeSensor10, double trackEdgeSensor12, 
        double trackEdgeSensor14, double angleToTrackAxis, int classLabel) {
        this.speed = speed;
        this.trackPosition = trackPosition;
        this.trackEdgeSensor4 = trackEdgeSensor4;
        this.trackEdgeSensor6 = trackEdgeSensor6;
        this.trackEdgeSensor8 = trackEdgeSensor8;
        this.trackEdgeSensor9 = trackEdgeSensor9;
        this.trackEdgeSensor10 = trackEdgeSensor10;
        this.trackEdgeSensor12 = trackEdgeSensor12;
        this.trackEdgeSensor14 = trackEdgeSensor14;
        this.angleToTrackAxis = angleToTrackAxis;
        this.classLabel = classLabel;
    }

    public TrainingData(double speed, double trackPosition, double trackEdgeSensor4, double trackEdgeSensor6, 
        double trackEdgeSensor8, double trackEdgeSensor9, double trackEdgeSensor10, double trackEdgeSensor12, 
        double trackEdgeSensor14, double angleToTrackAxis) {
        this.speed = speed;
        this.trackPosition = trackPosition;
        this.trackEdgeSensor4 = trackEdgeSensor4;
        this.trackEdgeSensor6 = trackEdgeSensor6;
        this.trackEdgeSensor8 = trackEdgeSensor8;
        this.trackEdgeSensor9 = trackEdgeSensor9;
        this.trackEdgeSensor10 = trackEdgeSensor10;
        this.trackEdgeSensor12 = trackEdgeSensor12;
        this.trackEdgeSensor14 = trackEdgeSensor14;
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
        this.trackEdgeSensor4 = Double.parseDouble(parts[2].trim());
        this.trackEdgeSensor6 = Double.parseDouble(parts[3].trim());
        this.trackEdgeSensor8 = Double.parseDouble(parts[4].trim());
        this.trackEdgeSensor9 = Double.parseDouble(parts[5].trim());
        this.trackEdgeSensor10 = Double.parseDouble(parts[6].trim());
        this.trackEdgeSensor12 = Double.parseDouble(parts[7].trim());
        this.trackEdgeSensor14 = Double.parseDouble(parts[8].trim());
        this.angleToTrackAxis = Double.parseDouble(parts[9].trim());
        this.classLabel = Integer.parseInt(parts[11].trim());
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
            + Math.pow(this.trackEdgeSensor4 - object.trackEdgeSensor4, 2)
            + Math.pow(this.trackEdgeSensor6 - object.trackEdgeSensor6, 2)
            + Math.pow(this.trackEdgeSensor8 - object.trackEdgeSensor8, 2)
            + Math.pow(this.trackEdgeSensor9 - object.trackEdgeSensor9, 2)
            + Math.pow(this.trackEdgeSensor10 - object.trackEdgeSensor10, 2)
            + Math.pow(this.trackEdgeSensor12 - object.trackEdgeSensor12, 2)
            + Math.pow(this.trackEdgeSensor14 - object.trackEdgeSensor14, 2)
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
                return trackEdgeSensor4;
            }
            case 3 -> {
                return trackEdgeSensor6;
            }
            case 4 -> {
                return trackEdgeSensor8;
            }
            case 5 -> {
                return trackEdgeSensor9;
            }
            case 6 -> {
                return trackEdgeSensor10;
            }
            case 7 -> {
                return trackEdgeSensor12;
            }
            case 8 -> {
                return trackEdgeSensor14;
            }
            case 9 -> {
                return angleToTrackAxis;
            }
            default -> throw new IllegalArgumentException("Invalid axis: "  + dataKey);
        }
    }

    /* public void normalize(double[] minValues, double[] maxValues) {
        this.speed = normalizeValue(this.speed, minValues[0], maxValues[0]);
        this.trackPosition = normalizeValue(this.trackPosition, minValues[1], maxValues[1]);
        this.trackEdgeSensor0 = normalizeValue(this.trackEdgeSensor0, minValues[2], maxValues[2]);
        this.trackEdgeSensor1 = normalizeValue(this.trackEdgeSensor1, minValues[3], maxValues[3]);
        this.trackEdgeSensor2 = normalizeValue(this.trackEdgeSensor2, minValues[4], maxValues[4]);
        this.trackEdgeSensor3 = normalizeValue(this.trackEdgeSensor3, minValues[5], maxValues[5]);
        this.trackEdgeSensor4 = normalizeValue(this.trackEdgeSensor4, minValues[6], maxValues[6]);
        this.trackEdgeSensor5 = normalizeValue(this.trackEdgeSensor5, minValues[7], maxValues[7]);
        this.trackEdgeSensor6 = normalizeValue(this.trackEdgeSensor6, minValues[8], maxValues[8]);
        this.trackEdgeSensor7 = normalizeValue(this.trackEdgeSensor7, minValues[9], maxValues[9]);
        this.trackEdgeSensor8 = normalizeValue(this.trackEdgeSensor8, minValues[10], maxValues[10]);
        this.trackEdgeSensor9 = normalizeValue(this.trackEdgeSensor9, minValues[11], maxValues[11]);
        this.trackEdgeSensor10 = normalizeValue(this.trackEdgeSensor10, minValues[12], maxValues[12]);
        this.trackEdgeSensor11 = normalizeValue(this.trackEdgeSensor11, minValues[13], maxValues[13]);
        this.trackEdgeSensor12 = normalizeValue(this.trackEdgeSensor12, minValues[14], maxValues[14]);
        this.trackEdgeSensor13 = normalizeValue(this.trackEdgeSensor13, minValues[15], maxValues[15]);
        this.trackEdgeSensor14 = normalizeValue(this.trackEdgeSensor14, minValues[16], maxValues[16]);
        this.trackEdgeSensor15 = normalizeValue(this.trackEdgeSensor15, minValues[17], maxValues[17]);
        this.trackEdgeSensor16 = normalizeValue(this.trackEdgeSensor16, minValues[18], maxValues[18]);
        this.trackEdgeSensor17 = normalizeValue(this.trackEdgeSensor17, minValues[19], maxValues[19]);
        this.trackEdgeSensor18 = normalizeValue(this.trackEdgeSensor18, minValues[20], maxValues[20]);
        this.angleToTrackAxis = normalizeValue(this.angleToTrackAxis, minValues[21], maxValues[21]);
    }

    private double normalizeValue(double value, double min, double max) {
        return (value - min) / (max - min);
    } */
}
