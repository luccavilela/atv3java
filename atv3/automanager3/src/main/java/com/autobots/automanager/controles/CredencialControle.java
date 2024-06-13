package com.autobots.automanager.controles;

import java.util.List;
import java.util.Set;

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

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.atualizadores.CredencialAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkCredencial;
import com.autobots.automanager.repositorios.CredencialRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
@RequestMapping("/credencial")
public class CredencialControle {
	@Autowired 
	private UsuarioRepositorio repositorioUsuario;
	@Autowired
	private CredencialRepositorio repositorioCredencial;
	@Autowired
	private AdicionadorLinkCredencial adicionadorLink;
	
	@PostMapping("/adicionar/{usuarioId}")
	public ResponseEntity<?> adicionarCredencial(@PathVariable Long usuarioId, @RequestBody Credencial credencial) {
		try {
			Usuario usuario = repositorioUsuario.findById(usuarioId).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
			usuario.getCredenciais().add(credencial);
			repositorioUsuario.save(usuario);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/credencial/{credencialId}")
	public ResponseEntity<Credencial> obterCredencial(@PathVariable Long credencialId) {
		try {
			Credencial credencial = repositorioCredencial.findById(credencialId).orElseThrow(() -> new EntityNotFoundException("Credencial não encontrada"));
			adicionadorLink.adicionarLink(credencial);
			return new ResponseEntity<>(credencial, HttpStatus.FOUND);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("credenciais")
	public ResponseEntity<List<Credencial>> obterCredenciais() {
		List<Credencial> credenciais = repositorioCredencial.findAll();
		if (credenciais.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			adicionadorLink.adicionarLink(credenciais);
			return new ResponseEntity<>(credenciais, HttpStatus.FOUND);
		}
	}
	
	@PutMapping("/atualizar/{credencialId}")
	public ResponseEntity<?> atualizarCredencial(@PathVariable Long credencialId, @RequestBody Credencial novaCredencial) {
		try {
			Credencial credencial = repositorioCredencial.findById(credencialId).orElseThrow(() -> new EntityNotFoundException("Credencial não encontrada"));
			CredencialAtualizador atualizador = new CredencialAtualizador();
			atualizador.atualizar(credencial, novaCredencial);
			repositorioCredencial.save(credencial);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/excluir/{usuarioId}/{credencialId}")
	public ResponseEntity<?> deletarCredencial(@PathVariable Long usuarioId, @PathVariable Long credencialId) {
		try {
			Usuario usuario = repositorioUsuario.findById(usuarioId).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
			Set<Credencial> credenciais = usuario.getCredenciais();
			credenciais.removeIf(credencial -> credencial.getId().equals(credencialId));
			repositorioUsuario.save(usuario);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
