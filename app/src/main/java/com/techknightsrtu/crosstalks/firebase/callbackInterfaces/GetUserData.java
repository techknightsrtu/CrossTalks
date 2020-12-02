package com.techknightsrtu.crosstalks.firebase.callbackInterfaces;

import com.techknightsrtu.crosstalks.app.models.User;

public interface GetUserData {

    void onCallback(User user, String collegeName);
}
