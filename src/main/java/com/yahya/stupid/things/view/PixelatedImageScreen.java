package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.ImageUtils;
import com.yahya.stupid.things.model.Screen;
import com.yahya.stupid.things.model.ScreenPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PixelatedImageScreen extends ScreenPanel {

    private final MainFrame mainFrame;
    private final JFileChooser fileChooser = new JFileChooser();

    private BufferedImage originalImage;
    private BufferedImage scaledImage;

    private final int MIN_X, MIN_Y, MAX_X, MAX_Y;
    private final AtomicInteger pixleSize;

    private ScheduledExecutorService service;
    private BufferedImage alteredImage;

    public PixelatedImageScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        MIN_X  = 30;
        MIN_Y = 30;
        MAX_X = mainFrame.getWidth() - MIN_X;
        MAX_Y = mainFrame.getHeight() - MIN_Y - 10;

        pixleSize = new AtomicInteger(200);

        Executors.newSingleThreadExecutor().execute(() -> {
            originalImage = ImageUtils.loadImage(fileChooser, mainFrame);
            scaledImage = ImageUtils.scaled(originalImage, MAX_X-MIN_X, MAX_Y-MIN_Y);
            alteredImage = pixelateImage(pixleSize.get());
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {

                    originalImage = ImageUtils.loadImage(fileChooser, mainFrame);
                    scaledImage = ImageUtils.scaled(originalImage, MAX_X-MIN_X, MAX_Y-MIN_Y);
                    pixleSize.set(Math.min(MAX_X-MIN_X, MAX_Y-MIN_Y));
                    alteredImage = pixelateImage(pixleSize.get());
                    repaint();
                }
            }
        });

        init();
    }

    private BufferedImage pixelateImage(int tileSize) {
        Raster src = scaledImage.getData();
        WritableRaster dest = src.createCompatibleWritableRaster();
        for(int y = 0; y < src.getHeight(); y += tileSize) {
            for(int x = 0; x < src.getWidth(); x += tileSize) {
                double[] pixel = new double[3];
                int midX = (x + Math.min(x+tileSize, src.getWidth()))/2;
                int midY = (y + Math.min(y+tileSize, src.getHeight()))/2;
                pixel = src.getPixel(midX, midY, pixel);
                for(int yd = y; (yd < y + tileSize) && (yd < dest.getHeight()); yd++) {
                    for(int xd = x; (xd < x + tileSize) && (xd < dest.getWidth()); xd++) {
                        dest.setPixel(xd, yd, pixel);
                    }
                }
            }
        }
        BufferedImage retImage = ImageUtils.deepCopy(scaledImage);
        retImage.setData(dest);
        return retImage;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

//        g2.drawImage(scaledImage, MIN_X, MIN_Y, (img, infoFlags, x, y, width, height) -> false);

        g2.drawImage(alteredImage, MIN_X, MIN_Y, (img, infoFlags, x, y, width, height) -> false);
    }

    private void init() {
        setBackground(Color.GREEN);
    }

    @Override
    public void start() {
        if (service != null && !service.isShutdown()) {
            return;
        }
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            alteredImage = pixelateImage(pixleSize.get());
            setBackground(new Color(alteredImage.getRGB(alteredImage.getWidth()/2, alteredImage.getHeight()/2)));
            repaint();
            if (pixleSize.get() == 1) {
                service.shutdown();
            }
            pixleSize.set(Math.max(pixleSize.get()-1, 1));
        }, 100, 10, TimeUnit.MILLISECONDS);
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
        if(service != null) {
            service.shutdown();;
            service = null;
        }
        pixleSize.set(200);
    }
}
