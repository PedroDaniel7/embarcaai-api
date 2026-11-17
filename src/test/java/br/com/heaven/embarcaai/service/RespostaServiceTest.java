package br.com.heaven.embarcaai.service;

import br.com.heaven.embarcaai.dao.ConsultaDAO;
import br.com.heaven.embarcaai.dao.ReservaAssentoDAO;
import br.com.heaven.embarcaai.dao.LembreteDAO;
import br.com.heaven.embarcaai.dto.RespostaEntradaDTO;
import br.com.heaven.embarcaai.exception.RegraNegocioException;
import br.com.heaven.embarcaai.model.Consulta;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RespostaServiceTest {
    private final ConsultaDAO consultas = mock(ConsultaDAO.class);
    private final ReservaAssentoDAO reservas = mock(ReservaAssentoDAO.class);
    private final ReservaAssentoService reservaService = mock(ReservaAssentoService.class);
    private final LembreteDAO lembretes = mock(LembreteDAO.class);
    private final RespostaService service = new RespostaService(consultas, reservas, reservaService, lembretes);

    @Test
    void naoConfirmaTransporteSemReserva() {
        Consulta consulta = new Consulta();
        consulta.setStatus("AGENDADA");
        consulta.setPrecisaTransporte("S");
        when(consultas.bloquear(5L)).thenReturn(consulta);
        when(reservas.listarPorConsulta(5L)).thenReturn(List.of());

        assertThrows(RegraNegocioException.class,
                () -> service.registrar(5L, new RespostaEntradaDTO("CONFIRMOU")));
        verify(lembretes, never()).registrarResposta(anyLong(), anyString());
    }

    @Test
    void cancelamentoLiberaAssentosERegistraHistorico() {
        Consulta consulta = new Consulta();
        consulta.setStatus("AGENDADA");
        consulta.setPrecisaTransporte("S");
        when(consultas.bloquear(5L)).thenReturn(consulta);
        when(consultas.atualizar(5L, consulta)).thenReturn(consulta);

        service.registrar(5L, new RespostaEntradaDTO("CANCELOU"));
        verify(reservas).cancelarReservasDaConsulta(5L);
        verify(lembretes).registrarResposta(5L, "CANCELOU");
    }
}
