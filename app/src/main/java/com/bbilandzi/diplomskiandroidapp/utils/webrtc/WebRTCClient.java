package com.bbilandzi.diplomskiandroidapp.utils.webrtc;


import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.ANSWER;
import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.ICE_CANDIDATE;
import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.OFFER;

import android.content.Context;

import com.bbilandzi.diplomskiandroidapp.model.WebsocketMessageDTO;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.bbilandzi.diplomskiandroidapp.utils.IceServerConfig;
import com.bbilandzi.diplomskiandroidapp.utils.MessageTypes;
import com.bbilandzi.diplomskiandroidapp.utils.WebSocketManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.Collections;

public class WebRTCClient {
    private final Gson gson = new Gson();
    private final Context context;
    private EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
    private  PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private CameraVideoCapturer videoCapturer;

    private VideoSource localVideoSource;
    private AudioSource localAudioSource;
    private String localTrackId = "local_track";
    private String localStreamId = "local_stream";
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private MediaStream localStream;
    private MediaConstraints mediaConstraints = new MediaConstraints();
    WebSocketManager webSocketManager = WebSocketManager.getInstance();


    public WebRTCClient(Context context, PeerConnection.Observer observer) {
        this.context = context;
        initPeerConnectionFactory();
        peerConnectionFactory = createPeerConnectionFactory();
        peerConnection = createPeerConnection(observer);
        localVideoSource = peerConnectionFactory.createVideoSource(false);
        localAudioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());

        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));

        webSocketManager.getMessageLiveData(OFFER).observeForever(msg->{
            onRemoteSessionReceived(onReceiveDescription(msg));
            answer("2");
        });
        webSocketManager.getMessageLiveData(ANSWER).observeForever(msg->{
            onRemoteSessionReceived(onReceiveDescription(msg));
            answer("2");
        });
        webSocketManager.getMessageLiveData(ICE_CANDIDATE).observeForever(msg->
            addIceCandidate(onReceiveIceCandidate(msg))
        );

    }

    private void initPeerConnectionFactory() {
        PeerConnectionFactory.InitializationOptions options =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                        .setEnableInternalTracer(true).createInitializationOptions();

        PeerConnectionFactory.initialize(options);
    }

    private PeerConnectionFactory createPeerConnectionFactory() {
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.disableEncryption = false;
        options.disableNetworkMonitor = false;
        return PeerConnectionFactory.builder()
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglBaseContext, true, true))
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglBaseContext))
                .setOptions(options).createPeerConnectionFactory();
    }

    private PeerConnection createPeerConnection(PeerConnection.Observer observer) {
        return peerConnectionFactory.createPeerConnection(IceServerConfig.ICE_SERVERS, observer);
    }

    public void initSurfaceViewRenderer(SurfaceViewRenderer viewRenderer) {
        viewRenderer.setEnableHardwareScaler(true);
        viewRenderer.setMirror(true);
        viewRenderer.init(eglBaseContext, null);
    }
    public void initLocalSurfaceView(SurfaceViewRenderer view) {
        initSurfaceViewRenderer(view);
        startLocalVideoStreaming(view);
    }

    private void startLocalVideoStreaming(SurfaceViewRenderer view) {
        SurfaceTextureHelper helper = SurfaceTextureHelper.create(
                Thread.currentThread().getName(), eglBaseContext);

        videoCapturer = getVideoCapturer();
        videoCapturer.initialize(helper, context, localVideoSource.getCapturerObserver());
        videoCapturer.startCapture(360, 304, 30);
        localVideoTrack = peerConnectionFactory.createVideoTrack(localTrackId+"_video", localVideoSource);
        localVideoTrack.addSink(view);



        localAudioTrack = peerConnectionFactory.createAudioTrack(localTrackId+"_audio", localAudioSource);
        localStream = peerConnectionFactory.createLocalMediaStream(localStreamId);
        localStream.addTrack(localVideoTrack);
        localStream.addTrack(localAudioTrack);
        peerConnection.addTrack(localVideoTrack);
        peerConnection.addTrack(localAudioTrack);
    }

    private CameraVideoCapturer getVideoCapturer () {
        Camera2Enumerator enumerator = new Camera2Enumerator(context);

        String[] deviceNames = enumerator.getDeviceNames();

        for (String device: deviceNames) {
            if (enumerator.isFrontFacing(device)) {
                return enumerator.createCapturer(device, null);
            }
        }
        throw new IllegalStateException("Front facing not found");
    }

    public void initRemoteSurfaceView(SurfaceViewRenderer view) {
        initSurfaceViewRenderer(view);
    }

    public void call(String target) {
        try {
            peerConnection.createOffer(new SdpObserver(){
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    super.onCreateSuccess(sessionDescription);
                    peerConnection.setLocalDescription(new SdpObserver(){
                        @Override
                        public void onSetSuccess() {
                            super.onSetSuccess();
                            WebsocketMessageDTO messageDTO = new WebsocketMessageDTO();
                            messageDTO.setType(OFFER);
                            messageDTO.setPayload(sessionDescription);
                            messageDTO.setSenderId(AuthUtils.getUserId(context));
                            messageDTO.setSenderName(AuthUtils.getUsername(context));
                            if (AuthUtils.getUsername(context).equalsIgnoreCase("admin"))
                                messageDTO.setRecipientIds(Collections.singletonList(2L));
                            else
                                messageDTO.setRecipientIds(Collections.singletonList(1L));
                            webSocketManager.sendMessage(messageDTO);
                        }
                    }, sessionDescription);
                }
            },mediaConstraints);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void answer(String target) {
        try {
            peerConnection.createOffer(new SdpObserver(){
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    super.onCreateSuccess(sessionDescription);
                    peerConnection.setLocalDescription(new SdpObserver(){
                        @Override
                        public void onSetSuccess() {
                            super.onSetSuccess();
                            WebsocketMessageDTO messageDTO = new WebsocketMessageDTO();
                            messageDTO.setType(ANSWER);
                            messageDTO.setPayload(sessionDescription);
                            messageDTO.setSenderId(AuthUtils.getUserId(context));
                            messageDTO.setSenderName(AuthUtils.getUsername(context));
                            if (AuthUtils.getUsername(context).equalsIgnoreCase("admin"))
                                messageDTO.setRecipientIds(Collections.singletonList(2L));
                            else
                                messageDTO.setRecipientIds(Collections.singletonList(1L));
                            webSocketManager.sendMessage(messageDTO);
                        }
                    }, sessionDescription);
                }
            },mediaConstraints);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SessionDescription onReceiveDescription(WebsocketMessageDTO websocketMessageDTO) {
        LinkedTreeMap offerMap = (LinkedTreeMap) websocketMessageDTO.getPayload();
        SessionDescription.Type type = SessionDescription.Type.fromCanonicalForm(offerMap.get("type").toString());
        String description = offerMap.get("description").toString();
        return new SessionDescription(type, description);
    }

    public IceCandidate onReceiveIceCandidate(WebsocketMessageDTO websocketMessageDTO) {
        JsonElement jsonElement = gson.toJsonTree(websocketMessageDTO.getPayload());
        return gson.fromJson(jsonElement, IceCandidate.class);
    }

    public void onRemoteSessionReceived(SessionDescription sessionDescription) {
        peerConnection.setRemoteDescription(new SdpObserver(), sessionDescription);
    }

    public void addIceCandidate(IceCandidate iceCandidate) {
        peerConnection.addIceCandidate(iceCandidate);
    }

    public void sendIceCandidate(IceCandidate iceCandidate) {
        addIceCandidate(iceCandidate);

        WebsocketMessageDTO messageDTO = new WebsocketMessageDTO();
        messageDTO.setType(MessageTypes.ICE_CANDIDATE);
        messageDTO.setPayload(iceCandidate);
        messageDTO.setSenderId(AuthUtils.getUserId(context));
        messageDTO.setSenderName(AuthUtils.getUsername(context));
        if (AuthUtils.getUsername(context).equalsIgnoreCase("admin"))
            messageDTO.setRecipientIds(Collections.singletonList(2L));
        else
            messageDTO.setRecipientIds(Collections.singletonList(1L));

        webSocketManager.sendMessage(messageDTO);
    }

    public void switchCamera() {
        videoCapturer.switchCamera(null);
    }

    public void toggleVideo(Boolean mute) {
        localVideoTrack.setEnabled(mute);
    }

    public void toggleAudio(Boolean mute) {
        localAudioTrack.setEnabled(mute);
    }

    public void closeConnection() {
        try{
            localVideoTrack.dispose();
            videoCapturer.stopCapture();
            videoCapturer.dispose();
            peerConnection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
