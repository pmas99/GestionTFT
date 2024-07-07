package com.gestionTFT.GestionTFT.entity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "titulacion")
public class Titulacion {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "titulacion")
    private Set<Tft> tfts = new LinkedHashSet<>();

    public Set<Tft> getTfts() {
        return tfts;
    }

    public void setTfts(Set<Tft> tfts) {
        this.tfts = tfts;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}