package com.gestionTFT.GestionTFT.repository;

import com.gestionTFT.GestionTFT.entity.Tribunal;
import com.gestionTFT.GestionTFT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TribunalRepository extends JpaRepository<Tribunal, Integer> {
    List<Tribunal> findBySecretario(User user);
}
