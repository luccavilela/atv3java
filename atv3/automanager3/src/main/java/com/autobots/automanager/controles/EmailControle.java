package com.autobots.automanager.controles;

import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.modelos.atualizadores.EmailAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkEmail;
import com.autobots.automanager.repositorios.EmailRepositorio;



@RestController
@RequestMapping("/email")
public class EmailControle {
	
	@Autowired 
	private EmailRepositorio repositorioEmail;
	
	@Autowired
	private AdicionadorLinkEmail adicionadorLink;
	
	@GetMapping("/email/{emailId}")
	public ResponseEntity<Email> obterEmail(@PathVariable Long emailId) {
	    try {
	        Email email = repositorioEmail.findById(emailId).orElseThrow(() -> new EntityNotFoundException("Email não encontrado"));  
	        adicionadorLink.adicionarLink(email);
	        
	        return new ResponseEntity<>(email, HttpStatus.FOUND);
	    } catch (EntityNotFoundException | NoSuchElementException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("emails")
	public ResponseEntity<List<Email>> obterEmails() {
		List<Email> emails = repositorioEmail.findAll();
		if (emails.isEmpty()) {
			ResponseEntity<List<Email>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(emails);
			ResponseEntity<List<Email>> resposta = new ResponseEntity<>(emails, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	
	@PutMapping("/atualizar/{emailId}")
    public ResponseEntity<?> atualizarEndereco(@PathVariable Long emailId, @RequestBody Email novoEmail) {
		try {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Email email = repositorioEmail.getById(emailId);
			if (email != null) {
				EmailAtualizador atualizador = new EmailAtualizador();
				atualizador.atualizar(email, novoEmail);
            
				repositorioEmail.save(email);
				status = HttpStatus.OK;
			} 
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
    }
}
