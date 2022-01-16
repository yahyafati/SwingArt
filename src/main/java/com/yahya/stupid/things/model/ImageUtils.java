package com.yahya.stupid.things.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageUtils {

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static BufferedImage scaled(BufferedImage original, int newWidth, int newHeight) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, original.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, 0, 0, original.getWidth(),
                original.getHeight(), null);
        g.dispose();
        return resized;
    }

    public static BufferedImage loadImage(FileDialog fileDialog) {
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            try {
                return ImageIO.read(Files.newInputStream(Path.of(fileDialog.getDirectory(), fileDialog.getFile())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static BufferedImage loadImage(JFileChooser fileChooser, Component mainFrame) {
        fileChooser.setFileFilter(ImageFileFilter.getInstance());
        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            try {
                return ImageIO.read(fileChooser.getSelectedFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static double getBrightness(Color color) {
//        (0.2126*color.getRed() + 0.7152*color.getGreen() + 0.0722*color.getBlue())
        return Math.sqrt(
                0.299*Math.pow(color.getRed(),2) +
                        0.587*Math.pow(color.getGreen(),2) +
                        0.114*Math.pow(color.getBlue(),2)
        );
    }

    public static double getBrightness(double[] pixel) {
//        (0.2126*color.getRed() + 0.7152*color.getGreen() + 0.0722*color.getBlue())
        return Math.sqrt(
                0.299*Math.pow(pixel[0],2) +
                        0.587*Math.pow(pixel[1],2) +
                        0.114*Math.pow(pixel[2],2)
        );
    }

    public static double getBrightness(int[] pixel) {
//        (0.2126*color.getRed() + 0.7152*color.getGreen() + 0.0722*color.getBlue())
        return Math.sqrt(
                0.299*Math.pow(pixel[0],2) +
                        0.587*Math.pow(pixel[1],2) +
                        0.114*Math.pow(pixel[2],2)
        );
    }

    private static boolean inRectBound(int x, int y, int startX, int startY, int endX, int endY) {
        return x > startX && x < endX && y > startY && y < endY;
    }

    private static  int getRGB(int r, int g, int b) {
        return ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
    }
    public static BufferedImage grayScaleExceptArea(BufferedImage image, int startX, int startY, int endX, int endY) {
        image = deepCopy(image);
        Raster raster = image.getData();
        startX = Math.min(startX, image.getWidth()-1);
        startY = Math.min(startY, image.getHeight()-1);
        endX = Math.min(endX, image.getWidth());
        endY = Math.min(endY, image.getHeight());
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                double[] pixel = raster.getPixel(x,y, new double[3]);
                double brightness =getBrightness(pixel);
                double bRate = brightness/255.0;
                if (bRate > .4) {
//                    image.setRGB(x, y, getRGB((int) (brightness), (int) (brightness), (int) (brightness)));
                    image.setRGB(x, y, (int) (Math.random()*Integer.MAX_VALUE));
                }
                else {
//                    image.setRGB(x, y, 0);

                }
            }
        }
        return image;
    }

    public static BufferedImage randomizeColorArea(BufferedImage image, int startX, int startY, int endX, int endY, double avgBrightness) {
        image = deepCopy(image);
        Raster raster = image.getData();
        startX = Math.min(startX, image.getWidth()-1);
        startY = Math.min(startY, image.getHeight()-1);
        endX = Math.min(endX, image.getWidth());
        endY = Math.min(endY, image.getHeight());
        final int TILE_SIZE = 10;
        for (int x = startX; x < endX; x+=TILE_SIZE) {
            for (int y = startY; y < endY; y+=TILE_SIZE) {
                int[] pixel = raster.getPixel(x, y, new int[3]);
                double brightness = getBrightness(pixel);
                double bRate = brightness / 255.0;
//                if ((avgBrightness > 0.5 && bRate > avgBrightness) || (avgBrightness < 0.5 && bRate < avgBrightness)) {
////                    image.setRGB(x, y, getRGB((int) (brightness), (int) (brightness), (int) (brightness)));
////                    image.setRGB(x, y, (int) (Math.random()*Integer.MAX_VALUE));
////                    image.setRGB(x,y, Color.white.getRGB());
//                    image.setRGB(x, y, );
//                }
                int width = Math.min(TILE_SIZE, endX - x - 1);
                int height = Math.min(TILE_SIZE, endY - y - 1);
                for (int xx = x; xx < x+width; xx++) {
                    for (int yy = y; yy < y+height; yy++) {
                        image.setRGB(xx, yy, image.getRGB(x,y));
                    }
                }
            }
        }
        return image;
    }

    public static BufferedImage grayScaleArea(BufferedImage image, int startX, int startY, int endX, int endY) {
        startX = Math.min(startX, image.getWidth()-1);
        startY = Math.min(startY, image.getHeight()-1);
        endX = Math.min(endX, image.getWidth());
        endY = Math.min(endY, image.getHeight());
        image = deepCopy(image);
//        System.out.println(image.getRaster().getBounds());
        int w = endX - startX;
        int h = endY - startY;
        try {
            image = image.getSubimage(startX, startY, w, h);
        } catch (RasterFormatException e) {
            System.err.println(e.getMessage());
            System.out.printf("Size: (%d, %d)\n", image.getWidth(), image.getHeight());
            System.out.printf("(%d, %d) -> (%d, %d)%n", startX, startY, endX, endY);
            System.out.println();
        }
        Raster src = image.getData();
//        System.out.println(image.getRaster().getBounds());
        WritableRaster dest = src.createCompatibleWritableRaster();
//        System.out.println(dest.getBounds());
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double[] pixel = new double[3];
                pixel = src.getPixel(x, y, pixel);
                double brightness =getBrightness(pixel);
                double bRate = brightness/255.0;
                dest.setPixel(x, y, new double[]{255*bRate,255*bRate,255*bRate});
            }
        }
        BufferedImage retImage = image;
        retImage.setData(dest);
        return retImage;
    }

    public static BufferedImage grayScale(BufferedImage image) {
        Raster src = image.getData();
        WritableRaster dest = src.createCompatibleWritableRaster();
        for(int y = 0; y < src.getHeight(); y++) {
            for(int x = 0; x < src.getWidth(); x++) {
                double[] pixel = new double[3];
                pixel = src.getPixel(x, y, pixel);
                double brightness =getBrightness(pixel);
                double bRate = brightness/255.0;
//                System.out.println("Brightness: " + brightness);
//                System.out.println("bRate: " + bRate);
                dest.setPixel(x, y, new double[]{255*bRate,255*bRate,255*bRate});
//                if (bRate > 0.5) {
////                    dest.setPixel(x, y, new double[]{bRate*pixel[0], bRate*pixel[1], bRate*pixel[2]});
//                    dest.setPixel(x, y, new double[]{255*bRate,255*bRate,255*bRate});
//                } else {
//                    dest.setPixel(x, y, new double[]{0.1,0.2,0.2});
//                }
            }
        }
        BufferedImage retImage = ImageUtils.deepCopy(image);
        retImage.setData(dest);
        return retImage;
    }

    public static int getAverageBrightness(BufferedImage originalImage) {
        double avg = 0;
        int count = 0;
        for (int i = 0; i < originalImage.getWidth(); i+=10) {
            for (int j = 0; j < originalImage.getHeight(); j+=10, count++) {
                avg += getBrightness(originalImage.getRGB(i, j));
            }
        }
//        return (int) getBrightness(new Color((int) (avg/count)));
        return (int) (avg/count);
    }

    private static double getBrightness(int rgb) {
        return getBrightness(new Color(rgb));
    }
}
