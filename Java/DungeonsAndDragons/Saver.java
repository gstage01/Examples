import java.io.*;

/**
 * Created by Garett on 8/24/2017.
 */

// Saver acts as a function, saving the contents of the file
public class Saver {
    private String file;
    String input;
    public Saver(int ID) {
        input = "";

        // The path to the file of the .jar is found
        file = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        file = new File(file).getParentFile().toString();
        // The save file chosen is then added to the path
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

        // Creates a string that represents the save file information
        // The variable "i" is the player index. It tells which player is being saved

        for (int i = 0; i < 10; i++) {
            input += MainFrame.players[i].name.getText(); // Saves the player name
            for (int j = 0; j < 10; j++) {
                input += "-"; // - is the character used to split the string and obtain the values for loading
                input += MainFrame.players[i].labels.get(j).getText(); // Saves the lefthand column value
            }
            for (int j = 0; j < 10; j++) { //Save format: name-tb1-tb2-tb3...-tb20-ign\n
                input += "-";
                input += MainFrame.players[i].label2.get(j).getText(); //Saves the righthand column value

            }
            input += "-";
            input += MainFrame.players[i].IGN.getText();
            input += "-" + MainFrame.players[i].imgName; //imgName is a path to the file of the image
            input += "\n";                               //if the image location moves, this will fail
        }
        // Saves the items of each character on their own line, separating names with a "-"
        for (int i=0; i<10; i++) {
            for (int j = 0; j<MainFrame.players[i].items.getModel().getSize(); j++) {
                input += MainFrame.players[i].items.getModel().getElementAt(j);
                input += "-";
            }
            input += "\n";
        }

        //Takes the contents of the string being saved, and puts it in the file path
        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] inputout = input.getBytes();
            out.write(inputout);
        } catch (IOException e) {
            return;
        }
    }
}
