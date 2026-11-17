package br.com.heaven.embarcaai.controller;

import br.com.heaven.embarcaai.model.*;
import br.com.heaven.embarcaai.dto.*;
import br.com.heaven.embarcaai.dao.ViagemDAO;
import br.com.heaven.embarcaai.service.ViagemService;
import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.dto.OcupacaoDTO;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/viagens")
public class ViagemController {
    private final ViagemDAO dao;
    private final ModelMapper mapper;
    private final ViagemService service;
    private final ReservaAssentoDAO reservaDao;

    public ViagemController(ViagemDAO dao, ModelMapper mapper, ViagemService service, ReservaAssentoDAO reservaDao) {
        this.dao = dao;
        this.mapper = mapper;
        this.service = service; this.reservaDao = reservaDao;
    }

    @GetMapping
    public List<ViagemSaidaDTO> listar() {
        return dao.listar().stream().map(i -> mapper.map(i, ViagemSaidaDTO.class)).toList();
    }

    @GetMapping("/{id}")
    public ViagemSaidaDTO buscar(@PathVariable Long id) {
        return mapper.map(dao.buscarPorId(id), ViagemSaidaDTO.class);
    }

    @PostMapping
    public ResponseEntity<ViagemSaidaDTO> criar(@Valid @RequestBody ViagemEntradaDTO dto) {
        Viagem item = mapper.map(dto, Viagem.class);
        Viagem criado = service.criar(item);
        return ResponseEntity.created(URI.create("/viagens/" + criado.getId()))
                .body(mapper.map(criado, ViagemSaidaDTO.class));
    }

    @PutMapping("/{id}")
    public ViagemSaidaDTO atualizar(@PathVariable Long id, @Valid @RequestBody ViagemEntradaDTO dto) {
        Viagem item = mapper.map(dto, Viagem.class);
        return mapper.map(service.atualizar(id, item), ViagemSaidaDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reservas")
    public List<ReservaAssentoSaidaDTO> reservas(@PathVariable Long id) {
        dao.buscarPorId(id);
        return reservaDao.listarPorViagem(id).stream()
                .map(r -> mapper.map(r, ReservaAssentoSaidaDTO.class)).toList();
    }

    @GetMapping("/{id}/ocupacao")
    public OcupacaoDTO ocupacao(@PathVariable Long id) { return service.ocupacao(id); }

}
