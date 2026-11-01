package com.example.spa_AdminPanel.controller;

import com.example.spa_AdminPanel.model.Servicio;
import com.example.spa_AdminPanel.service.ServicioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;


    @GetMapping
    public String listar(Model model) {
        //listar todos los servicios
        model.addAttribute("servicios", servicioService.obtenerServicios());
        return "servicios/lista";
    }

    //sirve para dar de alta un nuevo servicio, crea el objeto servicio vacio y lo manda como atributo en el modelo
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "servicios/formulario";
    }

    //recibe los datos del formulario para crear el servicio con el servicio vacio dea ntes
    @PostMapping
    public String crear(@ModelAttribute("servicio") Servicio servicio) {
        try {
            servicio.setId_servicio(null);
            servicioService.guardarServicio(servicio);
            return "redirect:/servicios";
        } catch (IllegalArgumentException exception) {
            return "redirect:/servicios/nuevo";
        }
    }

    //para obtener los detalles de un servicio
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("servicio", servicioService.obtenerServicioPorId(id));
        return "servicios/detalle";
    }

    @GetMapping("/{id}/editar")
    public String mostrarEdicion(@PathVariable Long id, Model model) {
        //permite editar los datos de un servicio supongo que deberian precargarlo
        model.addAttribute("servicio", servicioService.obtenerServicioPorId(id));
        return "servicios/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("servicio") Servicio cambios) {
        try {
            servicioService.actualizarServicio(id, cambios);
        } catch (Exception exception) {
            return "redirect:/servicios/" + id;
        }
        return "redirect:/servicios/" + id;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        try {
            servicioService.eliminarServicio(id);
        } catch (Exception exception) {
            return "redirect:/servicios";
        }
        return "redirect:/servicios";
    }
}
