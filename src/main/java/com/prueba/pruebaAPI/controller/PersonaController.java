package com.prueba.pruebaAPI.controller;

import com.prueba.pruebaAPI.dominio.Persona;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/persona")
public class PersonaController {

    @GetMapping(value = "/obtener")
    public Persona crear(@RequestBody Persona persona){
        System.out.println(persona.getNombre() + persona.getEdad());
        return (new Persona(persona.getNombre(), persona.getEdad()));
    } 
}
