package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CircleScreen extends JPanel implements Screen {


    private final MainFrame mainFrame;

    static class Radius {
        int radius;
        float stroke;
        Color color;

        public Radius(int radius, Color color) {
            this.color = color;
            this.radius = radius;
        }
    }

    private final String[] sentence = {"Hello", "Why", "Are", "You", "Still", "Reading", "This", "?", "Dumbass"};
    private final AtomicInteger circleCount = new AtomicInteger(0);
    private final ArrayList<Radius> radiuses = new ArrayList<>();
    private ScheduledExecutorService service;


//-----------------------------------------
//
//    Constructors:
//
//-----------------------------------------

    public CircleScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        radiuses.add(new Radius(20, Color.GREEN));
        circleCount.set((circleCount.get()+1)%sentence.length);
        init();
    }

    private void init() {
        setBackground(Color.BLACK);
        setSize(mainFrame.getSize());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = getWidth()/2, y = getHeight()/2;
        Graphics2D g2 = (Graphics2D)g;
        g2.setFont(g2.getFont().deriveFont(40f).deriveFont(Font.BOLD));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(Color.WHITE);

        Radius finalRad = null;
//        		g2.setColor(Color.WHITE);
        for (Radius radColor : (ArrayList<Radius>)radiuses.clone()) {
            finalRad = radColor;
            int radius = radColor.radius;
//        			g2.setStroke(new BasicStroke(radColor.stroke));
            g2.setStroke(new BasicStroke(30f));
            g2.setColor(radColor.color);
            g2.drawOval(x-radius, y-radius, radius*2, radius*2);
        }

//        		Color textColor = new Color(255 - finalRad.color.getRed(), 255-finalRad.color.getGreen(), 255-finalRad.color.getBlue());
//        		g2.setColor(textColor);
        g2.setColor(Color.WHITE);

        if(finalRad != null) {
            if (circleCount.get() == sentence.length -1) {
                g2.setFont(g2.getFont().deriveFont(80f).deriveFont(Font.BOLD));
            }
            FontMetrics fm = g2.getFontMetrics(g2.getFont());
            int width = fm.stringWidth(sentence[circleCount.get()]);
            int height = fm.getAscent();
            int startX = x - width/2;
            int startY = y - height/2;
            g2.drawString(sentence[circleCount.get()], startX, startY);
        }

    }

    @Override
    public void start() {
        if (service != null && !service.isShutdown()) {
            return;
        }
        service = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger counter = new AtomicInteger(0);
        service.scheduleAtFixedRate(() -> {
            counter.set(counter.get() +1);
            if (counter.get() >= 10) {
                radiuses.add(new Radius(20, new Color((int) (Math.random() * Integer.MAX_VALUE))));
                circleCount.set((circleCount.get()+1)%sentence.length);
                counter.set(0);
            }
            radiuses.removeIf(radius -> radius.radius > getWidth()/2);
            for (Radius radius : radiuses) {
                radius.radius += 10;
                radius.stroke = 1f;
            }
            repaint();
        }, 100, 50, TimeUnit.MILLISECONDS);
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
    public void pause() {
        if (service == null) return;
        service.shutdown();;
        service = null;
    }

    @Override
    public void clear() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
        radiuses.clear();
        circleCount.set(0);
        repaint();
    }
}
