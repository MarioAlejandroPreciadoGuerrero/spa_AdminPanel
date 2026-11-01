package com.example.spa_AdminPanel.service;

import com.example.spa_AdminPanel.model.*;
import com.example.spa_AdminPanel.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CitaService {

    // estados con los que se reserva la cita
    private static final List<EstadoCita> ESTADOS_QUE_RESERVAN_HORARIO =
            List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA);


    private CitaRepository citaRepository;

    private ClienteRepository clienteRepository;

    private ServicioRepository servicioRepository;

    private TerapeutaRepository terapeutaRepository;

    private SalaRepository salaRepository;


    @Transactional(readOnly = true)
    public List<Cita> obtenerCitas() {
        return citaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cita obtenerCitaPorId(Long idCita) {
        return citaRepository.findById(idCita)
                .orElseThrow(() -> new EntityNotFoundException("La cita no existe"));
    }

    @Transactional(readOnly = true)
    public List<Cita> obtenerCitasPendientesDeUsuario(Long idUsuario) {
        if (!clienteRepository.existsById(idUsuario)) {
            throw new EntityNotFoundException("El cliente no existe");
        }
        return citaRepository.findByClienteYEstado(idUsuario, EstadoCita.PENDIENTE);
    }

    @Transactional
    public Cita reservarCita(Cita cita) {
        // detalles de la cita
        if (cita == null || cita.getCliente() == null || cita.getServicio() == null
                || cita.getTerapeuta() == null
                || cita.getSala() == null
                || cita.getCliente().getId_cliente() == null
                || cita.getServicio().getId_servicio() == null
                || cita.getTerapeuta().getId_terapeuta() == null
                || cita.getSala().getId_sala() == null
                || cita.getFecha_hora_inicio() == null) {
            throw new IllegalArgumentException("La cita debe incluir cliente, servicio, terapeuta, sala y fecha de inicio");
        }

        Cliente cliente = clienteRepository.findById(cita.getCliente().getId_cliente())
                .orElseThrow(() -> new EntityNotFoundException("El cliente no existe"));

        Servicio servicio = servicioRepository.findById(cita.getServicio().getId_servicio())
                .orElseThrow(() -> new EntityNotFoundException("El servicio no existe"));

        Terapeuta terapeuta = terapeutaRepository.findById(cita.getTerapeuta().getId_terapeuta())
                .orElseThrow(() -> new EntityNotFoundException("El terapeuta no existe"));

        Sala sala = salaRepository.findById(cita.getSala().getId_sala())
                .orElseThrow(() -> new EntityNotFoundException("La sala no existe"));

        if (!cliente.isActivo()) {
            throw new IllegalStateException("No se puede reservar una cita para un cliente inactivo");
        }
        if (!sala.isActiva()) {
            throw new IllegalStateException("No se puede reservar una cita en una sala inactiva");
        }
        if (terapeuta.getServicios() == null || terapeuta.getServicios().stream()
                .noneMatch(servicioAsignado -> Objects.equals(
                        servicioAsignado.getId_servicio(), servicio.getId_servicio()))) {
            throw new IllegalStateException("El terapeuta no está asignado al servicio seleccionado");
        }
        if (terapeuta.getSucursal() == null || sala.getSucursal() == null
                || !Objects.equals(terapeuta.getSucursal().getId_sucursal(), sala.getSucursal().getId_sucursal())) {
            throw new IllegalStateException("El terapeuta y la sala deben pertenecer a la misma sucursal");
        }
        if (servicio.getMinutos_duracion() == null || servicio.getMinutos_duracion() <= 0) {
            throw new IllegalStateException("El servicio debe tener una duración válida");
        }
        if (servicio.getPrecio() == null || servicio.getPrecio() <= 0) {
            throw new IllegalStateException("El servicio debe tener un precio válido");
        }

        LocalDateTime inicio = cita.getFecha_hora_inicio();
        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita debe ser futura");
        }
        LocalDateTime fin = inicio.plusMinutes(servicio.getMinutos_duracion());

        if (citaRepository.existeConflictoDeTerapeuta(
                terapeuta.getId_terapeuta(), ESTADOS_QUE_RESERVAN_HORARIO, inicio, fin)) {
            throw new IllegalStateException("El terapeuta ya tiene una cita en ese horario");
        }
        if (citaRepository.existeConflictoDeSala(
                sala.getId_sala(), ESTADOS_QUE_RESERVAN_HORARIO, inicio, fin)) {
            throw new IllegalStateException("La sala ya está reservada en ese horario");
        }

        cita.setCliente(cliente);
        cita.setServicio(servicio);
        cita.setTerapeuta(terapeuta);
        cita.setSala(sala);
        cita.setFecha_hora_fin(fin);
        cita.setFecha_creada(LocalDateTime.now());
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setPrecio_total(servicio.getPrecio());
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita cambiarEstadoCita(Long idCita, EstadoCita nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado de la cita es obligatorio");
        }
        Cita cita = obtenerCitaPorId(idCita);
        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new IllegalStateException("No se puede cambiar el estado de una cita finalizada");
        }
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita cancelarCita(Long idCita) {
        return cambiarEstadoCita(idCita, EstadoCita.CANCELADA);
    }
}