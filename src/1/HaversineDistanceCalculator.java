import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HaversineDistanceCalculator extends JFrame {

    private JTextField lat1Field, lon1Field, lat2Field, lon2Field, radiusField;
    private JLabel resultLabel;
    private JButton solveButton, clearButton;

    public HaversineDistanceCalculator() {
        setTitle("Distance Calculation (Haversine Formula)");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));

        add(new JLabel("  Latitude 1 (lat1, deg):"));
        lat1Field = new JTextField();
        add(lat1Field);

        add(new JLabel("  Longitude 1 (lon1, deg):"));
        lon1Field = new JTextField();
        add(lon1Field);

        add(new JLabel("  Latitude 2 (lat2, deg):"));
        lat2Field = new JTextField();
        add(lat2Field);

        add(new JLabel("  Longitude 2 (lon2, deg):"));
        lon2Field = new JTextField();
        add(lon2Field);

        add(new JLabel("  Earth Radius R (m):"));
        radiusField = new JTextField("6371000");
        add(radiusField);

        solveButton = new JButton("Solve");
        clearButton = new JButton("Clear");
        add(solveButton);
        add(clearButton);

        add(new JLabel("  Distance D (meters):"));
        resultLabel = new JLabel("---");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setForeground(Color.BLUE);
        add(resultLabel);

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateDistance();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
    }

    private void calculateDistance() {
        try {
            double lat1 = Double.parseDouble(lat1Field.getText());
            double lon1 = Double.parseDouble(lon1Field.getText());
            double lat2 = Double.parseDouble(lat2Field.getText());
            double lon2 = Double.parseDouble(lon2Field.getText());
            double R = Double.parseDouble(radiusField.getText());

            double phi1 = lat1 * Math.PI / 180.0;
            double phi2 = lat2 * Math.PI / 180.0;
            double deltaPhi = (lat2 - lat1) * Math.PI / 180.0;
            double deltaLambda = (lon2 - lon1) * Math.PI / 180.0;

            double sinSqDeltaPhi = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2);
            double sinSqDeltaLambda = Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
            
            double a = sinSqDeltaPhi + Math.cos(phi1) * Math.cos(phi2) * sinSqDeltaLambda;
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double D = R * c;

            resultLabel.setText(String.format("%.2f m", D));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric values.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        lat1Field.setText("");
        lon1Field.setText("");
        lat2Field.setText("");
        lon2Field.setText("");
        radiusField.setText("6371000");
        resultLabel.setText("---");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HaversineDistanceCalculator().setVisible(true);
            }
        });
    }
}