(() => {
  const root = document.documentElement;
  const toggle = document.querySelector('.contrast-toggle');
  const storageKey = 'nossoLarAltoContraste';

  if (!toggle) return;

  let enabled = false;
  try {
    enabled = localStorage.getItem(storageKey) === 'true';
  } catch (_) {
    // A preferência continua funcional durante a página quando o storage estiver indisponível.
  }

  const updateContrast = (value) => {
    root.classList.toggle('high-contrast', value);
    toggle.setAttribute('aria-pressed', String(value));
    toggle.textContent = value ? 'Desativar alto contraste' : 'Alto contraste';
  };

  updateContrast(enabled);
  toggle.addEventListener('click', () => {
    enabled = !root.classList.contains('high-contrast');
    updateContrast(enabled);
    try {
      localStorage.setItem(storageKey, String(enabled));
    } catch (_) {
      // Não impedir o uso do controle se o navegador bloquear armazenamento local.
    }
  });
})();

const alertaVisivel = [...document.querySelectorAll('[role="alert"]')]
  .find((element) => element.textContent.trim() && element.getClientRects().length > 0);
if (alertaVisivel) {
  alertaVisivel.setAttribute('tabindex', '-1');
  alertaVisivel.focus({ preventScroll: false });
}
