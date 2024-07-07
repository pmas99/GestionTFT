package com.gestionTFT.GestionTFT.entity;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ROLE_Alumno, ROLE_Tutor, ROLE_PAS, ROLE_Secretario
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "departamento")
    private Departamento departamento;

    @Column(name = "password", nullable = false)
    private String password;

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email +'\'' +
                ", rol='" + role +
                '}';
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRol(Role role) {
        this.role = role;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}