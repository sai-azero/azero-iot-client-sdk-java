package com.azero.services.iot.client.shadow;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.azero.services.iot.client.AZEROIotConfig;
import com.azero.services.iot.client.AZEROIotDevice;
import com.azero.services.iot.client.AZEROIotDeviceProperty;
import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotQos;
import com.azero.services.iot.client.AZEROIotTimeoutException;
import com.azero.services.iot.client.AZEROIotTopic;
import com.azero.services.iot.client.core.AbstractAzeroIotClient;
import com.azero.services.iot.client.shadow.AzeroIotDeviceCommandManager.Command;
import com.azero.services.iot.client.shadow.AzeroIotDeviceCommandManager.CommandAck;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * The actual implementation of {@link AZEROIotDevice}.
 */
public abstract class AbstractAzeroIotDevice {
    private static final Logger LOGGER = Logger.getLogger(AbstractAzeroIotDevice.class.getName());
    protected final String thingName;
    protected final String accountPrefix;
    protected long reportInterval = AZEROIotConfig.DEVICE_REPORT_INTERVAL;
    protected boolean enableVersioning = AZEROIotConfig.DEVICE_ENABLE_VERSIONING;
    protected AZEROIotQos deviceReportQos = AZEROIotQos.valueOf(AZEROIotConfig.DEVICE_REPORT_QOS);
    protected AZEROIotQos shadowUpdateQos = AZEROIotQos.valueOf(AZEROIotConfig.DEVICE_SHADOW_UPDATE_QOS);
    protected AZEROIotQos methodQos = AZEROIotQos.valueOf(AZEROIotConfig.DEVICE_METHOD_QOS);
    protected AZEROIotQos methodAckQos = AZEROIotQos.valueOf(AZEROIotConfig.DEVICE_METHOD_ACK_QOS);
    private final Map<String, Field> reportedProperties;
    private final Map<String, Field> updatableProperties;
    private final AzeroIotDeviceCommandManager commandManager;
    private final ConcurrentMap<String, Boolean> deviceSubscriptions;
    private final ObjectMapper jsonObjectMapper;
    private AbstractAzeroIotClient client;
    private Future<?> syncTask;
    private AtomicLong localVersion;

    protected AbstractAzeroIotDevice(String thingName,String accountPrefix) {
        this.thingName = thingName;
        this.accountPrefix = accountPrefix;
        reportedProperties = getDeviceProperties(true, false);
        updatableProperties = getDeviceProperties(false, true);
        commandManager = new AzeroIotDeviceCommandManager(this);
        deviceSubscriptions = new ConcurrentHashMap<>();
        for (String topic : getDeviceTopics()) {
            deviceSubscriptions.put(topic, false);
        }
        jsonObjectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(AbstractAzeroIotDevice.class, new AzeroIotJsonSerializer());
        jsonObjectMapper.registerModule(module);
        localVersion = new AtomicLong(-1);
    }

    protected AbstractAzeroIotDevice getDevice() {
        return this;
    }

    protected String get() throws AZEROIotException {
        AZEROIotMessage message = new AZEROIotMessage(null, methodQos);
        return commandManager.runCommandSync(Command.GET, message);
    }

    protected String get(long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        AZEROIotMessage message = new AZEROIotMessage(null, methodQos);
        return commandManager.runCommandSync(Command.GET, message, timeout);
    }

    protected void get(AZEROIotMessage message, long timeout) throws AZEROIotException {
        commandManager.runCommand(Command.GET, message, timeout);
    }

    protected void update(String jsonState) throws AZEROIotException {
        AZEROIotMessage message = new AZEROIotMessage(null, methodQos, jsonState);
        commandManager.runCommandSync(Command.UPDATE, message);
    }

    protected void update(String jsonState, long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        AZEROIotMessage message = new AZEROIotMessage(null, methodQos, jsonState);
        commandManager.runCommandSync(Command.UPDATE, message, timeout);
    }

    protected void update(AZEROIotMessage message, long timeout) throws AZEROIotException {
        commandManager.runCommand(Command.UPDATE, message, timeout);
    }

    protected void delete() throws AZEROIotException {
        AZEROIotMessage message = new AZEROIotMessage(null, methodQos);
        commandManager.runCommandSync(Command.DELETE, message);
    }

    protected void delete(long timeout) throws AZEROIotException, AZEROIotTimeoutException {
        AZEROIotMessage message = new AZEROIotMessage(null, methodQos);
        commandManager.runCommandSync(Command.DELETE, message, timeout);
    }

    protected void delete(AZEROIotMessage message, long timeout) throws AZEROIotException {
        commandManager.runCommand(Command.DELETE, message, timeout);
    }

