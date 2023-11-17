package com.growit.posapp.fstore.interfaces;

import java.io.IOException;

public interface ApiResponseListener {
    void onResponse(String response, int requestCode) throws IOException;

    void onError(String error, int requestCode);
}
