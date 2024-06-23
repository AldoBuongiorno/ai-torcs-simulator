package scr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class KeyboardInputDistinguisher {

    private final SimpleDriver driver; //Oggetto identificante la vettura
    private final Action action; //Azione associata al driver.

    private final Set<Integer> pressedKeys = new HashSet<>();

    //Variabili di controllo
    private boolean checkUpOrW = false;
    private boolean checkDownOrS = false;
    private boolean checkRightOrD = false;
    private boolean checkLeftOrA = false;
    private boolean checkR = false;
    
    public KeyboardInputDistinguisher(SimpleDriver driver){
        this.driver = driver;
        this.action = driver.trainingAction;
        commandsRetrieval();
    }

    /*
     * Metodo aggiunto:
     * il metodo è fondamentale per la configurazione dell’interfaccia 
     * e del listener intercettante la pressione ed il rilascio dei tasti.
     */
    private void commandsRetrieval(){
        // Setup del layout del JFrame
        JFrame frame = new JFrame("Keyboard-Input Distinguisher");
        frame.setSize(300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Setup dell'input field utile a catturare i tasti premuti
        JTextField inputField = new JTextField(20);
        frame.add(inputField);
        inputField.addKeyListener(new KeyAdapter() {
           
            //Gestione delle pressioni dei tasti
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                driver.ch = e.getKeyCode();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        checkUpOrW = true;
                        action.accelerate = 1.0;  
                        break;
                    case KeyEvent.VK_S:
                        checkDownOrS = true;
                        action.brake = 1.0;
                        break;
                    case KeyEvent.VK_A:
                        checkLeftOrA = true;
                        action.steering = +0.5;
                        break;
                    case KeyEvent.VK_D:
                        checkRightOrD = true;
                        action.steering = -0.5;
                        break;
                    case KeyEvent.VK_R: //shift for reverse
                        checkR = true;
                        action.gear = -1;
                        break;
                }
            }

            // Gestione del rilascio dei tasti
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        checkUpOrW = false;
                        action.accelerate = 0.0;
                        break;
                    case KeyEvent.VK_S:
                        checkDownOrS = false;
                        action.brake = 0.0;
                        break;
                    case KeyEvent.VK_A:
                        checkLeftOrA = false;
                        action.steering = 0.0;
                        break;
                    case KeyEvent.VK_D:
                        checkRightOrD = false;
                        action.steering = 0.0;
                        break;
                    case KeyEvent.VK_R:
                        checkR = false;
                        action.gear = 1;
                        action.accelerate = 0.0;
                        break;
                }

                if (pressedKeys.isEmpty()) {
                    driver.ch = -1;
                } else {
                    updateLastPressedKey();
                }
            }

            /*
             * Metodo aggiunto:
             * Questo metodo serve ad aggiornare nel SimpleDriver, 
             * così da comunicare tramite la variabile ch, l'ultimo tasto premuto.
             */
            private void updateLastPressedKey() {
                if (checkUpOrW) {
                    driver.ch = KeyEvent.VK_W;
                } else if (checkDownOrS) {
                    driver.ch = KeyEvent.VK_S;
                } else if (checkLeftOrA) {
                    driver.ch = KeyEvent.VK_A;
                } else if (checkRightOrD) {
                    driver.ch = KeyEvent.VK_D;
                } else if (checkR) {
                    driver.ch = KeyEvent.VK_R;
                }
            }

        });
        frame.setVisible(true);
    }

}