    protected void onShadowUpdate(String jsonState) {
        // synchronized block to serialize device accesses
        synchronized (this) {
            try {
                AzeroIotJsonDeserializer.deserialize(this, jsonState);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to update device", e);
            }
        }
    }

    protected String onDeviceReport() {
        // synchronized block to serialize device accesses
        synchronized (this) {
            try {
                return jsonObjectMapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.WARNING, "Failed to generate device report", e);
                return null;
            }
        }
    }

    public void activate() throws AZEROIotException {
        stopSync();
        for (String topic : getDeviceTopics()) {
            AZEROIotTopic azeroIotTopic;
            if (commandManager.isDeltaTopic(topic)) {
                azeroIotTopic = new AzeroIotDeviceDeltaListener(topic, shadowUpdateQos, this);
            } else {
                azeroIotTopic = new AzeroIotDeviceCommandAckListener(topic, methodAckQos, this);
            }
            client.subscribe(azeroIotTopic, client.getServerAckTimeout());
        }
        startSync();
    }

    public void deactivate() throws AZEROIotException {
        stopSync();
        commandManager.onDeactivate();
        for (String topic : getDeviceTopics()) {
            deviceSubscriptions.put(topic, false);
            AZEROIotTopic azeroIotTopic = new AZEROIotTopic(topic);
            client.unsubscribe(azeroIotTopic, client.getServerAckTimeout());
        }
    }

    public boolean isTopicReady(String topic) {
        Boolean status = deviceSubscriptions.get(topic);
        return Boolean.TRUE.equals(status);
    }

    public boolean isCommandReady(Command command) {
        Boolean accepted = deviceSubscriptions.get(commandManager.getTopic(command, CommandAck.ACCEPTED));
        Boolean rejected = deviceSubscriptions.get(commandManager.getTopic(command, CommandAck.REJECTED));
        return (Boolean.TRUE.equals(accepted) && Boolean.TRUE.equals(rejected));
    }

    public void onSubscriptionAck(String topic, boolean success) {
        deviceSubscriptions.put(topic, success);
        commandManager.onSubscriptionAck(topic, success);
    }

    public void onCommandAck(AZEROIotMessage message) {
        commandManager.onCommandAck(message);
    }

    protected void startSync() {
        // don't start the publish task if no properties are to be published
        if (reportedProperties.isEmpty() || reportInterval <= 0) {
            return;
        }
        syncTask = client.scheduleRoutineTask(new Runnable() {
            @Override
            public void run() {
                if (!isCommandReady(Command.UPDATE)) {
                    LOGGER.fine("Device not ready for reporting");
                    return;
                }
                long reportVersion = localVersion.get();
                if (enableVersioning && reportVersion < 0) {
                    // if versioning is enabled, synchronize the version first
                    LOGGER.fine("Starting version sync");
                    startVersionSync();
                    return;
                }
                String jsonState = onDeviceReport();
                if (jsonState != null) {
                    LOGGER.fine("Sending device report");
                    sendDeviceReport(reportVersion, jsonState);
                }
            }
        }, 0L, reportInterval);
    }

    protected void stopSync() {
        if (syncTask != null) {
            syncTask.cancel(false);
            syncTask = null;
        }
        localVersion.set(-1);
    }

    protected void startVersionSync() {
        localVersion.set(-1);
        AzeroIotDeviceSyncMessage message = new AzeroIotDeviceSyncMessage(null, shadowUpdateQos, this);
        try {
            commandManager.runCommand(Command.GET, message, client.getServerAckTimeout(), true);
        } catch (AZEROIotTimeoutException e) {
        } catch (
        // async command, shouldn't receive timeout exception
        AZEROIotException e) {
            LOGGER.log(Level.WARNING, "Failed to publish version update message", e);
        }
    }

    private void sendDeviceReport(long reportVersion, String jsonState) {
        StringBuilder payload = new StringBuilder("{");
        if (enableVersioning) {
            payload.append("\"version\":").append(reportVersion).append(",");
        }
        payload.append("\"state\":{\"reported\":").append(jsonState).append("}}");
        AzeroIotDeviceReportMessage message = new AzeroIotDeviceReportMessage(null, shadowUpdateQos, reportVersion, payload.toString(), this);
        if (enableVersioning && reportVersion != localVersion.get()) {
            LOGGER.warning("Local version number has changed, skip reporting for this round");
            return;
        }
        try {
            commandManager.runCommand(Command.UPDATE, message, client.getServerAckTimeout(), true);
        } catch (AZEROIotTimeoutException e) {
        } catch (
        // async command, shouldn't receive timeout exception
        AZEROIotException e) {
            LOGGER.log(Level.WARNING, "Failed to publish device report message", e);
        }
    }

    private Map<String, Field> getDeviceProperties(boolean enableReport, boolean allowUpdate) {
        Map<String, Field> properties = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            AZEROIotDeviceProperty annotation = field.getAnnotation(AZEROIotDeviceProperty.class);
            if (annotation == null) {
                continue;
            }
            String propertyName = annotation.name().length() > 0 ? annotation.name() : field.getName();
            if ((enableReport && annotation.enableReport()) || (allowUpdate && annotation.allowUpdate())) {
                properties.put(propertyName, field);
            }
        }
        return properties;
    }

    private List<String> getDeviceTopics() {
        List<String> topics = new ArrayList<>();
        topics.add(commandManager.getTopic(Command.DELTA, null));
        topics.add(commandManager.getTopic(Command.GET, CommandAck.ACCEPTED));
        topics.add(commandManager.getTopic(Command.GET, CommandAck.REJECTED));
        topics.add(commandManager.getTopic(Command.UPDATE, CommandAck.ACCEPTED));
        topics.add(commandManager.getTopic(Command.UPDATE, CommandAck.REJECTED));
        topics.add(commandManager.getTopic(Command.DELETE, CommandAck.ACCEPTED));
        topics.add(commandManager.getTopic(Command.DELETE, CommandAck.REJECTED));
        return topics;
    }

    @java.lang.SuppressWarnings("all")
    public String getThingName() {
        return this.thingName;
    }
    
    @java.lang.SuppressWarnings("all")
    public String getAccountPrefix() {
        return this.accountPrefix;
    }

    @java.lang.SuppressWarnings("all")
    public long getReportInterval() {
        return this.reportInterval;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isEnableVersioning() {
        return this.enableVersioning;
    }

    @java.lang.SuppressWarnings("all")
    public AZEROIotQos getDeviceReportQos() {
        return this.deviceReportQos;
    }

    @java.lang.SuppressWarnings("all")
    public AZEROIotQos getShadowUpdateQos() {
        return this.shadowUpdateQos;
    }

    @java.lang.SuppressWarnings("all")
    public AZEROIotQos getMethodQos() {
        return this.methodQos;
    }

    @java.lang.SuppressWarnings("all")
    public AZEROIotQos getMethodAckQos() {
        return this.methodAckQos;
    }

    @java.lang.SuppressWarnings("all")
    public Map<String, Field> getReportedProperties() {
        return this.reportedProperties;
    }

    @java.lang.SuppressWarnings("all")
    public Map<String, Field> getUpdatableProperties() {
        return this.updatableProperties;
    }

    @java.lang.SuppressWarnings("all")
    public AzeroIotDeviceCommandManager getCommandManager() {
        return this.commandManager;
    }

    @java.lang.SuppressWarnings("all")
    public ConcurrentMap<String, Boolean> getDeviceSubscriptions() {
        return this.deviceSubscriptions;
    }

    @java.lang.SuppressWarnings("all")
    public ObjectMapper getJsonObjectMapper() {
        return this.jsonObjectMapper;
    }

    @java.lang.SuppressWarnings("all")
    public AbstractAzeroIotClient getClient() {
        return this.client;
    }

    @java.lang.SuppressWarnings("all")
    public Future<?> getSyncTask() {
        return this.syncTask;
    }

    @java.lang.SuppressWarnings("all")
    public AtomicLong getLocalVersion() {
        return this.localVersion;
    }

    @java.lang.SuppressWarnings("all")
    public void setReportInterval(final long reportInterval) {
        this.reportInterval = reportInterval;
    }

    @java.lang.SuppressWarnings("all")
    public void setEnableVersioning(final boolean enableVersioning) {
        this.enableVersioning = enableVersioning;
    }

    @java.lang.SuppressWarnings("all")
    public void setDeviceReportQos(final AZEROIotQos deviceReportQos) {
        this.deviceReportQos = deviceReportQos;
    }

    @java.lang.SuppressWarnings("all")
    public void setShadowUpdateQos(final AZEROIotQos shadowUpdateQos) {
        this.shadowUpdateQos = shadowUpdateQos;
    }

    @java.lang.SuppressWarnings("all")
    public void setMethodQos(final AZEROIotQos methodQos) {
        this.methodQos = methodQos;
    }

    @java.lang.SuppressWarnings("all")
    public void setMethodAckQos(final AZEROIotQos methodAckQos) {
        this.methodAckQos = methodAckQos;
    }

    @java.lang.SuppressWarnings("all")
    public void setClient(final AbstractAzeroIotClient client) {
        this.client = client;
    }

    @java.lang.SuppressWarnings("all")
    public void setSyncTask(final Future<?> syncTask) {
        this.syncTask = syncTask;
    }

    @java.lang.SuppressWarnings("all")
    public void setLocalVersion(final AtomicLong localVersion) {
        this.localVersion = localVersion;
    }
}
