import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVAnalyzer {
    public static void main(String[] args) {
        String csvFile = "/home/yeskaomar/Documents/distributed_applications/git/dsgt1-rmi/output.csv"; // Replace this with the path to your CSV file
        int lineCount = countLines(csvFile);
        System.out.println("Number of lines in the CSV file: " + lineCount);
    }

    private static int countLines(String filename) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
}

