import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class hashfunc {
    public static void main(String[] args) {
        
        try {
            String sha1Hash;
            sha1Hash = hashFile("hello.txt");
            File blobFile = new File("git/objects/" + sha1Hash + ".txt");
            FileWriter bw = new FileWriter("git/objects/" + sha1Hash + ".txt");
            String content = new String(Files.readAllBytes(Paths.get("hello.txt")));
            bw.write(content);//do file content;
            bw.close();
            System.out.println(blobFile);
            System.out.println(content);

            writein("hello.txt");


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



  


        
        
    }
    public static void writein(String filename){
        try {
            String sha1Hash = new String();
            sha1Hash = hashFile(filename);
            FileWriter bw = new FileWriter("git/index.txt");
            bw.write(sha1Hash + " " + filename);
            System.lineSeparator();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    

    public static String hashFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if(!Files.isRegularFile(path)){
            throw new IOException("no files" + filePath);
        }
        byte[] fileBytes = Files.readAllBytes(path);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("sha1 not avalible", e);
        }
        byte[] hash = digest.digest(fileBytes);
        System.out.println();

        return HexFormat.of().formatHex(hash);
    }
    
}
