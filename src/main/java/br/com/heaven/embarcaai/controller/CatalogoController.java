package br.com.heaven.embarcaai.controller;

import br.com.heaven.embarcaai.dao.CatalogoDAO;
import br.com.heaven.embarcaai.dto.UnidadeSaidaDTO;
import br.com.heaven.embarcaai.dto.VanSaidaDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class CatalogoController {
    private final CatalogoDAO dao;
    public CatalogoController(CatalogoDAO dao) { this.dao = dao; }

    @GetMapping("/unidades")
    public List<UnidadeSaidaDTO> unidades() { return dao.unidades(); }

    @GetMapping("/vans")
    public List<VanSaidaDTO> vans() { return dao.vans(); }
}
