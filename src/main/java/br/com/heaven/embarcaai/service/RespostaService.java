package br.com.heaven.embarcaai.service;

import br.com.heaven.embarcaai.dao.ConsultaDAO;
import br.com.heaven.embarcaai.dao.LembreteDAO;
import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.dto.RespostaEntradaDTO;
import br.com.heaven.embarcaai.exception.RegraNegocioException;
import br.com.heaven.embarcaai.model.Consulta;
import br.com.heaven.embarcaai.model.ReservaAssento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RespostaService {
    private final ConsultaDAO consultas;
    private final ReservaAssentoDAO reservas;
    private final ReservaAssentoService reservaService;
    private final LembreteDAO lembretes;

    public RespostaService(ConsultaDAO consultas, ReservaAssentoDAO reservas,
                          ReservaAssentoService reservaService, LembreteDAO lembretes) {
        this.consultas = consultas;
        this.reservas = reservas;
        this.reservaService = reservaService;
        this.lembretes = lembretes;
    }

    // Simula a resposta do WhatsApp; todas as alteracoes sao uma unica transacao.
    @Transactional
    public Consulta registrar(Long id, RespostaEntradaDTO dto) {
        Consulta consulta = consultas.bloquear(id);
        if ("REALIZADA".equals(consulta.getStatus()) || "FALTOU".equals(consulta.getStatus())) {
            throw new RegraNegocioException("A consulta ja foi encerrada");
        }
        if ("CONFIRMOU".equals(dto.resposta())) {
            if ("CANCELADA".equals(consulta.getStatus())) {
                throw new RegraNegocioException("Consulta cancelada nao pode ser confirmada sem reagendamento");
            }
            List<ReservaAssento> ativas = reservas.listarPorConsulta(id).stream()
                    .filter(r -> !"CANCELADO".equals(r.getStatusTransporte())).toList();
            if ("S".equals(consulta.getPrecisaTransporte()) && ativas.isEmpty()) {
                throw new RegraNegocioException("Cadastre uma reserva de assento antes da confirmacao");
            }
            for (ReservaAssento reserva : ativas) {
                reserva.setStatusTransporte("CONFIRMADO");
                reservaService.atualizar(reserva.getId(), reserva);
            }
            consulta.setStatus("CONFIRMADA");
        } else {
            consulta.setStatus("CANCELADA");
            reservas.cancelarReservasDaConsulta(id);
        }
        Consulta resultado = consultas.atualizar(id, consulta);
        lembretes.registrarResposta(id, dto.resposta());
        return resultado;
    }
}
