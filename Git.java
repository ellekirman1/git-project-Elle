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
import java.util.ArrayList;

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
            } else {// added what happens if it is new
                FileWriter writer = new FileWriter(HEAD);
                writer.write("ref: refs/heads/main");
                writer.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // new in 5.2

        File refs = new File("git/refs");
        if (!refs.exists()) {
            refs.mkdir();
        }
        File heads = new File("git/refs/heads");
        if (!heads.exists()) {
            heads.mkdir();
        }

        // here i added refs and refs/head (above)

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

            fr.close();

            // added code here for 5.2
            String branchpaath = null;
            if (parentCommit != null && parentCommit.startsWith("ref: ")) {
                branchpaath = "git/" + parentCommit.substring(5);// to get rid of the "ref: " if it start w it
                File branch = new File(branchpaath);
                if (branch.exists()) {
                    BufferedReader branchReader = new BufferedReader(new FileReader(branch));
                    parentCommit = branchReader.readLine();
                    branchReader.close();
                } else {
                    parentCommit = null;
                }
            }
            if (parentCommit != null && !parentCommit.isEmpty()) {
                commitContent.append("parent: " + parentCommit + "\n");
            }
            // added code done - moved the other stuff down below for correct order

            commitContent.append("author: " + author + "\n");
            commitContent.append("date: " + LocalDate.now() + "\n");
            commitContent.append("message: " + message);

            String commitHash = HashAndAdd.hashContent(commitContent.toString());// in a class
            //
            // moved
            // this down
            // BufferedWriter bw = new BufferedWriter(new FileWriter("git/objects/" +
            // commitHash));
            // BufferedWriter bw2 = new BufferedWriter(new FileWriter("git/HEAD"));
            // bw.write(commitContent.toString());
            // bw2.write(commitHash);
            // bw.close();
            // bw2.close();
            // return commitHash;

            // this is the old code - however I just wrote into code - i need it to also go
            // into branch

            // 5.2 new code
            BufferedWriter bw = new BufferedWriter(new FileWriter("git/objects/" + commitHash));
            bw.write(commitContent.toString());
            bw.close();

            if (branchpaath != null) {
                BufferedWriter bw2 = new BufferedWriter(new FileWriter(branchpaath));
                bw2.write(commitHash);
                bw2.close();
            } else {
                BufferedWriter bw2 = new BufferedWriter(new FileWriter("git/HEAD"));
                bw2.write(commitHash);
                bw2.close();
            }
            return commitHash;
            // end of new code

        } catch (Exception e) {
            System.out.println("Error in commit: " + e.getMessage());
        }
        return "";
    }

    public static void restoreHistory(String commit) {

        try {

            // here is new 5.2 code - here it accounts for branches and names "main, feture,
            //
            String branchName = commit;
            File branch = new File("git/refs/heads/" + commit);
            if (branch.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(branch));
                commit = reader.readLine();
                reader.close();
            }

            // new code done

            BufferedReader br = new BufferedReader(new FileReader("git/objects/" + commit));

            String rootHash = br.readLine();
            rootHash = rootHash.substring(rootHash.indexOf(" ") + 1);
            br.close();

            // untracked files!!!!

            ArrayList<String> trackedFiles = new ArrayList<String>();
            //make a list of tracked files
            BufferedReader indexReader = new BufferedReader(new FileReader("git/index"));
            String indexLine = indexReader.readLine();
            while (indexLine != null) {
                trackedFiles.add(indexLine.substring(indexLine.indexOf(" ") + 1));
                indexLine = indexReader.readLine();
            }

            //now call that and the helper
            untracked("", rootHash, trackedFiles);
            // deleteRepoFiles("myProgram");
            // 5.2 change!!! I dont wnat to delete all the files. did not make helper, just
            // wrote code here - delterepodfiels was already good, but not w update

            BufferedReader indexread = new BufferedReader(new FileReader("git/index"));

            String line = indexread.readLine();

            while (line != null) {
                int findspace = line.indexOf(" ");
                String fileName = line.substring(findspace + 1);
                File trackedFile = new File(fileName);

                if (trackedFile.exists()) {
                    trackedFile.delete();// just used delete
                }

                line = indexread.readLine();
            }

            indexread.close();

            // end of new code

            // I want to clear the old index so I can rebuild - new 5.2 code
            FileWriter indexWriter = new FileWriter("git/index");
            indexWriter.write("");
            indexWriter.close();

            // cleared index above
            restoreDirectory("", rootHash);
            BufferedWriter writer = new BufferedWriter(new FileWriter("git/HEAD"));

            if (branch.exists()) {// checks if branch and then ads the branch name
                writer.write("ref: refs/heads/" + branchName);
            } else {
                writer.write(commit);// or else puts in the commit
            }

            writer.close();

            // new code done
        } catch (Exception e) {
            System.out.println("error in restoreHistory: " + e.getMessage());
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

    public static void untracked(String dirPath, String hash,
            ArrayList<String> trackedFiles) throws IOException {

        FileReader x = new FileReader("git/objects/" + hash);
        BufferedReader br = new BufferedReader(x);
        String line = br.readLine();
        // gets what we are checking above

        while (line != null) {
            int firstSpace = line.indexOf(" ");
            int lastSpace = line.lastIndexOf(" ");
            // find " " to seperate

            String fileHash = line.substring(firstSpace + 1, lastSpace);
            String fileName = line.substring(lastSpace + 1);
            // seperates

            String path = dirPath + fileName;

            if (line.startsWith("tree ")) {// checks if tree (folder, goes into it)
                untracked(path + "/", fileHash, trackedFiles);// ooo recursion
            } else if (line.startsWith("blob ")) {// checks if blob - this would be untracked
                File file = new File(path);
                if (file.exists() && !trackedFiles.contains(path)) {
                    br.close();
                    // this stops it from being written over & lost
                    throw new IOException("would be written over");
                }
            }
            line = br.readLine();
        }
        br.close();
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

            // 5.2 added new code

            FileWriter indexWriter = new FileWriter("git/index", true);
            indexWriter.write(fileHash + " " + path + "\n");
            indexWriter.close();
            // post clearing index, this puts the file back into it

            // end of new code
            br.close();
        } catch (Exception e) {
            System.out.println("Error in restoreFile: " + e.getMessage());
        }
    }

    public static void createBranch(String name) {

        File branch = new File("git/refs/heads/" + name);

        if (branch.exists()) {
            throw new IllegalArgumentException(
                    "already exists");
        }

        try {
            // the code below is getting the current commit

            BufferedReader br = new BufferedReader(new FileReader("git/HEAD"));
            String hash = br.readLine();
            br.close();

            if (hash != null && hash.startsWith("ref: ")) {
                File currentBranch = new File("git/" + hash.substring(5));// cuts it to get refs; out
                if (currentBranch.exists()) {
                    FileReader x = new FileReader(currentBranch);
                    BufferedReader reader = new BufferedReader(x);
                    hash = reader.readLine();
                    reader.close();
                } else {
                    hash = null;
                }
            }

            // then uses that hash
            if (hash == null || hash.isEmpty()) {
                throw new IllegalStateException(
                        "no commits exist");
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(branch));

            bw.write(hash);
            bw.close();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "error", e);
        }
    }

}