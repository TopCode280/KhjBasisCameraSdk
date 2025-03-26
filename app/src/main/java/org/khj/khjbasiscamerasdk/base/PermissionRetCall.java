package org.khj.khjbasiscamerasdk.base;

public interface PermissionRetCall {

    void onAllow();

    default void onRefuse(boolean never) {
    }
}
