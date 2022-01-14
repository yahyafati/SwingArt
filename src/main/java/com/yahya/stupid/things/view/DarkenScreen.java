package com.yahya.stupid.things.view;

import com.yahya.stupid.things.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DarkenScreen extends ScreenPanel {

    private final MainFrame mainFrame;
    private final FileDialog fileDialog;

    private final int MIN_X, MIN_Y, MAX_X, MAX_Y;
    private final Form form;

    private ScheduledExecutorService service;
    private BufferedImage originalImage;
    private BufferedImage scaledImage;

    private final Point currentPosition;
    private final AtomicInteger width;
    private final AtomicInteger height;
    private final AtomicInteger avgBrightness;
    private final AtomicInteger xChange = new AtomicInteger(1);

    public DarkenScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
//        fileChooser  = new JFileChooser(Paths.get(System.getProperty("user.home"), "Pictures").toString());
        fileDialog = new java.awt.FileDialog(mainFrame, mainFrame.getTitle(), FileDialog.LOAD);
        fileDialog.setDirectory(Paths.get(System.getProperty("user.home"), "Pictures").toString());
        fileDialog.setFilenameFilter(ImageFileFilter.getInstance());
        MIN_X  = 30;
        MIN_Y = 30;
        MAX_X = mainFrame.getWidth() - MIN_X;
        MAX_Y = mainFrame.getHeight() - MIN_Y - 10;

        currentPosition = new Point(0, 0);
        width = new AtomicInteger(200);
        height = new AtomicInteger(150);
        avgBrightness = new AtomicInteger(0);
        this.form = new Form(1, 0);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {

                originalImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/img.png")));
                scaledImage = ImageUtils.scaled(originalImage, MAX_X-MIN_X, MAX_Y-MIN_Y);
                avgBrightness.set(ImageUtils.getAverageBrightness(originalImage));
                System.out.println(avgBrightness.get()/255.0);
                repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    originalImage = ImageUtils.loadImage(fileDialog);
                    if (originalImage == null) {
                        return;
                    }
                    scaledImage = ImageUtils.scaled(originalImage, MAX_X-MIN_X, MAX_Y-MIN_Y);
                    avgBrightness.set(ImageUtils.getAverageBrightness(originalImage));
                    System.out.println(avgBrightness.get()/255.0);
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.drawRect(MIN_X, MIN_Y, MAX_X-MIN_X, MAX_Y-MIN_Y);
        if (originalImage == null) {
            return;
        }

//        g2.drawImage(
//                scaledImage,
//                MIN_X,
//                MIN_Y,
//                (img, infoFlags, x, y, width, height) -> false
//        );

        g2.drawImage(
                ImageUtils.randomizeColorArea(
                        scaledImage,
                        currentPosition.x,
                        currentPosition.y,
                        currentPosition.x + width.get(),
                        currentPosition.y + height.get(),
                        avgBrightness.get()/255.0
                ),
                MIN_X, MIN_Y, (img, infoFlags, x, y, width, height) -> false
        );

        g2.setColor(Color.MAGENTA);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(currentPosition.x + MIN_X, currentPosition.y +MIN_Y, width.get(), height.get());

//        g2.setStroke(new BasicStroke(2f));
//        g2.setColor(Color.CYAN);
//        g2.drawRect(
//                MIN_X + currentPosition.x,
//                MIN_Y + currentPosition.y,
//                width.get(),
//                height.get()
//        );

    }

    private boolean isOutOfBound(Point position) {
        return isOutOfBoundHeight(position) || isOutOfBoundWidth(position);
    }

    private boolean isOutOfBoundHeight(Point position) {
        return position.y <= 0 || position.y + height.get() + MIN_Y > MAX_Y;
    }

    private boolean isOutOfBoundWidth(Point position) {
        return position.x <= 0 || position.x + width.get() + MIN_X > MAX_X;
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
    public void start() {
        if (service != null && !service.isShutdown()) {
            return;
        }
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
//            System.out.println(currentPosition);

//            int startX = currentPosition.x, startY = currentPosition.y;
//            int endX = startX + sectionSize.get(), endY = startY + sectionSize.get();

            currentPosition.setLocation(
                    currentPosition.x + xChange.get(),
                    form.calculate(currentPosition.x + xChange.get())
            );
            if (isOutOfBound(currentPosition)) {
                Point originalPosition = new Point(currentPosition);
                currentPosition.setLocation(
                        Math.min(Math.max(currentPosition.x, 0), MAX_X),
                        Math.min(Math.max(currentPosition.y, 0), MAX_Y)
                );

//                currentColor = new Color((int) (Math.random() * Integer.MAX_VALUE));
                if (isCorner(currentPosition)) {
                    form.rotate90(currentPosition);
                }
                form.rotate90(currentPosition);

                if (isOutOfBoundWidth(originalPosition)) {
                    xChange.set(-1 * xChange.get());
                }

            }
            repaint();
        }, 10, 10, TimeUnit.MILLISECONDS);
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
            service.shutdown();;
            service = null;
        }
    }
}
