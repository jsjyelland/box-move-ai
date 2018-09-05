package solution;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * An app window for displaying a visualisation.
 */
public class Window {
    public Window(Visualiser visualiser) {
        JFrame frame = new JFrame("Tree Visualiser");
        frame.setSize(700, 766);
        frame.setLocation(300, 20);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualiser, BorderLayout.CENTER);
        frame.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        frame.add(panel, BorderLayout.CENTER);
    }
}
