package com.yahya.stupid.things;

import com.yahya.stupid.things.view.MainFrame;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
//        System.getProperties().forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
