package com.example.spa_AdminPanel.repository;

import com.example.spa_AdminPanel.model.Cita;
import com.example.spa_AdminPanel.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    @Query("SELECT c FROM Cita c WHERE c.cliente.id_cliente = :idCliente AND c.estado = :estado")
    List<Cita> findByClienteYEstado(
            @Param("idCliente") Long idCliente,
            @Param("estado") EstadoCita estado
    );

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM Cita c
            WHERE c.terapeuta.id_terapeuta = :idTerapeuta
              AND c.estado IN :estados
              AND c.fecha_hora_inicio < :fin
              AND c.fecha_hora_fin > :inicio
            """)
    boolean existeConflictoDeTerapeuta(
            @Param("idTerapeuta") Long idTerapeuta,
            @Param("estados") List<EstadoCita> estados,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM Cita c
            WHERE c.sala.id_sala = :idSala
              AND c.estado IN :estados
              AND c.fecha_hora_inicio < :fin
              AND c.fecha_hora_fin > :inicio
            """)
    boolean existeConflictoDeSala(
            @Param("idSala") Long idSala,
            @Param("estados") List<EstadoCita> estados,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}
