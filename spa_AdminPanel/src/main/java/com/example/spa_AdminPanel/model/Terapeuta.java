package com.example.spa_AdminPanel.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "terapeutas")
public class Terapeuta {

    @Id
    @Column(name = "id_usuario")
    private Long id_terapeuta;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId //vincula la pk de esta entity a la fk de Usuario
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;

    @Column
    private LocalDate fecha_contratacion;

    @ManyToMany
    @JoinTable(
            name = "terapeutas_servicios",
            joinColumns = @JoinColumn(name = "id_terapeuta"),
            inverseJoinColumns = @JoinColumn(name = "id_servicio")
    )
    private List<Servicio> servicios;



}
