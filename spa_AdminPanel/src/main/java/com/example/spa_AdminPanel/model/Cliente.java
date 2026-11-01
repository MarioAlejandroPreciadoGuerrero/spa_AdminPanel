package com.example.spa_AdminPanel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @Column(name = "id_usuario")
    private Long id_cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column
    private LocalDateTime fecha_registro;

    @Column(nullable = false)
    private LocalDate fecha_nacimiento;

    @OneToOne
    @JoinColumn(name = "id_nota")
    private NotaMedica notasMedicas;

    @Column
    private boolean activo;
}
