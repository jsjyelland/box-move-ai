package solution;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * An app window for displaying a visualisation.
 */
public class Window {
    /**
     * Construct a window with a visualisation
     * @param visualiser the visualisation to display
     */
    public Window(JComponent visualiser) {
        // Create a window frame
        JFrame frame = new JFrame("Tree Visualiser");
        frame.setSize(700, 766);
        frame.setLocation(300, 20);

        // Quit program on window close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.setVisible(true);

        // Create an inset panel in the window, and add the visualiser to this panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualiser, BorderLayout.CENTER);
        frame.setLayout(new BorderLayout());

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED))
        );

        frame.add(panel, BorderLayout.CENTER);
    }
}
