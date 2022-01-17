package com.yahya.stupid.things.view;

import com.yahya.stupid.things.Main;
import com.yahya.stupid.things.model.Form;
import com.yahya.stupid.things.model.Screen;
import com.yahya.stupid.things.model.ScreenPanel;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DVDScreen extends ScreenPanel {
    private final MainFrame mainFrame;
    private final Form form;
    private final int MIN_X, MIN_Y, MAX_X, MAX_Y;
    private final int BOX_WIDTH, BOX_HEIGHT;
    Point currentPosition;
    final AtomicInteger xChange = new AtomicInteger(1);
    final ArrayList<Point> myPoints = new ArrayList<>();

    JButton startButton;
    ScheduledExecutorService service;
    Color currentColor;
    private boolean beepOnCollision;

    public DVDScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        MIN_X  = 30;
        MIN_Y = 30;
        MAX_X = mainFrame.getWidth() - MIN_X;
        MAX_Y = mainFrame.getHeight() - MIN_Y - 10;
        setBackground(new Color(32, 42, 79));

        BOX_WIDTH = (int) (Math.random()*150);
        BOX_HEIGHT = BOX_WIDTH/2;
        System.out.printf("(BOX_WIDTH: %d, BOX_HEIGHT: %d)", BOX_WIDTH, BOX_HEIGHT);

//        BOX_WIDTH = 0;
//        BOX_HEIGHT = 0;

        System.out.println(new Rectangle(MIN_X, MIN_Y, MAX_X, MAX_Y));
        this.form = new Form(1, 0);

        currentPosition = new Point(MIN_X + BOX_WIDTH/2, (int) form.calculate(MIN_X + BOX_WIDTH/2.0));
        System.out.println(currentPosition);
        myPoints.add(new Point(currentPosition));
        currentColor = new Color((int) (Math.random() * Integer.MAX_VALUE));
        init();
    }

    private void loadBeep() {

        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem
                    .getAudioInputStream(Objects.requireNonNull(Main.class.getResourceAsStream("/sounds/beep.wav")));
            clip.open(inputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        setLayout(null);
        startButton = new JButton("Start");
        setSize(mainFrame.getSize());

        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setBounds(
                (getWidth() - 200) / 2,
                (getHeight() - 50) / 2,
                200, 50
        );
        startButton.addActionListener(e -> start());
        add(startButton);
    }

    public void pause() {
        if (service != null && !service.isShutdown()) {
            service.shutdown();
            service = null;
//            startButton.setVisible(true);
            mainFrame.validate();
            mainFrame.repaint();
        }
    }

    public void clear() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
        form.reset();
        currentPosition = new Point(MIN_X + BOX_WIDTH/2, (int) form.calculate(MIN_X + BOX_WIDTH/2.0));
//        currentPosition = new Point(MIN_X, (int) form.calculate(MIN_X));
        myPoints.clear();
        myPoints.add(new Point(currentPosition));
        currentColor = new Color((int) (Math.random() * Integer.MAX_VALUE));
        xChange.set(1);
        repaint();
    }

    private boolean isOutOfBound(Point position) {
        return isOutOfBoundHeight(position) || isOutOfBoundWidth(position);
    }

    private boolean isOutOfBoundHeight(Point position) {
        return position.y + BOX_HEIGHT/2 > MAX_Y || position.y-BOX_HEIGHT/2 < MIN_Y;
    }

    private boolean isOutOfBoundWidth(Point position) {
        return position.x + BOX_WIDTH/2 > MAX_X || position.x-BOX_WIDTH/2 < MIN_X;
    }

    public void start() {
        mainFrame.setResizable(false);
        if (service != null && !service.isShutdown()) {
            return;
        }

        startButton.setVisible(false);
        service = Executors.newSingleThreadScheduledExecutor();

        service.scheduleAtFixedRate(() -> {
            currentPosition.setLocation(
                    currentPosition.x + xChange.get(),
                    form.calculate(currentPosition.x + xChange.get())
            );
            if (isOutOfBound(currentPosition)) {
                if (beepOnCollision)
                    loadBeep();
                Point originalPosition = new Point(currentPosition);
                currentPosition.setLocation(
                        Math.min(Math.max(currentPosition.x, MIN_X), MAX_X),
                        Math.min(Math.max(currentPosition.y, MIN_Y), MAX_Y)
                );

                myPoints.add(new Point(currentPosition));
                currentColor = new Color((int) (Math.random() * Integer.MAX_VALUE));
                if (isCorner(currentPosition)) {
                    form.rotate90(currentPosition);
                }
                form.rotate90(currentPosition);

                if (isOutOfBoundWidth(originalPosition)) {
                    xChange.set(-1 * xChange.get());
                }

            }
            repaint();
        }, 250, 500, TimeUnit.MICROSECONDS);
    }

    @Override
    public ScheduledExecutorService getService() {
        return service;
    }

    @Override
    public void setService(ScheduledExecutorService service) {
        this.service = service;
    }

    private boolean isCorner(Point position) {
        if (position.x == MIN_X && position.y == MIN_Y) {
            return true;
        }
        if (position.x == MIN_X && position.y == MAX_Y) {
            return true;
        }
        if (position.x == MAX_X && position.y == MIN_Y) {
            return true;
        }
        return position.x == MAX_X && position.y == MAX_Y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        g2.drawRect(MIN_X, MIN_Y, MAX_X - MIN_X, MAX_Y - MIN_Y);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        final int x =  currentPosition.x;
        final int y =  currentPosition.y;

        g2.setColor(currentColor);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < myPoints.size() - 1; i++) {
            g2.drawLine( myPoints.get(i).x,  myPoints.get(i).y,  myPoints.get(i + 1).x,  myPoints.get(i + 1).y);
        }

        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(2));
        Point lastMyPoint = myPoints.get(myPoints.size() - 1);
        g2.drawLine( lastMyPoint.x,  lastMyPoint.y,  currentPosition.x,  currentPosition.y);

        g2.setColor(Color.BLUE);
//        int radius = 5;
//        g2.fillOval(x-radius, y-radius, radius*2, radius*2);
        g2.drawRect(x-BOX_WIDTH/2, y - BOX_HEIGHT/2, BOX_WIDTH, BOX_HEIGHT);
    }

    public void setBeepOnCollision(boolean beepOnCollision) {
        this.beepOnCollision = beepOnCollision;
    }

    public boolean getBeepOnCollision() {
        return beepOnCollision;
    }
}
