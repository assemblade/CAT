package com.assemblade.client.model;

public class PasswordPolicy extends AuthenticationPolicy {
    private boolean forceChangeOnReset;

    @Override
    public String getType() {
        return "password";
    }

    public boolean isForceChangeOnReset() {
        return forceChangeOnReset;
    }

    public void setForceChangeOnReset(boolean forceChangeOnReset) {
        this.forceChangeOnReset = forceChangeOnReset;
    }

}
