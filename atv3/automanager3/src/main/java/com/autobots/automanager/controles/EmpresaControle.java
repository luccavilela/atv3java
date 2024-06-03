package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.repositorios.RepositorioEmpresa;

@RestController
@RequestMapping("/empresas")
public class EmpresaControle {
	
	@Autowired
    private RepositorioEmpresa repositorioEmpresa;
	
	@GetMapping("/listar")
    public ResponseEntity<List<Empresa>> obterEmpresas() {
        List<Empresa> empresas = repositorioEmpresa.findAll();
        if (empresas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(empresas, HttpStatus.OK);
        }
    }
}
