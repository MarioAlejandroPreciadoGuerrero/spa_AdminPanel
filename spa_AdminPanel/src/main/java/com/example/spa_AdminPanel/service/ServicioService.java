package com.example.spa_AdminPanel.service;

import com.example.spa_AdminPanel.model.Servicio;
import com.example.spa_AdminPanel.repository.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Transactional(readOnly = true)
    public List<Servicio> obtenerServicios() {
        return servicioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Servicio obtenerServicioPorId(Long idServicio) {
        return servicioRepository.findById(idServicio)
                .orElseThrow(() -> new EntityNotFoundException("El servicio no existe"));
    }

    @Transactional
    public Servicio guardarServicio(Servicio servicio) {
        validarServicio(servicio);
        return servicioRepository.save(servicio);
    }

    @Transactional
    public Servicio actualizarServicio(Long idServicio, Servicio cambios) {
        validarServicio(cambios);
        Servicio servicio = obtenerServicioPorId(idServicio);
        servicio.setNombre(cambios.getNombre());
        servicio.setMinutos_duracion(cambios.getMinutos_duracion());
        servicio.setDescripcion(cambios.getDescripcion());
        servicio.setCategoria(cambios.getCategoria());
        servicio.setPrecio(cambios.getPrecio());
        servicio.setUrl_imagen(cambios.getUrl_imagen());
        return servicioRepository.save(servicio);
    }

    @Transactional
    public void eliminarServicio(Long idServicio) {
        servicioRepository.delete(obtenerServicioPorId(idServicio));
    }

    private void validarServicio(Servicio servicio) {
        if (servicio == null || servicio.getNombre() == null || servicio.getNombre().isBlank()
                || servicio.getCategoria() == null || servicio.getCategoria().isBlank()
                || servicio.getMinutos_duracion() == null || servicio.getMinutos_duracion() <= 0
                || servicio.getPrecio() == null || servicio.getPrecio() <= 0) {
            throw new IllegalArgumentException("El servicio requiere nombre, categoría, duración y precio válidos");
        }
    }
}
