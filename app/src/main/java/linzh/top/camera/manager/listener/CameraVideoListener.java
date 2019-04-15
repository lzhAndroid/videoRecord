package linzh.top.camera.manager.listener;

import linzh.top.camera.listener.OnCameraResultListener;
import linzh.top.camera.util.Size;

import java.io.File;

public interface CameraVideoListener {

    void onVideoRecordStarted(Size videoSize);

    void onVideoRecordStopped(File videoFile, OnCameraResultListener callback);

    void onVideoRecordError();
}
