package br.com.heaven.embarcaai.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

public class ViagemEntradaDTO {
    @NotNull
    @Positive
    private Long vanId;
    @NotNull
    @Positive
    private Long unidadeDestinoId;
    @NotNull
    private LocalDateTime dataHoraSaida;
    @NotBlank
    @Pattern(regexp = "PROGRAMADA|REALIZADA|CANCELADA")
    private String status;

    public Long getVanId() { return vanId; }
    public void setVanId(Long value) { this.vanId = value; }

    public Long getUnidadeDestinoId() { return unidadeDestinoId; }
    public void setUnidadeDestinoId(Long value) { this.unidadeDestinoId = value; }

    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }
    public void setDataHoraSaida(LocalDateTime value) { this.dataHoraSaida = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

}
