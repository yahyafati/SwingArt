package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.Screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PixelatedImageScreen extends JPanel implements Screen {

    private final MainFrame mainFrame;

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

        originalImage = loadImage();
        scaledImage = scaled(originalImage, MAX_X-MIN_X, MAX_Y-MIN_Y);
        alteredImage = pixelateImage(pixleSize.get());

        init();
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private static BufferedImage scaled(BufferedImage original, int newWidth, int newHeight) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, original.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, 0, 0, original.getWidth(),
                original.getHeight(), null);
        g.dispose();
        return resized;
    }

    private static BufferedImage loadImage() {
        try {
            return ImageIO.read(Objects.requireNonNull(PixelatedImageScreen.class.getResourceAsStream("/images/img.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getAverageColor(BufferedImage img, int startX, int startY, int tileSize) {
        double rgb = 0;
        int count = 0;
        int lastX = Math.min(startX+tileSize, img.getWidth());
        int lastY = Math.min(startY+tileSize, img.getHeight());
        for (int i = startX; i < lastX; i++) {
            for (int j = startY; j < lastY; j++) {
                rgb += img.getRGB(i, j);
                count++;
            }
        }
        return (int) (rgb/count);
    }

    private void setColors(BufferedImage img, int startX, int startY, int tileSize, int rgb) {
        int lastX = Math.min(startX+tileSize, img.getWidth());
        int lastY = Math.min(startY+tileSize, img.getHeight());

        for (int i = startX; i < lastX; i++) {
            for (int j = startY; j < lastY; j++) {
                img.setRGB(i, j, rgb);
            }
        }
    }

    private BufferedImage pixelateImage(int tileSize) {
        BufferedImage image = deepCopy(scaledImage);
        int imgWidth = image.getWidth(), imgHeight = image.getHeight();
        for (int i = 0; i < imgWidth; i+=tileSize) {
            for (int j = 0; j < imgHeight; j+=tileSize) {
                int averageColorRGB = getAverageColor(image, i, j, tileSize);
                setColors(image, i, j, tileSize, averageColorRGB);
            }
        }
        return image;
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
            repaint();
            if (pixleSize.get() == 1) {
                service.shutdown();
            }
            pixleSize.set(Math.max(pixleSize.get()-1, 1));
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
    public void clear() {
        if(service != null) {
            service.shutdown();;
            service = null;
        }
        pixleSize.set(200);
    }
}
