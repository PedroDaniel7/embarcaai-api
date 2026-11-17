package br.com.heaven.embarcaai.service;

import br.com.heaven.embarcaai.dao.ConsultaDAO;
import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.exception.RegraNegocioException;
import br.com.heaven.embarcaai.model.Consulta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultaService {
    private final ConsultaDAO consultas;
    private final ReservaAssentoDAO reservas;

    public ConsultaService(ConsultaDAO consultas, ReservaAssentoDAO reservas) {
        this.consultas = consultas;
        this.reservas = reservas;
    }

    @Transactional
    public Consulta criar(Consulta consulta) { return consultas.criar(consulta); }

    @Transactional
    public Consulta atualizar(Long id, Consulta nova) {
        Consulta anterior = consultas.bloquear(id);
        boolean alterouDestino = !anterior.getUnidadeId().equals(nova.getUnidadeId()) ||
                !anterior.getDataHora().equals(nova.getDataHora()) ||
                !anterior.getPrecisaTransporte().equals(nova.getPrecisaTransporte());
        boolean existeReservaAtiva = reservas.listarPorConsulta(id).stream()
                .anyMatch(r -> !"CANCELADO".equals(r.getStatusTransporte()));
        if (alterouDestino && existeReservaAtiva && !"CANCELADA".equals(nova.getStatus())) {
            throw new RegraNegocioException("Cancele as reservas antes de alterar data, unidade ou transporte");
        }
        Consulta salva = consultas.atualizar(id, nova);
        if ("CANCELADA".equals(nova.getStatus())) reservas.cancelarReservasDaConsulta(id);
        return salva;
    }

    @Transactional
    public void remover(Long id) { consultas.remover(id); }
}
