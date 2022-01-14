package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.ScreenPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TriangleScreen extends ScreenPanel {

    private final MainFrame mainFrame;
    private final int MIN_X, MAX_X, MIN_Y, MAX_Y;
    private final AtomicInteger sideLength;
    private final AtomicInteger counter;
    private final AtomicReference<Double> angle;

    private final ArrayList<Point> points;
    private Point currentPoint;
    ScheduledExecutorService service;

    public TriangleScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        MIN_X = MIN_Y = 20;
        MAX_X = mainFrame.getWidth() - MIN_X;
        MAX_Y = mainFrame.getHeight() - MIN_Y - 20;
        sideLength = new AtomicInteger(200);
        counter = new AtomicInteger(0);
        angle = new AtomicReference<>(-36.0);
        points = new ArrayList<>();
        Point startPoint = new Point((MIN_X + MAX_X - sideLength.get())/2 , (MIN_Y+MAX_Y + sideLength.get())/2);
//        Point startPoint = new Point(MAX_X - 100, (MIN_Y+MAX_Y + sideLength.get())/2);

        points.add(startPoint);
        currentPoint = startPoint;



        setBackground(Color.BLACK);
        init();

    }

    private Double getNextAngle(Double currentAngle) {
        return (currentAngle + 180.0 + 36.0)%360;
    }

    private Point getNextPoint(Point prevPoint) {
        return new Point(
                (int)(prevPoint.x + sideLength.get() * Math.cos(Math.toRadians(angle.get()))),
                (int)(prevPoint.y + sideLength.get() * Math.sin(Math.toRadians(angle.get())))
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRect(MIN_X, MIN_Y, MAX_X-MIN_X, MAX_Y-MIN_Y);

        g2.setStroke(new BasicStroke(1f));
        for (int i = 1; i < points.size(); i++) {
            g2.drawLine(points.get(i-1).x, points.get(i-1).y, points.get(i).x, points.get(i).y);
        }

        Point curPoint = points.get(points.size()-1);
        g2.setColor(Color.RED);
        g2.fillOval(curPoint.x, curPoint.y, 5,5);



    }

    private void init() {
        setSize(mainFrame.getSize());
    }

    @Override
    public void start() {
        mainFrame.setResizable(false);
        if (service != null && !service.isShutdown()) {
            return;
        }
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            currentPoint = getNextPoint(currentPoint);
            points.add(currentPoint);
            System.out.println(angle.get());
            angle.set(getNextAngle(angle.get()));
            counter.set((counter.get()+1)%5);
            if (counter.get() == 4) {
                sideLength.set(sideLength.get()+50);
            }
            repaint();
        }, 0, 200, TimeUnit.MILLISECONDS);

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
        sideLength.set(200);
        counter.set(0);
        angle.set(-36.0);
        points.clear();
        currentPoint = new Point((MIN_X + MAX_X - sideLength.get())/2 , (MIN_Y+MAX_Y + sideLength.get())/2);
    }
}
