package br.com.heaven.embarcaai.service;

import br.com.heaven.embarcaai.dao.ConsultaDAO;
import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.dao.ViagemDAO;
import br.com.heaven.embarcaai.exception.RegraNegocioException;
import br.com.heaven.embarcaai.model.Consulta;
import br.com.heaven.embarcaai.model.ReservaAssento;
import br.com.heaven.embarcaai.model.Viagem;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservaAssentoServiceTest {
    private final ReservaAssentoDAO reservas = mock(ReservaAssentoDAO.class);
    private final ConsultaDAO consultas = mock(ConsultaDAO.class);
    private final ViagemDAO viagens = mock(ViagemDAO.class);
    private final ReservaAssentoService service = new ReservaAssentoService(reservas, consultas, viagens);

    @Test
    void rejeitaReservaQuandoVanEstaLotada() {
        Consulta consulta = new Consulta();
        consulta.setId(1L);
        consulta.setUnidadeId(4L);
        consulta.setPrecisaTransporte("S");
        consulta.setStatus("AGENDADA");
        consulta.setDataHora(LocalDateTime.parse("2026-12-15T09:00:00"));
        Viagem viagem = new Viagem();
        viagem.setId(1L);
        viagem.setUnidadeDestinoId(4L);
        viagem.setVanId(1L);
        viagem.setStatus("PROGRAMADA");
        viagem.setDataHoraSaida(LocalDateTime.parse("2026-12-15T06:00:00"));
        ReservaAssento reserva = new ReservaAssento();
        reserva.setConsultaId(1L);
        reserva.setViagemId(1L);
        reserva.setStatusTransporte("PENDENTE");

        when(consultas.buscarPorId(1L)).thenReturn(consulta);
        when(viagens.buscarPorId(1L)).thenReturn(viagem);
        when(viagens.capacidadeDaVan(1L)).thenReturn(2);
        when(reservas.contarOcupados(1L, null)).thenReturn(2);

        assertThrows(RegraNegocioException.class, () -> service.criar(reserva));
        verify(reservas, never()).criar(any());
    }

    @Test
    void rejeitaDestinoDiferenteDaConsulta() {
        Consulta consulta = new Consulta();
        consulta.setUnidadeId(4L);
        consulta.setPrecisaTransporte("S");
        consulta.setStatus("AGENDADA");
        Viagem viagem = new Viagem();
        viagem.setId(1L);
        viagem.setUnidadeDestinoId(3L);
        viagem.setStatus("PROGRAMADA");
        ReservaAssento reserva = new ReservaAssento();
        reserva.setConsultaId(1L);
        reserva.setViagemId(1L);
        reserva.setStatusTransporte("PENDENTE");

        when(consultas.buscarPorId(1L)).thenReturn(consulta);
        when(viagens.buscarPorId(1L)).thenReturn(viagem);

        assertThrows(RegraNegocioException.class, () -> service.criar(reserva));
        verify(reservas, never()).criar(any());
    }
}
