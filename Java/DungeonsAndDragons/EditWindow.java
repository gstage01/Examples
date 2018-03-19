import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Garett on 8/23/2017.
 */
public class EditWindow extends JPanel implements ActionListener {
    String name, imgName;
    String[] values;
    JFrame frame;
    JTextField nameField, IGN;
    JTextField[] fields;
    JLabel info1, attributes, pic, nickname;
    JButton submit, closer, browse1;
    int id;

    //Window that opens when the edit button is pressed
    //Used to change player values
    public EditWindow(JLabel name, Map<Integer, JLabel> valueSet, int id, JLabel ign) {
        this.id = id;

        //Set up window
        frame = new JFrame("Edit");
        frame.setLayout(new GridBagLayout());
        frame.setPreferredSize(new Dimension(400,550));
        GridBagConstraints c = new GridBagConstraints();

        info1 = new JLabel("Enter player name here: ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        frame.add(info1, c);



        //Add name box
        nameField = new JTextField();
        if (name.getText().equals("Player name")) {
            nameField.setToolTipText("Enter name here");
        } else {
            nameField = new JTextField(name.getText());
        }
        c.gridy = 1;
        c.gridwidth = 2;
        frame.add(nameField, c);

        IGN = new JTextField();
        IGN.setText(ign.getText());
        nickname = new JLabel("Enter in-game name: ");
        c.gridy = 2;
        c.gridwidth = 2;
        frame.add(nickname, c);
        c.gridy = 3;
        c.gridwidth = 2;
        frame.add(IGN, c);


        attributes = new JLabel("Change stats: ");
        c.gridy = 4;
        frame.add(attributes, c);


        //Place text fields
        fields = new JTextField[20];
        for (int i=0; i<20; i++) { // i<10 for keys -- 10<i<20 for values
            fields[i] = new JTextField();

            if (i<10) {
                fields[i].setColumns(10);
                if (MainFrame.players[id].labels.get(i).getText().equals(" ")) {
                    fields[i].setToolTipText("Enter trait");
                } else {
                    fields[i].setText(MainFrame.players[id].labels.get(i).getText());
                }
                c.gridx = 0;
                c.gridy = i+5;
                c.gridwidth = 1;
                frame.add(fields[i], c);
            } else {
                fields[i].setColumns(10);
                if (MainFrame.players[id].label2.get(i-10).getText().equals(" ")) {
                    fields[i].setToolTipText("Enter values");
                } else {

                    fields[i].setText(MainFrame.players[id].label2.get(i - 10).getText());
                }
                c.gridx = 1;
                c.gridy = i-5;
                c.gridwidth = 1;
                frame.add(fields[i], c);

            }
        }

        //Common themes arise:
        //The object being placed on the screen is created, then position is set, then it is added
        //After, the object is given a "listener" that lets it function when activated
        submit = new JButton("Submit");
        c.gridx = 0;
        c.gridy = 15;
        c.gridwidth = 1;
        frame.add(submit, c);
        submit.addActionListener(this);
        closer = new JButton("Close");
        c.gridx = 1;
        c.gridy = 15;
        c.gridwidth = 1;
        frame.add(closer, c);
        closer.addActionListener(this);

        browse1 = new JButton("Browse Images");
        c.gridx = 0;
        c.gridy = 16;
        c.gridwidth = 2;
        frame.add(browse1, c);
        browse1.addActionListener(this);


        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(closer)) { //Closes the screen without saving values
            frame.dispose();
        } else if (e.getSource().equals(submit)) { // Updates the values that were entered in the edit window
            //Updates the lefthand column values
            for (int i=0; i<10; i++) {
                for (int j = 0; j < 10; j++){
                    MainFrame.players[i].labels.get(j).setText(fields[j].getText());
                }
            }
            //Updates the righthand column values
            for (int i=0; i<10; i++) {
                String fieldText = fields[i+10].getText();
                if (fieldText.equals("")) {
                    fieldText = " ";
                }
                MainFrame.players[id].label2.get(i).setText(fieldText);
            }
            //Update the name and IGN, and then close the window
            MainFrame.players[id].name.setText(nameField.getText());
            MainFrame.players[id].IGN.setText(IGN.getText());
            frame.dispose();
        } else if (e.getSource().equals(browse1)) { // Allows for pictures to be chosen
            // File browser is created
            JFileChooser chooseFile = new JFileChooser();
            chooseFile.showOpenDialog(frame);

            // Selected file is saved
            File file = chooseFile.getSelectedFile();
            String path = file.getPath();

            // The player's image path is updated
            MainFrame.players[id].imgName = path;

            // The player's picture is rendered
            BufferedImage pic = null;
            try {
                pic = ImageIO.read(new File(path));
            } catch (IOException ex) {}
            JLabel picLabel = new JLabel(new ImageIcon(pic));
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(pic).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));

            // And updated
            MainFrame.players[id].picLabel.setIcon(imageIcon);
        }
    }
}
