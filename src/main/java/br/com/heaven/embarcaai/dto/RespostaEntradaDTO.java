package br.com.heaven.embarcaai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RespostaEntradaDTO(
        @NotBlank @Pattern(regexp = "CONFIRMOU|CANCELOU", message = "Use CONFIRMOU ou CANCELOU")
        String resposta) {}
