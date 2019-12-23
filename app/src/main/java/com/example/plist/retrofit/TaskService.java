package com.example.plist.retrofit;


import com.example.plist.Respuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TaskService {

        @FormUrlEncoded
        @POST("post_fotos_plist.php")
        Call<Respuesta> uploadImages(@Field("id_fotos") long idFoto,
                                     @Field("titulo") String title,
                                     @Field("image") String image,
                                     @Field("nombre_plist") String dispo,
                                     @Field("nombre_container") String container);




}
