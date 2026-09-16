package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.Noticia;
import com.nossolaritapetininga.InstitutoNossoLar.repository.NoticiaRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoticiaService {

    private final NoticiaRepository repository;

    public NoticiaService(NoticiaRepository repository) {
        this.repository = repository;
    }

    public List<Noticia> listarTodas() {
        return repository.findAll();
    }

    public List<Noticia> listarAtivas() {
        return repository.findByAtivoTrueOrderByDataPublicacaoDesc();
    }

    public Optional<Noticia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Noticia salvar(Noticia noticia) {
        return repository.save(noticia);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}