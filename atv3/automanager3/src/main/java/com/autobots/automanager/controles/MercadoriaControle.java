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

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.modelos.atualizadores.MercadoriaAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkMercadoria;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;


@RestController
@RequestMapping("/mercadoria")
public class MercadoriaControle {
	
	@Autowired 
	private MercadoriaRepositorio repositorioMercadoria;
	
	@Autowired
	private AdicionadorLinkMercadoria adicionadorLink;
	
	@GetMapping("/mercadoria/{mercadoriaId}")
	public ResponseEntity<Mercadoria> obterMercadoria(@PathVariable Long mercadoriaId) {
	    try {
	        Mercadoria mercadoria = repositorioMercadoria.findById(mercadoriaId).orElseThrow(() -> new EntityNotFoundException("Mercadoria não encontrada"));  
	        adicionadorLink.adicionarLink(mercadoria);
	        
	        return new ResponseEntity<>(mercadoria, HttpStatus.FOUND);
	    } catch (EntityNotFoundException | NoSuchElementException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("mercadorias")
	public ResponseEntity<List<Mercadoria>> obterMercadorias() {
		List<Mercadoria> mercadorias = repositorioMercadoria.findAll();
		if (mercadorias.isEmpty()) {
			ResponseEntity<List<Mercadoria>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(mercadorias);
			ResponseEntity<List<Mercadoria>> resposta = new ResponseEntity<>(mercadorias, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	@PostMapping("mercadoria/cadastro")
	public ResponseEntity<?> cadastrarMercadoria(@RequestBody Mercadoria mercadoria) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (mercadoria.getId() == null) {
			repositorioMercadoria.save(mercadoria);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}
	
	@PutMapping("/atualizar/{mercadoriaId}")
    public ResponseEntity<?> atualizarMercadoria(@PathVariable Long mercadoriaId, @RequestBody Mercadoria novaMercadoria) {
		try {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Mercadoria mercadoria = repositorioMercadoria.getById(mercadoriaId);
			if (mercadoria != null) {
				MercadoriaAtualizador atualizador = new MercadoriaAtualizador();
				atualizador.atualizar(mercadoria, novaMercadoria);
            
				repositorioMercadoria.save(mercadoria);
				status = HttpStatus.OK;
			} 
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
    }
	
	@DeleteMapping("/excluir/{mercadoriaId}")
	public ResponseEntity<?> excluirMercadoria(@PathVariable Long mercadoriaId) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    Mercadoria mercadoria = repositorioMercadoria.getById(mercadoriaId);
	    if (mercadoria != null) {
	        repositorioMercadoria.delete(mercadoria);
	        status = HttpStatus.OK;
	    }
	    return new ResponseEntity<>(status);
	}
}
