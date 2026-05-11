const pipas = document.querySelectorAll('.pipa');
pipas.forEach(pipa => {
    setInterval(() => {
        let x = Math.random() * 20 - 10;
        let y = Math.random() * 20 - 10;
        pipa.style.transform = `translate(${x}px, ${y}px)`;
    }, 2000);
});

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
}
setInterval(() => mudarSlide(1), 5000);

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

const btnHamburguer = document.getElementById('btnHamburguer');
const menuLinks = document.getElementById('menuLinks');
btnHamburguer.addEventListener('click', () => {
    menuLinks.classList.toggle('ativo');
});
const links = document.querySelectorAll('.menu a');
links.forEach(link => {
    link.addEventListener('click', () => {
        menuLinks.classList.remove('ativo');
    });
});

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
                btnSobre.textContent = "LEIA MAIS";
            }
        });
    }
});