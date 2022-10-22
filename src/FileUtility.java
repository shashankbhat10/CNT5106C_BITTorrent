import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {
    public static FileUtility getObj() {
        return new FileUtility();
    }

    public List<String> parseFile(String filePath) {
        List<String> fileContents = new ArrayList<String>();
        if (filePath != null) {
            String dirName = System.getProperty("user.dir") + File.separator + filePath;
            try {
                FileReader fr = new FileReader(dirName);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                while ((line = br.readLine()) != null) {
                    fileContents.add(line);
                }
                br.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return fileContents;
    }
}
