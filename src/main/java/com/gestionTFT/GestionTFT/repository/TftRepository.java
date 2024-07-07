package com.gestionTFT.GestionTFT.repository;

import com.gestionTFT.GestionTFT.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TftRepository extends JpaRepository<Tft, Integer> {

    Tft findByTitulo(String titulo);
    List<Tft> findByAlumno(User user);
    List<Tft> findByTutor(User user);
    List<Tft> findByEstado(String estado);
    Tft findById(int id);
    List<Tft> findByTutorAndEstado(User user, String estado);
    List<Tft> findByAlumnoAndTitulacion(User user, Titulacion titulacion);
    List<Tft> findByTribunal(Tribunal tribunal);

    @Query("SELECT t FROM Tft t WHERE t.alumno = :alumno AND t.titulacion = :titulacion AND t.estado NOT IN :estados")
    Tft findByAlumnoAndTitulacionAndEstadoNotIn(User alumno, Titulacion titulacion, Collection<String> estados);
}
