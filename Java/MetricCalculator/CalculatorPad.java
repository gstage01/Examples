import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Garett on 1/4/2018.
 */
public class CalculatorPad extends JPanel implements ActionListener {
    JPanel numPad;
    JButton decimal, delete, clear;
    JButton[] numbers;
    ActionListener[] buttonListeners;
    String input;
    Interface interf;
    public CalculatorPad(Interface inter) {
        interf = inter;
        input = "";
        numPad = new JPanel();
        numPad.setPreferredSize(new Dimension(175,300));
        numPad.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 15;

        numbers = new JButton[10];
        for (int i=0; i<10; i++) {
            numbers[i] = new JButton();
            numbers[i].setFont(new Font("Arial", Font.PLAIN, 40));
            numbers[i].setContentAreaFilled(false);
            numbers[i].addActionListener(this);
        }
        for (int i=1; i<10; i++) {
            numbers[i].setText(Integer.toString(i));
        }
        numbers[0].setText("0");
        buttonListeners = new ActionListener[10];
        for (int i=1; i<10; i++) {
                numPad.add(numbers[i], c);
                if (c.gridx < 2) {
                    c.gridx++;
                } else {
                    c.gridx = 0;
                    c.gridy++;
                }

        }
        decimal = new JButton(".");
        decimal.setFont(new Font("Arial", Font.PLAIN, 40));
        decimal.setContentAreaFilled(false);
        c.gridy = 4;
        c.gridx = 0;
        decimal.addActionListener(this);
        numPad.add(decimal,c);

        c.gridx = 1;
        numPad.add(numbers[0], c);

        delete = new JButton("←");
        delete.addActionListener(this);
        delete.setFont(new Font("Arial", Font.PLAIN, 20));
        delete.setContentAreaFilled(false);
        c.ipady = 23;
        c.gridx = 2;
        numPad.add(delete, c);

        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy++;
        clear = new JButton("CE");
        clear.setFont(new Font("Arial", Font.PLAIN, 20));
        clear.setVisible(true);
        clear.addActionListener(this);
        clear.setContentAreaFilled(false);
        numPad.add(clear,c);
        numPad.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(numbers[0])) {
            input += "0";
        } else if (e.getSource().equals(numbers[1])) {
            input += "1";
        } else if (e.getSource().equals(numbers[2])) {
            input += "2";
        } else if (e.getSource().equals(numbers[3])) {
            input += "3";
        } else if (e.getSource().equals(numbers[4])) {
            input += "4";
        } else if (e.getSource().equals(numbers[5])) {
            input += "5";
        } else if (e.getSource().equals(numbers[6])) {
            input += "6";
        } else if (e.getSource().equals(numbers[7])) {
            input += "7";
        } else if (e.getSource().equals(numbers[8])) {
            input += "8";
        } else if (e.getSource().equals(numbers[9])) {
            input += "9";
        } else if (e.getSource().equals(decimal)) {
            if (input.contains(".")) {
            } else {
                input += ".";
            }
        } else if (e.getSource().equals(delete)) {
            input = input.substring(0, input.length() - 1);
        } else if (e.getSource().equals(clear)) {
            input = "";
        }
        interf.update();
    }


}
