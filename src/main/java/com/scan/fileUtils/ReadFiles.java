package com.scan.fileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计当前项目所有的getBeans和getBeansOfType(
 */
public class ReadFiles {
    private static final Logger logger = LoggerFactory.getLogger(ReadFiles.class);

    @Override
    public String toString() {
        return super.toString();
    }

    public static void readfile(String filepath) throws FileNotFoundException, IOException {
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                String fileName = file.getName();
                if ("java".equals(fileName.substring(fileName.lastIndexOf(".") + 1))) {
                    readLine(file.toPath());
                }
            } else if (file.isDirectory()) {
                if (!file.getName().equals(".git")) {
                    String[] filelist = file.list();
                    for (int i = 0; i < filelist.length; i++) {
                        File readfile = new File(filepath + "\\" + filelist[i]);
                        if (!readfile.isDirectory()) {
                            String fileName = readfile.getName();
                            if ("java".equals(fileName.substring(fileName.lastIndexOf(".") + 1))) {
                                readLine(readfile.toPath());
                            }
                        } else if (readfile.isDirectory()) {
                            readfile(filepath + "\\" + filelist[i]);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("readfile()   Exception:" + e.getMessage());
        }
    }


    public static void readLine(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            List<String> head = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                int startType = line.indexOf("getBeansOfType(");
                if (-1 != startType) {
                    startType = startType + 15;
                    int endType = startType;
                    for (; endType < line.length(); ++endType) {
                        if (line.charAt(endType) == ')') {
                            break;
                        }
                    }
                    String value = line.substring(startType, endType);
                    int index = value.indexOf("class");
                    if (-1 != index) {
//                        String className = type.get(value);
//                        if (null == className) {
                        String trueName = value.substring(0, index - 1);
                        List<String> headStrings = head.stream().filter(h -> -1 != h.indexOf("." + trueName + ";")).collect(Collectors.toList());

                        if (!headStrings.isEmpty()) {
                            boolean importClass = false;
                            String headString = headStrings.get(0);
                            int importIndex = headString.indexOf("import");
                            if (-1 != importIndex) {
                                type.add(new Beans(value, headString.substring(importIndex + 7, headString.length() - 1) + ".class"));
                                importClass = true;
                            }
                            if (!importClass) {
                                headString = headStrings.get(0);
                                int packageIndex = headString.indexOf("package");
                                if (-1 != packageIndex) {
                                    type.add(new Beans(value, headString.substring(packageIndex + 8, headString.length() - 1) + ".class"));
                                    importClass = true;
                                }
                            }
                            if (!importClass) {
                                type.add(new Beans(value, value));
                            }
                        } else {
                            String headString = head.get(0);
                            int packageIndex = headString.indexOf("package");
                            if (-1 != packageIndex) {
                                type.add(new Beans(value, headString.substring(packageIndex + 8, headString.length() - 1) + "." + value));
                            }
                        }
//                        }
                    } else {
                        String absolutePath = path.toFile().getAbsolutePath();
                        typeNo.add(new Beans(value, absolutePath));
                    }
                }

                int startName = line.indexOf("getBean(");
                if (-1 != startName) {
                    startName = startName + 8;
                    int endName = startName;
                    for (; endName < line.length(); ++endName) {
                        if (line.charAt(endName) == ')') {
                            break;
                        }
                    }
                    String value = line.substring(startName, endName);
                    if (value.length() > 2 && value.charAt(0) == '"') {
                        value = value.substring(1, value.length() - 1);
                        if (!name.contains(value)) {
                            name.add(new Beans(value, value));
                        }
                    } else {
                        String absolutePath = path.toFile().getAbsolutePath();
                        nameNo.add(new Beans(value, absolutePath));
                    }

                }
                if (-1 != line.indexOf("package") || -1 != line.indexOf("import")) {
                    head.add(line);
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public static void writeFile(String name, Collection<Beans> value) {
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

    public static void writeFileString(String name, Collection<String> value) {
        try {
            FileWriter writer = new FileWriter(name);
            value.forEach(a -> {
                try {
                    writer.write(a);
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

    static List<Beans> name;
    static List<Beans> nameNo;
    static List<Beans> type;
    static List<Beans> typeNo;

    public static void main(String[] args) {
        name = new LinkedList<>();
        nameNo = new LinkedList<>();
        type = new LinkedList<>();
        typeNo = new LinkedList<>();
        ms();

        name = new LinkedList<>();
        nameNo = new LinkedList<>();
        type = new LinkedList<>();
        typeNo = new LinkedList<>();
        v5();
    }

    public static void ms() {
        try {
            readfile("D:/CODE/ctp-microservice");

            LinkedHashSet<String> nameSet = name.stream().map(Beans::getPack).collect(Collectors.toCollection(LinkedHashSet::new));
            System.out.println("name:" + nameSet.size());
            LinkedList<String> nameList = new LinkedList<>(nameSet);
            nameList.sort(String::compareTo);
            writeFileString("ms-name.txt", nameList);
            nameNo.sort(Beans::compareTo);
            writeFile("ms-nameNo.txt", nameNo);

            LinkedHashSet<String> typeSet = type.stream().map(Beans::getPack).collect(Collectors.toCollection(LinkedHashSet::new));
            System.out.println("type:" + typeSet.size());
            LinkedList<String> typeList = new LinkedList<>(typeSet);
            typeList.sort(String::compareTo);
            writeFileString("ms-type.txt", typeList);
            typeNo.sort(Beans::compareTo);
            writeFile("ms-typeNo.txt", typeNo);
        } catch (IOException ex) {
        }
        System.out.println("ms-ok");
    }

    public static void v5() {
        try {
            readfile("D:/CODE/V5");

            LinkedHashSet<String> nameSet = name.stream().map(Beans::getPack).collect(Collectors.toCollection(LinkedHashSet::new));
            System.out.println("name:" + nameSet.size());
            LinkedList<String> nameList = new LinkedList<>(nameSet);
            nameList.sort(String::compareTo);
            writeFileString("v5-name.txt", nameList);
            nameNo.sort(Beans::compareTo);
            writeFile("v5-nameNo.txt", nameNo);

            LinkedHashSet<String> typeSet = type.stream().map(Beans::getPack).collect(Collectors.toCollection(LinkedHashSet::new));
            System.out.println("type:" + typeSet.size());
            LinkedList<String> typeList = new LinkedList<>(typeSet);
            typeList.sort(String::compareTo);
            writeFileString("v5-type.txt", typeList);
            typeNo.sort(Beans::compareTo);
            writeFile("v5-typeNo.txt", typeNo);
        } catch (IOException ex) {
        }
        System.out.println("v5-ok");
    }

    static class Beans implements Comparable<Beans> {
        private String value;
        private String pack;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPack() {
            return pack;
        }

        public void setPack(String pack) {
            this.pack = pack;
        }

        public Beans() {
        }

        public Beans(String value, String pack) {
            this.value = value;
            this.pack = pack;
        }

        @Override
        public String toString() {
            return value + "  -->  " + pack;
        }

        @Override
        public int compareTo(Beans o) {
            int i = value.compareTo(o.value);
            if (0 != i) {
                return i;
            } else {
                return pack.compareTo(o.pack);
            }
        }
    }
}