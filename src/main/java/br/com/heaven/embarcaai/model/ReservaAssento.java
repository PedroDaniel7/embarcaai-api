package br.com.heaven.embarcaai.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaAssento {
    private Long id;
    private Long consultaId;
    private Long viagemId;
    private String statusTransporte;

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public Long getConsultaId() { return consultaId; }
    public void setConsultaId(Long value) { this.consultaId = value; }

    public Long getViagemId() { return viagemId; }
    public void setViagemId(Long value) { this.viagemId = value; }

    public String getStatusTransporte() { return statusTransporte; }
    public void setStatusTransporte(String value) { this.statusTransporte = value; }

}
