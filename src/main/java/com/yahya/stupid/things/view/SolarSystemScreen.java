package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.Planet;
import com.yahya.stupid.things.model.ScreenPanel;
import com.yahya.stupid.things.utils.PlanetInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SolarSystemScreen extends ScreenPanel {

    private final MainFrame mainFrame;
    private final int CENTER_Y;
    private final int CENTER_X;

    private ScheduledExecutorService service;

    private final int MIN_X, MIN_Y, MAX_X, MAX_Y;

    private final int SUN_RADIUS;
    private final int MERCURY_RADIUS = 20;

    ArrayList<Planet> planets;


    public SolarSystemScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        MIN_X  = 30;
        MIN_Y = 30;
        MAX_X = mainFrame.getWidth() - MIN_X;
        MAX_Y = mainFrame.getHeight() - MIN_Y - 10;

        CENTER_X = (MIN_X + MAX_X)/2;
        CENTER_Y = (MIN_Y + MAX_Y)/2;

        SUN_RADIUS = 40;

        setBackground(Color.decode("#000044"));
        planets = new ArrayList<>();
        for (Planet planet : PlanetInfo.PLANETS) {
            planets.add(planet.clone());
        }
//        mercuryDegree = new AtomicReference<>(0.0);
//        venusDegree = new AtomicReference<>(0.0);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(Color.WHITE);
        g2.drawRect(MIN_X, MIN_Y, MAX_X - MIN_X, MAX_Y - MIN_Y);

//        final int SHADOW_SIZE = 3;
//        g2.setColor(Color.decode("#666666"));
//        g2.fillOval(CENTER_X - SUN_RADIUS + SHADOW_SIZE, CENTER_Y - SUN_RADIUS - SHADOW_SIZE, SUN_RADIUS*2, SUN_RADIUS*2);
        g2.setColor(Color.decode("#FFCC66"));
        g2.fillOval(CENTER_X - SUN_RADIUS, CENTER_Y - SUN_RADIUS, SUN_RADIUS*2, SUN_RADIUS*2);

        for (Planet planet : planets) {
            g2.setColor(Color.WHITE);
            g2.drawOval((int) (CENTER_X - planet.getRealOrbitRadius()),
                    (int) (CENTER_Y - planet.getRealOrbitRadius()),
                    (int) (2*planet.getRealOrbitRadius()),
                    (int) (2* planet.getRealOrbitRadius()));
            g2.setColor(planet.getColor());

            g2.fillOval((int) (CENTER_X - planet.currentX() - planet.getRadius()),
                    (int) (CENTER_Y - planet.currentY() - planet.getRadius()),
                    2*planet.getRadius(),2*planet.getRadius());
        }
    }

    @Override
    public void start() {
        if (service != null) return;
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            for (Planet planet : planets) {
                planet.increaseDegree();
            }
            repaint();
        }, 0,10, TimeUnit.MILLISECONDS);
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

    }
}
