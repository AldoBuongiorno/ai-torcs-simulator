package scr;

import java.lang.Math;

public class TrainingData {

    private double speed;
    private double trackPos;
    private double trackedgeSensors4;
    private double trackedgeSensors6;
    private double trackedgeSensors8;
    private double trackedgeSensors9;
    private double trackedgeSensors10;
    private double trackedgeSensors12;
    private double trackedgeSensors14;
    private double angle;
    public int classLabel;

    
    public TrainingData(double speed, double trackPos, double trackedgeSensors4, 
        double trackedgeSensors6, double trackedgeSensors8, double trackedgeSensors9, double trackedgeSensors10, 
        double trackedgeSensors12, double trackedgeSensors14, double angle) {
        this.speed = speed;
        this.trackPos = trackPos;
        this.trackedgeSensors4 = trackedgeSensors4;
        this.trackedgeSensors6 = trackedgeSensors6;
        this.trackedgeSensors8 = trackedgeSensors8;
        this.trackedgeSensors9 = trackedgeSensors9;
        this.trackedgeSensors10 = trackedgeSensors10;
        this.trackedgeSensors12 = trackedgeSensors12;
        this.trackedgeSensors14 = trackedgeSensors14;
        this.angle = angle;
    }

    public TrainingData(double speed, double trackPos, double trackedgeSensors4, 
        double trackedgeSensors6, double trackedgeSensors8, double trackedgeSensors9, double trackedgeSensors10, 
        double trackedgeSensors12, double trackedgeSensors14, double angle, int classLabel) {
        
        this.speed = speed;
        this.trackPos = trackPos;
        this.trackedgeSensors4 = trackedgeSensors4;
        this.trackedgeSensors6 = trackedgeSensors6;
        this.trackedgeSensors8 = trackedgeSensors8;
        this.trackedgeSensors9 = trackedgeSensors9;
        this.trackedgeSensors10 = trackedgeSensors10;
        this.trackedgeSensors12 = trackedgeSensors12;
        this.trackedgeSensors14 = trackedgeSensors14;
        this.angle = angle;
        this.classLabel = classLabel; 
    }

    public TrainingData(String line) {
        String[] parts = line.split(";");
        this.speed = Double.parseDouble(parts[0].trim());
        this.trackPos = Double.parseDouble(parts[1].trim());
        this.trackedgeSensors4 = Double.parseDouble(parts[2].trim());
        this.trackedgeSensors6 = Double.parseDouble(parts[3].trim());
        this.trackedgeSensors8 = Double.parseDouble(parts[4].trim());
        this.trackedgeSensors9 = Double.parseDouble(parts[5].trim());
        this.trackedgeSensors10 = Double.parseDouble(parts[6].trim());
        this.trackedgeSensors12 = Double.parseDouble(parts[7].trim());
        this.trackedgeSensors14 = Double.parseDouble(parts[8].trim());
        this.angle = Double.parseDouble(parts[9].trim());
        this.classLabel = Integer.parseInt(parts[10].trim()); // Parse class correctly
    }

    public double distance(TrainingData other) {
        return Math.sqrt(
            Math.pow(this.speed - other.speed, 2) +
            Math.pow(this.trackPos - other.trackPos, 2) +
            Math.pow(this.trackedgeSensors4 - other.trackedgeSensors4, 2) +
            Math.pow(this.trackedgeSensors6 - other.trackedgeSensors6, 2) +
            Math.pow(this.trackedgeSensors8 - other.trackedgeSensors8, 2) +
            Math.pow(this.trackedgeSensors9 - other.trackedgeSensors9, 2) +
            Math.pow(this.trackedgeSensors10 - other.trackedgeSensors10, 2) +
            Math.pow(this.trackedgeSensors12 - other.trackedgeSensors12, 2) +
            Math.pow(this.trackedgeSensors14 - other.trackedgeSensors14, 2) +
            Math.pow(this.angle - other.angle, 2)
        );
    }

    public double getCoordinate(int axis) {
        switch (axis) {
            case 0 -> {
                return speed;
            }
            case 1 -> {
                return trackPos;
            }
            case 2 -> {
                return trackedgeSensors4;
            }
            case 3 -> {
                return trackedgeSensors6;
            }
            case 4 -> {
                return trackedgeSensors8;
            }
            case 5 -> {
                return trackedgeSensors9;
            }
            case 6 -> {
                return trackedgeSensors10;
            }
            case 7 -> {
                return trackedgeSensors12;
            }
            case 8 -> {
                return trackedgeSensors14;
            }
            case 9 -> {
                return angle;
            }
            default -> throw new IllegalArgumentException("Invalid axis: "  + axis);
        }
    }

}