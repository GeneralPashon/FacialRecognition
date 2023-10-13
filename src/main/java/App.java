import jpize.Jpize;
import jpize.glfw.key.Key;
import jpize.graphics.texture.Pixmap;
import jpize.graphics.texture.PixmapIO;
import jpize.graphics.util.Canvas;
import jpize.io.context.JpizeApplication;
import jpize.util.time.Sync;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class App extends JpizeApplication{

    private final Canvas canvas;
    private final VideoCapture capture;
    private final CascadeClassifier classifier;
    private final Sync sync;

    public App(){
        this.canvas = new Canvas();

        this.capture = new VideoCapture(0);
        this.classifier = new CascadeClassifier();
        this.classifier.load("haarcascade/haarcascade_frontalface_alt.xml");

        this.sync = new Sync(capture.get(Videoio.CAP_PROP_FPS));

        Jpize.window().setAspect(
            (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH),
            (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT)
        );
    }


    @Override
    public void update(){
        sync.sync();
        Jpize.window().setTitle("ur face (" + Jpize.getFPS() + "/" + sync.getTPS() + " fps)");

        if(Key.ESCAPE.isDown())
            Jpize.exit();
    }

    @Override
    public void render(){
        final Mat mat = getCapture();
        detectFaces(mat);

        final Pixmap pixmap = matToPixmap(mat);
        canvas.drawPixmap(pixmap, (float) Jpize.getHeight() / pixmap.getHeight());

        canvas.render();
    }

    @Override
    public void resize(int width, int height){
        canvas.resize(width, height);
    }

    @Override
    public void dispose(){
        canvas.dispose();
    }


    public Mat getCapture(){
        final Mat mat = new Mat();
        capture.read(mat);
        return mat;
    }

    public Pixmap matToPixmap(Mat mat){
        try{
            final MatOfByte bytes = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, bytes);

            final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());
            final BufferedImage image = ImageIO.read(inputStream);

            return PixmapIO.load(image, false, true);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private void detectFaces(Mat mat){
        final MatOfRect facesDetected = new MatOfRect();

        classifier.detectMultiScale(
            mat,
            facesDetected
        );

        final Rect[] facesArray = facesDetected.toArray();
        for(Rect face : facesArray)
            Imgproc.rectangle(mat, face.tl(), face.br(), new Scalar(0, 0, 255), 2);
    }

}
