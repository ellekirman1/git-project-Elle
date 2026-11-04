import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Tester {
    public static void main(String[] args) throws IOException {

        GitWrapper.init();
        GitWrapper.add("myProgram/docs/Hello.txt");
        GitWrapper.add("myProgram/docs/World.txt");
        GitWrapper.add("myProgram/README.md");
        GitWrapper.add("myProgram/World.txt");
        // HashAndAdd.addToIndex("myProgram/docs/Check.txt");
        // ArrayList<String> workingList = TreeCreation.buildWorkingList();
        // for (String s : workingList) {
        // System.out.println(s);
        // }
        // System.out.println(TreeCreation.createTreeFromIndex());
        // System.out.println(TreeCreation.buildWorkingList());
        // System.out.println(TreeCreation.createTreeFromIndex());
        GitWrapper.commit("Yari", "Test commit 1");
        // Adding a new file and staging
        System.out.println("== EDGE CASE CHECKS ==");
        try {
            // GitWrapper.add("myProgram/ThisIsWrong.txt");
            GitWrapper.add("myProgram/docs");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("myProgram/Oh.txt"));
            bw.write("This is amazing");
            bw.close();
            GitWrapper.add("myProgram/Oh.txt");
            GitWrapper.commit("Yari", "Wow!");
            bw = new BufferedWriter(new FileWriter("myProgram/Oh.txt", true));
            bw.write(". Actually yes it is amazing.");
            bw.close();
            GitWrapper.add("myProgram/Oh.txt");
            GitWrapper.commit("Yari", "Wow!");
            GitWrapper.checkout("1c25f54632bdfc1152d8cb2cc959d9427976701b");
            GitWrapper.checkout("7267e5b68f9e6aa0e62e8e9f9e4daa0a8a13fad0");
            GitWrapper.checkout("ca8e34c4708b3caa2bb75299c0fbc9a06ba1ba28");
            GitWrapper.checkout("7267e5b68f9e6aa0e62e8e9f9e4daa0a8a13fad0");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
