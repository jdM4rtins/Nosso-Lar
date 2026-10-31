# Plano de melhorias de acessibilidade

## Objetivo e escopo

Este documento organiza melhorias de acessibilidade para o site público e o painel administrativo do Instituto Nosso Lar. Foi preparado por inspeção do código existente em `src/main/resources/templates`, `src/main/resources/static/css` e `src/main/resources/static/js`. É um levantamento inicial do código, não uma certificação de conformidade WCAG: contraste, comportamento real no navegador, leitores de tela, zoom e uso apenas por teclado ainda precisam ser verificados manualmente.

O foco é melhorar o uso por pessoas com deficiência visual, motora, auditiva e cognitiva, além de facilitar navegação por teclado e por tecnologias assistivas. As mudanças devem preservar a identidade visual e o funcionamento existente.

## Implementado neste ajuste

- Criado um fragmento Thymeleaf compartilhado com link para pular ao conteúdo, alternância de alto contraste persistente no navegador e integração do VLibras. O fragmento está incluído nas páginas públicas e administrativas.
- Criados estilos compartilhados para foco visível e preferência `prefers-reduced-motion`.
- Adicionado `id="conteudo"` e foco programático ao conteúdo principal das páginas, permitindo que o atalho leve ao destino.
- Removido o avanço automático do carrossel; seus indicadores permanecem operáveis por teclado e anunciam a atividade selecionada.
- Ajustada a visibilidade do botão “Voltar ao topo” para não entrar na sequência de foco enquanto está oculto; o retorno respeita movimento reduzido.
- Adicionado fechamento do menu responsivo com Escape e retorno do foco ao botão do menu.
- Mensagens gerais de erro recebem `role="alert"` e foco; mensagens de sucesso recebem `role="status"`. Cabeçalhos das tabelas identificam suas colunas com `scope="col"`.
- Adicionada uma página pública de acessibilidade com orientações e mapa dos blocos do site; as tabelas administrativas identificam os cabeçalhos de coluna.

## Recursos de acessibilidade já presentes

- Os templates definem `lang="pt-BR"`, permitindo que tecnologias assistivas identifiquem o idioma principal.
- A página pública tem link “Ir para o conteúdo”, uma região `<main>` identificada, navegação com rótulo acessível e hierarquia de títulos.
- O menu responsivo da página inicial usa um botão nativo com `aria-expanded` e `aria-controls`; o JavaScript atualiza o estado quando o menu abre ou fecha.
- Imagens decorativas na página inicial usam `alt=""` e algumas regiões têm `aria-hidden="true"`; imagens informativas usam texto alternativo.
- O mapa incorporado tem `title`, os campos de formulário em geral têm rótulos associados e controles nativos são usados para links, botões, formulários, detalhes e caixas de seleção.
- O carrossel marca sua região e seus botões de navegação com rótulos ARIA. Os controles são botões HTML e podem receber foco.
- O CSS administrativo define foco para campos de formulário. O controle de exibição de senha possui nome acessível e estado `aria-pressed`.
- O fluxo de redefinição temporária de senha inclui elementos com `role="alert"` e `role="status"`, além de mover o foco para o resultado.

## Melhorias priorizadas

### Prioridade 1 — corrigir barreiras de teclado e movimento

1. **[Implementado] Carrossel com avanço automático sem pausa**

   O código anterior avançava sozinho a cada três segundos. Neste ajuste, o avanço automático foi removido; a pessoa escolhe a atividade pelos botões de navegação. Os cartões e controles recebem nomes e estados acessíveis.

   **Critérios de aceite:** a pessoa pode ler cada atividade sem que o conteúdo mude sozinho; a navegação funciona por teclado; o estado do item selecionado é percebido por leitor de tela; animações podem ser reduzidas ou desativadas pelo sistema.

2. **[Implementado, falta validação visual] Foco visível consistente em todos os controles**

   `src/main/resources/static/css/acessibilidade.css` define um estilo `:focus-visible` comum para links, botões, campos, resumos e elementos com `tabindex`. É necessário confirmar visualmente que ele se mantém legível em todas as páginas, incluindo diálogos e estados de alto contraste.

   **Critérios de aceite:** ao percorrer todas as páginas com Tab e Shift+Tab, sempre é possível identificar visualmente o elemento focado; o indicador tem contraste com o fundo e não fica coberto por cabeçalhos fixos ou diálogos.

