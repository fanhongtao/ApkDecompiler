/*
 * This file is in PUBLIC DOMAIN. You can use it freely. No guarantee.
 */
package org.fanhongtao.decompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Fan Hongtao &ltfanhongtao@gmail.com&gt
 */
public class Decompiler {
    private Map<String, String> resMap = new HashMap<String, String>();
    
    public void decompile(String projectDir, String RFileName) throws IOException {
        readRFile(projectDir, RFileName);
        addExtraInfo();
        decodeResourceFiles(projectDir);
    }
    
    
    private final static String CLASS_DEFINE = "public static final class ";
    private final static String RESOURCE_DEFINE = "public static final int ";
    private void readRFile(String dir, String RFileName) throws IOException {
        File rFile = new File(RFileName);
        if (!rFile.exists()) {
            rFile = new File(dir + "/" + RFileName);
        }
        if (!rFile.exists()) {
            throw new RuntimeException(RFileName + " does not exist");
        }
        
        String line = null;
        String className=null;
        String [] array = null;
        String resourceName = null;
        String resourceValue = null;
        int index;
        BufferedReader br = new BufferedReader(new FileReader(rFile));
        while ((line = br.readLine()) != null) {
            // Example: public static final class id
            if ((index = line.indexOf(CLASS_DEFINE)) != -1) {
                className = line.substring(index + CLASS_DEFINE.length()).trim();
                continue;
            }
            
            if ((index = line.indexOf(RESOURCE_DEFINE)) != -1) {
                // Example: public static final int black = 0x7f060002;
                array = line.substring(index + RESOURCE_DEFINE.length()).trim().split("[=;]");
                if (array.length != 2) {
                    System.err.println("Bad line: " + line);
                    continue;
                }
                resourceName = array[0].trim();
                resourceValue = array[1].trim().substring(2).toUpperCase();
                if (className.equals("id")) {
                    resourceName = "+id/" + resourceName;
                } else {
                    resourceName = className + "/" + resourceName;
                }
                resMap.put(resourceValue, resourceName);
                System.out.println(resourceValue + ", " + resourceName);
                continue;
            }
        }
    }
    
    private void addExtraInfo() {
        //TODO:
        resMap.put("android:fadingEdge=\"0x00000000\"", "android:fadingEdge=\"none\"");
        
        resMap.put("android:gravity=\"0x00000010\"", "android:gravity=\"center_vertical\"");
        resMap.put("android:gravity=\"0x00000011\"", "android:gravity=\"center\"");
        
        resMap.put("android:layout_gravity=\"0x00000001\"", "android:layout_gravity=\"center_horizontal\"");
        resMap.put("android:layout_gravity=\"0x00000003\"", "android:layout_gravity=\"left\"");
        resMap.put("android:layout_gravity=\"0x00000005\"", "android:layout_gravity=\"right\"");
        resMap.put("android:layout_gravity=\"0x00000010\"", "android:layout_gravity=\"center_vertical\"");
        resMap.put("android:layout_gravity=\"0x00000011\"", "android:layout_gravity=\"center\"");
        resMap.put("android:layout_gravity=\"0x00000030\"", "android:layout_gravity=\"top\"");
        
        resMap.put("android:layout_gravity=\"0x00000015\"", "android:layout_gravity=\"right|center_vertical\"");
        resMap.put("android:layout_gravity=\"0x00000031\"", "android:layout_gravity=\"top|center_horizontal\"");
        resMap.put("android:layout_gravity=\"0x00000035\"", "android:layout_gravity=\"top|right|center\"");
        
        resMap.put("android:layout_height=\"-1\"", "android:layout_height=\"fill_parent\"");
        resMap.put("android:layout_height=\"-2\"", "android:layout_height=\"wrap_content\"");
        
        resMap.put("android:layout_width=\"-1\"", "android:layout_width=\"fill_parent\"");
        resMap.put("android:layout_width=\"-2\"", "android:layout_width=\"wrap_content\"");
        
        resMap.put("android:orientation=\"0\"", "android:orientation=\"horizontal\"");
        resMap.put("android:orientation=\"1\"", "android:orientation=\"vertical\"");
        
        // TODO
        resMap.put("android:scrollbars=\"0x00000000\"", "android:scrollbars=\"none\"");
        
        // TODO:
        resMap.put("android:shape=\"3\"", "android:shape=\"ring\"");
        
        // TODO:
        resMap.put("android:type=\"2\"", "android:type=\"sweep\"");
        
        // TODO:
        resMap.put("android:textStyle=\"0x00000001\"", "android:textStyle=\"bold\"");
        
        resMap.put("android:visibility=\"0\"", "android:visibility=\"visible\"");
        resMap.put("android:visibility=\"1\"", "android:visibility=\"invisible\"");
        resMap.put("android:visibility=\"2\"", "android:visibility=\"gone\"");
    }
    
    private void decodeResourceFiles(String projectDir) throws IOException {
        String resDir = projectDir + "/res";
        List<File> fileList = listFiles(new File(resDir), ".xml");
        
        StringBuilder fileData = new StringBuilder(8 * 1024);
        for (File file : fileList) {
            System.out.println("Process file: " + file.getName());
            readFile(file, fileData);
            replaceResIDs(fileData);
            writeFile(file, fileData);
        }
    }
    
    /**
     * Return a list of files in the directory <i>dir</i> and it's sub-directories whose name end with <i>suffix</i>
     * @param dir Searching directory
     * @param suffix File name suffix
     * @return A list of files. The list will be empty if none of the file's name end with <i>suffix</i>
     */
    private List<File> listFiles(File dir, String suffix) {
        List<File> fileList = new ArrayList<File>();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll(listFiles(file, suffix));
            } else if (file.getName().endsWith(suffix)) {
                fileList.add(file);
            }
        }
        return fileList;
    }
    
    private void readFile(File file, StringBuilder fileData) throws java.io.IOException {
        fileData.delete(0, fileData.length());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            fileData.append(buf, 0, numRead);
        }
        reader.close();
    }

    private void replaceResIDs(StringBuilder original) {
        Set<Entry<String, String>> entrySet = resMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String resValue = entry.getKey();
            String resId = entry.getValue();
            int index;
            int length = resValue.length();
            while ((index = original.indexOf(resValue)) != -1) {
                original.replace(index, index + length, resId);
            }
        }
    }
    
    private void writeFile(File file, StringBuilder fileData) throws java.io.IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(fileData.toString());
        writer.close();
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: decompiler project_dir   R_file");
            System.out.println("Example:");
            System.out.println("\t decompiler D:/temp   class/com/foo/R.java");
            return;
        }
//        
//        String dir = args[0];
//        String R_file = args[1];
//        
//        System.out.println(R_file);
        try {
            new Decompiler().decompile(args[0], args[1]);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
