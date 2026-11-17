package br.com.heaven.embarcaai.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

public class ReservaAssentoEntradaDTO {
    @NotNull
    @Positive
    private Long consultaId;
    @NotNull
    @Positive
    private Long viagemId;
    @NotBlank
    @Pattern(regexp = "PENDENTE|CONFIRMADO|CANCELADO")
    private String statusTransporte;

    public Long getConsultaId() { return consultaId; }
    public void setConsultaId(Long value) { this.consultaId = value; }

    public Long getViagemId() { return viagemId; }
    public void setViagemId(Long value) { this.viagemId = value; }

    public String getStatusTransporte() { return statusTransporte; }
    public void setStatusTransporte(String value) { this.statusTransporte = value; }

}
