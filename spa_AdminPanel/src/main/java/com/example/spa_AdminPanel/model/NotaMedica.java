package com.example.spa_AdminPanel.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notas_medicas")
public class NotaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_nota;

    @OneToOne(mappedBy = "notasMedicas")
    private Cliente cliente;

    @Column(columnDefinition = "TEXT")
    private String notas;

}
