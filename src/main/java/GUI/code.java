package GUI;

import DecompositionGreedy.DecompositionGreedy;
import DecompositionGreedy.dgDbConnection;
import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.genDbConnection;
import SimpleGreedy.SimpleGreedy;
import SimpleGreedy.sgDbConnection;
import main.CheckResult;
import main.Flight;

import javax.swing.*;
import java.awt.*;

public class code {
    private JRadioButton sgRadioButton;
    private JRadioButton cgRadioButton;
    private JRadioButton geneticRadioButton;
    private JButton goButton;
    private JPanel panelMain;
    private JTextField path;
    private JLabel pathTip;
    private JTextPane State;
    private ButtonGroup buttonGroup1;

    public code() {
        goButton.addActionListener(e -> {
            if (geneticRadioButton.isSelected()){
                genDbConnection ga = new genDbConnection();
                Flight flight = new Flight(ga);
                String str = GeneticAlgorithm.runGeneticAlgorithm(ga,flight);
                State.setText(str);
                if (!path.getText().equals("")){
                    String scvPath = path.getText();
                    scvPath += "GA_RESULT.csv";
                    ga.outputResult(scvPath);
                }
                JTextArea textArea = new JTextArea(flight.toString());
                textArea.append("\n"+flight.status.toString());
                textArea.append("\n"+CheckResult.UniformTest(ga,"\"geneticAlgorithm\""));
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                scrollPane.setPreferredSize( new Dimension( 800, 300 ) );
                JOptionPane.showMessageDialog(null, scrollPane, "Статус расстановки",
                        JOptionPane.PLAIN_MESSAGE);

            }
            if (cgRadioButton.isSelected()){
                dgDbConnection dg= new dgDbConnection();
                Flight flight = new Flight(dg);
                String str = DecompositionGreedy.runDecompositionGreedy(dg,flight);
                State.setText(str);
                if (!path.getText().equals("")){
                    String scvPath = path.getText();
                    scvPath += "DG_RESULT.csv";
                    dg.outputResult(scvPath);
                }
                JTextArea textArea = new JTextArea(flight.toString());
                textArea.append("\n"+flight.status.toString());
                textArea.append("\n"+CheckResult.UniformTest(dg,"\"decompositionGreedy\""));
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                scrollPane.setPreferredSize( new Dimension( 800, 300 ) );
                JOptionPane.showMessageDialog(null, scrollPane, "Статус расстановки",
                        JOptionPane.PLAIN_MESSAGE);

            }
            if (sgRadioButton.isSelected()){
                sgDbConnection sg= new sgDbConnection();
                Flight flight = new Flight(sg);
                String str = SimpleGreedy.runSimpleGreedy(sg,flight);
                State.setText(str);
                if (!path.getText().equals("")){
                    String scvPath = path.getText();
                    scvPath += "SG_RESULT.csv";
                    sg.outputResult(scvPath);
                }
                JTextArea textArea = new JTextArea(flight.toString());
                textArea.append("\n"+flight.status.toString());
                textArea.append("\n"+CheckResult.UniformTest(sg,"\"simpleGreedy\""));
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                scrollPane.setPreferredSize( new Dimension( 800, 300 ) );
                JOptionPane.showMessageDialog(null, scrollPane, "Статус расстановки",
                        JOptionPane.PLAIN_MESSAGE);

            }
        });
    }

    private void createUIComponents() {
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Smart spot manager");
        frame.setResizable(false);
        frame.setContentPane(new code().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
