package com.shevirah.androidagent;

import android.app.Application;
import android.content.Context;

/**
 * Created by administrator on 12/7/16.
 */

public class AgentApplication extends Application {

    public static AgentApplication instance = null;


    public static Context getInstance() {
        if (null == instance) {
            instance = new AgentApplication();
        }
        return instance;
    }
}
