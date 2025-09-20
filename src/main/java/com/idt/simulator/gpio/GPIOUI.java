package com.idt.simulator.gpio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GPIOUI extends JFrame {
    private static final int NUM_PINS = 32;
    private final boolean[] pinStates = new boolean[NUM_PINS];
    private final JPanel pinPanel;

    public GPIOUI() {
        setTitle("GPIO Module");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pinPanel = new JPanel(new GridLayout(2, 16, 5, 5));

        for (int i = 0; i < NUM_PINS; i++) {
            JButton pin = new JButton();
            pin.setPreferredSize(new Dimension(50, 50));
            pin.setBackground(Color.GRAY);
            pin.setOpaque(true);
            pin.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            pin.addMouseListener(new PinMouseListener(i));
            pinPanel.add(pin);
        }


        add(pinPanel);
        pack();
    }

    private class PinMouseListener extends MouseAdapter {
        private final int pinNumber;

        PinMouseListener(int pinNumber) {
            this.pinNumber = pinNumber;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            togglePin(pinNumber);
        }
    }

    private void togglePin(int pinNumber) {
        pinStates[pinNumber] = !pinStates[pinNumber];
        Color color = pinStates[pinNumber] ? Color.GREEN : Color.GRAY;
        JButton pin = (JButton) pinPanel.getComponent(pinNumber);
        pin.setBackground(color);
    }

    public void setPin(int pin) {
        if (!pinStates[pin]) {
            togglePin(pin);
        }
    }

    public void resetPin(int pin) {
        if (pinStates[pin]) {
            togglePin(pin);
        }
    }

    public boolean getPin(int pin) {
        return this.pinStates[pin];
    }
}