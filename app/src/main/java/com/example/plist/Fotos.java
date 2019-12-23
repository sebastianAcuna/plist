package com.example.plist;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fotos")
public class Fotos {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "ruta")
    private String ruta;
    @ColumnInfo(name = "nombre")
    private String nombre;
    @ColumnInfo(name = "nombre_plist")
    private String nombrePlist;

    @ColumnInfo(name = "nombre_container")
    private String nombreContainer;
    @ColumnInfo(name = "id_plist_tabla")
    private int idPlistTabla;

    @ColumnInfo(name =  "estados")
    private int estadoFoto;


    public Fotos() { }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombrePlist() {
        return nombrePlist;
    }

    public void setNombrePlist(String nombrePlist) {
        this.nombrePlist = nombrePlist;
    }

    public int getIdPlistTabla() {
        return idPlistTabla;
    }

    public void setIdPlistTabla(int idPlistTabla) {
        this.idPlistTabla = idPlistTabla;
    }

    public int getEstadoFoto() {
        return estadoFoto;
    }

    public void setEstadoFoto(int estadoFoto) {
        this.estadoFoto = estadoFoto;
    }


    public String getNombreContainer() {
        return nombreContainer;
    }

    public void setNombreContainer(String nombreContainer) {
        this.nombreContainer = nombreContainer;
    }
}
