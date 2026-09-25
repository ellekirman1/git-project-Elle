import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class direct{
        public static void main(String[] args) {
            initate();

    }
    public static void initate(){
    

            int x = 0;
            
            File root = new File("git/");
            if (!root.exists()) {
                root.mkdir();
                System.out.println("make root directory");
                
            }else{
                x+=1;
            }
            

            File objectsDir = new File("git/objects/");
            if (!objectsDir.exists()) {
                objectsDir.mkdir();
                System.out.println("make object directory");

            }else{
                x+=1;
            }

            File index = new File("git/index.txt");
            try {
                index.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(!index.exists()){
                x+=1;
            }
            System.out.println("make index file");
            
            File HEAD = new File("git/HEAD.txt");
            try {
                HEAD.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(!HEAD.exists()){
                x+=1;
            }
            System.out.println("make HEAD file");
            if(x ==4){
                System.out.println("Git Repository Already Exists");

            }
            else{
                System.out.println("Git Repository Created\n" + //
                                        "");
            }

        

    }

       


}