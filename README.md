### git-project-Elle
direct.java
everything worked good
New Repository Sep 17
direct.java
hashfunc.java
Part 3 made/ edited in main
writein method


## GP-4 Done by Yari

# Bugs found and fixed
1. Index does store relative paths with the same hashes, but rerunning the code multiple times simply adds more lines to the code rather than rewriting the index. Similarly, changing a file and then restaging causes it to be added to the index as a new line rather than overriding the old line. Finally, the index always had an uncecessary newline at the end of it. This was fixed in previous commits on the peer-review branch. 
2. When creating a new tree object, a new line is unnecessarly added to the end. This was fixed in a previous commit on the peer-review branch. 
All known bugs were submitted as part of the peer verification assignment.

# Commit(String author, String message)
Commit takes in a String representing the author and a String representing the commit message. It first creates trees from the index using createTreeFromIndex and takes the returned root tree hash. The method then opens the git/HEAD file and reads it for the parent commit. If the HEAD file is empty, the parent line is omitted. The method then writes the new commit into a StringBuilder, including the root tree hash, the current date, the author, the message, and the parent commit (if any). The StringBuilder content is written into a new file in git/objects with the hash of the StringBuilder contents as the name. That StringBuilder hash is then written into the HEAD file.

# deleteRepoFiles(String dirPath)
Takes in a String representing the path of a directory and deletes every file in it recursively. It deletes any regular diles and calls itself on any subdirectories. It then deletes the directory itself at the end.

# restoreDirectory(String dirPath, String hash)
Restores a directory from the working history by taking its hash and the directory it is in (empty string if root directory). Reads the git/objects file corresponding to the directory and restores every BLOB file in it using restoreFile. Every time it encounters a tree entry in the git/objects, it calls itself on the new directory, passing the tree hash and an updated dirPath with the name of the current directory added to it.

# restoreFile(String path, String fileHash)
Restores a BLOB file from the working history by taking its hash and its file path. The entire method is in a try catch block that prints an error message if the file doesn't exist. It then reads the BLOB file in git/objects corresponding to the hash, takes all the bytes from that file, and writes them into a new created file with the String path.

# restoreHistory(String commit)
Takes in a String representing a commit hash, accesses the git/objects file corresponding to the hash, and reads in the root tree hash from that commit. Deletes all files from the root repository using deleteRepoFiles and then calls restoreDirectory using the root hash and an empty string.

## GitWrapper written by Yari

# init()
Initializes git using Git.initialize(). Reports whether the git repository already existed.

# add(String filePath)
Stages a file using HashAndAdd.addToIndex(). Throws an error if the file is a directory or doesn't exist.

# commit(String author, String message)
Writes a new commit using Git.commit()

# checkout(String commitHash)
Restores a previous commit using Git.restoreHistory().
