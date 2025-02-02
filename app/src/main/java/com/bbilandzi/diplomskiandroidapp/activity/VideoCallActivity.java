package com.bbilandzi.diplomskiandroidapp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.utils.webrtc.PeerConnectionObserver;
import com.bbilandzi.diplomskiandroidapp.utils.webrtc.WebRTCClient;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.RtpReceiver;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VideoCallActivity extends BaseActivity {
    private static final int REQUEST_PERMISSIONS = 1;
    private WebRTCClient webRTCManager;
    private SurfaceViewRenderer localRenderer;
    private SurfaceViewRenderer remoteRenderer;
    private Button makeCallButton;

    private PowerManager.WakeLock wakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        localRenderer = findViewById(R.id.local_surface_view);
        remoteRenderer = findViewById(R.id.remote_surface_view);
        makeCallButton = findViewById(R.id.btn_make_call);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "Diplomski::VideoCallActive");
        wakeLock.acquire(5 * 60 * 1000L);
        this.webRTCManager = new WebRTCClient(this, new PeerConnectionObserver() {
            @Override
            public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {
                super.onAddTrack(receiver, mediaStreams);
                if (receiver.track() instanceof VideoTrack) {
                    VideoTrack videoTrack = (VideoTrack) receiver.track();
                    videoTrack.addSink(remoteRenderer);
                }
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                webRTCManager.sendIceCandidate(iceCandidate);
            }
        });

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, REQUEST_PERMISSIONS);
        }

        initialize();
    }

    @Override
    protected void onClose(Bundle savedInstanceState) {
        wakeLock.release();
    }

    private void initialize() {
        initLocalView(localRenderer);
        initRemoteView(remoteRenderer);

        makeCallButton.setOnClickListener(v->{
            startCall("2");
        });
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
                initialize();
            } else {
                Toast.makeText(this, "Permissions are required for WebRTC to work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void initLocalView(SurfaceViewRenderer view) {
        webRTCManager.initLocalSurfaceView(view);
    }

    public void initRemoteView(SurfaceViewRenderer view) {
        webRTCManager.initRemoteSurfaceView(view);
    }

    public void startCall(String target) {
        webRTCManager.call(target);
    }

}
