package com.example.spa_AdminPanel.service;

import com.example.spa_AdminPanel.model.EstadoPago;
import com.example.spa_AdminPanel.model.Pago;
import com.example.spa_AdminPanel.model.Cita;
import com.example.spa_AdminPanel.repository.CitaRepository;
import com.example.spa_AdminPanel.repository.MetodoPagoRepository;
import com.example.spa_AdminPanel.repository.PagoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    private final CitaRepository citaRepository;

    private final MetodoPagoRepository metodoPagoRepository;

    public PagoService(PagoRepository pagoRepository, CitaRepository citaRepository,
                       MetodoPagoRepository metodoPagoRepository) {
        this.pagoRepository = pagoRepository;
        this.citaRepository = citaRepository;
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerPagos() {
        return pagoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pago obtenerPagoPorId(Long idPago) {
        return pagoRepository.findById(idPago)
                .orElseThrow(() -> new EntityNotFoundException("El pago no existe"));
    }

    @Transactional
    public Pago registrarPago(Pago pago) {
        if (pago == null || pago.getCita() == null || pago.getCita().getId_cita() == null
                || pago.getMetodoPago() == null || pago.getMetodoPago().getId_metodo_pago() == null) {
            throw new IllegalArgumentException("El pago debe incluir una cita y un método de pago");
        }
        Cita cita = citaRepository.findById(pago.getCita().getId_cita())
                .orElseThrow(() -> new EntityNotFoundException("La cita no existe"));
        if (pagoRepository.existePagoParaCita(cita.getId_cita())) {
            throw new IllegalStateException("La cita ya tiene un pago registrado");
        }
        pago.setMetodoPago(metodoPagoRepository.findById(pago.getMetodoPago().getId_metodo_pago())
                .orElseThrow(() -> new EntityNotFoundException("El método de pago no existe")));
        if (cita.getPrecio_total() == null || cita.getPrecio_total() <= 0) {
            throw new IllegalStateException("La cita no tiene un precio total válido");
        }
        pago.setCita(cita);
        pago.setMonto(cita.getPrecio_total());
        pago.setEstado_pago(EstadoPago.PENDIENTE);
        pago.setFecha_pago(LocalDateTime.now());
        return pagoRepository.save(pago);
    }

    @Transactional
    public Pago actualizarEstadoPago(Long idPago, EstadoPago estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del pago es obligatorio");
        }
        Pago pago = obtenerPagoPorId(idPago);
        pago.setEstado_pago(estado);
        return pagoRepository.save(pago);
    }
}
