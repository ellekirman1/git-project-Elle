import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.LocalDate;

public class Git {
    public static void initiate() {

        int counter = 0;

        File git = new File("git/");
        if (!git.exists()) {
            git.mkdir();
            System.out.println("make git directory");

        } else {
            counter += 1;
        }

        File objectsDir = new File("git/objects/");
        if (!objectsDir.exists()) {
            objectsDir.mkdir();
            System.out.println("make object directory");

        } else {
            counter += 1;
        }

        File index = new File("git/index");
        try {
            if (!index.createNewFile()) {
                counter += 1;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File HEAD = new File("git/HEAD");
        try {
            if (!HEAD.createNewFile()) {
                counter += 1;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (counter == 4) {
            System.out.println("Git Repository Already Exists");

        } else {
            System.out.println("Git Repository Created\n" + //
                    "");
        }

    }

    public static String commit(String author, String message) {
        if (author == null || message == null) {
            throw new IllegalArgumentException("Neither author nor message can be null!");
        }
        try {
            String rootTreeHash = TreeCreation.createTreeFromIndex();
            StringBuilder commitContent = new StringBuilder("tree: " + rootTreeHash + "\n");
            BufferedReader fr = new BufferedReader(new FileReader("git/HEAD"));
            String parentCommit = fr.readLine();
            if (parentCommit != null) {
                commitContent.append("parent: " + parentCommit + "\n");
            }
            fr.close();
            commitContent.append("author: " + author + "\n");
            commitContent.append("date: " + LocalDate.now() + "\n");
            commitContent.append("message: " + message);
            String commitHash = HashAndAdd.hashContent(commitContent.toString());
            BufferedWriter bw = new BufferedWriter(new FileWriter("git/objects/" + commitHash));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter("git/HEAD"));
            bw.write(commitContent.toString());
            bw2.write(commitHash);
            bw.close();
            bw2.close();
            return commitHash;
        } catch (Exception e) {
            System.out.println("Error in commit: " + e.getMessage());
        }
        return "";
    }

    public static void restoreHistory(String commit) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("git/objects/" + commit));
            String rootHash = br.readLine();
            rootHash = rootHash.substring(rootHash.indexOf(" ") + 1);
            br.close();
            deleteRepoFiles("myProgram");
            restoreDirectory("", rootHash);
        } catch (Exception e) {
            System.out.println("Error in restoreHistory: " + e.getMessage());
        }
    }

    public static void deleteRepoFiles(String dirPath) {
        // Deletes files so they can be refilled by restoreHistory
        File rootDir = new File(dirPath);
        File[] files = rootDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                deleteRepoFiles(files[i].getPath());
            } else {
                files[i].delete();
            }
        }
        rootDir.delete();
    }

    public static void restoreDirectory(String dirPath, String hash) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("git/objects/" + hash));
            while (br.ready()) {
                String line = br.readLine();
                if (line.indexOf("blob") != -1) {
                    // BLOB file here
                    int firstSpace = line.indexOf(" ");
                    int lastSpace = line.lastIndexOf(" ");
                    String fileHash = line.substring(firstSpace + 1, lastSpace);
                    String fileName = line.substring(lastSpace + 1);
                    restoreFile(dirPath + fileName, fileHash);
                } else if (line.indexOf("tree") != -1) {
                    // tree file
                    int firstSpace = line.indexOf(" ");
                    int lastSpace = line.lastIndexOf(" ");
                    String dirHash = line.substring(firstSpace + 1, lastSpace);
                    String dirName = line.substring(lastSpace + 1);
                    File newDir = new File(dirPath + dirName);
                    newDir.mkdir();
                    restoreDirectory(dirPath + dirName + "/", dirHash);
                } else {
                    br.close();
                    throw new IllegalStateException("Something went wrong during tree creation!");
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error in restoreDirectory: " + e.getMessage());
        }

    }

    public static void restoreFile(String path, String fileHash) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("git/objects/" + fileHash));
            Path blobPath = Path.of("git/objects/" + fileHash);
            byte[] blobContents = Files.readAllBytes(blobPath);
            Path filePath = Path.of(path);

            File restoredFile = new File(path);

            restoredFile.createNewFile();
            Files.write(filePath, blobContents);
            br.close();
        } catch (Exception e) {
            System.out.println("Error in restoreFile: " + e.getMessage());
        }
    }

}