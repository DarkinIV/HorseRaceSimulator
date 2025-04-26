import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UITheme {
    // Color scheme
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Lighter blue
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);     // Red
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light gray
    public static final Color CELL_BACKGROUND = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);      // Dark blue
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141); // Gray

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Borders
    public static final Border CELL_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    );
    
    public static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(15, 15, 15, 15);

    // Component styling methods
    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    public static void styleTitle(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT_PRIMARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public static void styleHeader(JLabel label) {
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_PRIMARY);
    }

    public static void styleLabel(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_PRIMARY);
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(PANEL_BORDER);
    }

    public static void styleCell(JPanel cell) {
        cell.setBackground(CELL_BACKGROUND);
        cell.setBorder(CELL_BORDER);
    }

    public static void styleDialog(JDialog dialog) {
        dialog.getRootPane().setBackground(BACKGROUND_COLOR);
        ((JPanel)dialog.getContentPane()).setBackground(BACKGROUND_COLOR);
        dialog.getRootPane().setBorder(PANEL_BORDER);
    }
}