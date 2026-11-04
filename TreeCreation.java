import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TreeCreation {
    public static void main(String[] args) throws IOException {
        System.out.println(createTreeFromIndex());
    }

    public static String createTreeFromIndex() throws IOException {
        ArrayList<String> workingList = buildWorkingList();

        String directory = findDeepestDirectory(workingList);
        while (!directory.equals("")) {
            String hash = createTree(workingList, directory);
            workingList = collapseIntoSingleTreeEntry(workingList, directory, hash);
            directory = findDeepestDirectory(workingList);
        }

        return (createTree(workingList, directory));
    }

    public static String findDeepestDirectory(ArrayList<String> workingList) {
        String deepest = "";
        int maxDepth = -1;
        for (String line : workingList) {
            String path = line.split(" ", 3)[2];
            String folder = getParentDir(path);

            if (!folder.equals("")) {
                int depth = 0;
                for (int i = 0; i < folder.length(); i++) {
                    if (folder.charAt(i) == '/') {
                        depth++;
                    }
                }
                if (depth > maxDepth) {
                    maxDepth = depth;
                    deepest = folder;
                }
            }
        }
        return deepest;
    }

    public static void addToSortedWorkingList(ArrayList<String> list, String entry) {
        String path = entry.split(" ", 3)[2];
        int index = list.size();
        for (int i = 0; i < list.size(); i++) {
            String currentPath = list.get(i).split(" ", 3)[2];
            if (path.compareTo(currentPath) < 0) {
                index = i;
                break;
            }
        }
        list.add(index, entry);
    }

    public static ArrayList<String> buildWorkingList() throws IOException {
        ArrayList<String> workingList = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new FileReader("git/index"));
        String line;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            String listEntry = "blob " + line;
            if (!workingList.contains(listEntry)) {
                addToSortedWorkingList(workingList, listEntry);
            }
        }
        br.close();

        return workingList;
    }

    public static String getParentDir(String filePath) {
        int index = -1;
        for (int i = 0; i < filePath.length(); i++) {
            if (filePath.charAt(i) == '/') {
                index = i;
            }
        }
        if (index == -1) {
            return "";
        }
        return filePath.substring(0, index);
    }

    public static String createTree(ArrayList<String> workingList, String dirPath) throws IOException {
        ArrayList<String> dirPathInstances = new ArrayList<String>();

        for (String workingListLine : workingList) {
            String[] listParts = workingListLine.split(" ", 3);
            String listPath = listParts[2];
            if (getParentDir(listPath).equals(dirPath)) {
                String fileName = listPath.substring(listPath.lastIndexOf('/') + 1);
                dirPathInstances.add(listParts[0] + " " + listParts[1] + " " + fileName);
            }
        }

        StringBuilder treeContent = new StringBuilder();
        for (int i = 0; i < dirPathInstances.size(); i++) {
            String kid = dirPathInstances.get(i);
            treeContent.append(kid);
            if (i != dirPathInstances.size() - 1) {
                treeContent.append("\n");
            }
        }

        String contentString = treeContent.toString();
        String hash = HashAndAdd.hashContent(contentString);

        FileWriter writeToTree = new FileWriter("git/objects/" + hash);
        writeToTree.write(contentString);
        writeToTree.close();

        return hash;
    }

    public static ArrayList<String> collapseIntoSingleTreeEntry(ArrayList<String> workingList, String directory, String hash) { // collapses
                                                                                                             // into
                                                                                                             // tree
        ArrayList<String> newWorkingList = new ArrayList<String>();

        for (String line : workingList) {
            String path = line.split(" ", 3)[2];
            if (!getParentDir(path).equals(directory)) {
                newWorkingList.add(line);
            }
        }

        addToSortedWorkingList(newWorkingList, "tree " + hash + " " + directory);
        return newWorkingList;
    }

}
