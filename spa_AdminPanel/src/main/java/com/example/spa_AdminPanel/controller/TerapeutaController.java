package com.example.spa_AdminPanel.controller;

import com.example.spa_AdminPanel.model.Terapeuta;
import com.example.spa_AdminPanel.repository.ServicioRepository;
import com.example.spa_AdminPanel.repository.SucursalRepository;
import com.example.spa_AdminPanel.repository.UsuarioRepository;
import com.example.spa_AdminPanel.service.TerapeutaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/terapeutas")
public class TerapeutaController {

    @Autowired
    private TerapeutaService terapeutaService;

    //ver todos los terapeutas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("terapeutas", terapeutaService.obtenerTerapeutas());
        return "terapeutas/lista";
    }

    //editar datos de un terapeuta
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("terapeuta", new Terapeuta());
        return "terapeutas/formulario";
    }

    //recibe los datos del formulario para crear un terapeuta
    @PostMapping
    public String crear(@ModelAttribute("terapeuta") Terapeuta terapeuta) {
        try {
            terapeuta.setId_terapeuta(null);
            terapeutaService.guardarTerapeuta(terapeuta);
            return "redirect:/terapeutas";
        } catch (IllegalArgumentException | EntityNotFoundException exception) {
            return "redirect:/terapeutas/nuevo";
        }
    }

    //obtener terapeutas por servicio
    @GetMapping("/por-servicio")
    public String listarPorServicio(@RequestParam String nombre, Model model) {
        model.addAttribute("terapeutas", terapeutaService.obtenerTerapeutasPorServicio(nombre));
        return "terapeutas/lista";
    }

    //mostrar los detalles de un terpaueta
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("terapeuta", terapeutaService.obtenerTerapeutaPorId(id));
        return "terapeutas/detalle";
    }

   //para editar información de un terapeuta
    @GetMapping("/{id}/editar")
    public String mostrarEdicion(@PathVariable Long id, Model model) {
        model.addAttribute("terapeuta", terapeutaService.obtenerTerapeutaPorId(id));
        return "terapeutas/formulario";
    }

    //permite actualizar un terapeuta con un nuevo terapeuta
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("terapeuta") Terapeuta cambios) {
        try {
            terapeutaService.actualizarTerapeuta(id, cambios);
        } catch (IllegalArgumentException | EntityNotFoundException exception) {
            return "redirect:/terapeutas/" + id;
        }
        return "redirect:/terapeutas/" + id;
    }

}