3. **[Implementado] Botão “Voltar ao topo” invisível na navegação por teclado**

   O botão agora recebe o atributo `hidden` enquanto não está visível, e o JavaScript sincroniza essa condição com a posição de rolagem. Ao ativá-lo, o foco retorna ao conteúdo principal.

   **Critérios de aceite:** não existe controle invisível na sequência de Tab; quando visível, o botão tem foco destacado e leva a pessoa a um ponto previsível no topo da página.

4. **[Implementado] Redução de movimento**

   `src/main/resources/static/css/acessibilidade.css` reduz animações e transições quando o sistema solicita movimento reduzido. O botão de retorno ao topo também troca rolagem suave por rolagem imediata nesse modo; o carrossel não avança sozinho.

   **Critérios de aceite:** com a preferência de movimento reduzido ativada no sistema, não há rotação automática e as animações decorativas são removidas ou minimizadas.

### Prioridade 2 — facilitar navegação e compreensão

5. **[Implementado] Link para pular ao conteúdo nas páginas**

   O fragmento `templates/fragments/acessibilidade.html` insere o link de salto em todas as páginas; cada região `<main>` tem destino identificável e pode receber foco programático.

   **Critérios de aceite:** ao pressionar Tab no início de cada página, o primeiro atalho permite ir diretamente ao conteúdo principal; o destino recebe foco ou fica claramente identificável.

6. **Padronizar regiões semânticas e títulos do painel**

   Conferir todas as páginas em `templates/admin/` para garantir um `<main>` por página, títulos em ordem lógica e nomes claros para as regiões de navegação. Algumas páginas já usam `<main>` e `h1`, mas o padrão deve ser consistente em listagens, formulários, login, mensagens de acesso negado e histórico.

   **Critérios de aceite:** a navegação por regiões de leitor de tela encontra cabeçalho, navegação e conteúdo principal; cada página tem um título principal único e descritivo.

7. **[Parcialmente implementado] Mensagens de erro e resultado em formulários**

   Mensagens globais de erro agora usam `role="alert"` e recebem foco; mensagens de sucesso usam `role="status"`. Ainda falta associar erros específicos aos campos com `aria-describedby` e `aria-invalid`, revisar validação campo a campo e testar o anúncio de mensagens após submissões inválidas.

   **Critérios de aceite:** erros não são comunicados apenas por cor; a pessoa entende o que ocorreu, qual campo precisa de correção e como corrigir; leitores de tela anunciam o erro após a submissão.

8. **[Implementado, falta validação com leitor de tela] Nomes e estados dos controles do carrossel**

   Os botões são criados dinamicamente com rótulo, o grupo é nomeado, cada cartão é descrito como slide e o botão ativo atualiza `aria-pressed`. A interação ainda deve ser confirmada com leitor de tela.

   **Critérios de aceite:** leitor de tela identifica a seção, cada botão e o cartão selecionado; a pessoa pode operar os controles sem depender de cor ou movimento.

### Prioridade 3 — consistência visual, formulários e conteúdo

9. **[Parcialmente implementado] Verificar contraste e estados visuais**

   Foi adicionado um modo de alto contraste, mas as cores padrão e todos os estados ainda precisam de medição. As cores estão definidas principalmente nos arquivos `style.css` e `admin.css`; verificar texto, botões, links, rótulos, mensagens, bordas e estados ativo/inativo.

   **Critérios de aceite:** cores de texto e componentes são legíveis nos estados normal, hover, foco, selecionado, erro e desabilitado; estado não é transmitido apenas por cor.

