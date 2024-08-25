package org.wip.moneymanager.model.types;

public class Color {
    private int red;
    private int green;
    private int blue;
    private int alpha = 255;

    // Ecco a voi costruttori in 30 modi diversi
    public Color() {
        this(0, 0, 0, 255);
    }

    public Color(int[] rgb) {
        if (rgb.length == 3) {
            setRGB(rgb[0], rgb[1], rgb[2]);
        } else if (rgb.length == 4) {
            setRGBA(rgb[0], rgb[1], rgb[2], rgb[3]);
        }
    }

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(String hex) {
        setFromHex(hex);
    }

    public void setRGB(int[] rgb) {
        setRGB(rgb[0], rgb[1], rgb[2]);
    }

    public void setRGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void setRGBA(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color setFromHex(String hex) {
        if (hex.length() == 7) {
            red = Integer.valueOf(hex.substring(1, 3), 16);
            green = Integer.valueOf(hex.substring(3, 5), 16);
            blue = Integer.valueOf(hex.substring(5, 7), 16);
        } else if (hex.length() == 9) {
            alpha = Integer.valueOf(hex.substring(1, 3), 16);
            red = Integer.valueOf(hex.substring(3, 5), 16);
            green = Integer.valueOf(hex.substring(5, 7), 16);
            blue = Integer.valueOf(hex.substring(7, 9), 16);
        }
        return this;
    }

    public int getRGBInt() {
        return (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
    }

    public int getARGBInt() {
        return (alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
    }

    public int[] getRGB() {
        return new int[] {red, green, blue};
    }

    public int[] getRGBA() {
        return new int[] {red, green, blue, alpha};
    }

    public String getHex() {
        int color = (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public String getAHex() {
        int color = (alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
