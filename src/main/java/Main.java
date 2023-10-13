import jpize.Jpize;
import jpize.io.context.ContextBuilder;
import nu.pattern.OpenCV;
import org.opencv.core.*;

public class Main{

    public static void main(String[] args){
        OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        ContextBuilder.newContext(720, 720, "ur face")
            .register().setAdapter(new App());

        Jpize.runContexts();
    }

}
