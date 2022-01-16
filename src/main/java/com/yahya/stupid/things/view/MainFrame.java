package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.ScreenPanel;
import com.yahya.stupid.things.model.ScreenType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class MainFrame extends JFrame {

    private static final ScreenType DEFAULT_SCREEN = ScreenType.DVDScreen;

    private ScreenPanel screen;
    private final JPanel contentPane;

    public MainFrame() {
        contentPane = new JPanel(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setUndecorated(true);
        init();
    }

    private void init() {
        setScreen(getScreen(ScreenType.DVDScreen));
        initToolbar(contentPane);
        setContentPane(contentPane);
    }

    public ScreenPanel getScreen(ScreenType type) {
        switch (type) {
            case CircleScreen: return new CircleScreen(this);
            case DarkenScreen: return new DarkenScreen(this);
            case ManualScreen: return new ManualScreen(this);
            case MosaicScreen: return new MosaicScreen(this);
            case PixelatedImageScreen: return new PixelatedImageScreen(this);
            case TriangleScreen: return new TriangleScreen(this);
            case RandomScreen: return new RandomScreen(this);
            case DVDScreen:
            default: return new DVDScreen(this);
        }
    }

    private void initToolbar(JPanel contentPane) {
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


        JLabel changeScreeLabel = new JLabel("Change Screen");
        changeScreeLabel.setForeground(Color.decode("#008800"));
        changeScreeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeScreeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openChangeScreenDialog();
            }
        });
        controllerPanel.add(changeScreeLabel);

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

    private void openChangeScreenDialog() {
        new ChangeScreen(this, this).setVisible(true);
    }

    private void exit() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }


    public void setScreen(ScreenPanel screen) {
        if (this.screen != null) {
            this.screen.clear();
            contentPane.remove(this.screen);
        }

        this.screen = screen;
        contentPane.add(screen, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
