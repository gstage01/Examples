/**
 * Created by Garett on 8/21/2017.
 */
import sun.misc.IOUtils;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/* MainFrame is the main class that creates the entire simulation
 * main() creates a new MainFrame object
 *
 *
*/
public class MainFrame extends JFrame implements MenuListener, ActionListener {
    JFrame frame;
    JMenuBar menuBar;
    JMenu fileMenu, saveMenu, loadMenu;
    JMenuItem stop, saveFile1, saveFile2, saveFile3, saveFile4, saveFile5, loadFile1, loadFile2, loadFile3, loadFile4, loadFile5;
    static Player[] players; //Static files can be accessed by any object

    /* Note: when an object is created, methods that have the same name as the class are constructors
     * They run as soon as the object is created
     * Thus, this runs as soon as main() creates the MainFrame object
     * This project was done with Swing, a Java library for interfacing
    */
    public MainFrame() {
        //Set the frame
        frame = new JFrame("Dunces and Dragons");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        //Create Menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        stop = new JMenuItem("exit");

        //Action Listeners are how buttons and menus interact with the user
        //The class will actively "listen" for the object to activate, and when it does
        //the ActionPerformed method (below) will play
        stop.addActionListener(this);

        // .add() is a common method for Swing components
        // In short, all components have parents, except for JFrame objects
        // Here, a menu is added to the frame, another menu is added to that menu
        fileMenu.add(stop);
        menuBar.add(fileMenu);
        saveMenu = new JMenu("Save");
        fileMenu.add(saveMenu);

        // The save operations are added to the save menu, in the main menu bar
        saveFile1 = new JMenuItem("Save to file 1");
        saveFile2 = new JMenuItem("Save to file 2");
        saveFile3 = new JMenuItem("Save to file 3");
        saveFile4 = new JMenuItem("Save to file 4");
        saveFile5 = new JMenuItem("Save to file 5");
        saveMenu.add(saveFile1);
        saveMenu.add(saveFile2);
        saveMenu.add(saveFile3);
        saveMenu.add(saveFile4);
        saveMenu.add(saveFile5);
        //ActionListeners are added to all save buttons, so that they can function
        saveFile1.addActionListener(this);
        saveFile2.addActionListener(this);
        saveFile3.addActionListener(this);
        saveFile4.addActionListener(this);
        saveFile5.addActionListener(this);

        //This is repeated with the load menu
        loadMenu = new JMenu("Load");
        fileMenu.add(loadMenu);
        loadFile1 = new JMenuItem("Load file 1");
        loadFile2 = new JMenuItem("Load file 2");
        loadFile3 = new JMenuItem("Load file 3");
        loadFile4 = new JMenuItem("Load file 4");
        loadFile5 = new JMenuItem("Load file 5");
        loadMenu.add(loadFile1);
        loadMenu.add(loadFile2);
        loadMenu.add(loadFile3);
        loadMenu.add(loadFile4);
        loadMenu.add(loadFile5);
        loadFile1.addActionListener(this);
        loadFile2.addActionListener(this);
        loadFile3.addActionListener(this);
        loadFile4.addActionListener(this);
        loadFile5.addActionListener(this);

        //The frame is sized, then made visible
        frame.setSize(1440,720);
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
        players = new Player[10];
        c.ipadx = 25;

        // Creates 10 new player objects, and adds them to the frame
        // Each player object is the same as a JPanel, essentially adding 10 frames inside the main frame
        for (int i=0; i<10; i++) {
            c.gridx = i;
            c.gridy = 0;
            players[i] = new Player(i);
            frame.add(players[i].playerFrame, c);
        }

        //pack removes empty space and aligns all objects in the frame
        frame.pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //e is an event that stores an action
        //its source is the object that called the action
        //Depending on the source of the action, we can perform different tasks corresponding to the action
        if (e.getSource().equals(stop)) {
            System.exit(0);
        } else if (e.getSource().equals(saveFile1)) {
            Saver saveTheFile = new Saver(1);
        } else if (e.getSource().equals(saveFile2)) {
            Saver saveTheFile = new Saver(2);
        } else if (e.getSource().equals(saveFile3)) {
            Saver saveTheFile = new Saver(3);
        } else if (e.getSource().equals(saveFile4)) {
            Saver saveTheFile = new Saver(4);
        } else if (e.getSource().equals(saveFile5)) {
            Saver saveTheFile = new Saver(5);
        } else if (e.getSource().equals(loadFile1)) {
            Loader loadTheFile = new Loader(1);
        } else if (e.getSource().equals(loadFile2)) {
            Loader loadTheFile = new Loader(2);
        } else if (e.getSource().equals(loadFile3)) {
            Loader loadTheFile = new Loader(3);
        } else if (e.getSource().equals(loadFile4)) {
            Loader loadTheFile = new Loader(4);
        } else if (e.getSource().equals(loadFile5)) {
            Loader loadTheFile = new Loader(5);
        }
    }

    @Override
    public void menuSelected(MenuEvent e) {

    }

    @Override
    public void menuDeselected(MenuEvent e) {

    }

    @Override
    public void menuCanceled(MenuEvent e) {

    }
    public static void main(String[] args) {
        MainFrame gameFrame = new MainFrame();
        gameFrame.setVisible(false);
    }

}
