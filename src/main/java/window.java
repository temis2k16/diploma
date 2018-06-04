import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class window {
    private JRadioButton sgRadioButton;
    private JRadioButton cgRadioButton;
    private JRadioButton geneticRadioButton;
    private JButton goButton;
    private JCheckBox saveCSV;
    private JPanel panelMain;
    private JTextField textField1;
    private ButtonGroup buttonGroup1;

    public window() {
        saveCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (saveCSV.isSelected()){
                    textField1.setEnabled(true);
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Smart spot manager");
        frame.setContentPane(new window().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
