package com.gestionTFT.GestionTFT.repository;

import com.gestionTFT.GestionTFT.entity.Tft;
import com.gestionTFT.GestionTFT.entity.Titulacion;
import com.gestionTFT.GestionTFT.entity.Tribunal;
import com.gestionTFT.GestionTFT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TitulacionRepository extends JpaRepository<Titulacion, Integer> {

}