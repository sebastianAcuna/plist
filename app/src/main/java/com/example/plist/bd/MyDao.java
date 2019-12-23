package com.example.plist.bd;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.plist.Fotos;

import java.util.List;

@Dao
public  interface MyDao {


    @Insert()
    long insertFoto(Fotos fotos);



    @Query("SELECT  * FROM fotos WHERE nombre_plist = :nombrePlist ORDER BY id DESC LIMIT 1")
    Fotos getFotosById(String nombrePlist);

    @Query("SELECT * FROM fotos WHERE estados = 0")
    List<Fotos> getFotoByEstado();

    @Query("UPDATE fotos SET estados = 1 WHERE id = :idFoto")
    int updateFotoByIdAndEstado(long idFoto);

}
