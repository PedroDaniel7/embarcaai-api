package br.com.heaven.embarcaai.dto;

import java.time.LocalDateTime;

public record LembreteSaidaDTO(Long id, Long consultaId, LocalDateTime dataEnvio, String resposta) {}
