package br.com.heaven.embarcaai.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ViagemSaidaDTO {
    private Long id;
    private Long vanId;
    private Long unidadeDestinoId;
    private LocalDateTime dataHoraSaida;
    private String status;

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public Long getVanId() { return vanId; }
    public void setVanId(Long value) { this.vanId = value; }

    public Long getUnidadeDestinoId() { return unidadeDestinoId; }
    public void setUnidadeDestinoId(Long value) { this.unidadeDestinoId = value; }

    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }
    public void setDataHoraSaida(LocalDateTime value) { this.dataHoraSaida = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

}
