import model.Profile;
import parser.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;


public class Main {

    public static void main(String[] args) {
        try (FileWriter fileWriter = new FileWriter(new File("testData.txt"))) {
            Parser parser = new Parser();
            Set<Profile> profiles = parser.searchAll(10_000);
            for (Profile profile : profiles) {
                fileWriter.write(profile.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
