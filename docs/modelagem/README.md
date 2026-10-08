# Modelo de dados proposto

Este diretório documenta o modelo lógico proposto para discussão antes da migração definitiva do banco.

Os diagramas combinam:

- os atributos realmente existentes nas entidades `Administrador`, `Atividade`, `Conteudo`, `Evento` e `Noticia`;
- os atributos apresentados no diagrama inicial;
- atributos técnicos mínimos para recuperação de senha, auditoria e acompanhamento dos backups.

## Diagramas

1. `01-nucleo-institucional.mmd`: identidade, contatos, endereço, horários, redes sociais e mídias.
2. `02-conteudo-comunicacao.mmd`: páginas, seções, atividades, notícias, eventos, ajuda e comunicação.
3. `03-administracao-seguranca.mmd`: usuários, perfis, permissões, recuperação, auditoria e backups.

Cada arquivo `.mmd` possui imagens `.svg` e `.png` correspondentes, prontas para serem abertas ou compartilhadas. O SVG é a versão vetorial e editável; o PNG é uma cópia conveniente para envio. Para atualizar os SVGs após editar os diagramas, execute:

```bash
node scripts/render-modelagem.mjs
```

## Compatibilidade com o sistema atual

Nesta primeira etapa de implementação, as colunas `imagem` atuais foram preservadas como campos legados. As novas relações com `MIDIA` são opcionais. A tabela genérica `conteudos` também foi mantida enquanto as telas atuais dependerem dela.

As entidades `Campanha`, `Equipe`, `Contratante`, `Newsletter`, `InscritoNewsletter` e `EnvioNewsletter` não possuíam implementação anterior. Seus atributos representam uma proposta mínima para discussão e já foram traduzidos para classes JPA, sem telas ou regras de negócio nesta etapa.

## Regras adotadas

- nomes de tabelas no plural no modelo físico;
- chaves primárias numéricas geradas pelo banco;
- datas de criação e atualização nas entidades administrativas;
- exclusão lógica por meio do atributo `ativo` onde houver conteúdo publicado;
- arquivos centralizados em `MIDIA`;
- senhas e tokens armazenados somente como hash;
- auditoria sem armazenamento de senhas, tokens ou outros segredos;
- backups armazenados fora do banco; `EXECUCAO_BACKUP` contém apenas metadados da execução.
