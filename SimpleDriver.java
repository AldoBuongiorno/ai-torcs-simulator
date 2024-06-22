package scr;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class SimpleDriver extends Controller {

	private final boolean train;
	private final boolean auto;
	private final boolean classify;
	public Action trainingAction;
	public int ch;
	private NearestNeighbor knn;
    private String filename;
	private boolean correct = false;
	private final HashMap<String, Double> maxValues; 
	private final HashMap<String, Double> minValues;

	/* Costanti di cambio marcia */
	final int[] gearUp = {7500, 8000, 8500, 9000, 9500, 0};
	final int[] gearDown = {0, 2800, 3200, 4800, 5200, 5000};

	/* Constanti */
	final int stuckTime = 25;
	final float stuckAngle = (float) 0.523598775; // PI/6

	/* Costanti di accelerazione e di frenata */
	final float maxSpeedDist = 70;
	final float maxSpeed = 150;
	final float sin5 = (float) 0.08716;
	final float cos5 = (float) 0.99619;

	/* Costanti di sterzata */
	final float steerLock = (float) 0.785398;
	final float steerSensitivityOffset = (float) 80.0;
	final float wheelSensitivityCoeff = 1;

	/* Costanti del filtro ABS */
	final float wheelRadius[] = { (float) 0.3179, (float) 0.3179, (float) 0.3276, (float) 0.3276 };
	final float absSlip = (float) 2.0;
	final float absRange = (float) 3.0;
	final float absMinSpeed = (float) 3.0;

	/* Costanti da stringere */
	final float clutchMax = (float) 0.5;
	final float clutchDelta = (float) 0.05;
	final float clutchRange = (float) 0.82;
	final float clutchDeltaTime = (float) 0.02;
	final float clutchDeltaRaced = 10;
	final float clutchDec = (float) 0.01;
	final float clutchMaxModifier = (float) 1.3;
	final float clutchMaxTime = (float) 1.5;

	private int stuck = 0;

	// current clutch
	private float clutch = 0;

	public SimpleDriver(){
		auto = false;
		train = false;
		classify = true;
		trainingAction = new Action();

		maxValues = new HashMap<String, Double>();
		minValues = new HashMap<String, Double>();

		maxValues.put("speed", 310.0);
		maxValues.put("negativeSpeed", -0.001);
		maxValues.put("trackPosition", 1.0);
		maxValues.put("trackEdgeSensor4", 200.0);
		maxValues.put("trackEdgeSensor6", 200.0);
		maxValues.put("trackEdgeSensor8", 200.0);
		maxValues.put("trackEdgeSensor9", 200.0);
		maxValues.put("trackEdgeSensor10", 200.0);
		maxValues.put("trackEdgeSensor12", 200.0);
		maxValues.put("trackEdgeSensor14", 200.0);
		maxValues.put("angleToTrackAxis", Math.PI);

		minValues.put("speed", 0.0);
		minValues.put("negativeSpeed", -60.0);
		minValues.put("trackPosition", -1.0);
		minValues.put("trackEdgeSensor4", 0.0);
		minValues.put("trackEdgeSensor6", 0.0);
		minValues.put("trackEdgeSensor8", 0.0);
		minValues.put("trackEdgeSensor9", 0.0);
		minValues.put("trackEdgeSensor10", 0.0);
		minValues.put("trackEdgeSensor12", 0.0);
		minValues.put("trackEdgeSensor14", 0.0);
		minValues.put("angleToTrackAxis", (Math.PI*-1));

		if(train){
			try (BufferedWriter bw = new BufferedWriter(new FileWriter("Torcs_data.csv", true))) {
				bw.append("speed;trackPosition;trackEdgeSensor4;trackEdgeSensor6;trackEdgeSensor8;trackEdgeSensor9;"
					+ "trackEdgeSensor10;trackEdgeSensor12;trackEdgeSensor14;angleToTrackAxis;classLabel" + '\n');
			} catch (IOException ex) {
				Logger.getLogger(SimpleDriver.class.getName()).log(Level.SEVERE, null, ex);
			}
			SwingUtilities.invokeLater(() -> new KeyboardInputDistinguisher(this));
		}
		if (classify) {
            //filename = "C:\\Users\\salva\\Desktop\\Universita\\2023-2024\\AI\\AI-Torcs-Project\\ClientTorcSorgente\\classes\\Torcs_data.csv";
			filename = "C:\\Users\\Aldo\\Desktop\\AI\\Sorgente\\classes\\Torcs_data.csv";
            knn = new NearestNeighbor(filename);
        }
	}

	public void reset() {
		System.out.println("Restarting the race!");

	}

	public void shutdown() {
		System.out.println("Bye bye!");
	}

	private int getGear(SensorModel sensors) {
		int gear = sensors.getGear();
		double rpm = sensors.getRPM();

		// Se la marcia è 0 (N) o -1 (R) restituisce semplicemente 1
		if (gear < 1)
			return 1;

		// Se il valore di RPM dell'auto è maggiore di quello suggerito
		// sale di marcia rispetto a quella attuale
		if (gear < 6 && rpm >= gearUp[gear - 1])
			return gear + 1;
		// check if the RPM value of car is greater than the one suggested
        // to shift up the gear from the current one
        if (gear < 6 && rpm >= gearUp[gear - 1]) {
            return gear + 1;
        } else // check if the RPM value of car is lower than the one suggested
        // to shift down the gear from the current one
        if (gear > 1 && rpm <= gearDown[gear - 1]) {
            return gear - 1;
        } else // otherwhise keep current gear
        {
            return gear;
        }
	}

	private float getSteer(SensorModel sensors) {
		/** L'angolo di sterzata viene calcolato correggendo l'angolo effettivo della vettura
		 * rispetto all'asse della pista [sensors.getAngle()] e regolando la posizione della vettura
		 * rispetto al centro della pista [sensors.getTrackPos()*0,5].
		 */
		float targetAngle = (float) (sensors.getAngleToTrackAxis() - sensors.getTrackPosition() * 0.5);
		// ad alta velocità ridurre il comando di sterzata per evitare di perdere il controllo
		if (sensors.getSpeed() > steerSensitivityOffset)
			return (float) (targetAngle
					/ (steerLock * (sensors.getSpeed() - steerSensitivityOffset) * wheelSensitivityCoeff));
		else
			return (targetAngle) / steerLock;
	}

	private float getAccel(SensorModel sensors) {
		// controlla se l'auto è fuori dalla carreggiata
		if (sensors.getTrackPosition() > -1 && sensors.getTrackPosition() < 1) {
			// lettura del sensore a +5 gradi rispetto all'asse dell'automobile
			float rxSensor = (float) sensors.getTrackEdgeSensors()[10];
			// lettura del sensore parallelo all'asse della vettura
			float sensorsensor = (float) sensors.getTrackEdgeSensors()[9];
			// lettura del sensore a -5 gradi rispetto all'asse dell'automobile
			float sxSensor = (float) sensors.getTrackEdgeSensors()[8];

			float targetSpeed;

			// Se la pista è rettilinea e abbastanza lontana da una curva, quindi va alla massima velocità
			if (sensorsensor > maxSpeedDist || (sensorsensor >= rxSensor && sensorsensor >= sxSensor))
				targetSpeed = maxSpeed;
			else {
				// In prossimità di una curva a destra
				if (rxSensor > sxSensor) {

					// Calcolo dell'"angolo" di sterzata
					float h = sensorsensor * sin5;
					float b = rxSensor - sensorsensor * cos5;
					float sinAngle = b * b / (h * h + b * b);

					// Set della velocità in base alla curva
					targetSpeed = maxSpeed * (sensorsensor * sinAngle / maxSpeedDist);
				}
				// In prossimità di una curva a sinistra
				else {
					// Calcolo dell'"angolo" di sterzata
					float h = sensorsensor * sin5;
					float b = sxSensor - sensorsensor * cos5;
					float sinAngle = b * b / (h * h + b * b);

					// eSet della velocità in base alla curva
					targetSpeed = maxSpeed * (sensorsensor * sinAngle / maxSpeedDist);
				}
			}

			/**
			 * Il comando di accelerazione/frenata viene scalato in modo esponenziale rispetto
			 * alla differenza tra velocità target e quella attuale
			 */
			return (float) (2 / (1 + Math.exp(sensors.getSpeed() - targetSpeed)) - 1);
		} else
			// Quando si esce dalla carreggiata restituisce un comando di accelerazione moderata
			return (float) 0.3;
	}

	public Action control(SensorModel sensors){

		if(auto){
			// Controlla se l'auto è attualmente bloccata
			/**
				Se l'auto ha un angolo, rispetto alla traccia, superiore a 30°
				incrementa "stuck" che è una variabile che indica per quanti cicli l'auto è in
				condizione di difficoltà.
				Quando l'angolo si riduce, "stuck" viene riportata a 0 per indicare che l'auto è
				uscita dalla situaizone di difficoltà
			**/
			if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle) {
				// update stuck counter
				stuck++;
			} else {
				// if not stuck reset stuck counter
				stuck = 0;
			}

			// Applicare la polizza di recupero o meno in base al tempo trascorso
			/**
			Se "stuck" è superiore a 25 (stuckTime) allora procedi a entrare in situaizone di RECOVERY
			per far fronte alla situazione di difficoltà
			**/

			if (stuck > stuckTime) { //Auto Bloccata
				/**
				 * Impostare la marcia e il comando di sterzata supponendo che l'auto stia puntando
				 * in una direzione al di fuori di pista
				 **/

				// Per portare la macchina parallela all'asse TrackPos
				float steer = (float) (-sensors.getAngleToTrackAxis() / steerLock);
				int gear = -1; // Retromarcia

				// Se l'auto è orientata nella direzione corretta invertire la marcia e sterzare
				if (sensors.getAngleToTrackAxis() * sensors.getTrackPosition() > 0) {
					gear = 1;
					steer = -steer;
				}
				clutch = clutching(sensors, clutch);
				// Costruire una variabile CarControl e restituirla
				Action action = new Action();
				action.gear = gear;
				action.steering = steer;
				action.accelerate = 1.0;
				action.brake = 0;
				action.clutch = clutch;
			}

			else //Auto non Bloccata
			{
				// Calcolo del comando di accelerazione/frenata
				float accel_and_brake = getAccel(sensors);

				// Calcolare marcia da utilizzare
				int gear = getGear(sensors);

				// Calcolo angolo di sterzata
				float steer = getSteer(sensors);

				// Normalizzare lo sterzo
				if (steer < -1)
					steer = -1;
				if (steer > 1)
					steer = 1;

				// Impostare accelerazione e frenata dal comando congiunto accelerazione/freno
				float accel, brake;
				if (accel_and_brake > 0) {
					accel = accel_and_brake;
					brake = 0;
				} else {
					accel = 0;

					// Applicare l'ABS al freno
					brake = filterABS(sensors, -accel_and_brake);
				}
				clutch = clutching(sensors, clutch);

				// Costruire una variabile CarControl e restituirla
				Action action = new Action();
				action.gear = gear;
				action.steering = steer;
				action.accelerate = accel;
				action.brake = brake;
				action.clutch = clutch;
				return action;
			}
		}

		if(trainingAction.gear != -1.0 || sensors.getSpeed() > 1 ) {
			trainingAction.gear = getGear(sensors);
		}
		else if(trainingAction.gear == -1 || sensors.getSpeed() < 1 ) {
			trainingAction.accelerate = 1.0;
		}
		trainingAction.brake = filterABS(sensors, (float) trainingAction.brake);
		//con o senza clutching non cambia niente
        trainingAction.clutch = clutching(sensors, (float) trainingAction.clutch);			

		if(train){
			//esportazione dati su file .csv
			try{
				//Thread.sleep(3000);
				exportToCSV(sensors);
			}
			catch(IOException ex){
				System.err.println("Error: " + ex.getMessage());
			} /* catch (InterruptedException ex) {
				System.err.println(ex.getMessage());
			}  */
			return trainingAction;
		}

		if (classify) {
            predictControl(sensors);
			return trainingAction;
        }

		//ritornare un Action appropriata
		return trainingAction;
		
	}

	private float filterABS(SensorModel sensors, float brake) {
		// Converte la velocità in m/s
		float speed = (float) (sensors.getSpeed() / 3.6);

		// Quando la velocità è inferiore alla velocità minima per l'abs non interviene in caso di frenata
		if (speed < absMinSpeed)
			return brake;

		// Calcola la velocità delle ruote in m/s
		float slip = 0.0f;
		for (int i = 0; i < 4; i++) {
			slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
		}

		// Lo slittamento è la differenza tra la velocità effettiva dell'auto e la velocità media delle ruote
		slip = speed - slip / 4.0f;

		// Quando lo slittamento è troppo elevato, si applica l'ABS
		if (slip > absSlip) {
			brake = brake - (slip - absSlip) / absRange;
		}

		// Controlla che il freno non sia negativo, altrimenti lo imposta a zero
		if (brake < 0)
			return 0;
		else
			return brake;
	}

	float clutching(SensorModel sensors, float clutch) {

		float maxClutch = clutchMax;

		// Controlla se la situazione attuale è l'inizio della gara
		if (sensors.getCurrentLapTime() < clutchDeltaTime && getStage() == Stage.RACE
				&& sensors.getDistanceRaced() < clutchDeltaRaced)
			clutch = maxClutch;

		// Regolare il valore attuale della frizione
		if (clutch > 0) {
			double delta = clutchDelta;
			if (sensors.getGear() < 2) {

				// Applicare un'uscita più forte della frizione quando la marcia è una e la corsa è appena iniziata.
				delta /= 2;
				maxClutch *= clutchMaxModifier;
				if (sensors.getCurrentLapTime() < clutchMaxTime)
					clutch = maxClutch;
			}

			// Controllare che la frizione non sia più grande dei valori massimi
			clutch = Math.min(maxClutch, clutch);

			// Se la frizione non è al massimo valore, diminuisce abbastanza rapidamente
			if (clutch != maxClutch) {
				clutch -= delta;
				clutch = Math.max((float) 0.0, clutch);
			}
			// Se la frizione è al valore massimo, diminuirla molto lentamente.
			else
				clutch -= clutchDec;
		}
		return clutch;
	}

	public float[] initAngles() {

		float[] angles = new float[19];

		/*
		 * set angles as
		 * {-90,-75,-60,-45,-30,-20,-15,-10,-5,0,5,10,15,20,30,45,60,75,90}
		 */
		for (int i = 0; i < 5; i++) {
			angles[i] = -90 + i * 15;
			angles[18 - i] = 90 - i * 15;
		}

		for (int i = 5; i < 9; i++) {
			angles[i] = -20 + (i - 5) * 5;
			angles[18 - i] = 20 - (i - 5) * 5;
		}
		angles[9] = 0;
		return angles;
	}

	//Scrittura dati auto su un file csv
	private void exportToCSV(SensorModel sensors) throws IOException {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter("Torcs_data.csv", true))) {
			System.out.println("speed = " + sensors.getSpeed());
			if(sensors.getSpeed() >= 0) {
				bw.append(normalizeSensorValue(sensors.getSpeed(), "speed") + ";");
			}
			else if(sensors.getSpeed() < 0) {
				bw.append(normalizeSensorValue(sensors.getSpeed(), "negativeSpeed") + ";");
			}
			bw.append(normalizeSensorValue(sensors.getTrackPosition(), "trackPosition") + "; ");
			bw.append(normalizeSensorValue(sensors.getTrackEdgeSensors()[4], "trackEdgeSensor4") + "; ");
			bw.append(normalizeSensorValue(sensors.getTrackEdgeSensors()[6], "trackEdgeSensor6") + "; ");
			bw.append(normalizeSensorValue(sensors.getTrackEdgeSensors()[8], "trackEdgeSensor8") + "; ");
			bw.append(normalizeSensorValue(sensors.getTrackEdgeSensors()[9], "trackEdgeSensor9") + "; ");
			bw.append(normalizeSensorValue(sensors.getTrackEdgeSensors()[10], "trackEdgeSensor10") + "; ");
			bw.append(normalizeSensorValue(sensors.getTrackEdgeSensors()[12], "trackEdgeSensor12") + "; ");
			bw.append(normalizeSensorValue(sensors.getTrackEdgeSensors()[14], "trackEdgeSensor14") + "; ");
			bw.append(normalizeSensorValue(sensors.getAngleToTrackAxis(), "angleToTrackAxis") + "; ");

			bw.append(
				(ch == KeyEvent.VK_W ? String.valueOf(0)
            	: ch == KeyEvent.VK_S ? String.valueOf(1)
                : ch == KeyEvent.VK_A ? String.valueOf(2)
				: ch == KeyEvent.VK_D ? String.valueOf(3)
                : ch == KeyEvent.VK_R ? String.valueOf(4)
				: String.valueOf(5))
			);

			bw.append('\n');
            
        } catch (IOException ex) {
            Logger.getLogger(SimpleDriver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

	public double normalizeSensorValue(double value, String sensorIndex){
		double max = maxValues.get(sensorIndex);
		double min = minValues.get(sensorIndex);
		return (value - min) / (max - min);
	}

	public void predictControl(SensorModel sensors) {
        //valore di k per il K-NN. Se voglio usare NN, allora k=1 altrimenti k= (es) 5
        int k = 3;
		double speed = 0;
		if(sensors.getSpeed() >= 0) {
			speed = normalizeSensorValue(sensors.getSpeed(), "speed");
		}
		else if(sensors.getSpeed() < 0) {
			speed = normalizeSensorValue(sensors.getSpeed(), "negativeSpeed");
		}
        TrainingData datas = new TrainingData(
			speed,
			normalizeSensorValue(sensors.getTrackPosition(), "trackPosition"),
			normalizeSensorValue(sensors.getTrackEdgeSensors()[4], "trackEdgeSensor4"),
			normalizeSensorValue(sensors.getTrackEdgeSensors()[6], "trackEdgeSensor6"),			
			normalizeSensorValue(sensors.getTrackEdgeSensors()[8], "trackEdgeSensor8"),
			normalizeSensorValue(sensors.getTrackEdgeSensors()[9], "trackEdgeSensor9"),
			normalizeSensorValue(sensors.getTrackEdgeSensors()[10], "trackEdgeSensor10"),
			normalizeSensorValue(sensors.getTrackEdgeSensors()[12], "trackEdgeSensor12"),
			normalizeSensorValue(sensors.getTrackEdgeSensors()[14], "trackEdgeSensor14"),
			normalizeSensorValue(sensors.getAngleToTrackAxis(), "angleToTrackAxis") 
		);

        int predicted = knn.classify(datas, k);
        System.out.println(predicted);
        autonomous(predicted, sensors);
    }

	private int frenaCounter = 0;

	private void autonomous(int cls, SensorModel sensors) {

		//correzione dei fuori pista
		correctOffTrack(sensors);

		if(correct == false) {

			switch (cls) {
				case 0 : {
					accelera(sensors);
					break;
				}
				case 1 : {
					frena();
					break;
				}
				case 2 : {
					sterzaSX();
					break;
				}
				case 3 : {
					sterzaDX();
					break;
				}
				case 4 : {
					retro();
					break;
				}
				case 5 : {
					setDefault();
					break;
				}
        	}
		}
    }

	public void accelera(SensorModel sensor){
        trainingAction.accelerate = 1.0;
		trainingAction.steering = 0.0;
		trainingAction.brake = 0.0;
    }

    public void frena(){
        trainingAction.brake = 0.8;
		trainingAction.accelerate = 0.0;
		trainingAction.steering = 0.0;
    }

    public void sterzaSX(){
        trainingAction.steering = +0.5;
		trainingAction.accelerate = 0.25;
		trainingAction.brake = 0.0;
    }

    public void sterzaDX(){
        trainingAction.steering = -0.5;
		trainingAction.accelerate = 0.25;
		trainingAction.brake = 0.0;
    }

    public void retro(){
		trainingAction.gear = -1;
		trainingAction.accelerate = 0.6;	
		trainingAction.steering = 0.0;
		trainingAction.brake = 0.0;		
    }
	
	public void setDefault(){
		trainingAction.accelerate = 0.3;
		trainingAction.steering = 0.0;
		trainingAction.brake = 0.0;
	}

	private void correctOffTrack(SensorModel sensors){
		//rileva fuori pista bordo sx
		if(sensors.getTrackPosition() > 1.00) {
			correct = true;
			offTrackSterzaDX();
		}
		//rileva fuori pista bordo dx
		else if(sensors.getTrackPosition() < -1.00) {
			correct = true;
			offTrackSterzaSX();
		}
		else {
			correct = false;
		}

	}

	public void offTrackSterzaSX(){
        trainingAction.steering = +0.15;
		trainingAction.accelerate = 0.1;
		trainingAction.brake = 0.0;
    }

    public void offTrackSterzaDX(){
        trainingAction.steering = -0.15;
		trainingAction.accelerate = 0.1;
		trainingAction.brake = 0.0;
    }


}
