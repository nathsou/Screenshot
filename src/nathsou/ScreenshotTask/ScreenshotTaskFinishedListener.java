package nathsou.ScreenshotTask;

import java.util.EventListener;

/**
 * Created by nathan on 09/08/15.
 */
public interface ScreenshotTaskFinishedListener extends EventListener {
    void ScreenshotTaskFinished(ScreenshotTaskFinishedEvent event);
}
