package com.example.spa_AdminPanel.repository;

import com.example.spa_AdminPanel.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pago p WHERE p.cita.id_cita = :idCita")
    boolean existePagoParaCita(@Param("idCita") Long idCita);
}
