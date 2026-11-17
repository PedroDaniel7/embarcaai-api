package br.com.heaven.embarcaai.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Paciente {
    private Long id;
    private String nome;
    private String telefone;
    private LocalDate dataNascimento;
    private String zonaRural;

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public String getNome() { return nome; }
    public void setNome(String value) { this.nome = value; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String value) { this.telefone = value; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate value) { this.dataNascimento = value; }

    public String getZonaRural() { return zonaRural; }
    public void setZonaRural(String value) { this.zonaRural = value; }

}