10. **Revisar formulários administrativos**

   Os formulários usam principalmente elementos HTML nativos e `label`, o que é uma boa base. Conferir se cada controle tem um rótulo associado, se textos de ajuda estão ligados via `aria-describedby`, se os formatos esperados são explicados e se campos obrigatórios são indicados por texto além do atributo HTML `required`. Revisar especialmente upload, datas, links, edição de conteúdo e redefinição de senha.

   **Critérios de aceite:** todos os campos têm nome acessível e instruções disponíveis antes da entrada; erros e obrigatoriedade são compreensíveis sem depender apenas do comportamento do navegador.

11. **Revisar imagens e conteúdo alternativo**

   A página inicial já usa `alt` dinâmico em atividades, notícias e eventos, e o formulário de evento tem campo para descrição acessível. Validar se o texto alternativo descreve a informação relevante da imagem, e não apenas repete o título. Para imagens decorativas, manter alternativa vazia. Verificar como o painel trata imagens sem descrição e se é possível fornecer texto alternativo para os tipos de conteúdo que necessitam dele.

   **Critérios de aceite:** imagens informativas têm descrição contextual; imagens puramente decorativas são ignoradas pelo leitor de tela; a mesma imagem não é anunciada duas vezes sem necessidade.

12. **[Parcialmente implementado] Tabelas e ações em listas administrativas**

   As tabelas administrativas agora marcam os cabeçalhos de coluna com `scope="col"`. Conferir títulos, comportamento em telas estreitas e nomes das ações por registro. Quando várias linhas têm botões “Editar” ou “Excluir”, garantir que a relação com o respectivo registro fique clara para tecnologia assistiva.

   **Critérios de aceite:** leitor de tela consegue identificar coluna, linha e contexto de cada ação; a tabela pode ser consultada sem perda de informação em zoom e em tela estreita.

## Roteiro de implementação sugerido

1. Validar o carrossel, o foco visível, a redução de movimento e o atalho principal com teclado.
2. Revisar semântica de regiões, hierarquia de títulos e mensagens de erro nos templates administrativos.
3. Revisar formulários, descrições de imagens e tabelas responsivas.
4. Medir contraste em todos os estados e ajustar quando necessário.
5. Validar páginas públicas e administrativas com navegação por teclado, zoom, leitor de tela e ferramentas de inspeção de acessibilidade.

## Como validar

- Navegar por todas as páginas sem mouse, usando Tab, Shift+Tab, Enter, Espaço e Escape quando aplicável.
- Conferir foco visível, sequência lógica de foco, abertura/fechamento de menu, controles de carrossel, diálogos e retorno de foco após fechar diálogos.
- Testar zoom do navegador em 200% e largura equivalente a dispositivos móveis, verificando ausência de conteúdo cortado ou rolagem horizontal desnecessária.
- Usar pelo menos um leitor de tela em combinações representativas de navegador e sistema, verificando idioma, títulos, regiões, formulários, erros e mudanças de estado.
- Medir contraste de texto e componentes, em vez de aprová-lo apenas pela aparência.
- Repetir as verificações nas páginas administrativas autenticadas e na página pública, pois são fluxos e templates diferentes.

## Pendências de validação e implementação

A aplicação dos pontos acima não conclui todas as recomendações eMAG. Permanecem a associação de mensagens de erro a campos específicos, a verificação de contraste em todos os estados, a checagem das descrições alternativas de imagens cadastradas, a avaliação responsiva das tabelas e a validação com teclado e leitor de tela. A página de acessibilidade e os recursos implementados precisam ser confirmados com pessoas usuárias e navegadores assistivos.

## Limites desta análise

Este plano foi construído por inspeção estática dos arquivos do projeto. Ele identifica funcionalidades existentes e riscos que merecem correção ou validação, mas não afirma conformidade integral com WCAG nem substitui testes com usuários com deficiência. A ordem de prioridade é uma proposta de trabalho; os itens podem ser divididos em entregas menores após a definição do escopo e dos critérios de aceite da instituição.

## Referências oficiais consultadas

- [eMAG 3.1 — recomendações e elementos padronizados](https://emag.governoeletronico.gov.br/cursodesenvolvedor/desenvolvimento-web/recomendacoes-de-acessibilidade-emag3.html)
- [Manual oficial de integração do VLibras Widget](https://www.vlibras.gov.br/doc/widget/installation/webpageintegration.html)
