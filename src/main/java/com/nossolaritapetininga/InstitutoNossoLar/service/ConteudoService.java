package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.Conteudo;
import com.nossolaritapetininga.InstitutoNossoLar.repository.ConteudoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConteudoService {

    private final ConteudoRepository repository;

    public ConteudoService(ConteudoRepository repository) {
        this.repository = repository;
    }

    public List<Conteudo> listarTodos() {
        return repository.findAll();
    }

    public Conteudo buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Conteudo buscarPorChave(String chave) {
        return repository.findByChave(chave).orElse(null);
    }

    public Conteudo salvar(Conteudo conteudo) {
        return repository.save(conteudo);
    }

    public Conteudo atualizar(
            Long id,
            String chave,
            String titulo,
            String texto,
            String textoExtra,
            String imagem,
            String link,
            boolean ativo) {

        Conteudo conteudo =
                repository.findById(id).orElse(null);

        if (conteudo == null) {
            return null;
        }

        conteudo.setChave(chave);
        conteudo.setTitulo(titulo);
        conteudo.setTexto(texto);
        conteudo.setTextoExtra(textoExtra);
        conteudo.setImagem(imagem);
        conteudo.setLink(link);
        conteudo.setAtivo(ativo);

        return repository.save(conteudo);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}