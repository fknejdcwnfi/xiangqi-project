package edu.sustech.xiangqi;

import javax.imageio.IIOException;
import java.io.*;

public class GamePersistence {

    //Serializes(saves) the GameSession object to file named after the player

    public static void saveGame(PlayGameSession session) {
        String filename = session.getPlayerNameID() + ".sav";
        try (FileOutputStream fileOut = new FileOutputStream(filename);//this like bulid the file for the data to save
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {//this like is a object in the file you have bulid and it is like a machine that writes your serialization of the object to the file to save it into your file

            out.writeObject(session);//this is the entire object to be written into the file in byte
            System.out.println("Game saved successfully to: " + filename);
        } catch (IOException i) {
            System.err.println("Error saving name: " + i.getMessage());
            i.printStackTrace();
        }
    }


    //Deserializes (loads) a GameSession object from a player's save file
    //@return the loaded PlayGameSession object or null if the file is not found
    public static PlayGameSession loadGame(String playerName) {
        String filename = playerName + ".sav";
        try (FileInputStream fileIn = new FileInputStream(filename);//it is like to read(just open) the file through its name
             ObjectInputStream in = new ObjectInputStream(fileIn)) {//it is a machine(you bulid an object in the file you open) and it can convert the byte to the java data (deserialization of the data of the player you save in the file)

            PlayGameSession session = (PlayGameSession) in.readObject();//This method reads the byte data from the file and attempts to reconstruct the object that was originally saved.
            //it is just like renew a Object to the new Object but use the old data you restore so that it looks like loading your game

            System.out.println("Game loaded successfully for: " + playerName);
            return session;
        } catch (FileNotFoundException e) {
            System.out.println("NO saved game found for " + playerName + ".Starting new game");
        return null;//return null to indicate a new game should be created
        } catch (IOException i) {
            System.err.println("Error loading game: " +  i.getMessage());
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.err.println("PlayGameSession class not found. " +  c.getMessage());
            c.printStackTrace();
            return null;
        }
    }


}
