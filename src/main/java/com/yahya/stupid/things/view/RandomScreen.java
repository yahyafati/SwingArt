package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.Screen;
import com.yahya.stupid.things.model.ScreenPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RandomScreen extends ScreenPanel {


    private final int MIN_X;
    private final int MIN_Y;
    private final int MAX_X;
    private final int MAX_Y;
    private final MainFrame mainFrame;

    private final ArrayList<Point> points;
    private ScheduledExecutorService service;
    private final Random random;

    public RandomScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        MIN_X  = 30;
        MIN_Y = 30;
        MAX_X = mainFrame.getWidth() - MIN_X;
        MAX_Y = mainFrame.getHeight() - MIN_Y - 10;

        points = new ArrayList<>();
        random = new Random();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(Color.BLACK);
        g2.drawRect(MIN_X, MIN_Y, MAX_X - MIN_X, MAX_Y - MIN_Y);

        g2.setColor(Color.DARK_GRAY);
        for (int i = 1; i < points.size(); i++) {
            g2.drawLine(points.get(i-1).x, points.get(i-1).y, points.get(i).x, points.get(i).y);
        }

        g2.setColor(Color.MAGENTA);
        if (points.size() > 0) {
            int radius = 5;
            g2.fillOval(points.get(points.size() - 1).x - radius, points.get(points.size() - 1).y - radius, 2*radius, 2*radius);
        }

    }

    @Override
    public void start() {
        if (service != null && !service.isShutdown()) {
            return;
        }
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            points.add(new Point(random.nextInt(MAX_X-MIN_X)+MIN_X, random.nextInt(MAX_Y-MIN_Y)+MIN_Y));
            repaint();
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledExecutorService getService() {
        return service;
    }

    @Override
    public void setService(ScheduledExecutorService service) {
        this.service = service;
    }

    @Override
    public void clear() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
        points.clear();
        repaint();
    }
}
