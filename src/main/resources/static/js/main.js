const toggle = document.querySelector('.menu-toggle');
const menu = document.querySelector('.main-nav');
if (toggle && menu) {
  toggle.addEventListener('click', () => {
    const open = menu.classList.toggle('is-open');
    toggle.setAttribute('aria-expanded', String(open));
  });
  menu.querySelectorAll('a').forEach((link) => link.addEventListener('click', () => {
    menu.classList.remove('is-open');
    toggle.setAttribute('aria-expanded', 'false');
  }));
  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape' && menu.classList.contains('is-open')) {
      menu.classList.remove('is-open');
      toggle.setAttribute('aria-expanded', 'false');
      toggle.focus();
    }
  });
}

const year = document.querySelector('#year');
if (year) year.textContent = new Date().getFullYear();

const backToTop = document.querySelector('.back-to-top');
if (backToTop) {
  const updateBackToTop = () => {
    const visible = window.scrollY > 450;
    backToTop.hidden = !visible;
    backToTop.classList.toggle('is-visible', visible);
  };
  window.addEventListener('scroll', updateBackToTop, { passive: true });
  backToTop.addEventListener('click', () => {
    const movimentoReduzido = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    window.scrollTo({ top: 0, behavior: movimentoReduzido ? 'auto' : 'smooth' });
    document.querySelector('#conteudo')?.focus({ preventScroll: true });
  });
  updateBackToTop();
}

const activityCarousel = document.querySelector('.activity-carousel');
if (activityCarousel) {
  const track = activityCarousel.querySelector('.activity-track');
  const cards = [...track.querySelectorAll('.activity-card')];
  const dots = activityCarousel.querySelector('.activity-dots');
  let current = 0;

  if (cards.length === 0) {
    activityCarousel.hidden = true;
  } else {
    const render = (index) => {
      current = (index + cards.length) % cards.length;
      track.style.transform = `translateX(-${current * 100}%)`;
      cards.forEach((card, cardIndex) => {
        card.classList.toggle('is-featured', cardIndex === current);
        card.setAttribute('role', 'group');
        card.setAttribute('aria-roledescription', 'slide');
        const nome = card.querySelector('h3')?.textContent.trim();
        card.setAttribute('aria-label', `Atividade ${cardIndex + 1} de ${cards.length}: ${nome}`);
      });
      [...dots.children].forEach((dot, dotIndex) => {
        const active = dotIndex === current;
        dot.classList.toggle('is-active', active);
        dot.setAttribute('aria-pressed', String(active));
      });
    };

    if (cards.length > 1) {
      cards.forEach((card, index) => {
        const dot = document.createElement('button');
        dot.type = 'button';
        dot.className = 'activity-dot';
        dot.setAttribute('aria-label', `Exibir atividade ${index + 1}: ${card.querySelector('h3').textContent}`);
        dot.setAttribute('aria-pressed', 'false');
        dot.addEventListener('click', () => render(index));
        dots.append(dot);
      });
    }
    render(0);
  }
}
