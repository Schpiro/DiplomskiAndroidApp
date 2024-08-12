package com.bbilandzi.diplomskiandroidapp.utils;

import android.content.Context;
import android.util.Log;

import com.bbilandzi.diplomskiandroidapp.model.WebsocketMessageDTO;

import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

public class WebRTCClient {

    @Getter
    private PeerConnectionFactory peerConnectionFactory;
    WebSocketManager webSocketManager = WebSocketManager.getInstance();
    private PeerConnection peerConnection;
    private VideoTrack localVideoTrack;
    private VideoTrack remoteVideoTrack;
    @Getter
    private EglBase eglBase;
    private SurfaceViewRenderer remoteVideoView;
    private SurfaceViewRenderer localVideoView;
    Context context;
    private List<PeerConnection> peerConnections = new ArrayList<>();

    private final PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            if (iceCandidate != null) {
                WebsocketMessageDTO messageDTO = new WebsocketMessageDTO();
                messageDTO.setType(MessageTypes.ICE_CANDIDATE);
                messageDTO.setPayload(iceCandidate);
                messageDTO.setSenderId(AuthUtils.getUserId(context));
                messageDTO.setSenderName(AuthUtils.getUsername(context));
                webSocketManager.sendMessage(messageDTO);
            }
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            if (mediaStream.videoTracks.size() > 0) {
                remoteVideoTrack = mediaStream.videoTracks.get(0);
                remoteVideoTrack.addSink(remoteVideoView);
            }
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {}

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {}

        @Override
        public void onIceConnectionReceivingChange(boolean b) {}

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {}

        @Override
        public void onRemoveStream(MediaStream mediaStream) {}

        @Override
        public void onDataChannel(DataChannel dataChannel) {}

        @Override
        public void onRenegotiationNeeded() {}

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {}
    };


    public WebRTCClient(Context context) {
        this.context = context;
        initializeWebRTC(context);
    }

    private void initializeWebRTC(Context context) {
        // Initialize EglBase
        eglBase = EglBase.create();

        // Initialize PeerConnectionFactory
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(
                eglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(
                eglBase.getEglBaseContext());

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    public void makeCall(SurfaceViewRenderer remoteVideoView, SurfaceViewRenderer localVideoView, String selectedRecipient) {
        createPeerConnection(remoteVideoView, localVideoView, selectedRecipient);

        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(this, sessionDescription);
                WebsocketMessageDTO messageDTO = new WebsocketMessageDTO();
                messageDTO.setType(MessageTypes.OFFER);
                messageDTO.setPayload(sessionDescription);
                messageDTO.setSenderId(AuthUtils.getUserId(context));
                messageDTO.setSenderName(AuthUtils.getUsername(context));
                webSocketManager.sendMessage(messageDTO);
            }

            @Override
            public void onSetSuccess() {}

            @Override
            public void onCreateFailure(String s) {}

            @Override
            public void onSetFailure(String s) {}
        }, new MediaConstraints());
    }

    public void createPeerConnection(SurfaceViewRenderer remoteVideoView, SurfaceViewRenderer localVideoView, String selectedRecipient) {
        this.remoteVideoView = remoteVideoView;
        this.localVideoView = localVideoView;

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(IceServerConfig.ICE_SERVERS);
        rtcConfig.iceCandidatePoolSize = 10;
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, peerConnectionObserver);
    }

    public MediaStream createMediaStream(String streamId) {
        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream(streamId);

        // Create AudioTrack
        AudioTrack audioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", peerConnectionFactory.createAudioSource(new MediaConstraints()));
        mediaStream.addTrack(audioTrack);

        // Create VideoTrack
        VideoCapturer videoCapturer = createCameraCapturer();
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
        VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
        videoCapturer.startCapture(1280,720,30);
        VideoTrack videoTrack = peerConnectionFactory.createVideoTrack("ARDAMSv0", videoSource);
        mediaStream.addTrack(videoTrack);

        return mediaStream;
    }

    private VideoCapturer createCameraCapturer() {
        Camera1Enumerator enumerator = new Camera1Enumerator(true);
        for (String deviceName : enumerator.getDeviceNames()) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer capturer = enumerator.createCapturer(deviceName, null);
                if (capturer != null) {
                    Log.d("WebRTCClient", "Camera capturer created");
                } else {
                    Log.e("WebRTCClient", "Failed to create camera capturer");
                }
                return capturer;
            }
        }
        return null;
    }


    public VideoTrack setupLocalRenderer() {
        MediaStream mediaStream = createMediaStream("local_stream");
        return mediaStream.videoTracks.get(0); // Assuming the first video track
    }


    public void dispose() {
        for (PeerConnection pc : peerConnections) {
            pc.dispose();
        }
        peerConnections.clear();
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
        }
        if (eglBase != null) {
            eglBase.release();
        }
    }
}
