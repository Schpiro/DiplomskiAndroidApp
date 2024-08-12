package com.bbilandzi.diplomskiandroidapp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.utils.WebRTCClient;

import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VideoCallActivity extends BaseActivity{

    private static final int REQUEST_PERMISSIONS = 1;
    private WebRTCClient webRTCManager;
    private SurfaceViewRenderer localRenderer;
    private SurfaceViewRenderer remoteRenderer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        webRTCManager = new WebRTCClient(this);

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, REQUEST_PERMISSIONS);
        } else {
            initializeWebRTC();
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeWebRTC();
            } else {
                Toast.makeText(this, "Permissions are required for WebRTC to work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeWebRTC() {
        Log.d("VideoCallActivity", "Initializing WebRTC");
        localRenderer = findViewById(R.id.local_surface_view);
        remoteRenderer = findViewById(R.id.local_surface_view);

        if (localRenderer != null) {
            localRenderer.init(webRTCManager.getEglBase().getEglBaseContext(), null);
            localRenderer.setMirror(true);
            Log.d("VideoCallActivity", "SurfaceViewRenderer initialized");
            VideoTrack videoTrack = webRTCManager.setupLocalRenderer();
            videoTrack.addSink(localRenderer);
            videoTrack.setEnabled(true);
        } else {
            Log.e("VideoCallActivity", "SurfaceViewRenderer is null");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webRTCManager != null) {
            webRTCManager.dispose();
        }
        if (localRenderer != null) {
            localRenderer.release();
        }
        if (remoteRenderer != null) {
            remoteRenderer.release();
        }
        Log.d("VideoCallActivity", "Resources disposed");
    }
}
