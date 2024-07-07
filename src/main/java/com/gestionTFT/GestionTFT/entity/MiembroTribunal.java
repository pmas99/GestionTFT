package com.gestionTFT.GestionTFT.entity;

import com.gestionTFT.GestionTFT.entity.Temporal.MiembroTribunalId;

import javax.persistence.*;

@Entity
@Table(name = "miembrotribunal")
public class MiembroTribunal {
    @EmbeddedId
    private MiembroTribunalId id;

    @MapsId("idTribunal")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tribunal", nullable = false)
    private Tribunal idCourt;

    @MapsId("idUsuario")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User idUser;

    public MiembroTribunalId getId() {
        return id;
    }

    public void setId(MiembroTribunalId id) {
        this.id = id;
    }

    public Tribunal getIdTribunal() {
        return idCourt;
    }

    public void setIdTribunal(Tribunal idCourt) {
        this.idCourt = idCourt;
    }

    public User getIdUsuario() {
        return idUser;
    }

    public void setIdUsuario(User idUser) {
        this.idUser = idUser;
    }

}