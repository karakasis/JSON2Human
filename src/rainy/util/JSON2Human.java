package rainy.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Christos
 */
public class JSON2Human {
    
        private static String jsonstring;
        private static int str_len;
        private static int tab_counter = 0;
        private static boolean switch_key = false;
        private static boolean switch_value = false;
        private static boolean switch_value_nonstr = false;
        private static boolean switch_forvalue = false;
        private static boolean switch_tabbing = false;
        private static BufferedWriter bw = null;
        private static ArrayList<String> template = new ArrayList<>();
        //private static ArrayList<ArrayList<String>> templates = new ArrayList<>();
        
        public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Run with -path -code (where path is your json file and code: 0 toHuman, 1 beautify, 2 combined)");
                    System.exit(1);
        } else if(args.length == 1){
            try{
                //InputStream in = JSON2Human.class.getResourceAsStream(args[0]);
                InputStream in = new FileInputStream(args[0]);
                //default is combined.
                JSON2Human.combined(in);
            }catch(Exception e){
                e.printStackTrace();
                System.exit(2);
            }
        }
        else {
            try{
                //InputStream in = JSON2Human.class.getResourceAsStream(args[0]);
                InputStream in = new FileInputStream(args[0]);
                if(Integer.parseInt(args[1]) == 0){
                    JSON2Human.toHuman(in);
                }else if(Integer.parseInt(args[1]) == 1){
                    JSON2Human.beautify(in);
                }else if(Integer.parseInt(args[1]) == 2){
                    JSON2Human.combined(in);
                }else{
                    System.err.println("-code is not set correctly.");
                    System.exit(-1);
                }
            }catch(NumberFormatException e){
                e.printStackTrace();
                System.err.println("-code is not set correctly.");
                    System.exit(-2);
            }catch(Exception e){
                e.printStackTrace();
                    System.exit(-3);
            }
        }

        //InputStream in = JSON2Human.class.getResourceAsStream("/data.json");

    }
    
    private static void setup(InputStream in, int code){
        Scanner s = new Scanner(in).useDelimiter("\\A");
        jsonstring = s.hasNext() ? s.next() : "";
        
        jsonstring = jsonstring.replaceAll("\\r|\\n", "");
        jsonstring = jsonstring.replaceAll("\t", "");
        jsonstring = jsonstring.replaceAll("\\s{2,}", " ");
        
        if(code == 1 || code == 2){
            
        File fout = new File("beautified.json");
	FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(fout);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
            }
 
	bw = new BufferedWriter(new OutputStreamWriter(fos));
        
        }
        
    }
    
    private static void close(int code){
        if(code == 1 || code == 2){
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        if(code == 0 || code == 2){
            File fout_template = new File("json_structure.txt");
            FileOutputStream fos_template = null;
                try {
                    fos_template = new FileOutputStream(fout_template);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
                }

            bw = new BufferedWriter(new OutputStreamWriter(fos_template));

            for(String st : template){
                try {
                    bw.write(st);
                } catch (IOException ex) {
                    Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    bw.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Prints in the console and exports a txt file at the source of your project,
     * a vague structure of your JSON file. Tabs will indicate the inheritance in a tree-form.
     * Values in "value" are the keys and values after the colon (:) indicate the type of the value.
     * @param json An input stream containing a valid JSON file
     */
    public static void toHuman(InputStream json){
        setup(json,0);
        
        str_len = jsonstring.length();
        int indexof = 0;
        for(int i =0; i<str_len; i++){
            if(jsonstring.charAt(i) == '{' || jsonstring.charAt(i) == '['){
                indexof = i;
                break;

            }
        }
        tab_counter++;
        
        
        try {
            destrHum(jsonstring.charAt(indexof) , indexof);
        } catch (IOException ex) {
            Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        close(0);
    }
    /**
     * Produces a tab-indexed JSON file with line breaks when appropriate,
     * at the source of your project.
     * @param json An input stream containing a valid JSON file
     */
    public static void beautify(InputStream json){
        setup(json,1);
        
        str_len = jsonstring.length();
        int indexof = 0;
        for(int i =0; i<str_len; i++){
            if(jsonstring.charAt(i) == '{' || jsonstring.charAt(i) == '['){
                indexof = i;
                break;

            }
        }
        tab_counter++;
        
        
        try {
            destrBeaut(jsonstring.charAt(indexof) , indexof);
        } catch (IOException ex) {
            Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        close(1);
    }
    /**
     * Prints in the console and exports a txt file at the source of your project,
     * a vague structure of your JSON file. Tabs will indicate the inheritance in a tree-form.
     * Values in "value" are the keys and values after the colon (:) indicate the type of the value.
     * Also produces a tab-indexed JSON file with line breaks when appropriate,
     * at the source of your project.
     * @param json An input stream containing a valid JSON file
     */
    public static void combined(InputStream json){
        setup(json,2);
        str_len = jsonstring.length();
        int indexof = 0;
        for(int i =0; i<str_len; i++){
            if(jsonstring.charAt(i) == '{' || jsonstring.charAt(i) == '['){
                indexof = i;
                break;

            }
        }
        tab_counter++;
        
        
        try {
            destr(jsonstring.charAt(indexof) , indexof);
        } catch (IOException ex) {
            Logger.getLogger(JSON2Human.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        close(2);
    }
    
        private static int destr(char escapeChar , int index) throws IOException{
            
            String key = "";
            String value = "";
            if(escapeChar == '{'){
                escapeChar = '}';
            }else if(escapeChar == '['){
                escapeChar = ']';
            }
            for(int j = 0; j<tab_counter; j++){
                //System.out.print("\t");
                bw.write("\t");
            }
           for(int i =index; i<str_len; i++){
               
               if(jsonstring.charAt(i) == '"') {
                   switch_tabbing = ! switch_tabbing;
               }
               
               if(jsonstring.charAt(i) == '"') {
                   if(!switch_forvalue){
                    switch_key = ! switch_key;
                   }else{
                       switch_value = ! switch_value;
                   }
                   
                   if(switch_forvalue && !switch_value){
                       humanize(jsonstring.charAt(i) , key, value);
                   }
               }
               
               if(switch_forvalue && !switch_value && jsonstring.charAt(i) != ' ' 
                       && jsonstring.charAt(i) != '"' && jsonstring.charAt(i) != '{' 
                       && jsonstring.charAt(i) != '[' && !switch_value_nonstr){
                   switch_tabbing = ! switch_tabbing;
                   switch_value_nonstr = true;
               }
               
               if(switch_value_nonstr && 
                       (jsonstring.charAt(i) == ',' || jsonstring.charAt(i) == escapeChar)){
                humanize(jsonstring.charAt(i) , key, value);
                switch_forvalue = false;
                switch_value = false;
                switch_value_nonstr = false;
                switch_key = false;
                key = "";
                value = "";
                switch_tabbing = false;
                
                if(jsonstring.charAt(i) == escapeChar){
                    tab_counter--;
                    bw.newLine();
                    //System.out.println();
                    for(int j = 0; j<tab_counter; j++){
                        //System.out.print("\t");
                        bw.write("\t");
                    }
                    //System.out.print(jsonstring.charAt(i));
                    bw.write(" "+jsonstring.charAt(i));
                    return i;
                }
               }
               
               
               
               if(!switch_tabbing){
                   if(jsonstring.charAt(i) == ':'){
                        switch_forvalue = ! switch_forvalue;
                        bw.write(jsonstring.charAt(i));
                    }else if(jsonstring.charAt(i) == '{' || jsonstring.charAt(i) == '['){
                        humanize(jsonstring.charAt(i) , key, value);
                        key = "";
                        value = "";
                        //System.out.print(jsonstring.charAt(i));
                        bw.newLine();
                        for(int j = 0; j<tab_counter; j++){
                            //System.out.print("\t");
                            bw.write("\t");
                        }
                        bw.write(" "+jsonstring.charAt(i));
                        //System.out.println(); 
                        bw.newLine();
                        tab_counter++;
                        i = destr(jsonstring.charAt(i) , ++i);
                    }else if(jsonstring.charAt(i) == ','){
                        switch_forvalue = false;
                        switch_value = false;
                        switch_value_nonstr = false;
                        switch_key = false;
                        key = "";
                        value = "";
                        //System.out.print(jsonstring.charAt(i));
                        bw.write(jsonstring.charAt(i));
                        bw.newLine();
                        //System.out.println();
                        for(int j = 0; j<tab_counter; j++){
                            //System.out.print("\t");
                            bw.write("\t");
                        }
                    }else if(jsonstring.charAt(i) == escapeChar){
                        //System.out.println();
                        tab_counter--;
                        bw.newLine();
                        //System.out.println();
                        for(int j = 0; j<tab_counter; j++){
                            //System.out.print("\t");
                            bw.write("\t");
                        }
                        //System.out.print(jsonstring.charAt(i));
                        bw.write(" "+jsonstring.charAt(i) + " ");
                        return i;
                    }else{
                        //System.out.print(jsonstring.charAt(i));
                        bw.write(jsonstring.charAt(i));
                    }
                }else{
                   //System.out.print(jsonstring.charAt(i));
                   if(switch_value)
                    value+=jsonstring.charAt(i);
                   else if(switch_key)
                    key+=jsonstring.charAt(i);
                   else if(switch_value_nonstr)
                    value+=jsonstring.charAt(i);
                   bw.write(jsonstring.charAt(i));
               }
               
                
            }
           return 0;
        }
        
        private static int destrBeaut(char escapeChar , int index) throws IOException{
            
            if(escapeChar == '{'){
                escapeChar = '}';
            }else if(escapeChar == '['){
                escapeChar = ']';
            }
            for(int j = 0; j<tab_counter; j++){
                //System.out.print("\t");
                bw.write("\t");
            }
           for(int i =index; i<str_len; i++){
             
               if(jsonstring.charAt(i) == '"') {
                   switch_tabbing = ! switch_tabbing;
               }
               
               if(!switch_tabbing){
                if(jsonstring.charAt(i) == '{' || jsonstring.charAt(i) == '['){
                     bw.newLine();
                     for(int j = 0; j<tab_counter; j++){
                         //System.out.print("\t");
                         bw.write("\t");
                     }
                     bw.write(" "+jsonstring.charAt(i));
                     //System.out.println(); 
                     bw.newLine();
                     tab_counter++;
                     i = destrBeaut(jsonstring.charAt(i) , ++i);
                 }else if(jsonstring.charAt(i) == ','){
                     bw.write(jsonstring.charAt(i));
                     bw.newLine();
                     //System.out.println();
                     for(int j = 0; j<tab_counter; j++){
                         //System.out.print("\t");
                         bw.write("\t");
                     }
                 }else if(jsonstring.charAt(i) == escapeChar){
                     //System.out.println();
                     tab_counter--;
                     bw.newLine();
                     //System.out.println();
                     for(int j = 0; j<tab_counter; j++){
                         //System.out.print("\t");
                         bw.write("\t");
                     }
                     //System.out.print(jsonstring.charAt(i));
                     bw.write(" "+jsonstring.charAt(i) + " ");
                     return i;
                 }else{
                     //System.out.print(jsonstring.charAt(i));
                     bw.write(jsonstring.charAt(i));
                 }
               }else{
                   bw.write(jsonstring.charAt(i));
               }
         }
           return 0;
        }
        
        private static int destrHum(char escapeChar , int index) throws IOException{
            
            String key = "";
            String value = "";
            if(escapeChar == '{'){
                escapeChar = '}';
            }else if(escapeChar == '['){
                escapeChar = ']';
            }
           for(int i =index; i<str_len; i++){
               
               if(jsonstring.charAt(i) == '"') {
                   switch_tabbing = ! switch_tabbing;
               }
               
               if(jsonstring.charAt(i) == '"') {
                   if(!switch_forvalue){
                    switch_key = ! switch_key;
                   }else{
                       switch_value = ! switch_value;
                   }
                   
                   if(switch_forvalue && !switch_value){
                       humanize(jsonstring.charAt(i) , key, value);
                   }
               }
               
               if(switch_forvalue && !switch_value && jsonstring.charAt(i) != ' ' 
                       && jsonstring.charAt(i) != '"' && jsonstring.charAt(i) != '{' 
                       && jsonstring.charAt(i) != '[' && !switch_value_nonstr){
                   switch_tabbing = ! switch_tabbing;
                   switch_value_nonstr = true;
               }
               
               if(switch_value_nonstr && 
                       (jsonstring.charAt(i) == ',' || jsonstring.charAt(i) == escapeChar)){
                humanize(jsonstring.charAt(i) , key, value);
                switch_forvalue = false;
                switch_value = false;
                switch_value_nonstr = false;
                switch_key = false;
                key = "";
                value = "";
                switch_tabbing = false;
                
                if(jsonstring.charAt(i) == escapeChar){
                    tab_counter--;
                    return i;
                }
               }
               
               
               
               if(!switch_tabbing){
                   if(jsonstring.charAt(i) == ':'){
                        switch_forvalue = ! switch_forvalue;
                    }else if(jsonstring.charAt(i) == '{' || jsonstring.charAt(i) == '['){
                        humanize(jsonstring.charAt(i) , key, value);
                        key = "";
                        value = "";
                        //System.out.print(jsonstring.charAt(i));
                        
                        tab_counter++;
                        i = destrHum(jsonstring.charAt(i) , ++i);
                    }else if(jsonstring.charAt(i) == ','){
                        switch_forvalue = false;
                        switch_value = false;
                        switch_value_nonstr = false;
                        switch_key = false;
                        key = "";
                        value = "";
                    }else if(jsonstring.charAt(i) == escapeChar){
                        //System.out.println();
                        tab_counter--;
                        return i;
                    }
                }else{
                   //System.out.print(jsonstring.charAt(i));
                   if(switch_value)
                    value+=jsonstring.charAt(i);
                   else if(switch_key)
                    key+=jsonstring.charAt(i);
                   else if(switch_value_nonstr)
                    value+=jsonstring.charAt(i);
               }
               
                
            }
           return 0;
        }
        
        private static void humanize(char escapeChar, String key, String value){
            String templa = "";
            if(value.isEmpty() && !key.isEmpty() && (escapeChar == '{' || escapeChar == '[')){
                switch_forvalue = false;
                key = key.trim().substring(1);
                for(int j = 0; j<tab_counter; j++){
                    System.out.print("\t");
                    templa += "\t";
                    //bw.write("\t");
                }
                if(escapeChar == '{'){
                    System.out.println(" \""+ key+"\" : Object");
                    templa += " \""+ key+"\" : Object";
                }else{
                    System.out.println(" \""+ key+"\" : Array");
                    templa += " \""+ key+"\" : Array";
                }
                template.add(templa);
            }else if(!value.isEmpty() && !key.isEmpty()){
                switch_forvalue = false;
                key = key.trim().substring(1);
                
                for(int j = 0; j<tab_counter; j++){
                    System.out.print("\t");
                    templa += "\t";
                    //bw.write("\t");
                }
                boolean found = false;
                if(value.charAt(0) == '"')
                    //value = value.trim().substring(1);
                    value = "String";
                else{
                    if(!value.equals("null")){
                        if(!found)
                         try{
                              int parseInt = Integer.parseInt(value);
                              value = "integer";
                              found = true;

                          }catch(NumberFormatException e){

                          } 
                        if(!found)
                         try{
                              int parseInt = Integer.parseInt(value);
                              value = "integer";
                              found = true;

                          }catch(NumberFormatException e){

                          }
                        if(!found)
                         try{
                              long parseInt = Long.parseLong(value);
                              value = "long";
                              found = true;
                          }catch(NumberFormatException e){

                          }
                        if(!found)
                         try{
                              float parseInt = Float.parseFloat(value);
                              value = "float";
                              found = true;

                          }catch(NumberFormatException e){

                          }
                        if(!found)
                         try{
                              Double parseInt = Double.parseDouble(value);
                              value = "Double";
                              found = true;

                          }catch(NumberFormatException e){

                          }
                        if(!found)
                         try{
                              boolean parseInt = Boolean.parseBoolean(value);
                              value = "boolean";
                              found = true;
                          }catch(NumberFormatException e){

                          }
                     }
                }
                
                  
                
                System.out.println(" \""+ key+"\" : "+value+" ");
                templa += " \""+ key+"\" : "+value+" ";
                template.add(templa);
            }else if(value.isEmpty() && key.isEmpty()){
                switch_forvalue = false;
                for(int j = 0; j<tab_counter; j++){
                    System.out.print("\t");
                    templa += "\t";
                    //bw.write("\t");
                }
                if(escapeChar == '{'){
                    System.out.println("Object : ");
                    templa += "Object : ";
                }else{
                    System.out.println("Array : ");
                    templa += "Array : ";
                }
                template.add(templa);
            }
            
        }
        
}
