package br.com.heaven.embarcaai.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

public class PacienteEntradaDTO {
    @NotBlank
    @Size(max = 100)
    private String nome;
    @NotBlank
    @Size(min = 8, max = 15)
    private String telefone;
    @NotNull
    @Past
    private LocalDate dataNascimento;
    @NotBlank
    @Pattern(regexp = "S|N")
    private String zonaRural;

    public String getNome() { return nome; }
    public void setNome(String value) { this.nome = value; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String value) { this.telefone = value; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate value) { this.dataNascimento = value; }

    public String getZonaRural() { return zonaRural; }
    public void setZonaRural(String value) { this.zonaRural = value; }

}
