package br.com.heaven.embarcaai.controller;

import br.com.heaven.embarcaai.model.*;
import br.com.heaven.embarcaai.dto.*;
import br.com.heaven.embarcaai.dao.PacienteDAO;

import br.com.heaven.embarcaai.dao.ConsultaDAO;
import br.com.heaven.embarcaai.service.ConsultaService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
    private final PacienteDAO dao;
    private final ModelMapper mapper;
    private final ConsultaDAO consultaDao;
    private final ConsultaService consultaService;

    public PacienteController(PacienteDAO dao, ModelMapper mapper, ConsultaDAO consultaDao, ConsultaService consultaService) {
        this.dao = dao;
        this.mapper = mapper;
        this.consultaDao = consultaDao; this.consultaService = consultaService;
    }

    @GetMapping
    public List<PacienteSaidaDTO> listar() {
        return dao.listar().stream().map(i -> mapper.map(i, PacienteSaidaDTO.class)).toList();
    }

    @GetMapping("/{id}")
    public PacienteSaidaDTO buscar(@PathVariable Long id) {
        return mapper.map(dao.buscarPorId(id), PacienteSaidaDTO.class);
    }

    @PostMapping
    public ResponseEntity<PacienteSaidaDTO> criar(@Valid @RequestBody PacienteEntradaDTO dto) {
        Paciente item = mapper.map(dto, Paciente.class);
        Paciente criado = dao.criar(item);
        return ResponseEntity.created(URI.create("/pacientes/" + criado.getId()))
                .body(mapper.map(criado, PacienteSaidaDTO.class));
    }

    @PutMapping("/{id}")
    public PacienteSaidaDTO atualizar(@PathVariable Long id, @Valid @RequestBody PacienteEntradaDTO dto) {
        Paciente item = mapper.map(dto, Paciente.class);
        return mapper.map(dao.atualizar(id, item), PacienteSaidaDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        dao.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/consultas")
    public List<ConsultaSaidaDTO> consultas(@PathVariable Long id) {
        dao.buscarPorId(id);
        return consultaDao.listarPorPaciente(id).stream()
                .map(c -> mapper.map(c, ConsultaSaidaDTO.class)).toList();
    }

    @PostMapping("/{id}/consultas")
    public ResponseEntity<ConsultaSaidaDTO> criarConsulta(@PathVariable Long id,
            @Valid @RequestBody ConsultaDoPacienteDTO dto) {
        dao.buscarPorId(id);
        Consulta consulta = mapper.map(dto, Consulta.class);
        consulta.setPacienteId(id);
        Consulta criada = consultaService.criar(consulta);
        return ResponseEntity.created(URI.create("/consultas/" + criada.getId()))
                .body(mapper.map(criada, ConsultaSaidaDTO.class));
    }

}
