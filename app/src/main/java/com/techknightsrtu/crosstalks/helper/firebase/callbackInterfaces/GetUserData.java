package com.techknightsrtu.crosstalks.helper.firebase.callbackInterfaces;

import com.techknightsrtu.crosstalks.models.User;

public interface GetUserData {

    void onCallback(User user, String collegeName);
}
