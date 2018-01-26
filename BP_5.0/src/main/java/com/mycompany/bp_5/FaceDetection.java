package com.mycompany.bp_5;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.bytedeco.javacpp.Loader;
import static org.bytedeco.javacpp.helper.opencv_imgproc.cvDrawContours;
import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import static org.bytedeco.javacpp.opencv_calib3d.cvRodrigues2;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvApproxPoly;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourPerimeter;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvFillConvexPoly;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;
import static org.bytedeco.javacpp.opencv_imgproc.cvWarpPerspective;
import org.bytedeco.javacpp.opencv_objdetect;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;



public class FaceDetection {

    /**
     *
     * @param args
     * @throws Exception
     */
    public void  faceDetecting() throws MalformedURLException, FrameGrabber.Exception, IOException {
//        URL url = new URL("https://raw.github.com/Itseez/opencv/2.4.0/data/haarcascades/haarcascade_frontalface_alt.xml");
//        File file = Loader.extractResource(url, null, "classifier", ".xml");
//        file.deleteOnExit();
//        String classifierName1 = file.getAbsolutePath();
        String classifierName = "haarcascade_frontalface_alt.xml";

        // Preload the opencv_objdetect module to work around a known bug.
//        Loader.load(opencv_objdetect.class);
//          We can "cast" Pointer objects by instantiating a new object of the desired class.
        opencv_objdetect.CvHaarClassifierCascade classifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \".");
            System.exit(1);
        }
        
        
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();
       
        
        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, or OpenCV).
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

        // FAQ about IplImage and Mat objects from OpenCV:
        // - For custom raw processing of data, createBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, or vice versa, we can chain calls to
        //   Java2DFrameConverter and OpenCVFrameConverter, one after the other.
        // - Java2DFrameConverter also has static copy() methods that we can use to transfer
        //   data more directly between BufferedImage and IplImage or Mat via Frame objects.
        opencv_core.IplImage grabbedImage = converter.convert(grabber.grab());
        int width  = grabbedImage.width();
        int height = grabbedImage.height();
        opencv_core.IplImage grayImage    = opencv_core.IplImage.create(width, height, IPL_DEPTH_8U, 1);
        opencv_core.IplImage rotatedImage = grabbedImage.clone();
        
        // The OpenCVFrameRecorder class simply uses the CvVideoWriter of opencv_videoio,
        // but FFmpegFrameRecorder also exists as a more versatile alternative.
//        FrameRecorder recorder = FrameRecorder.createDefault("output.avi", width, height);
//        recorder.start();
        
        
        
        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma()/grabber.getGamma());
        
        
        
        // Objects allocated with a create*() or clone() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling release().
        // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();
        
        
        
        
        
        // Let's create some random 3D rotation...
        opencv_core.CvMat randomR = opencv_core.CvMat.create(3, 3), randomAxis = opencv_core.CvMat.create(3, 1);
        // We can easily and efficiently access the elements of matrices and images
        // through an Indexer object with the set of get() and put() methods.
        DoubleIndexer Ridx = randomR.createIndexer(), axisIdx = randomAxis.createIndexer();
//        axisIdx.put(0, (Math.random()-0.5)/4, (Math.random()-0.5)/4, (Math.random()-0.5)/4);
        axisIdx.put(0, 0, 0, 0);
        cvRodrigues2(randomAxis, randomR, null);
        double f = (width + height)/2.0;  Ridx.put(0, 2, Ridx.get(0, 2)*f);
                                          Ridx.put(1, 2, Ridx.get(1, 2)*f);
        Ridx.put(2, 0, Ridx.get(2, 0)/f); Ridx.put(2, 1, Ridx.get(2, 1)/f);
//        System.out.println(Ridx);

        int lastTotal = 0;          //ukazuje jestli pocet oblicaje na obrazku se zmenil
        GregorianCalendar calendar = new GregorianCalendar();
        Date dateActual = new Date();
        long dateLast = 0;
        String time;
        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            cvClearMemStorage(storage);

            // Let's try to detect some faces! but we need a grayscale image...
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            opencv_core.CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,
                    1.1, 3, CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
            int total = faces.total();
            if(total != lastTotal && total != 0) {
                dateActual = new Date();
                long dA = dateActual.getTime();
                if(dA - dateLast> 2000) {
                time = dateActual.toString();
//                String[] timeA = time.split(" ");
                System.out.println("New face    time: "+ time);
                }
                dateLast =  dateActual.getTime();
            }
            for (int i = 0; i < total; i++) {
                opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(faces, i));
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x+w, y+h), opencv_core.CvScalar.RED, 1, CV_AA, 0);
            }
            cvWarpPerspective(grabbedImage, rotatedImage, randomR);

            Frame rotatedFrame = converter.convert(rotatedImage);
            frame.showImage(rotatedFrame);
            lastTotal = total;
        }
        frame.dispose();
        grabber.stop();
    }
}