package com.yahya.stupid.things.model;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class ImageFileFilter extends FileFilter implements FilenameFilter {

    private static ImageFileFilter imageFileFilter;

    private ImageFileFilter() {
        imageFileFilter = this;
    }

    public static ImageFileFilter getInstance() {
        if (imageFileFilter == null)
            imageFileFilter = new ImageFileFilter();
        return imageFileFilter;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        if (!f.isFile()) return false;
        return isAcceptableExtension(f.getName());
    }

    private boolean isAcceptableExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) return false;
        String[] acceptableExts = {"bmp", "gif", "jpeg", "jpg", "png", "webmp"};
        return Arrays.binarySearch(acceptableExts, fileName.substring(dotIndex+1)) >= 0;
    }

    @Override
    public String getDescription() {
        return "Image Files";
    }

    @Override
    public boolean accept(File dir, String name) {
        return isAcceptableExtension(name);
    }
}
