package nathsou.ScreenshotTask;

import nathsou.ScreenshotTask.ScreenshotTakenEvent.ScreenshotTakenEvent;
import nathsou.ScreenshotTask.ScreenshotTakenEvent.ScreenshotTakenListener;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;

/**
 * Created by nathan on 09/08/15.
 */
public class ScreenshotTask extends TimerTask{

    private File saveDir;
    private int snapCount = 0;
    private boolean limited = false;
    private int limit = -1;
    private float compressionFactor = 1f;
    private EventListenerList listenerList = new EventListenerList();
    private boolean finished = false;

    public ScreenshotTask(File saveDir) {
        this.saveDir = saveDir;
        finished = false;
    }

    public ScreenshotTask(File saveDir, int limit){
        this.saveDir = saveDir;
        this.limit = limit;
        limited = limit != 0;
        finished = false;
    }

    public void run(){

        if(!saveDir.exists()){
            System.err.println("Folder " + saveDir + " doesn't exist.");
        }else {
            try {
                if(!limited || snapCount <= limit - 1) {
                    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage capture = new Robot().createScreenCapture(screenRect);

                    Date date = new Date();

                    File dir = new File(saveDir + "/" + new SimpleDateFormat("dd-MM-yyyy").format(date));
                    String time = new SimpleDateFormat("HH mm ss").format(date);

                   if(!dir.exists() && !dir.mkdir())
                       throw new IOException("Couldn't create directory : " + dir.getAbsolutePath() + ".");

                    if(compressionFactor == -1f) { // Do not compress -> .bmp
                        ImageIO.write(capture, "bmp", new File(dir + "/" + time + ".bmp"));
                    }else {
                        compressAndSaveImage(capture, new File(dir + "/" + time + ".jpg"));
                    }
                    snapCount++;

                    fireScreenshotTakenEvent(new ScreenshotTakenEvent(this));
                }else{
                    fireScreenshotTaskFinishedEvent(new ScreenshotTaskFinishedEvent(this));
                }

            } catch (AWTException awte) {
                System.err.println("Error while taking the screenshot: " + awte.getMessage());
            } catch (IOException ioe) {
                System.err.println("Error while writing the image: " + ioe.getMessage());
            }
        }
    }

    private void compressAndSaveImage(BufferedImage bufferedImage, File saveFile) throws IOException{
        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        OutputStream os = new FileOutputStream(saveFile);

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(compressionFactor);
        writer.write(null, new IIOImage(bufferedImage, null, null), param);

        os.close();
        writer.dispose();

    }

    public int getSnapCount() {
        return snapCount;
    }

    public File getSaveDir() {
        return saveDir;
    }

    public int getSnapLimit() {
        return limit;
    }

    public boolean isLimited() {
        return limited;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setCompressionFactor(float compressionFactor) {
        this.compressionFactor = compressionFactor;
    }

    public float getCompressionFactor() {
        return compressionFactor;
    }

    public void stop(){
        fireScreenshotTaskFinishedEvent(new ScreenshotTaskFinishedEvent(this));
    }

    //ScreenshotTaskFinished Event

    public void addScreenshotTaskFinishedListener(ScreenshotTaskFinishedListener listener) {
        listenerList.add(ScreenshotTaskFinishedListener.class, listener);
    }

    public void removeScreenshotTaskFinishedListener(ScreenshotTaskFinishedListener listener) {
        listenerList.remove(ScreenshotTaskFinishedListener.class, listener);
    }

    private void fireScreenshotTaskFinishedEvent(ScreenshotTaskFinishedEvent event) {
        finished = true;

        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ScreenshotTaskFinishedListener.class) {
                ((ScreenshotTaskFinishedListener) listeners[i + 1]).ScreenshotTaskFinished(event);
            }
        }
    }

    //ScreenshotTakenEvent

    public void addScreenshotTakenListener(ScreenshotTakenListener listener) {
        listenerList.add(ScreenshotTakenListener.class, listener);
    }

    public void removeScreenshotTakenListener(ScreenshotTakenListener listener) {
        listenerList.remove(ScreenshotTakenListener.class, listener);
    }

    private void fireScreenshotTakenEvent(ScreenshotTakenEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ScreenshotTakenListener.class) {
                ((ScreenshotTakenListener) listeners[i + 1]).ScreenshotTakenEvent(event);
            }
        }
    }
}
