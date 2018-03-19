import sun.misc.IOUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

/**
 * Created by Garett on 8/24/2017.
 */

//Loader does what Saver does in reverse
public class Loader {
    private String input;
    String file;

    public Loader(int ID) {

        //Same algorithm as Saver to get save file path
        input = "";
        file = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        file = new File(file).getParentFile().toString();
        switch (ID) {
            case 1:
                file += "\\save1.txt";
                break;
            case 2:
                file += "\\save2.txt";
                break;
            case 3:
                file += "\\save3.txt";
                break;
            case 4:
                file += "\\save4.txt";
                break;
            case 5:
                file += "\\save5.txt";
                break;
            default:
                file += "\\save1.txt";
                break;
        }


        try { // pointless try block, just aims to avoid exceptions and crashes

            // Reads the entire file contents into one string
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            while((line = br.readLine()) != null){
                input += line;
                input += "\n";
            }


        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {return;}

        // Split removes all new line characters, and separates each one into new lists
        String[] allValues = input.split("\n");
        String[][] stats;
        stats = new String[20][];

        //The string is further split by the "-" character, to achieve a matrix of stats
        for (int i=0; i<allValues.length; i++) {
            stats[i] = allValues[i].split("-");
        }

        // Update ALL simulator values using the stats matrix values
        for (int playerID=0; playerID<stats.length; playerID++) {

            //Because there are 20 lines, the first 10 correspond to each players values
            //Lines 10-20 correspond to items, each line being its own player
            if (playerID < 10) {
                for (int i = 0; i < stats[playerID].length; i++) {

                    //Each line, 10-20 has a specific number of values split by the "-"
                    // i=0 is the player name
                    // 1-10 is the lefthand values
                    // 11-20 is the righthand values
                    // 21 is the ingame name
                    // 22 is the file path string
                    if (i == 0) {
                        MainFrame.players[playerID].name.setText(stats[playerID][i]);
                    } else if ((i < 11) && i != 0) {
                        MainFrame.players[playerID].labels.get(i - 1).setText(stats[playerID][i]);
                    } else if ((i > 11) && (i < 20)) {
                        MainFrame.players[playerID].label2.get(i - 11).setText(stats[playerID][i]);
                    } else if (i == 21) {
                        MainFrame.players[playerID].IGN.setText(stats[playerID][i]);
                    } else {
                        BufferedImage pic = null;
                        try {
                            pic = ImageIO.read(new File(stats[playerID][i]));
                        } catch (IOException ex) {
                            try {
                                String imgName = "";
                                imgName = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                                imgName = new File(imgName).getParentFile().toString();
                                imgName += "\\strawberry.jpg";
                                pic = ImageIO.read(new File(imgName));
                            } catch (IOException e) {
                            }
                        }
                        JLabel picLabel = new JLabel(new ImageIcon(pic));
                        ImageIcon imageIcon = new ImageIcon(new ImageIcon(pic).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
                        MainFrame.players[playerID].picLabel.setIcon(imageIcon);

                    }
                }

            // Once i>10, the lines 10-20 are the player's items
            } else {
                MainFrame.players[playerID-10].itemList.removeAllElements(); // Remove all current items

                //Add existing items back into the item list
                for (int i=0; i<stats[playerID].length; i++) {
                    MainFrame.players[playerID-10].itemList.addElement(stats[playerID][i]);
                }
            }
        }
    }
}

