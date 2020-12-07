import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindBeanUtils {


    public static void main(String[] args) {
        File f= new File("E:\\source_code\\gitlab_source\\ctp-microservice-bpm\\ctp-microservice-bpm\\cap-microservice-core\\src\\main\\java");
        Map<String,String> result = new HashMap<>(2048);
        testLoad(f,result);
    }
    private static void testLoad(File path,Map<String,String> result){
        if(path.isFile()){
            result.putAll(findBean(path));
        } else {
            File[] child = path.listFiles();
            for(File f : child){
                testLoad(f,result);
            }
        }
    }
    private static Map<String,String> findBean(File file){
        InputStream is = null;
        try {
            Map<String,String> imports = new HashMap<>(1024);
            is = new FileInputStream(file);
            List<String> content =  IOUtils.readLines(is,"UTF-8");
            for(int x = 0;x<content.size();x ++){
                String s = content.get(x).trim();
                if(s.startsWith("import")){
                    String[] className = s.split("[.]");
                    imports.put(className[className.length -1].trim(),s.trim());
                    continue;
                }
                if(s.contains("@Inject")){
                    String privateContent = content.get(x + 1).trim();
                    System.out.println(privateContent);
                    //"private CAPBatchImportUniqueManager capBatchImportUniqueManager;"
                }
            }
        }catch (Exception e){

        }finally {
            if(is != null) {
                try {
                    is.close();
                }catch (Exception e){

                }

            }
        }
        return Collections.emptyMap();
    }

}
