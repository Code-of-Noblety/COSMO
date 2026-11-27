package cosmo.ui;

import javafx.scene.Node;


public final class UiUtil {

    private UiUtil() { }

    /** Show or hide a node AND give back (or take) its space in the layout. */
    public static void show(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

    /** Adds the CSS class when on is true, removes it otherwise. */
    public static void toggleClass(Node node, String cssClass, boolean on) {
        node.getStyleClass().remove(cssClass);
        if (on) {
            node.getStyleClass().add(cssClass);
        }
    }

    /** Removes any CSS class starting with prefix, then adds newClass. */
    public static void replaceClass(Node node, String prefix, String newClass) {
        node.getStyleClass().removeIf(c -> c.startsWith(prefix));
        node.getStyleClass().add(newClass);
    }

    //  value CSS class name (the classes live in badges.css)

    public static String categoryClass(String category) {
        String name = slug(category);
        boolean known = name.equals("academic") || name.equals("personal") || name.equals("work");
        return known ? "pill-" + name : "pill-other";     // custom categories use the "other" color
    }

    public static String statusClass(String status)     { return "status-" + slug(status); }
    public static String priorityClass(String priority) { return "priority-" + slug(priority); }

    /** "In Progress" -> "in-progress". */
    private static String slug(String text) {
        return text.trim().toLowerCase().replace(' ', '-');
    }
}
