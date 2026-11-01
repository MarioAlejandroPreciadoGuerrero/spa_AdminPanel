package com.example.spa_AdminPanel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sucursales")
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_sucursal;

    @Column
    private String nombre;

    @Column
    private String direccion;

    @Column
    private String colonia;

    @Column
    private String codigo_postal;

    @Column
    private String telefono;

    @Column
    private LocalTime hora_apertura;

    @Column
    private LocalTime hora_cierre;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<Terapeuta> terapeutas;
}
