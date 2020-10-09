package com.techknightsrtu.crosstalks.firebase.callbackInterfaces;

import com.techknightsrtu.crosstalks.models.User;

public interface GetUserData {

    void onCallback(User user, String collegeName);
}
