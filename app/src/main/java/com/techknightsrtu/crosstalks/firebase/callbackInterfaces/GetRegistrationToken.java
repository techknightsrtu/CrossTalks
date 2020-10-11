package com.techknightsrtu.crosstalks.firebase.callbackInterfaces;

import java.util.List;

public interface GetRegistrationToken {
    void onCallback(List<String> tokens);
}
