package com.mycompany.bp_5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import org.bytedeco.javacpp.Loader;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import org.bytedeco.javacpp.opencv_objdetect;


public class Main {

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws IOException, Exception  {
        classifierSerialization clSer = new classifierSerialization();
//        clSer.serializeClisifier();
////        opencv_objdetect.CvHaarClassifierCascade classifier = classifierSerialization.deseralizace();
////        clSer.toJSON();
//        opencv_objdetect.CvHaarClassifierCascade classifier = clSer.deseralizace();
        FaceDetection faceDetector = new FaceDetection();
        faceDetector.faceDetecting();
        
    }
    
//    
//    void serializeClisifier() throws MalformedURLException, IOException{
//        URL url = new URL("https://raw.github.com/Itseez/opencv/2.4.0/data/haarcascades/haarcascade_frontalface_alt.xml");
//        File file = Loader.extractResource(url, null, "classifier", ".xml");
//        file.deleteOnExit();
//        String classifierName = file.getAbsolutePath();
//
//        // Preload the opencv_objdetect module to work around a known bug.
//        Loader.load(opencv_objdetect.class);
//         // We can "cast" Pointer objects by instantiating a new object of the desired class.
//        opencv_objdetect.CvHaarClassifierCascade classifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(classifierName));
//        if(serializace(classifier))
//            System.out.println("Clasifire succesed serialized");
//        else
//            System.out.println("Clasifire serialization faild");
//    }
//    
//    
//     boolean serializace(Object objectToSerialize) {
//        try {
//            String path = "classifier.out";
//            FileOutputStream fileOut = new FileOutputStream(path);
//            ObjectOutputStream out = new ObjectOutputStream(fileOut);
//            out.writeObject(objectToSerialize);
//            out.close();
//            fileOut.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
}
