package com.example.spa_AdminPanel.repository;

import com.example.spa_AdminPanel.model.Terapeuta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerapeutaRepository extends JpaRepository<Terapeuta, Long> {
    List<Terapeuta> findDistinctByServicios_NombreIgnoreCase(String servicio);
}
