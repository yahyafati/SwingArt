package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.Screen;
import com.yahya.stupid.things.model.ScreenPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MosaicScreen extends ScreenPanel {

    private final MainFrame mainFrame;

    private final int TILE_SIZE = 20;
    private ScheduledExecutorService service;
    private final AtomicInteger currentColumn;
    private final AtomicInteger currentRow;
    private final Color[][] tileColors;

    private final int MIN_X, MIN_Y, MAX_X, MAX_Y;
    private final int COL_SIZE, ROW_SIZE;

    public MosaicScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        MIN_X  = 30;
        MIN_Y = 30;
        MAX_X = mainFrame.getWidth() - MIN_X;
        MAX_Y = mainFrame.getHeight() - MIN_Y - 10;

        COL_SIZE = (MAX_X-MIN_X) / TILE_SIZE;
        ROW_SIZE = (MAX_Y-MIN_Y) / TILE_SIZE;
        tileColors = new Color[ROW_SIZE][COL_SIZE];
        for (Color[] colors : tileColors) {
            Arrays.fill(colors, Color.BLUE);
        }

        currentColumn = new AtomicInteger(0);
        currentRow = new AtomicInteger(0);
        init();
    }

    private void init() {
        setBackground(Color.BLACK);
    }

    private Color getRandomColor() {
        return new Color((int) (Math.random()*Integer.MAX_VALUE));
    }
    @Override
    public void start() {
        if (service != null && !service.isShutdown()) {
            return;
        }
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
//            for (int i = 0; i < ROW_SIZE; i++) {
//                tileColors[currentRow.get()][currentColumn.get()] = getRandomColor();
//                repaint();
//            }
//            repaint(MIN_X, MIN_Y+TILE_SIZE*currentColumn.get(), TILE_SIZE, COL_SIZE*TILE_SIZE);
//            repaint(MIN_X+TILE_SIZE*currentRow.get(), MIN_Y+TILE_SIZE*currentColumn.get(), TILE_SIZE, TILE_SIZE);
            if (currentColumn.get() >= COL_SIZE) {
                int[] pt = {(int) (Math.random()*ROW_SIZE), (int) (Math.random()*COL_SIZE)};
                tileColors[pt[0]][pt[1]] = getRandomColor();
//                repaint(MIN_X+pt[0]*TILE_SIZE, MIN_Y+pt[1]*TILE_SIZE,TILE_SIZE, TILE_SIZE);
                repaint();
                return;
            }
            tileColors[currentRow.get()][currentColumn.get()] = getRandomColor();
            repaint();
            if (currentRow.incrementAndGet() >= ROW_SIZE) {
                currentColumn.set((currentColumn.get() + 1));
                currentRow.set(0);
            }
        }, 100, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(Color.WHITE);
        g2.drawRect(MIN_X, MIN_Y, MAX_X - MIN_X, MAX_Y - MIN_Y);

        for (int i = 0; i < currentColumn.get(); i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                if (i == currentColumn.get()-1 && j >= currentRow.get()) {
                    break;
                }
                g2.setColor(tileColors[j][i]);
                g2.fillRect(MIN_X+TILE_SIZE*i, MIN_Y+TILE_SIZE*j, TILE_SIZE, TILE_SIZE);
            }
        }
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

        for (Color[] colors : tileColors) {
            Arrays.fill(colors, Color.BLUE);
        }
        currentColumn.set(0);
        repaint();
    }
}
