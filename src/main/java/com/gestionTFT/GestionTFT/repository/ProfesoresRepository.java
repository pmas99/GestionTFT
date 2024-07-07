package com.gestionTFT.GestionTFT.repository;

import com.gestionTFT.GestionTFT.entity.Departamento;
import com.gestionTFT.GestionTFT.entity.Profesores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfesoresRepository extends JpaRepository<Profesores, Integer> {

    public List<Profesores> findByNombre(String nombre);
}
