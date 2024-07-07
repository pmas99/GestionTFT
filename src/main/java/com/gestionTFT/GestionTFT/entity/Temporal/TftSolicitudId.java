package com.gestionTFT.GestionTFT.entity.Temporal;

import javax.persistence.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class TftSolicitudId implements java.io.Serializable {
    private static final long serialVersionUID = -4885733507037037291L;
    @Column(name = "id_tft", nullable = false)
    private Integer idTft;

    @Column(name = "id_solicitud", nullable = false)
    private Integer idSolicitud;

    public Integer gettft() {
        return idTft;
    }

    public void settft(Integer idTft) {
        this.idTft = idTft;
    }

    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TftSolicitudId entity = (TftSolicitudId) o;
        return Objects.equals(this.idTft, entity.idTft) &&
                Objects.equals(this.idSolicitud, entity.idSolicitud);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTft, idSolicitud);
    }

}