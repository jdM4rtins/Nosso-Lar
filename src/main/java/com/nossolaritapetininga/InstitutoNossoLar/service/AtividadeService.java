package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.Atividade;
import com.nossolaritapetininga.InstitutoNossoLar.repository.AtividadeRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtividadeService {

    private final AtividadeRepository repository;

    public AtividadeService(
            AtividadeRepository repository) {

        this.repository = repository;
    }

    public List<Atividade> listarTodos() {

        return repository.findAllByOrderByIdAsc();
    }

    public Atividade buscarPorId(Long id) {

        return repository.findById(id).orElse(null);
    }

    public Atividade salvar(Atividade atividade) {

        return repository.save(atividade);
    }

    public void excluir(Long id) {

        repository.deleteById(id);
    }
}