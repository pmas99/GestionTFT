package com.gestionTFT.GestionTFT.repository;

import com.gestionTFT.GestionTFT.entity.Solicitud;
import com.gestionTFT.GestionTFT.entity.Tft;
import com.gestionTFT.GestionTFT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    List<Solicitud> findByTutor(User user);

    List<Solicitud> findByTutorAndTftAndTipo(User tutor, Tft tft, Solicitud.Tipo tipo);

    List<Solicitud> findByTutorAndTft(User tutor, Tft tft);

    List<Solicitud> findByTutorAndTipo(User tutor, Solicitud.Tipo tipo);

    List<Solicitud> findByTft(Tft tf);

    void deleteAllByTft(Tft tft);

    List<Solicitud> findByAlumno(User alumno);

    List<Solicitud> findByAlumnoAndTftAndTipo(User alumno, Tft tft, Solicitud.Tipo tipo);

    List<Solicitud> findByAlumnoAndTft(User alumno, Tft tft);

    List<Solicitud> findByAlumnoAndTipo(User tutor, Solicitud.Tipo tipo);

    List<Solicitud> findByTftAndTipo(Tft tft, Solicitud.Tipo tipo);

    List<Solicitud> findByTipo(Solicitud.Tipo tipoEntity);
}
