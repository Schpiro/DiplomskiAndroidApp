package com.bbilandzi.diplomskiandroidapp.utils;

import org.webrtc.PeerConnection;
import java.util.ArrayList;
import java.util.List;

public class IceServerConfig {
    public static final List<PeerConnection.IceServer> ICE_SERVERS;

    static {
        ICE_SERVERS = new ArrayList<>();
        ICE_SERVERS.add(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer());
        ICE_SERVERS.add(PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer());
    }
}
