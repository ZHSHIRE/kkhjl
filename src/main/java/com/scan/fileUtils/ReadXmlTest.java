package com.scan.fileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.Attribute;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计当前项目所有xml注入的bean
 */
public abstract  class ReadXmlTest {
    public static LinkedHashSet<XmlData> xmlDataList;
    public static void main(String[] args) {
        xmlDataList = new LinkedHashSet<>();
        testFileDirOrName("D:\\CODE\\V5");
        writeFile("xmlBean.txt", xmlDataList);
//        xmlDataList = new LinkedList<>();
    }

    public static void writeFile(String name, Collection<XmlData> value) {
        try {
            FileWriter writer = new FileWriter(name);
            value.forEach(a -> {
                try {
                    writer.write(a.toString());
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testFileDirOrName(String path) {
        File dirFile = new File(path);
        if (dirFile.exists()) {
            File[] files = dirFile.listFiles();
            if (files != null) {
                for (File fileChildDir : files) {
                    if (fileChildDir.isDirectory()) {
                        if(fileChildDir.getName().equals(".git"))
                            continue;
                        //通过递归的方式,可以把目录中的所有文件全部遍历出来

                        testFileDirOrName(fileChildDir.getAbsolutePath());
                    }
                    if (fileChildDir.isFile()) {
                        //文件后缀名
                        if(fileChildDir.getName().endsWith(".xml")) {
                            if (fileChildDir.getName().equals("pom.xml") || fileChildDir.getName().endsWith(".hbm.xml"))
                                continue;
                            //传递文件路径
                            try {
                                getidandclass(fileChildDir.toString());
                            } catch (Exception e) {
                                System.out.println(fileChildDir.toString());
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("你想查找的文件不存在");
        }
    }

           //利用框架读取xml内容，并解析输出
    private static void getidandclass(String path) throws SAXException {
        SAXReader reader = new SAXReader();
//        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        //2.读取xml文件，获得Document对象
        Document doc = null;
        try {
            doc = reader.read(new File(path));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //3.获取根元素
        Element root = doc.getRootElement();
        Iterator<Element> it = root.elementIterator("bean");
        System.out.println("扫描xml:" + path);
        while (it.hasNext()) {
            Element e = it.next();
            Attribute id = e.attribute("id");
            Attribute name = e.attribute("name");
            Attribute targetClass = e.attribute("class");

            XmlData xmlData = new XmlData();

            //判断id元素不为空不为空
            if (id != null) {
                //输出ID元素
                xmlData.setId(id.getStringValue());
            }
            if (name != null) {
                //输出name元素
                xmlData.setName(name.getStringValue());
            }
            if (targetClass != null) {
                xmlData.setTargetClass(targetClass.getStringValue());
            }
            xmlDataList.add(xmlData);
        }
    }

    public static class XmlData implements Comparable<XmlData>{
        private String id;
        private String name;
        private String targetClass;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTargetClass() {
            return targetClass;
        }

        public void setTargetClass(String targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public String toString() {
            //判断id元素不为空不为空
            StringBuilder sb = new StringBuilder();
            if (id != null) {
                //输出ID元素
                sb.append("id = ").append(id);
            }
            if (name != null) {
                //输出name元素
                if(null != id) {
                    sb.append("  \\  ");
                }
                sb.append("name = ").append(name);
            }
            if (targetClass != null) {
                sb.append("  -->  class = ").append(targetClass);
            }
            return sb.toString();
        }

        @Override
        public int compareTo(XmlData o) {
            if(null == id) {
                return name.compareTo(o.name);
            } else {
                return id.compareTo(o.id);
            }
        }
    }
}



/*
<!-- https://mvnrepository.com/artifact/dom4j/dom4j -->
<dependency>
<groupId>dom4j</groupId>
<artifactId>dom4j</artifactId>
<version>1.6</version>
</dependency>
*/
