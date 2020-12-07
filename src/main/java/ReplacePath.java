import org.apache.commons.io.IOUtils;
import java.io.*;

public class ReplacePath {
    public static void main(String[] args) {
        String path="E:\\idea_tomcat\\8.0sp1\\tomcat\\webapps\\seeyon";
        String path1="E:\\idea_tomcat\\trunk\\apache-tomcat-8.0.37\\webapps";

        File f = new File(path1);
        replace(f,"/STATIC_PATH","/seeyon");
    }
    private static void replace(File f, String oldStr,String newStr){
        if(f.isFile()){
            if(f.getName().endsWith("html") || f.getName().endsWith("css") ){
                try {
                    Reader reader = new FileReader(f);
                    String content = IOUtils.toString(reader);
                    reader.close();
                    content = content.replaceAll(oldStr,newStr);
                    OutputStream os = new FileOutputStream(f);
                    IOUtils.write(content,os,"UTF-8");
                    os.flush();
                    os.close();
                }catch (Exception e){

                }

            }
        } else {
            File[] files = f.listFiles();
            for (File file :files){
                replace(file,oldStr,newStr);
            }
        }
    }
}
