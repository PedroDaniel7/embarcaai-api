package br.com.heaven.embarcaai.controller;

import br.com.heaven.embarcaai.model.*;
import br.com.heaven.embarcaai.dto.*;
import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.service.ReservaAssentoService;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaAssentoController {
    private final ReservaAssentoDAO dao;
    private final ModelMapper mapper;
    private final ReservaAssentoService service;

    public ReservaAssentoController(ReservaAssentoDAO dao, ModelMapper mapper, ReservaAssentoService service) {
        this.dao = dao;
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping
    public List<ReservaAssentoSaidaDTO> listar() {
        return dao.listar().stream().map(i -> mapper.map(i, ReservaAssentoSaidaDTO.class)).toList();
    }

    @GetMapping("/{id}")
    public ReservaAssentoSaidaDTO buscar(@PathVariable Long id) {
        return mapper.map(dao.buscarPorId(id), ReservaAssentoSaidaDTO.class);
    }

    @PostMapping
    public ResponseEntity<ReservaAssentoSaidaDTO> criar(@Valid @RequestBody ReservaAssentoEntradaDTO dto) {
        ReservaAssento item = mapper.map(dto, ReservaAssento.class);
        ReservaAssento criado = service.criar(item);
        return ResponseEntity.created(URI.create("/reservas/" + criado.getId()))
                .body(mapper.map(criado, ReservaAssentoSaidaDTO.class));
    }

    @PutMapping("/{id}")
    public ReservaAssentoSaidaDTO atualizar(@PathVariable Long id, @Valid @RequestBody ReservaAssentoEntradaDTO dto) {
        ReservaAssento item = mapper.map(dto, ReservaAssento.class);
        return mapper.map(service.atualizar(id, item), ReservaAssentoSaidaDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

}
