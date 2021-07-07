import javafx.scene.input.KeyCode;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.awt.GridBagConstraints.FIRST_LINE_START;
import static javax.swing.SwingConstants.CENTER;

/**
 * Created by Garett on 8/21/2017.
 */

// Class that creates a player panel, to be added to the main frame
//Note the "Player extends JPanel":
    //Jpanel is a "Component" object, and that is the requirement to be added to a JFrame
    // Thus, extending the Player class to JPanel makes the Player object act as a Component object as well
public class Player extends JPanel implements ActionListener {
    String imgName;
    JLabel name, IGN, picLabel, itemLabel, addItem;
    JPanel playerFrame;
    JButton edit, addButton, deleteButton;
    JTextField itemField;
    Map<Integer, JLabel> labels = new HashMap<Integer, JLabel>(), label2 = new HashMap<Integer, JLabel>();
    JList<String> items;
    DefaultListModel itemList;
    JScrollPane itemPane;
    private int id;

    //Player() constructor that runs when the object is created
    public Player(int a) {
        id = a; // an ID is given to the players to help in the save and load functions

        //A JPanel functions almost exactly like a JFrame (our MainFrame class)
        //The only difference: JFrames can't be placed inside other JFrames
        playerFrame = new JPanel();

        playerFrame.setLayout(new GridBagLayout());
        playerFrame.setPreferredSize(new Dimension(150,575));
        //Set the frame
        playerFrame.setBorder(new LineBorder(Color.black, 1));
        edit = new JButton("Edit");
        name = new JLabel();
        name.setText("Player Name");
        name.setFont(new Font(Font.SERIF, Font.BOLD, 25));
        GridBagConstraints c = new GridBagConstraints();

        // FILE IO I HATE YOU SO
        // Gets the file path to the parent directory, then adds the path to the strawberry image
        imgName = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        imgName = new File(imgName).getParentFile().toString();
        imgName += "\\strawberry.jpg";
        BufferedImage pic = null;

        // If an image wasn't loaded onto the JPanel, then we can't go back and change the image later
        // This means a file is needed that will consistently load on startup to allow for file changes
        try {
            pic = ImageIO.read(new File(imgName));
        } catch (IOException e) {}
        picLabel = new JLabel(new ImageIcon(pic));
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(imgName).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
        picLabel.setIcon(imageIcon);

        // c is the grid constraints. It controls the layout of the Panel
        // Every object added to the frame is set at a specific grid location
        // and then sized using these constraints
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        playerFrame.add(picLabel, c);

        IGN = new JLabel("IGN");
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.ipady = 5;
        c.gridwidth = 2;
        playerFrame.add(name, c);
        c.gridx = 0;
        c.gridy = 2;
        c.ipady = 5;
        c.gridwidth = 2;
        playerFrame.add(IGN, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        playerFrame.add(edit, c);

        //Set and Create Labels (The left values)
        for (int i=0; i<10; i++) {
            labels.put(i, new JLabel(" "));
        }

        for (Integer key : labels.keySet()) {
            c.anchor = FIRST_LINE_START;
            c.fill = GridBagConstraints.NONE;
            c.gridx = 0;
            c.gridy = key + 4;
            c.gridwidth = 1;
            c.ipadx = 25;
            playerFrame.add(labels.get(key), c);
        }

        //Set and create Labels2, for values (right values)
        for (int i=0; i<10; i++) {
            label2.put(i, new JLabel(" "));
        }

        for (Integer key : labels.keySet()) {

            c.fill = GridBagConstraints.NONE;
            c.gridx = 1;
            c.gridy = key + 4;
            c.gridwidth = 1;
            playerFrame.add(label2.get(key), c);
        }
        itemLabel = new JLabel("Items:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 20;
        c.gridwidth = 2;
        playerFrame.add(itemLabel, c);

        //itemList carries the items of the list
        itemList = new DefaultListModel();
        itemList.addElement("");
        itemList.addElement("");

        // items packs the itemList into one object
        items = new JList<>(itemList);
        items.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        items.setLayoutOrientation(JList.VERTICAL);
        items.setVisibleRowCount(-1);

        //itemPane shows the objects in a way that allows for scrolling
        itemPane = new JScrollPane(items);
        itemPane.setPreferredSize(new Dimension(400,400));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 21;
        c.ipady = 100;
        playerFrame.add(itemPane,c);
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 22;

        c.gridwidth = 2;
        c.gridheight = 1;
        itemField = new JTextField();
        playerFrame.add(itemField, c);


        c.gridx = 0;
        c.gridy = 23;
        c.gridwidth = 1;
        addButton = new JButton("Add");
        addButton.addActionListener(this);
        playerFrame.add(addButton,c);
        c.gridx = 1;
        c.gridy = 23;
        c.gridwidth = 1;
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        playerFrame.add(deleteButton, c);
        itemField.addActionListener(this);


        //Action for edit buttons

        edit.addActionListener(this);
        playerFrame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(edit)) {   // edit creates a new editWindow object
            EditWindow change = new EditWindow(name, labels, id, IGN); // Used to change player name, stats, etc..
        } else if (e.getSource().equals(addButton) ||e.getSource().equals(itemField) ) {
            //Adds the item in the item text box to the list when the "add" button is pushed, or the enter key is pressed
            String enter = itemField.getText();
            itemField.setText("");
            if (enter == "") {

            } else {
                itemList.addElement(enter);
            }
        } else if (e.getSource().equals(deleteButton)) {
            //Removes the item selected when delete is pressed
            itemList.removeElementAt(items.getSelectedIndex());
        }
    }
}
