package com.guoxiaoxing.phoenix.compress.video;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.souche.android.sdk.media.compress.video.format.MediaFormatStrategyPresets;
import com.souche.android.sdk.media.core.PhoenixOption;
import com.souche.android.sdk.media.core.listener.OnProcessorListener;
import com.souche.android.sdk.media.core.listener.Processor;
import com.souche.android.sdk.media.core.model.MediaEntity;

import java.io.File;
import java.io.IOException;

/**
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 * @since 2017/8/4 下午1:44
 */
public class VideoHardCompressProcessor implements Processor {

    private static final String TAG = "VideoHardCompressProcessor";

    @Override
    public MediaEntity syncProcess(Context context, MediaEntity mediaEntity, PhoenixOption phoenixOption) {

        if (mediaEntity == null) {
            throw new IllegalArgumentException("The onProcessorListener can not be null");
        }

        final MediaEntity result = mediaEntity;

        final File compressFile;
        try {
            File compressCachePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "outputs");
            compressCachePath.mkdir();
            compressFile = File.createTempFile("compress", ".mp4", compressCachePath);
        } catch (IOException e) {
            Toast.makeText(context, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
            return null;
        }

        try {
           String compressPath =  MediaTranscoder.getInstance().syncTranscodeVideo(mediaEntity.getLocalPath(), compressFile.getAbsolutePath(),
                    MediaFormatStrategyPresets.createAndroid480pFormatStrategy());
            result.setCompressed(true);
            result.setCompressPath(compressPath);
            return mediaEntity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void asyncProcess(Context context, final MediaEntity mediaEntity, PhoenixOption phoenixOption, final OnProcessorListener onProcessorListener) {

        if (mediaEntity == null) {
            throw new IllegalArgumentException("The onProcessorListener can not be null");
        }

        if (onProcessorListener == null) {
            throw new IllegalArgumentException("The onProcessorListener can not be null");
        }

        final MediaEntity result = mediaEntity;

        final File compressFile;
        try {
            File compressCachePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "outputs");
            compressCachePath.mkdir();
            compressFile = File.createTempFile("compress", ".mp4", compressCachePath);
        } catch (IOException e) {
            Toast.makeText(context, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
            return;
        }
        MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {
                onProcessorListener.onProgress((int) progress);
            }

            @Override
            public void onTranscodeCompleted() {
                result.setCompressed(true);
                result.setCompressPath(compressFile.getAbsolutePath());
                onProcessorListener.onSuccess(result);
            }

            @Override
            public void onTranscodeCanceled() {

            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                onProcessorListener.onFailed(exception.getMessage());
            }
        };
        try {
            MediaTranscoder.getInstance().asyncTranscodeVideo(mediaEntity.getLocalPath(), compressFile.getAbsolutePath(),
                    MediaFormatStrategyPresets.createAndroid480pFormatStrategy(), listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
