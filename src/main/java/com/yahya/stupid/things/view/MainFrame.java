package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    public MainFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setUndecorated(true);
        init();
    }

    private void init() {
        JPanel contentPane = new JPanel(new BorderLayout());



//        DVDScreen screen = new DVDScreen(this);
//        TriangleScreen screen = new TriangleScreen(this);
//        CircleScreen screen = new CircleScreen(this);
//        MosaicScreen screen = new MosaicScreen(this);
//        PixelatedImageScreen screen = new PixelatedImageScreen(this);

//        DarkenScreen screen = new DarkenScreen(this);

//        RandomScreen screen = new RandomScreen(this);
        ManualScreen screen = new ManualScreen(this);
        initToolbar(contentPane, screen);
        contentPane.add(screen, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void initToolbar(JPanel contentPane, Screen screen) {
        JLabel exitLabel = new JLabel("Exit");

        JPanel controllerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel startLabel = new JLabel("Start");
        startLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                screen.start();
            }
        });
        startLabel.setForeground(Color.BLUE);
        startLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        controllerPanel.add(startLabel);

        JLabel pauseLabel = new JLabel("Pause");
        pauseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                screen.pause();
            }
        });
        pauseLabel.setForeground(Color.BLUE);
        pauseLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        controllerPanel.add(pauseLabel);

        JLabel restartLabel = new JLabel("Restart");
        restartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                screen.clear();
                screen.start();
            }
        });
        restartLabel.setForeground(Color.BLUE);
        restartLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        controllerPanel.add(restartLabel);

        JLabel clearLabel = new JLabel("Clear");
        clearLabel.setForeground(Color.BLUE);
        clearLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                screen.clear();
            }
        });
        controllerPanel.add(clearLabel);




        exitLabel.setForeground(Color.PINK);
        exitLabel.setBounds(getWidth() - 50, 0, 50, 20);
        exitLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                exit();
            }
        });

        controllerPanel.setBackground(getBackground());
        controllerPanel.setBounds(0, -3, getWidth() - 50, 20);
        controllerPanel.add(exitLabel);

        contentPane.add(controllerPanel, BorderLayout.NORTH);
    }

    private void exit() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }


}
