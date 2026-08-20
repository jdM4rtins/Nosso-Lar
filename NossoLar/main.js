/* ============================================================
   MAIN.JS
   Scripts de interatividade do site:
   1. Carrossel de eventos (agora navegado por dots)
   2. Botão "voltar ao topo"
   3. Menu hambúrguer (mobile)
   4. Botão "Mostrar mais" (seção Sobre)
   5. Modo escuro (dark mode)
   ============================================================ */

/* ------------------------------------------------------------
   1. CARROSSEL DE EVENTOS
   Os dots são criados dinamicamente (um para cada ".template"),
   então basta adicionar ou remover slides no HTML que a
   navegação se ajusta sozinha. Avança automaticamente a cada
   5 segundos, e qualquer interação manual (clique num dot)
   reinicia esse temporizador.
   ------------------------------------------------------------ */
let indexAtual = 0;
let intervaloCarrossel;

const slides = document.querySelectorAll('.template');
const trilho = document.querySelector('.carrossel-slide');
const dotsContainer = document.querySelector('.carrossel-dots');

// Cria um dot para cada slide existente
slides.forEach((_, i) => {
    const dot = document.createElement('button');
    dot.classList.add('dot');
    dot.type = 'button';
    dot.setAttribute('aria-label', `Ir para o slide ${i + 1}`);
    dot.addEventListener('click', () => irParaSlide(i));
    dotsContainer.appendChild(dot);
});

const dots = document.querySelectorAll('.dot');

// Move o trilho até o slide atual e marca o dot correspondente como ativo
function atualizarCarrossel() {
    const distancia = -indexAtual * 100;
    trilho.style.transform = `translateX(${distancia}%)`;

    dots.forEach((dot, i) => {
        const ativo = i === indexAtual;
        dot.classList.toggle('ativo', ativo);
        dot.setAttribute('aria-current', ativo ? 'true' : 'false');
    });
}

// Reinicia o avanço automático (chamado após qualquer interação manual)
function reiniciarAutoplay() {
    clearInterval(intervaloCarrossel);
    intervaloCarrossel = setInterval(() => mudarSlide(1), 5000);
}

// Navega diretamente para um slide específico (usado pelos dots)
function irParaSlide(indice) {
    indexAtual = indice;
    atualizarCarrossel();
    reiniciarAutoplay();
}

// Avança/retrocede um slide (usado pelo avanço automático)
function mudarSlide(direcao) {
    indexAtual += direcao;
    if (indexAtual >= slides.length) {
        indexAtual = 0;
    } else if (indexAtual < 0) {
        indexAtual = slides.length - 1;
    }
    atualizarCarrossel();
}

atualizarCarrossel(); // marca o primeiro dot como ativo assim que a página carrega
intervaloCarrossel = setInterval(() => mudarSlide(1), 5000);

/* ------------------------------------------------------------
   2. BOTÃO "VOLTAR AO TOPO"
   Mostra o botão quando o usuário rola a página para baixo e
   leva suavemente até o topo ao clicar.
   ------------------------------------------------------------ */
let botaoTopo = document.getElementById("btnTopo");
window.onscroll = function () {
    verificarRolagem();
};

function verificarRolagem() {
    if (document.body.scrollTop > 100 || document.documentElement.scrollTop > 100) {
        botaoTopo.style.display = "block";
    } else {
        botaoTopo.style.display = "none";
    }
}

function subirTopo() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

/* ------------------------------------------------------------
   3. MENU HAMBÚRGUER (MOBILE)
   Abre/fecha o menu em telas pequenas e fecha o menu
   automaticamente após o usuário clicar em um link
   (útil para navegação por âncoras "#secao").
   ------------------------------------------------------------ */
const btnHamburguer = document.getElementById('btnHamburguer');
const menuLinks = document.getElementById('menuLinks');

btnHamburguer.addEventListener('click', () => {
    menuLinks.classList.toggle('ativo');
    // Sugestão futura de acessibilidade: alternar também o
    // atributo "aria-expanded" do botão (true/false) para que
    // leitores de tela informem se o menu está aberto ou fechado.
});

const links = document.querySelectorAll('.menu a');
links.forEach(link => {
    link.addEventListener('click', () => {
        menuLinks.classList.remove('ativo');
    });
});

/* ------------------------------------------------------------
   4. BOTÃO "MOSTRAR MAIS" (seção Sobre)
   Alterna a exibição do texto extra e o texto do próprio botão.
   ------------------------------------------------------------ */
document.addEventListener("DOMContentLoaded", () => {
    const btnSobre = document.getElementById("btnSobre");
    const textoExtra = document.getElementById("texto-extra");

    // Verifica se os elementos existem na página para não causar erros
    if (btnSobre && textoExtra) {
        btnSobre.addEventListener("click", () => {
            // Alterna a classe 'mostrar' no texto
            textoExtra.classList.toggle("mostrar");

            // Muda o texto do botão dependendo se o texto extra está visível ou não
            if (textoExtra.classList.contains("mostrar")) {
                btnSobre.textContent = "MOSTRAR MENOS";
            } else {
                btnSobre.textContent = "MOSTRAR MAIS";
            }
        });
    }
});

/* ------------------------------------------------------------
   5. MODO ESCURO (DARK MODE)
   Aplica/remove a classe "dark-mode" no <body> com base no
   switch do menu, e salva a preferência do usuário no
   localStorage para que ela seja lembrada em futuras visitas.
   ------------------------------------------------------------ */
const toggleSwitch = document.querySelector('.theme-switch input[type="checkbox"]');
const currentTheme = localStorage.getItem('theme');

// Verifica se já existe uma preferência salva
if (currentTheme === 'dark-mode') {
    document.body.classList.add(currentTheme);
    toggleSwitch.checked = true;
}

// Função para trocar o tema
function switchTheme(e) {
    if (e.target.checked) {
        document.body.classList.add('dark-mode');
        localStorage.setItem('theme', 'dark-mode');
    } else {
        document.body.classList.remove('dark-mode');
        localStorage.setItem('theme', 'light-mode');
    }
}

toggleSwitch.addEventListener('change', switchTheme, false);
