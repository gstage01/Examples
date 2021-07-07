/**
 * Created by Garett on 1/4/2018.
 */
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

//This code aims to make a simple calculator GUI to convert metric to SI or SI to metric.

public class Interface extends JFrame implements ActionListener, KeyListener {
    JFrame iFrame;
    CalculatorPad pad;
    JLabel text1, text2;
    JTextField metric, si, currentFocus;
    JButton swap;
    double multiplier = 25.4;
    char[] allowed = {'1','2','3','4','5','6','7','8','0','.'};
    public Interface() {

        iFrame = new JFrame();
        iFrame.setSize(720,800);
        iFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        iFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;

        text1 = new JLabel("SI units, in inches");
        text1.setFont(new Font("Arial", Font.PLAIN, 20));
        iFrame.add(text1, c);
        text1.setVisible(true);
        c.gridy++;

        si = new JTextField();
        si.setVisible(true);
        si.setHorizontalAlignment(SwingConstants.RIGHT);
        si.setFont(new Font("Arial", Font.PLAIN, 20));
        si.addKeyListener(this);
        c.ipady = 15;
        iFrame.add(si, c);
        c.ipady = 0;
        c.gridy++;

        swap = new JButton("Switch");
        swap.setFont(new Font("Arial", Font.PLAIN, 20));
        swap.setContentAreaFilled(false);
        swap.setVisible(true);
        swap.addActionListener(this);
        c.fill = GridBagConstraints.RELATIVE;
        c.ipady = 15;
        iFrame.add(swap,c);
        c.gridy++;
        c.ipady = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        text2 = new JLabel();
        text2.setText("Metric units, in millimeters");
        text2.setFont(new Font("Arial", Font.PLAIN, 20));
        iFrame.add(text2, c);
        text2.setVisible(true);
        c.gridy++;

        metric = new JTextField();
        metric.setVisible(true);
        metric.setFont(new Font("Arial", Font.PLAIN, 20));
        metric.setHorizontalAlignment(SwingConstants.RIGHT);


        c.ipady = 15;
        iFrame.add(metric, c);
        c.ipady = 0;
        c.gridy++;

        pad = new CalculatorPad(this);
        iFrame.add(pad.numPad, c);
        iFrame.setVisible(true);
        iFrame.pack();


    }
    public void update() {
        si.setText(pad.input);
        double value = 0;
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        String current;

        current = si.getText();
        if (!current.isEmpty()) {
            if (current.substring(current.length() - 1, current.length()) == ".") {
                current += "0";
            }
            value = Double.parseDouble(current);
            metric.setText("" + df.format(value*multiplier));
        } else {
            metric.setText("");
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String hold = "";
        pad.input = metric.getText();
        multiplier = 1.0/multiplier;
        update();

        hold = text1.getText();
        text1.setText(text2.getText());
        text2.setText(hold);
    }

    public static void main(String[] args) {
        Interface start = new Interface();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        if (key == '\b') {
            pad.input = pad.input.substring(0,pad.input.length()-1);
        } else {

            for (int i = 0; i < allowed.length; i++) {
                if (allowed[i] == key) {
                    if (key == '.' && pad.input.contains(".")) {

                    } else {
                        pad.input += "" + key;
                    }
                    break;
                }
            }
        }
        update();
        si.setText(si.getText().substring(0, si.getText().length()-1));
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}


