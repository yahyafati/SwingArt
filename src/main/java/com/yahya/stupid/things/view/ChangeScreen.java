package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.ScreenType;

import javax.swing.*;
import java.awt.*;

public class ChangeScreen extends JDialog {

    private final MainFrame mainFrame;

    public ChangeScreen(JFrame owner, MainFrame mainFrame) {
        super(owner);
        this.mainFrame = mainFrame;
        setModal(true);
        init();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Change current Screen");
        setLocationRelativeTo(owner);
    }

    private void init() {
        JPanel contentPane = new JPanel();
//        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setLayout(new GridLayout(-1,1, 5, 5));
        for (ScreenType type : ScreenType.values()) {
            JButton btn = new JButton(type.name());
            btn.addActionListener(e -> {
                mainFrame.setScreen(mainFrame.getScreen(type));
                dispose();
            });
            contentPane.add(btn);
        }

        JButton cancelDialog = new JButton("Cancel");
        cancelDialog.addActionListener(e -> dispose());
        contentPane.add(cancelDialog);
        setContentPane(contentPane);
        this.pack();
    }
}
