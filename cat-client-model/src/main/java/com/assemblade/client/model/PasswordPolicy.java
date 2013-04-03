package com.assemblade.client.model;

import java.io.Serializable;

public class PasswordPolicy implements Serializable {
    private static final long serialVersionUID = -5364038640494616148L;

    private String url;
    private boolean forceChangeOnReset;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isForceChangeOnReset() {
        return forceChangeOnReset;
    }

    public void setForceChangeOnReset(boolean forceChangeOnReset) {
        this.forceChangeOnReset = forceChangeOnReset;
    }

}
