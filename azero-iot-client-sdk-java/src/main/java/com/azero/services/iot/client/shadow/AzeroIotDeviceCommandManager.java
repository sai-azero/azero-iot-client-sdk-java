package com.azero.services.iot.client.shadow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.azero.services.iot.client.AZEROIotDeviceErrorCode;
import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotTimeoutException;
import com.azero.services.iot.client.core.AzeroIotRuntimeException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class manages the commands sent to the shadow. It maintains a list of
 * pending commands that are yet to be accepted or rejected by the shadow. Upon
 * receiving the shadow response for a command, it will notify therefore resume
 * the execution of the caller.
 */
public class AzeroIotDeviceCommandManager {
    private static final Logger LOGGER = Logger.getLogger(AzeroIotDeviceCommandManager.class.getName());
    private static final String TOPIC_PREFIX = "$pub/{accountPrefix}/$azero/things/{thingName}/shadow/update/documents";
    private static final String COMMAND_ID_FIELD = "clientToken";
    private static final String ERROR_CODE_FIELD = "code";
    private static final String ERROR_MESSAGE_FIELD = "message";
    private static final Map<Command, String> COMMAND_PATHS;
    private static final Map<CommandAck, String> COMMAND_ACK_PATHS;
    private static final Pattern commandPattern;
    private static final Pattern deltaPattern;
    private final ConcurrentMap<String, AzeroIotDeviceCommand> pendingCommands;
    private final AbstractAzeroIotDevice device;
    private final ObjectMapper objectMapper;


    public static enum Command {
        GET, UPDATE, DELETE, DELTA;
    }


    public static enum CommandAck {
        ACCEPTED, REJECTED;
    }

    static {
        COMMAND_PATHS = new HashMap<Command, String>();
        COMMAND_PATHS.put(Command.GET, "/get");
        COMMAND_PATHS.put(Command.UPDATE, "/update");
        COMMAND_PATHS.put(Command.DELETE, "/delete");
        COMMAND_PATHS.put(Command.DELTA, "/update/delta");
        COMMAND_ACK_PATHS = new HashMap<CommandAck, String>();
        COMMAND_ACK_PATHS.put(CommandAck.ACCEPTED, "/accepted");
        COMMAND_ACK_PATHS.put(CommandAck.REJECTED, "/rejected");
        commandPattern = Pattern.compile("^\\$azero/things/[^/]+/shadow/(get|update|delete)/(?:accepted|rejected)$");
        deltaPattern = Pattern.compile("^\\$azero/things/[^/]+/shadow/update/delta$");
    }

    public AzeroIotDeviceCommandManager(AbstractAzeroIotDevice device) {
        this.pendingCommands = new ConcurrentHashMap<>();
        this.device = device;
        this.objectMapper = new ObjectMapper();
    }

    public String getTopic(Command command, CommandAck ack) {
        String topic = TOPIC_PREFIX.replace("{thingName}", device.getThingName()).replace("{accountPrefix}", device.getAccountPrefix());
        if (COMMAND_PATHS.containsKey(command)) {
            topic += COMMAND_PATHS.get(command);
        }
        if (COMMAND_ACK_PATHS.containsKey(ack)) {
            topic += COMMAND_ACK_PATHS.get(ack);
        }
        return topic;
    }

    public String runCommandSync(Command command, AZEROIotMessage request) throws AZEROIotException {
        try {
            return runCommand(command, request, 0, false);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AzeroIotRuntimeException(e);
        }
    }

    public String runCommandSync(Command command, AZEROIotMessage request, long commandTimeout) throws AZEROIotException, AZEROIotTimeoutException {
        return runCommand(command, request, commandTimeout, false);
    }

    public String runCommand(Command command, AZEROIotMessage request, long commandTimeout) throws AZEROIotException {
        try {
            return runCommand(command, request, commandTimeout, true);
        } catch (AZEROIotTimeoutException e) {
            // We shouldn't get timeout exception because it's asynchronous call
            throw new AzeroIotRuntimeException(e);
        }
    }

    public String runCommand(Command command, AZEROIotMessage request, long commandTimeout, boolean isAsync) throws AZEROIotException, AZEROIotTimeoutException {
        String commandId = newCommandId();
        appendCommandId(request, commandId);
        request.setTopic(getTopic(command, null));
        AzeroIotDeviceCommand deviceCommand = new AzeroIotDeviceCommand(this, command, commandId, request, commandTimeout, isAsync);
        pendingCommands.put(commandId, deviceCommand);
        LOGGER.fine("Number of pending commands: " + pendingCommands.size());
        try {
            deviceCommand.put(device);
        } catch (AZEROIotException e) {
            // if exception happens during publish, we remove the command
            // from the pending list as we'll never get ack for it.
            pendingCommands.remove(commandId);
            throw e;
        }
        return deviceCommand.get(device);
    }

