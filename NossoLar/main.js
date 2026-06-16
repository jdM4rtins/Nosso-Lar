/* ============================================================
   MAIN.JS
   Scripts de interatividade do site:
   1. Pipas flutuantes (animação aleatória)
   2. Carrossel de eventos
   3. Botão "voltar ao topo"
   4. Menu hambúrguer (mobile)
   5. Botão "Mostrar mais" (seção Sobre)
   6. Modo escuro (dark mode)
   ============================================================ */

/* ------------------------------------------------------------
   1. PIPAS FLUTUANTES
   A cada 2 segundos, move levemente cada pipa em uma direção
   aleatória, criando um efeito sutil de "balanço ao vento".
   ------------------------------------------------------------ */
const pipas = document.querySelectorAll('.pipa');
pipas.forEach(pipa => {
    setInterval(() => {
        let x = Math.random() * 20 - 10;
        let y = Math.random() * 20 - 10;
        pipa.style.transform = `translate(${x}px, ${y}px)`;
    }, 2000);
});

/* ------------------------------------------------------------
   2. CARROSSEL DE EVENTOS
   Troca os slides ao clicar nas setas (mudarSlide) e também
   avança automaticamente a cada 5 segundos.
   ------------------------------------------------------------ */
let indexAtual = 0;

function mudarSlide(direcao) {
    const slides = document.querySelectorAll('.template');
    const trilho = document.querySelector('.carrossel-slide');
    indexAtual += direcao;
    if (indexAtual >= slides.length) {
        indexAtual = 0;
    }
    else if (indexAtual < 0) {
        indexAtual = slides.length - 1;
    }
    const distancia = -indexAtual * 100;
    trilho.style.transform = `translateX(${distancia}%)`;

    // Sempre que o slide muda (seja por clique nas setas ou
    // automaticamente), reinicia o temporizador do avanço
    // automático. Isso evita que, logo após o usuário clicar
    // manualmente, o carrossel "pule" de novo poucos instantes
    // depois.
    clearInterval(intervaloCarrossel);
    intervaloCarrossel = setInterval(() => mudarSlide(1), 5000);
}

// Avanço automático do carrossel (a cada 5 segundos)
let intervaloCarrossel = setInterval(() => mudarSlide(1), 5000);

/* ------------------------------------------------------------
   3. BOTÃO "VOLTAR AO TOPO"
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
   4. MENU HAMBÚRGUER (MOBILE)
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
   5. BOTÃO "MOSTRAR MAIS" (seção Sobre)
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
   6. MODO ESCURO (DARK MODE)
   Aplica/remove a classe "dark-mode" no <body> com base no
   switch do menu, e salva a preferência do usuário no
   localStorage para que ela seja lembrada em futuras visitas.
   ------------------------------------------------------------ */
const toggleSwitch = document.querySelector('.theme-switch input[type="checkbox"]');
const currentTheme = localStorage.getItem('theme');

// Verifica se já existe uma preferência salva
if (currentTheme) {
    document.body.classList.add(currentTheme);

    if (currentTheme === 'dark-mode') {
        toggleSwitch.checked = true;
    }
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
