package com.azero.services.iot.client.shadow;

import java.util.logging.Logger;

import com.azero.services.iot.client.AZEROIotException;
import com.azero.services.iot.client.AZEROIotMessage;
import com.azero.services.iot.client.AZEROIotTimeoutException;
import com.azero.services.iot.client.core.AzeroIotCompletion;
import com.azero.services.iot.client.shadow.AzeroIotDeviceCommandManager.Command;

/**
 * This is a helper class that can be used to manage the execution result of a
 * shadow command, i.e. get, update, and delete. It makes sure that the command
 * is not published until the subscription requests for the acknowledgment
 * topics, namely accepted and rejected, have completed successfully.
 * 
 * @see com.azero.services.iot.client.core.AzeroIotCompletion
 */
public class AzeroIotDeviceCommand extends AzeroIotCompletion {
    private static final Logger LOGGER = Logger.getLogger(AzeroIotDeviceCommand.class.getName());
    private final AzeroIotDeviceCommandManager commandManager;
    private final Command command;
    private final String commandId;
    private AZEROIotMessage response;
    private Boolean requestSent;

    public AzeroIotDeviceCommand(AzeroIotDeviceCommandManager commandManager, Command command, String commandId, AZEROIotMessage request, long commandTimeout, boolean isAsync) {
        super(request, commandTimeout, isAsync);
        this.commandManager = commandManager;
        this.command = command;
        this.commandId = commandId;
        this.requestSent = false;
    }

    public void put(AbstractAzeroIotDevice device) throws AZEROIotException {
        if (device.isCommandReady(command)) {
            _put(device);
        } else {
            LOGGER.info("Request is pending: " + command.name() + "/" + commandId);
        }
    }

    public String get(AbstractAzeroIotDevice device) throws AZEROIotException, AZEROIotTimeoutException {
        super.get(device.getClient());
        return (response != null) ? response.getStringPayload() : null;
    }

    public boolean onReady(AbstractAzeroIotDevice device) {
        try {
            LOGGER.info("Request is resumed: " + command.name() + "/" + commandId);
            _put(device);
            return true;
        } catch (AZEROIotException e) {
            return false;
        }
    }

    @Override
    public void onSuccess() {
        // first callback is for the command ack, which we ignore
        if (response == null) {
            return;
        } else {
            request.setPayload(response.getPayload());
        }
        super.onSuccess();
    }

    @Override
    public void onFailure() {
        super.onFailure();
    }

    @Override
    public void onTimeout() {
        commandManager.onCommandTimeout(this);
        super.onTimeout();
    }

    private void _put(AbstractAzeroIotDevice device) throws AZEROIotException {
        synchronized (this) {
            if (requestSent) {
                LOGGER.warning("Request was already sent: " + command.name() + "/" + commandId);
                return;
            } else {
                requestSent = true;
            }
        }
        device.getClient().publish(this, timeout);
    }

    @java.lang.SuppressWarnings("all")
    public AzeroIotDeviceCommandManager getCommandManager() {
        return this.commandManager;
    }

    @java.lang.SuppressWarnings("all")
    public Command getCommand() {
        return this.command;
    }

    @java.lang.SuppressWarnings("all")
    public String getCommandId() {
        return this.commandId;
    }

    @java.lang.SuppressWarnings("all")
    public AZEROIotMessage getResponse() {
        return this.response;
    }

    @java.lang.SuppressWarnings("all")
    public Boolean getRequestSent() {
        return this.requestSent;
    }

    @java.lang.SuppressWarnings("all")
    public void setResponse(final AZEROIotMessage response) {
        this.response = response;
    }
}
