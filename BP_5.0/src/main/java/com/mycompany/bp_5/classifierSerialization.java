package com.mycompany.bp_5;

import com.alibaba.fastjson.JSON;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.bytedeco.javacpp.Loader;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import org.bytedeco.javacpp.opencv_objdetect;
import java.io.Serializable;

/**
 *
 * @author Comp
 */
public class classifierSerialization {
    
    
    public opencv_objdetect.CvHaarClassifierCascade ClassifierCascade;
    String JsonString;
    
    public classifierSerialization() {}
    
    public classifierSerialization(opencv_objdetect.CvHaarClassifierCascade classifierCascade) {
        ClassifierCascade = classifierCascade;
    }
    
    void serializeClisifier() throws MalformedURLException, IOException{
        URL url = new URL("https://raw.github.com/Itseez/opencv/2.4.0/data/haarcascades/haarcascade_frontalface_alt.xml");
        File file = Loader.extractResource(url, null, "classifier", ".xml");
        file.deleteOnExit();
        String classifierName = file.getAbsolutePath();

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);
         // We can "cast" Pointer objects by instantiating a new object of the desired class.
        opencv_objdetect.CvHaarClassifierCascade classifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(classifierName));
        
        ClassifierCascade = classifier;
        if(toJSON() && serializace())
            System.out.println("Clasifire succesed serialized");
        else
            System.out.println("Clasifire serialization faild");
        ClassifierCascade = null;
    }
    
    
    boolean serializace() {
        try {
            String path = "classifierClass.out";
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(JsonString);
            out.close();
            fileOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
     
    boolean toJSON() {
          // convert to json
        JsonString = JSON.toJSONString(this);
        System.out.println("json " + JsonString); // print "json {"message":"Hi","place":{"name":"World"}}"
        return true;
    }
     
        
    public opencv_objdetect.CvHaarClassifierCascade fromJSON() {
        opencv_objdetect.CvHaarClassifierCascade vystup = null;
        classifierSerialization chidClassifierSerialization = JSON.parseObject(JsonString, classifierSerialization.class);
        vystup = chidClassifierSerialization.ClassifierCascade;
        return vystup;
    }
     
    opencv_objdetect.CvHaarClassifierCascade deseralizace() throws Exception {
        String way = "classifierClass";
        opencv_objdetect.CvHaarClassifierCascade vystup = null;
        classifierSerialization clasifireS = null;
        try {
            way += ".out";
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(way));
            JsonString = (String) in.readObject();
            vystup = fromJSON();
        } catch (Exception e) {
            System.out.println("Deserialization faild");
        }
        return vystup;
    }
}