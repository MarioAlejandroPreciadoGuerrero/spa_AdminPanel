package com.example.spa_AdminPanel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "horario_terapeuta")
public class HorarioTerapeuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_horario_terapeuta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_terapeuta", nullable = false)
    private Terapeuta terapeuta;

    @Column(nullable = false)
    private String dias_trabajo;

    @Column
    private LocalTime hora_inicio;

    @Column
    private LocalTime hora_fin;

    private void validarHorarioDentroDeSucursal() {
        if (terapeuta == null || terapeuta.getSucursal() == null) {
            throw new IllegalStateException("El horario debe pertenecer a un terapeuta asignado a una sucursal");
        }

        Sucursal sucursal = terapeuta.getSucursal();
        if (hora_inicio == null || hora_fin == null
                || sucursal.getHora_apertura() == null || sucursal.getHora_cierre() == null
                || !hora_inicio.isBefore(hora_fin)
                || hora_inicio.isBefore(sucursal.getHora_apertura())
                || hora_fin.isAfter(sucursal.getHora_cierre())) {
            throw new IllegalStateException("El horario del terapeuta debe quedar dentro del horario de su sucursal");
        }
    }

}
