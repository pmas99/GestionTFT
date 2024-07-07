package com.gestionTFT.GestionTFT.entity;

import com.gestionTFT.GestionTFT.entity.Temporal.TftSolicitudId;

import javax.persistence.*;

@Entity
@Table(name = "tft_solicitud")
public class TftSolicitud {
    @EmbeddedId
    private TftSolicitudId id;

    @MapsId("idTft")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tft", nullable = false)
    private Tft idTft;

    @MapsId("idSolicitud")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud idSolicitud;

    public TftSolicitudId getId() {
        return id;
    }

    public void setId(TftSolicitudId id) {
        this.id = id;
    }

    public Tft gettft() {
        return idTft;
    }

    public void settft(Tft idTft) {
        this.idTft = idTft;
    }

    public Solicitud getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Solicitud idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

}