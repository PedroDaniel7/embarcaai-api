package br.com.heaven.embarcaai.controller;

import br.com.heaven.embarcaai.model.*;
import br.com.heaven.embarcaai.dto.*;
import br.com.heaven.embarcaai.dao.ConsultaDAO;
import br.com.heaven.embarcaai.service.ConsultaService;
import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.dao.LembreteDAO;
import br.com.heaven.embarcaai.service.RespostaService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {
    private final ConsultaDAO dao;
    private final ModelMapper mapper;
    private final ConsultaService service;
    private final ReservaAssentoDAO reservaDao;
    private final LembreteDAO lembreteDao;
    private final RespostaService respostaService;

    public ConsultaController(ConsultaDAO dao, ModelMapper mapper, ConsultaService service, ReservaAssentoDAO reservaDao, LembreteDAO lembreteDao, RespostaService respostaService) {
        this.dao = dao;
        this.mapper = mapper;
        this.service = service; this.reservaDao = reservaDao; this.lembreteDao = lembreteDao; this.respostaService = respostaService;
    }

    @GetMapping
    public List<ConsultaSaidaDTO> listar() {
        return dao.listar().stream().map(i -> mapper.map(i, ConsultaSaidaDTO.class)).toList();
    }

    @GetMapping("/{id}")
    public ConsultaSaidaDTO buscar(@PathVariable Long id) {
        return mapper.map(dao.buscarPorId(id), ConsultaSaidaDTO.class);
    }

    @PostMapping
    public ResponseEntity<ConsultaSaidaDTO> criar(@Valid @RequestBody ConsultaEntradaDTO dto) {
        Consulta item = mapper.map(dto, Consulta.class);
        Consulta criado = service.criar(item);
        return ResponseEntity.created(URI.create("/consultas/" + criado.getId()))
                .body(mapper.map(criado, ConsultaSaidaDTO.class));
    }

    @PutMapping("/{id}")
    public ConsultaSaidaDTO atualizar(@PathVariable Long id, @Valid @RequestBody ConsultaEntradaDTO dto) {
        Consulta item = mapper.map(dto, Consulta.class);
        return mapper.map(service.atualizar(id, item), ConsultaSaidaDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reservas")
    public List<ReservaAssentoSaidaDTO> reservas(@PathVariable Long id) {
        dao.buscarPorId(id);
        return reservaDao.listarPorConsulta(id).stream()
                .map(r -> mapper.map(r, ReservaAssentoSaidaDTO.class)).toList();
    }

    @GetMapping("/{id}/lembretes")
    public List<LembreteSaidaDTO> lembretes(@PathVariable Long id) {
        dao.buscarPorId(id);
        return lembreteDao.listarPorConsulta(id);
    }

    // Simula a resposta ao WhatsApp. Nao envia mensagens reais.
    @PostMapping("/{id}/respostas")
    public ConsultaSaidaDTO registrarResposta(@PathVariable Long id,
            @Valid @RequestBody RespostaEntradaDTO resposta) {
        return mapper.map(respostaService.registrar(id, resposta), ConsultaSaidaDTO.class);
    }

}
