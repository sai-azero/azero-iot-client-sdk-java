package com.azero.services.iot.client.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.azero.services.iot.client.AZEROIotException;

/**
 * This class extends {@link SSLSocketFactory} to enforce TLS v1.2 to be used
 * for SSL sockets created by the library.
 */
public class AzeroIotTlsSocketFactory extends SSLSocketFactory {
    private static final String TLS_V_1_2 = "TLSv1.2";

    /**
     * SSL Socket Factory A SSL socket factory is created and passed into this
     * class which decorates it to enable TLS 1.2 when sockets are created.
     */
    private final SSLSocketFactory sslSocketFactory;

    public AzeroIotTlsSocketFactory(KeyStore keyStore, String keyPassword) throws AZEROIotException {
        try {
            SSLContext context = SSLContext.getInstance(TLS_V_1_2);

            KeyManagerFactory managerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            managerFactory.init(keyStore, keyPassword.toCharArray());
            context.init(managerFactory.getKeyManagers(), null, null);

            sslSocketFactory = context.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
            throw new AZEROIotException(e);
        }
    }

    public AzeroIotTlsSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return sslSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return sslSocketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return ensureTls(sslSocketFactory.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return ensureTls(sslSocketFactory.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return ensureTls(sslSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException, UnknownHostException {
        return ensureTls(sslSocketFactory.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return ensureTls(sslSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return ensureTls(sslSocketFactory.createSocket(address, port, localAddress, localPort));
    }

    /**
     * Enable TLS 1.2 on any socket created by the underlying SSL Socket
     * Factory.
     *
     * @param socket
     *            newly created socket which may not have TLS 1.2 enabled.
     * @return TLS 1.2 enabled socket.
     */
    private Socket ensureTls(Socket socket) {
        if (socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket) socket).setEnabledProtocols(new String[] { TLS_V_1_2 });

            // Ensure hostname is validated againt the CN in the certificate
            SSLParameters sslParams = new SSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            ((SSLSocket) socket).setSSLParameters(sslParams);
        }
        return socket;
    }

}
