package linzh.top.camera.lifecycle.listener;

import android.support.annotation.Nullable;
import android.view.View;

import linzh.top.camera.config.CameraConfig;
import linzh.top.camera.listener.OnCameraResultListener;
import linzh.top.camera.util.Size;

/**
 * The camera view
 * <p>
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 */
public interface CameraView {

    void updateCameraPreview(Size size, View cameraPreview);

    void updateUiForMediaAction(@CameraConfig.MediaAction int mediaAction);

    void updateCameraSwitcher(int numberOfCameras);

    void onPictureTaken(byte[] bytes, @Nullable OnCameraResultListener callback);

    void onVideoRecordStart(int width, int height);

    void onVideoRecordStop(@Nullable OnCameraResultListener callback);

    void releaseCameraPreview();
}
