const toggle = document.querySelector('.menu-toggle');
const menu = document.querySelector('.main-nav');
if (toggle && menu) {
  toggle.addEventListener('click', () => { const open = menu.classList.toggle('is-open'); toggle.setAttribute('aria-expanded', String(open)); });
  menu.querySelectorAll('a').forEach(link => link.addEventListener('click', () => { menu.classList.remove('is-open'); toggle.setAttribute('aria-expanded', 'false'); }));
}
document.querySelector('#year').textContent = new Date().getFullYear();

const backToTop = document.querySelector('.back-to-top');
if (backToTop) {
  const updateBackToTop = () => backToTop.classList.toggle('is-visible', window.scrollY > 450);
  window.addEventListener('scroll', updateBackToTop, { passive: true });
  backToTop.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));
  updateBackToTop();
}

const activityCarousel = document.querySelector('.activity-carousel');
if (activityCarousel) {
  const track = activityCarousel.querySelector('.activity-track');
  const cards = [...track.querySelectorAll('.activity-card')];
  const dots = activityCarousel.querySelector('.activity-dots');
  let current = 0;

  const render = (index) => {
    current = (index + cards.length) % cards.length;
    track.style.transform = `translateX(-${current * 100}%)`;
    cards.forEach((card, cardIndex) => card.classList.toggle('is-featured', cardIndex === current));
    [...dots.children].forEach((dot, dotIndex) => {
      const active = dotIndex === current;
      dot.classList.toggle('is-active', active);
      dot.setAttribute('aria-current', String(active));
    });
  };

  if (cards.length > 1) {
    cards.forEach((card, index) => {
      const dot = document.createElement('button');
      dot.type = 'button';
      dot.className = 'activity-dot';
      dot.setAttribute('aria-label', `Exibir atividade ${index + 1}: ${card.querySelector('h3').textContent}`);
      dot.addEventListener('click', () => render(index));
      dots.append(dot);
    });
    window.setInterval(() => render(current + 1), 3000);
  }
  render(0);
}
