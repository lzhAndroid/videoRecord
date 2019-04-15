package linzh.top.camera.manager.listener;

public interface CameraCloseListener<CameraId> {
    void onCameraClosed(CameraId closedCameraId);
}
