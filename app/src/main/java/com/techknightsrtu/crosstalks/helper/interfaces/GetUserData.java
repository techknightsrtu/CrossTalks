package com.techknightsrtu.crosstalks.helper.interfaces;

import com.techknightsrtu.crosstalks.models.User;

import java.util.Map;

public interface GetUserData {

    void onCallback(User user, String collegeName);
}
