package com.bbilandzi.diplomskiandroidapp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView muteButton;
    private boolean isMuted = false; // Track mute state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, REQUEST_PERMISSIONS);
        }

        muteButton = findViewById(R.id.img_mute); // Reference to mute button

        // Set mute button click listener with animation
        muteButton.setOnClickListener(v -> {
            animateButton(v); // Apply animation
            toggleMute(); // Toggle mute state
        });
        // Add hangup button reference
        ImageView hangupButton = findViewById(R.id.img_hangup); // Reference to hangup button
        hangupButton.setOnClickListener(v -> {
            endCall();
            animateButton(v);
        });
    }

    @Override
    protected void onClose(Bundle savedInstanceState) {
    }

    private void initialize() {
        initLocalView(localRenderer);
        initRemoteView(remoteRenderer);

        makeCallButton.setOnClickListener(v -> startCall("2"));
    }

    private void endCall() {
        finish();
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

    // Toggle the mute state
    private void toggleMute() {
        isMuted = !isMuted;

        if (isMuted) {
            // Change image and background for mute
            muteButton.setImageResource(R.drawable.microphone_off); // Set muted image
            muteButton.setBackgroundResource(R.drawable.round_button_muted); // Set background for muted state
        } else {
            // Change image and background for unmute
            muteButton.setImageResource(R.drawable.microphone); // Set unmuted image
            muteButton.setBackgroundResource(R.drawable.round_button_2); // Set background for unmuted state
        }
    }

    // Method to trigger animation on button click
    public void animateButton(View v) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_click);
        v.startAnimation(animation);
    }
}
