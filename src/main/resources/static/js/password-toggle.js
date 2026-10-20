document.querySelectorAll('[data-password-toggle]').forEach((button) => {
  const input = document.getElementById(button.dataset.passwordToggle);
  const icon = button.querySelector('i');
  if (!input || !icon) return;

  button.addEventListener('click', () => {
    const mostrar = input.type === 'password';
    input.type = mostrar ? 'text' : 'password';
    button.setAttribute('aria-pressed', String(mostrar));
    button.setAttribute('aria-label', mostrar ? 'Ocultar senha' : 'Mostrar senha');
    icon.classList.toggle('fa-eye', !mostrar);
    icon.classList.toggle('fa-eye-slash', mostrar);
  });
});
