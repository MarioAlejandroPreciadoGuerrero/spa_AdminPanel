package com.example.spa_AdminPanel.controller;

import com.example.spa_AdminPanel.model.*;
import com.example.spa_AdminPanel.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//este controlador se encarga de lo relacionado a citas

@Controller
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    //por si necesitan listar las citas
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citas", citaService.obtenerCitas());
        return "citas/lista";
    }

    /*crea una cita vacia y la manda al abrir el formulario para que despues pueda setear
    los atributos de la cita con los campos del formulario*/
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cita", new Cita());
        return "citas/formulario";
    }

    @PostMapping
    public String reservar(@ModelAttribute("cita") Cita cita) {
        //recibe los datos enviados del formulario como una cita
        try {
            //reserva la cita con citaService
            cita.setId_cita(null);
            citaService.reservarCita(cita);
            return "redirect:/citas";
        } catch (Exception exception) {
            //si falla redirecciona a donde estaba
            return "redirect:/citas/nueva";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        //recibe el id de la cita desde la url, consulta con esa id y arega los estados que puede tener esa cita
        model.addAttribute("cita", citaService.obtenerCitaPorId(id));
        model.addAttribute("estadosCita", EstadoCita.values());
        return "citas/detalle";
    }

    //retorna una lista de citas pendientes de un cliente con el id
    @GetMapping("/cliente/{idCliente}/pendientes")
    public String listarPendientes(@PathVariable Long idCliente, Model model) {
        model.addAttribute("citas", citaService.obtenerCitasPendientesDeUsuario(idCliente));
        model.addAttribute("idCliente", idCliente);
        return "citas/lista";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestBody EstadoCita estado) {
        //recibe en la url el id de la cita y en el body recibe el estado nuevo
        try {
            citaService.cambiarEstadoCita(id, estado);
        } catch (Exception exception) {
            return "redirect:/"; //no se a donde retornar
        }
        return "redirect:/citas/" + id;
    }

    /**
     * 1. Recibe el identificador de la cita que se desea cancelar.
     * 2. Solicita al servicio cambiar su estado a cancelada y prepara un mensaje de resultado.
     * 3. Redirige al detalle de la cita.
     */
    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id) {
        //recibe en la url el id de la cita a cancelar
        try {
            citaService.cancelarCita(id);
        } catch (Exception exception) {
            return "redirect:/"; //tampoco se
        }
        return "redirect:/citas/" + id;
    }

}
