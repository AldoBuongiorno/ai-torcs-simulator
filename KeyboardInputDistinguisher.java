package scr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class KeyboardInputDistinguisher {

    private final SimpleDriver driver;
    private final Action action;

    private final Set<Integer> pressedKeys = new HashSet<>();
    private boolean checkUpOrW;
    private boolean checkDownOrS;
    private boolean checkLeftOrA;
    private boolean checkRightOrD;
    private boolean checkR;
    
    public KeyboardInputDistinguisher(SimpleDriver driver){
        this.driver = driver;
        this.action = driver.trainingAction;
        this.checkDownOrS = false;
        this.checkLeftOrA = false;
        this.checkRightOrD = false;
        this.checkR = false;
        this.checkUpOrW = false;
        commandsRetrieval();//takeTrainingCommands
    }

    private void commandsRetrieval(){
        // Imposta il layout del JFrame
        JFrame frame = new JFrame("Keyboard-Input distinguisher");
        frame.setSize(300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Imposta il textField
        JTextField inputField = new JTextField(20);
        frame.add(inputField);

        // Imposta il listener per i tasti
        inputField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                driver.setCh(e.getKeyChar());

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        checkUpOrW = true;
                        action.accelerate = 1.0;
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        checkDownOrS = true;
                        action.brake = 1.0;
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        checkLeftOrA = true;
                        action.steering = +0.5;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        checkRightOrD = true;
                        action.steering = -0.5;
                        break;
                    case KeyEvent.VK_R:
                        checkR = true;
                        action.gear = -1;
                        break;
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        checkUpOrW = false;
                        action.accelerate = 0.0;
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        checkDownOrS = false;
                        action.brake = 0;
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        checkLeftOrA = false;
                        action.steering = 0;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        checkRightOrD = false;
                        action.steering = 0;
                        break;
                    case KeyEvent.VK_R:
                        checkR = false;
                        action.gear = 1;
                        action.accelerate = 0.0;
                        break;
                }

                if (pressedKeys.isEmpty()) {
                    driver.setCh(KeyEvent.getKeyText(KeyEvent.VK_1).charAt(0));
                } else {
                    updateLastPressedKey();
                }
            }

            private void updateLastPressedKey() {
                if (checkUpOrW) {
                    driver.setCh(KeyEvent.getKeyText(KeyEvent.VK_W).charAt(0));
                } else if (checkDownOrS) {
                    driver.setCh(KeyEvent.getKeyText(KeyEvent.VK_S).charAt(0));
                } else if (checkLeftOrA) {
                    driver.setCh(KeyEvent.getKeyText(KeyEvent.VK_A).charAt(0));
                } else if (checkRightOrD) {
                    driver.setCh(KeyEvent.getKeyText(KeyEvent.VK_D).charAt(0));
                } else if (checkR) {
                    driver.setCh(KeyEvent.getKeyText(KeyEvent.VK_R).charAt(0));
                }
            }

        });
        frame.setVisible(true);
    }

}
