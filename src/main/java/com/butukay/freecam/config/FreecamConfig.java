package com.butukay.freecam.config;

public class FreecamConfig {
    private int flySpeed = 10;
    private boolean enableCommand = false;
    private String commandName = ".freecam";
    private boolean actionBar = true;

    public int getFlySpeed() {
        return flySpeed;
    }

    public void setFlySpeed(int flySpeed) {
        this.flySpeed = flySpeed;
    }

    public boolean isEnableCommand() {
        return enableCommand;
    }

    public void setEnableCommand(boolean enableCommand) {
        this.enableCommand = enableCommand;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public boolean isActionBar() {
        return actionBar;
    }

    public void setActionBar(boolean actionBar) {
        this.actionBar = actionBar;
    }
}
