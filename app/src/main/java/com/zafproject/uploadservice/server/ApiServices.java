package com.zafproject.uploadservice.server;

import com.zafproject.uploadservice.model.ResponseApiModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiServices {
    //todo 17.
    @Multipart
    @POST("uploadimage.php")
    Call<ResponseApiModel> uploadImage (@Part MultipartBody.Part image, @Part("name") RequestBody fname);

//    @Multipart
//    @POST("uploadimage.php")
//    Call<ResponseApiModel> uploadImage (@Part MultipartBody.Part image);

//    //todo 24.
//    @GET("getDataImage.php")
//    Call<ResponseGetData> getAllImage();
//

//    //todo 53.
//    @FormUrlEncoded
//    @POST("deleteimage.php")
//    Call<ResponseDelete> deleteData(@Field("id") String id);
//
//    //todo 48.
//    @FormUrlEncoded
//    @POST("updateimage.php")
//    Call<ResponseGambarNoImage> updateDataNoImageChanged(@Field("id") String id,
//                                                         @Field("name") String nama);
//
//    //todo 45.
//    @Multipart
//    @POST("updateimage.php")
//    Call<ResponseApiModel> uploadImageUpdate (@Part MultipartBody.Part image, @Part("name") RequestBody nama, @Part("isNewImage") RequestBody isNewImage);
//

}




