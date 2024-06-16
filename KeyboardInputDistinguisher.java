package scr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardInputDistinguisher {

    private SimpleDriver driver;
    private final Action action;

    private boolean checkUpOrW;
    private boolean checkDownOrS;
    private boolean checkLeftOrA;
    private boolean checkRightOrD;
    private boolean checkShift;
    public char ch;

    public KeyboardInputDistinguisher(SimpleDriver driver) {
        this.driver = driver;
        this.action = driver.trainingAction;
        this.checkDownOrS = false;
        this.checkLeftOrA = false;
        this.checkRightOrD = false;
        this.checkShift = false;
        this.checkUpOrW = false;

        Timer timer = new Timer(50, e -> update());
        timer.start();

        trainingStart();
    }
    

    private void trainingStart(){
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
                driver.setCh(e.getKeyChar());
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        checkUpOrW = true;
                        // action.accelerate = 1.0;
                        return;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        checkDownOrS = true;
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        checkLeftOrA = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        checkRightOrD = true;
                        break;
                    case KeyEvent.VK_SHIFT:
                        checkShift = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        checkUpOrW = false;
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
                    case KeyEvent.VK_SHIFT:
                        checkShift = false;
                        action.gear = 0;
                        action.accelerate = 0;
                        break;
                }
            }
        });
        frame.setVisible(true);
    }

    public void update(){
        if(checkUpOrW){
            // if (action.accelerate == 1.0) {
            //     // action.accelerate = Math.min(action.accelerate + 0.02, 1.0);
            //     action.gear += 1;
            // }
            action.accelerate = Math.min(action.accelerate + 0.2, 1.0);
            // action.steering = 0;
            action.brake = 0;
        }else if(checkDownOrS){
            if(action.accelerate >= 0 || action.brake < 1){
                action.steering = 0;
                action.accelerate = Math.max(action.accelerate - 0.02, 0);
                action.brake += 0.2;
            }
        }else if(checkLeftOrA){
            action.steering = Math.min(action.steering + 0.03, 1.0);
            action.accelerate = Math.max(action.accelerate - 0.05, 0.4);
        }else if(checkRightOrD){
            action.steering = Math.max(action.steering - 0.03, -1.0);
            action.accelerate = Math.max(action.accelerate - 0.05, 0.4);
        }else if(checkShift){
            action.gear = -1;
            action.clutch = 1;
            if (action.steering > 0) {
                action.steering -= 0.1;
            } else if (action.steering < 0) {
                action.steering += 0.1;
            }
            action.accelerate = 0.4;
        }
    }
}
