package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.Evento;
import com.nossolaritapetininga.InstitutoNossoLar.repository.EventoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    public List<Evento> listarTodos() {
        return repository.findAllByOrderByIdAsc();
    }

    public List<Evento> listarAtivosOrdenados() {
        return repository.findByAtivoTrueAndTituloIsNotNullAndDataInicioIsNotNullOrderByDataInicioAsc();
    }

    public Evento buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Evento salvar(Evento evento) {
        return repository.save(evento);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
