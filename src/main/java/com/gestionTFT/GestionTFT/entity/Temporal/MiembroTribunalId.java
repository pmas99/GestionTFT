package com.gestionTFT.GestionTFT.entity.Temporal;

import javax.persistence.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class MiembroTribunalId implements java.io.Serializable {
    private static final long serialVersionUID = -9060852710945783664L;
    @Column(name = "id_tribunal", nullable = false)
    private Integer idTribunal;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    public Integer getIdTribunal() {
        return idTribunal;
    }

    public void setIdTribunal(Integer idTribunal) {
        this.idTribunal = idTribunal;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MiembroTribunalId entity = (MiembroTribunalId) o;
        return Objects.equals(this.idUsuario, entity.idUsuario) &&
                Objects.equals(this.idTribunal, entity.idTribunal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idTribunal);
    }

}