import org.apache.commons.io.IOUtils;

import java.io.*;

public class FindContent {
    public static void main(String[] args) {
        String path="E:\\idea_tomcat\\trunk\\apache-tomcat-8.0.37\\webapps\\seeyon\\WEB-INF\\cfgHome";
        File f = new File(path);
        find(f,"/fileUpload.do ","xml");
    }
    private static void find(File f, String content,String profix){
        if(f.isFile()){
            if(f.getName().endsWith(profix) ){
                try {
                    Reader reader = new FileReader(f);
                    String fileContent = IOUtils.toString(reader);
                    if(fileContent.contains(content)){
                        System.out.println(f.getPath());
                    }
                    reader.close();
                }catch (Exception e){

                }

            }
        } else {
            File[] files = f.listFiles();
            for (File file :files){
                find(file,content,profix);
            }
        }
    }
}
