package br.com.heaven.embarcaai.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

public class ConsultaDoPacienteDTO {
    @NotNull
    @Positive
    private Long unidadeId;
    @NotBlank
    @Size(max = 40)
    private String especialidade;
    @NotNull
    private LocalDateTime dataHora;
    @NotBlank
    @Pattern(regexp = "S|N")
    private String precisaTransporte;
    @NotBlank
    @Pattern(regexp = "AGENDADA|CONFIRMADA|CANCELADA|REALIZADA|FALTOU")
    private String status;

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
