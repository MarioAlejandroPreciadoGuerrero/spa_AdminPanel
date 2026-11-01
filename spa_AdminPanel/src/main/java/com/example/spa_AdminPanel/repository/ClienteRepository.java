package com.example.spa_AdminPanel.repository;

import com.example.spa_AdminPanel.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuario_NombreIgnoreCase(String nombre);
}
