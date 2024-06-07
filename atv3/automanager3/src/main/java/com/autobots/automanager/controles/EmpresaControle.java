package com.autobots.automanager.controles;

import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.modelos.atualizadores.EmpresaAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkEmpresa;
import com.autobots.automanager.repositorios.RepositorioEmpresa;




@RestController
@RequestMapping("/empresa")
public class EmpresaControle {
	
	@Autowired 
	private RepositorioEmpresa empresaRepositorio;
	
	@Autowired
	private AdicionadorLinkEmpresa adicionadorLink;
	
	@GetMapping("/empresa/{empresaId}")
	public ResponseEntity<Empresa> obterEmpresa(@PathVariable Long empresaId) {
	    try {
	        Empresa empresa = empresaRepositorio.findById(empresaId).orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));  
	        adicionadorLink.adicionarLink(empresa);
	        
	        return new ResponseEntity<>(empresa, HttpStatus.FOUND);
	    } catch (EntityNotFoundException | NoSuchElementException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("empresas")
	public ResponseEntity<List<Empresa>> obterEmpresas() {
		List<Empresa> empresas = empresaRepositorio.findAll();
		if (empresas.isEmpty()) {
			ResponseEntity<List<Empresa>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(empresas);
			ResponseEntity<List<Empresa>> resposta = new ResponseEntity<>(empresas, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	@PostMapping("empresa/cadastro")
	public ResponseEntity<?> cadastrarEmpresa(@RequestBody Empresa empresa) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (empresa.getId() == null) {
			empresaRepositorio.save(empresa);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}
	
	@PutMapping("/atualizar/{empresaId}")
    public ResponseEntity<?> atualizarEmpresa(@PathVariable Long empresaId, @RequestBody Empresa novaEmpresa) {
		try {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Empresa empresa = empresaRepositorio.getById(empresaId);
			if (empresa != null) {
				EmpresaAtualizador atualizador = new EmpresaAtualizador();
				atualizador.atualizar(empresa, novaEmpresa);
            
				empresaRepositorio.save(empresa);
				status = HttpStatus.OK;
			} 
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
    }
	
	@DeleteMapping("/excluir/{empresaId}")
	public ResponseEntity<?> excluirEmpresa(@PathVariable Long empresaId) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    Empresa empresa = empresaRepositorio.getById(empresaId);
	    if (empresa != null) {
	        empresaRepositorio.delete(empresa);
	        status = HttpStatus.OK;
	    }
	    return new ResponseEntity<>(status);
	}
}
