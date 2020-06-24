package com.azero.services.iot.client.sample.pubSub;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotMqttClient;
import com.azero.services.iot.client.AZEROIotQos;
import com.azero.services.iot.client.AZEROIotTimeoutException;
import com.azero.services.iot.client.AZEROIotTopic;
import com.azero.services.iot.client.sample.sampleUtil.CommandArguments;
import com.azero.services.iot.client.sample.sampleUtil.SampleUtil;
import com.azero.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

/**
 * This is an example that uses {@link AZEROIotMqttClient} to subscribe to a
 * topic and publish messages to it. Both blocking and non-blocking publishing
 * are demonstrated in this example.
 */
public class PublishSubscribeSample {

	private static final String TestTopic = "$pub/{endpoint}/$azero/things/{thingName}/shadow/update/documents";
	private static final AZEROIotQos TestTopicQos = AZEROIotQos.QOS0;
	private static final String TLS_V_1_2 = "TLSv1.2";

	private static AZEROIotMqttClient azeroIotClient;

	public static void setClient(AZEROIotMqttClient client) {
		azeroIotClient = client;
	}

	public static class BlockingPublisher implements Runnable {
		private final AZEROIotMqttClient azeroIotClient;

		public BlockingPublisher(AZEROIotMqttClient azeroIotClient) {
			this.azeroIotClient = azeroIotClient;
		}

		@Override
		public void run() {
			long counter = 1;

			while (true) {
				String payload = "hello from blocking publisher - " + (counter++);
				try {
					azeroIotClient.publish(TestTopic, payload);
				} catch (AZEROIotException e) {
					System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
					e.printStackTrace();
				}
				System.out.println(System.currentTimeMillis() + ": >>> " + payload);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println(System.currentTimeMillis() + ": BlockingPublisher was interrupted");
					e.printStackTrace();
					return;
				}
			}
		}
	}

	public static class NonBlockingPublisher implements Runnable {
		private final AZEROIotMqttClient azeroIotClient;

		public NonBlockingPublisher(AZEROIotMqttClient azeroIotClient) {
			this.azeroIotClient = azeroIotClient;
		}

		@Override
		public void run() {
			long counter = 1;

			while (true) {
				String payload = "hello from non-blocking publisher - " + (counter++);
				AZEROIotMessage message = new NonBlockingPublishListener(TestTopic, TestTopicQos, payload);
				try {
					azeroIotClient.publish(message);
				} catch (AZEROIotException e) {
					System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
					e.printStackTrace();
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println(System.currentTimeMillis() + ": NonBlockingPublisher was interrupted");
					e.printStackTrace();
					return;
				}
			}
		}
	}

	private static void initClient(CommandArguments arguments) throws AZEROIotException {
		String clientEndpoint = arguments.getNotNull("clientEndpoint", SampleUtil.getConfig("clientEndpoint"));
		String clientId = arguments.getNotNull("clientId", SampleUtil.getConfig("clientId"));

		String certificateFile = arguments.get("certificateFile", SampleUtil.getConfig("certificateFile"));
		String privateKeyFile = arguments.get("privateKeyFile", SampleUtil.getConfig("privateKeyFile"));
		int tlsPort = Integer.valueOf(arguments.get("tlsPort", SampleUtil.getConfig("tlsPort")));
		if (azeroIotClient == null && certificateFile != null && privateKeyFile != null) {
			String algorithm = arguments.get("keyAlgorithm", SampleUtil.getConfig("keyAlgorithm"));

			KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);

			azeroIotClient = new AZEROIotMqttClient(clientEndpoint, clientId,
					getSocketFactory(pair.keyStore, pair.keyPassword), tlsPort);
		}

		if (azeroIotClient == null) {
			String azeroAccessKeyId = arguments.get("azeroAccessKeyId", SampleUtil.getConfig("azeroAccessKeyId"));
			String azeroSecretAccessKey = arguments.get("azeroSecretAccessKey", SampleUtil.getConfig("azeroSecretAccessKey"));
			String sessionToken = arguments.get("sessionToken", SampleUtil.getConfig("sessionToken"));

			if (azeroAccessKeyId != null && azeroSecretAccessKey != null) {
				azeroIotClient = new AZEROIotMqttClient(clientEndpoint, clientId, azeroAccessKeyId, azeroSecretAccessKey,
						sessionToken);
			}
		}

		if (azeroIotClient == null) {
			throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
		}
	}

	public static SSLSocketFactory getSocketFactory(KeyStore keyStore, String keyPassword) throws AZEROIotException {
		try {
			SSLContext context = SSLContext.getInstance(TLS_V_1_2);

			KeyManagerFactory managerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			managerFactory.init(keyStore, keyPassword.toCharArray());

			// server truststore
			TrustManager[] tm = { new MyX509TrustManager() };
			context.init(managerFactory.getKeyManagers(), tm, null);
			return context.getSocketFactory();
		} catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
			throw new AZEROIotException(e);
		}
	}

	public static void main(String args[]) throws InterruptedException, AZEROIotException, AZEROIotTimeoutException {
		CommandArguments arguments = CommandArguments.parse(args);
		initClient(arguments);

		azeroIotClient.connect();

		AZEROIotTopic topic = new TestTopicListener(TestTopic, TestTopicQos);
		azeroIotClient.subscribe(topic, true);

		Thread blockingPublishThread = new Thread(new BlockingPublisher(azeroIotClient));
		Thread nonBlockingPublishThread = new Thread(new NonBlockingPublisher(azeroIotClient));

		blockingPublishThread.start();
		nonBlockingPublishThread.start();

		blockingPublishThread.join();
		nonBlockingPublishThread.join();
	}

}
