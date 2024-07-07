package com.gestionTFT.GestionTFT.repository;

import com.gestionTFT.GestionTFT.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
}
