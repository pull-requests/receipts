package com.alexfu.onereceiptcamera.service;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public interface UploadImageApi {
    @POST("/upload") @Multipart
    void upload(@Part("file") TypedFile file, @Part("email") TypedString email, Callback<UploadImageServiceResponse> cb);
}
