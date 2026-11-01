package com.example.spa_AdminPanel.repository;

import com.example.spa_AdminPanel.model.NombreRol;
import com.example.spa_AdminPanel.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(NombreRol nombre);
}
