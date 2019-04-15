package linzh.top.camera.manager.listener;

import linzh.top.camera.util.Size;

public interface CameraOpenListener<CameraId, SurfaceListener> {

    void onCameraOpened(CameraId openedCameraId, Size previewSize, SurfaceListener surfaceListener);

    void onCameraOpenError();
}