    public void onCommandAck(AZEROIotMessage response) {
        if (response == null || response.getTopic() == null) {
            return;
        }
        AzeroIotDeviceCommand command = getPendingCommand(response);
        if (command == null) {
            LOGGER.warning("Unknown command received from topic " + response.getTopic());
            return;
        }
        boolean success = response.getTopic().endsWith(COMMAND_ACK_PATHS.get(CommandAck.ACCEPTED));
        if (!success && (Command.DELETE.equals(command.getCommand()) && AZEROIotDeviceErrorCode.NOT_FOUND.equals(command.getErrorCode()))) {
            // Ignore empty document error (NOT_FOUND) for delete command
            success = true;
        }
        if (success) {
            command.setResponse(response);
            command.onSuccess();
        } else {
            command.onFailure();
        }
    }

    public void onCommandTimeout(AzeroIotDeviceCommand command) {
        pendingCommands.remove(command.getCommandId());
    }

    public void onSubscriptionAck(String topic, boolean success) {
        boolean ready = false;
        Command command = getCommandFromTopic(topic);
        if (command == null) {
            return;
        }
        String accepted = getTopic(command, CommandAck.ACCEPTED);
        String rejected = getTopic(command, CommandAck.REJECTED);
        if (accepted.equals(topic) || rejected.equals(topic)) {
            if (success && device.isTopicReady(accepted) && device.isTopicReady(rejected)) {
                ready = true;
            }
        }
        Iterator<Entry<String, AzeroIotDeviceCommand>> it = pendingCommands.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, AzeroIotDeviceCommand> entry = it.next();
            AzeroIotDeviceCommand deviceCommand = entry.getValue();
            boolean failCommand = false;
            if (command.equals(deviceCommand.getCommand())) {
                if (ready) {
                    if (!deviceCommand.onReady(device)) {
                        failCommand = true;
                    }
                } else if (!success) {
                    failCommand = true;
                }
            }
            if (failCommand) {
                it.remove();
                deviceCommand.onFailure();
            }
        }
    }

    public void onDeactivate() {
        Iterator<Entry<String, AzeroIotDeviceCommand>> it = pendingCommands.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, AzeroIotDeviceCommand> entry = it.next();
            it.remove();
            final AzeroIotDeviceCommand deviceCommand = entry.getValue();
            LOGGER.warning("Request was cancelled: " + deviceCommand.getCommand().name() + "/" + deviceCommand.getCommandId());
            device.getClient().scheduleTask(new Runnable() {
                @Override
                public void run() {
                    deviceCommand.onFailure();
                }
            });
        }
    }

    public boolean isDeltaTopic(String topic) {
        if (topic == null) {
            return false;
        }
        Matcher matcher = deltaPattern.matcher(topic);
        return matcher.matches();
    }

    private Command getCommandFromTopic(String topic) {
        if (topic == null) {
            return null;
        }
        Matcher matcher = commandPattern.matcher(topic);
        if (matcher.find()) {
            String name = matcher.group(1);
            return Command.valueOf(name.toUpperCase());
        }
        return null;
    }

    private void appendCommandId(AZEROIotMessage message, String commandId) throws AZEROIotException {
        String payload = message.getStringPayload();
        if (payload == null) {
            payload = "{}";
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            if (!jsonNode.isObject()) {
                throw new AZEROIotException("Invalid Json string in payload");
            }
            ((ObjectNode) jsonNode).put(COMMAND_ID_FIELD, commandId);
            message.setStringPayload(jsonNode.toString());
        } catch (IOException e) {
            throw new AZEROIotException(e);
        }
    }

    private AzeroIotDeviceCommand getPendingCommand(AZEROIotMessage message) {
        String payload = message.getStringPayload();
        if (payload == null) {
            return null;
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            if (!jsonNode.isObject()) {
                return null;
            }
            JsonNode node = jsonNode.get(COMMAND_ID_FIELD);
            if (node == null) {
                return null;
            }
            String commandId = node.textValue();
            AzeroIotDeviceCommand command = pendingCommands.remove(commandId);
            if (command == null) {
                return null;
            }
            node = jsonNode.get(ERROR_CODE_FIELD);
            if (node != null) {
                command.setErrorCode(AZEROIotDeviceErrorCode.valueOf(node.longValue()));
            }
            node = jsonNode.get(ERROR_MESSAGE_FIELD);
            if (node != null) {
                command.setErrorMessage(node.textValue());
            }
            return command;
        } catch (IOException e) {
            return null;
        }
    }

    private String newCommandId() {
        return UUID.randomUUID().toString();
    }

    @java.lang.SuppressWarnings("all")
    public ConcurrentMap<String, AzeroIotDeviceCommand> getPendingCommands() {
        return this.pendingCommands;
    }

    @java.lang.SuppressWarnings("all")
    public AbstractAzeroIotDevice getDevice() {
        return this.device;
    }

    @java.lang.SuppressWarnings("all")
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }
}
