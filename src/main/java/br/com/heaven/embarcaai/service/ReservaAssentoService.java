package br.com.heaven.embarcaai.service;

import br.com.heaven.embarcaai.dao.ConsultaDAO;
import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.dao.ViagemDAO;
import br.com.heaven.embarcaai.exception.RegraNegocioException;
import br.com.heaven.embarcaai.model.Consulta;
import br.com.heaven.embarcaai.model.ReservaAssento;
import br.com.heaven.embarcaai.model.Viagem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaAssentoService {
    private final ReservaAssentoDAO reservas;
    private final ConsultaDAO consultas;
    private final ViagemDAO viagens;

    public ReservaAssentoService(ReservaAssentoDAO reservas, ConsultaDAO consultas, ViagemDAO viagens) {
        this.reservas = reservas;
        this.consultas = consultas;
        this.viagens = viagens;
    }

    // Uma reserva PENDENTE tambem ocupa um assento ate ser cancelada.
    private void validar(ReservaAssento reserva, Long idAtual) {
        Consulta consulta = consultas.buscarPorId(reserva.getConsultaId());
        viagens.bloquear(reserva.getViagemId()); // evita duas reservas simultaneas da ultima vaga
        Viagem viagem = viagens.buscarPorId(reserva.getViagemId());

        if ("CANCELADO".equals(reserva.getStatusTransporte())) return;

        if (!"S".equals(consulta.getPrecisaTransporte())) {
            throw new RegraNegocioException("A consulta nao precisa de transporte");
        }
        if ("CANCELADA".equals(consulta.getStatus()) || "FALTOU".equals(consulta.getStatus())) {
            throw new RegraNegocioException("Consulta cancelada ou com falta registrada");
        }
        if (!"PROGRAMADA".equals(viagem.getStatus())) {
            throw new RegraNegocioException("A viagem nao esta programada");
        }
        if (!consulta.getUnidadeId().equals(viagem.getUnidadeDestinoId())) {
            throw new RegraNegocioException("Unidade de destino diferente da consulta");
        }
        if (!consulta.getDataHora().toLocalDate().equals(viagem.getDataHoraSaida().toLocalDate()) ||
                !consulta.getDataHora().isAfter(viagem.getDataHoraSaida())) {
            throw new RegraNegocioException("A viagem deve ocorrer antes da consulta, no mesmo dia");
        }
        int limite = viagens.capacidadeDaVan(viagem.getVanId());
        if (reservas.contarOcupados(viagem.getId(), idAtual) >= limite) {
            throw new RegraNegocioException("Van lotada: nao existe assento disponivel");
        }
    }

    @Transactional
    public ReservaAssento criar(ReservaAssento reserva) {
        validar(reserva, null);
        return reservas.criar(reserva);
    }

    @Transactional
    public ReservaAssento atualizar(Long id, ReservaAssento reserva) {
        reservas.buscarPorId(id);
        validar(reserva, id);
        return reservas.atualizar(id, reserva);
    }

    @Transactional
    public void remover(Long id) { reservas.remover(id); }
}
