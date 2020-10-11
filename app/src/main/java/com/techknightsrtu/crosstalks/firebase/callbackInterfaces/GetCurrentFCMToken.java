package com.techknightsrtu.crosstalks.firebase.callbackInterfaces;

import java.lang.invoke.ConstantCallSite;

public interface GetCurrentFCMToken {

    void onCallback(String currToken);

}
