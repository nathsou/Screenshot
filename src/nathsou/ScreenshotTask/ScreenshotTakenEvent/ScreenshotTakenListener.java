package nathsou.ScreenshotTask.ScreenshotTakenEvent;

import java.util.EventListener;

/**
 * Created by Nathan on 06/11/2015.
 */
public interface ScreenshotTakenListener extends EventListener {
    void ScreenshotTakenEvent(ScreenshotTakenEvent event);
}
