package style;

public class Style {
    public static String[] gras = { "\u001B[1m", "\u001B[0m" };
    public static String cyan = "\u001B[36m";
    public static String reset = "\u001B[0m";
    public static String green = "\u001B[32m";

    public static String[] getGras() {
        return gras;
    }

    public static String getCyan() {
        return cyan;
    }

    public static String getReset() {
        return reset;
    }

    public static String getGreen() {
        return green;
    }

}
