package br.com.heaven.embarcaai.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConsultaSaidaDTO {
    private Long id;
    private Long pacienteId;
    private Long unidadeId;
    private String especialidade;
    private LocalDateTime dataHora;
    private String precisaTransporte;
    private String status;

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long value) { this.pacienteId = value; }

    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long value) { this.unidadeId = value; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String value) { this.especialidade = value; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime value) { this.dataHora = value; }

    public String getPrecisaTransporte() { return precisaTransporte; }
    public void setPrecisaTransporte(String value) { this.precisaTransporte = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

}
