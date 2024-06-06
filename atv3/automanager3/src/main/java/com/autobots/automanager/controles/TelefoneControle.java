package com.autobots.automanager.controles;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import java.util.Set;
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

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.atualizadores.TelefoneAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkTelefone;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {
	@Autowired 
	private UsuarioRepositorio repositorioUsuario;
	@Autowired
	private TelefoneRepositorio repositorioTelefone;
	@Autowired
	private AdicionadorLinkTelefone adicionadorLink;
	
	@PostMapping("/adicionar/{usuarioId}")
    public ResponseEntity<?> adicionarDocumento(@PathVariable Long usuarioId, @RequestBody Telefone telefone) {
		try {
        	Usuario usuario = repositorioUsuario.getById(usuarioId);
        	HttpStatus status = HttpStatus.BAD_REQUEST;
        	if (usuario.getId() != null) {
        		usuario.getTelefones().add(telefone);
        		repositorioUsuario.save(usuario);
        		status = HttpStatus.CREATED;
        	}
        	return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
        	
    }
	
	@GetMapping("/telefone/{telefoneId}")
	public ResponseEntity<Telefone> obterTelefone(@PathVariable Long telefoneId) {
		try {
	    	Telefone telefone = repositorioTelefone.findById(telefoneId).orElseThrow(() -> new EntityNotFoundException("Telefone não encontrado"));  
	    	adicionadorLink.adicionarLink(telefone);
	    	
	    	return new ResponseEntity<>(telefone, HttpStatus.FOUND);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("telefones")
	public ResponseEntity<List<Telefone>> obterTelefones() {
		List<Telefone> telefones = repositorioTelefone.findAll();
		if (telefones.isEmpty()) {
			ResponseEntity<List<Telefone>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(telefones);
			ResponseEntity<List<Telefone>> resposta = new ResponseEntity<>(telefones, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	
	@PutMapping("/atualizar/{telefoneId}")
	public ResponseEntity<?> atualizarTelefone(@PathVariable Long telefoneId, @RequestBody Telefone novoTelefone) {
		try {
			Telefone telefone = repositorioTelefone.getById(telefoneId);
			TelefoneAtualizador atualizador = new TelefoneAtualizador();
			HttpStatus status = HttpStatus.BAD_REQUEST;
			if (telefone != null) {
			
				atualizador.atualizar(telefone, novoTelefone);		    
				repositorioTelefone.save(telefone);
				status = HttpStatus.OK;
			} else {
				status = HttpStatus.NOT_FOUND;
			}
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@DeleteMapping("/excluir/{usuarioId}/{telefoneId}")
	public ResponseEntity<?> deletarTelefone(@PathVariable Long usuarioId, @PathVariable Long telefoneId) {
		try {
	    	Usuario usuario = repositorioUsuario.getById(usuarioId);
	    	HttpStatus status = HttpStatus.BAD_REQUEST;
	    	if (usuario != null && telefoneId != null)  {
	    		Set<Telefone> telefones = usuario.getTelefones();
	    		telefones.removeIf(telefone -> telefone.getId().equals(telefoneId));    
	    		repositorioUsuario.save(usuario);
	    		status = HttpStatus.OK;
	    	}
	    	return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
}
