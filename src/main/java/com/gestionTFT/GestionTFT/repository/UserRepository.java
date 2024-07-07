package com.gestionTFT.GestionTFT.repository;

import com.gestionTFT.GestionTFT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findByNombre(String nombre);
    List<User> findAllByRole(User.Role role);
}
