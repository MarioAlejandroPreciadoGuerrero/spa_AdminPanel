package com.example.spa_AdminPanel.controller;

import com.example.spa_AdminPanel.model.EstadoPago;
import com.example.spa_AdminPanel.model.Pago;
import com.example.spa_AdminPanel.repository.CitaRepository;
import com.example.spa_AdminPanel.repository.MetodoPagoRepository;
import com.example.spa_AdminPanel.service.CitaService;
import com.example.spa_AdminPanel.service.PagoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    //obtener todos los pagos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagos", pagoService.obtenerPagos());
        return "pagos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("pago", new Pago());
        return "pagos/formulario";
    }

    //crear un pago con el pago vacio
    @PostMapping
    public String registrar(@ModelAttribute("pago") Pago pago) {
        try {
            pago.setId_pago(null);
            pagoService.registrarPago(pago);
            return "redirect:/pagos";
        } catch (Exception exception) {
            return "redirect:/pagos/nuevo";
        }
    }

    //para ver detalles del pago
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        //
        model.addAttribute("pago", pagoService.obtenerPagoPorId(id));
        model.addAttribute("estadosPago", EstadoPago.values());
        return "pagos/detalle";
    }

    //para cambiar el estado de un pago
    @PostMapping("/{id}/estado")
    public String actualizarEstado(@PathVariable Long id, @RequestParam EstadoPago estado,
                                   RedirectAttributes redirectAttributes) {
        try {
            pagoService.actualizarEstadoPago(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "El estado del pago se actualizó");
        } catch (IllegalArgumentException | EntityNotFoundException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/pagos/" + id;
    }
}
