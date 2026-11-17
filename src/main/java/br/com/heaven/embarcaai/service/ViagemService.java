package br.com.heaven.embarcaai.service;

import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.dao.ViagemDAO;
import br.com.heaven.embarcaai.dto.OcupacaoDTO;
import br.com.heaven.embarcaai.exception.RegraNegocioException;
import br.com.heaven.embarcaai.model.Viagem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViagemService {
    private final ViagemDAO viagens;
    private final ReservaAssentoDAO reservas;

    public ViagemService(ViagemDAO viagens, ReservaAssentoDAO reservas) {
        this.viagens = viagens;
        this.reservas = reservas;
    }

    @Transactional
    public Viagem criar(Viagem viagem) {
        viagens.capacidadeDaVan(viagem.getVanId());
        return viagens.criar(viagem);
    }

    @Transactional
    public Viagem atualizar(Long id, Viagem nova) {
        viagens.bloquear(id);
        Viagem anterior = viagens.buscarPorId(id);
        int ocupados = reservas.contarOcupados(id, null);
        int capacidadeNovaVan = viagens.capacidadeDaVan(nova.getVanId());
        if (ocupados > capacidadeNovaVan) {
            throw new RegraNegocioException("Nova van tem capacidade insuficiente");
        }
        if (ocupados > 0 && "CANCELADA".equals(nova.getStatus())) {
            throw new RegraNegocioException("Cancele as reservas antes de cancelar a viagem");
        }
        if (ocupados > 0 && (!anterior.getUnidadeDestinoId().equals(nova.getUnidadeDestinoId()) ||
                !anterior.getDataHoraSaida().equals(nova.getDataHoraSaida()))) {
            throw new RegraNegocioException("Ha reservas: nao altere destino nem horario da viagem");
        }
        return viagens.atualizar(id, nova);
    }

    @Transactional
    public void remover(Long id) { viagens.remover(id); }

    public OcupacaoDTO ocupacao(Long id) {
        Viagem viagem = viagens.buscarPorId(id);
        int capacidade = viagens.capacidadeDaVan(viagem.getVanId());
        int ocupados = reservas.contarOcupados(id, null);
        return new OcupacaoDTO(id, capacidade, ocupados, capacidade - ocupados);
    }
}
