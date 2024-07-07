package com.gestionTFT.GestionTFT.repository;

import com.gestionTFT.GestionTFT.entity.Acta;
import com.gestionTFT.GestionTFT.entity.Tft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActaRepository extends JpaRepository<Acta, Integer> {
    public Acta findByTft(Tft tft);
}
