(() => {
    const dialog = document.getElementById('passwordResetDialog');
    if (!dialog) return;

    const confirmSection = document.getElementById('passwordResetConfirm');
    const resultSection = document.getElementById('passwordResetResult');
    const phraseDisplay = document.getElementById('passwordResetPhrase');
    const phraseInput = document.getElementById('passwordResetPhraseInput');
    const confirmButton = document.getElementById('confirmPasswordReset');
    const errorMessage = document.getElementById('passwordResetError');
    const passwordInput = document.getElementById('temporaryPassword');
    const copyStatus = document.getElementById('copyPasswordStatus');
    const csrfToken = document.getElementById('csrfToken').value;
    let selectedUserId = null;

    function clearDialog() {
        selectedUserId = null;
        phraseInput.value = '';
        confirmButton.disabled = true;
        confirmButton.textContent = 'Gerar senha temporária';
        confirmSection.hidden = false;
        resultSection.hidden = true;
        errorMessage.hidden = true;
        errorMessage.textContent = '';
        passwordInput.value = '';
        passwordInput.type = 'password';
        copyStatus.textContent = '';
    }

    document.querySelectorAll('.open-password-reset').forEach((button) => {
        button.addEventListener('click', () => {
            clearDialog();
            selectedUserId = button.dataset.userId;
            phraseDisplay.textContent = `Redefinir senha de ${button.dataset.userName}`;
            document.getElementById('resetUserEmail').textContent = button.dataset.userEmail;
            dialog.showModal();
            phraseInput.focus();
        });
    });

    phraseInput.addEventListener('input', () => {
        confirmButton.disabled = phraseInput.value.trim() !== phraseDisplay.textContent;
        errorMessage.hidden = true;
    });

    confirmButton.addEventListener('click', async () => {
        if (!selectedUserId || phraseInput.value.trim() !== phraseDisplay.textContent) return;
        confirmButton.disabled = true;
        confirmButton.textContent = 'Gerando…';
        errorMessage.hidden = true;
        try {
            const response = await fetch(`/admin/administradores/senha-temporaria/${encodeURIComponent(selectedUserId)}`, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                }
            });
            const body = await response.json();
            if (!response.ok) throw new Error(body.erro || 'Não foi possível redefinir a senha.');
            passwordInput.value = body.senhaTemporaria;
            document.getElementById('resetUserEmail').textContent = body.usuario;
            confirmSection.hidden = true;
            resultSection.hidden = false;
            document.querySelector('#passwordResetResult h2').focus();
        } catch (error) {
            errorMessage.textContent = error.message || 'Falha de comunicação. Tente novamente.';
            errorMessage.hidden = false;
            confirmButton.disabled = false;
            confirmButton.textContent = 'Tentar novamente';
        }
    });

    document.getElementById('toggleTemporaryPassword').addEventListener('click', (event) => {
        const visible = passwordInput.type === 'text';
        passwordInput.type = visible ? 'password' : 'text';
        event.currentTarget.textContent = visible ? 'Mostrar' : 'Ocultar';
        event.currentTarget.setAttribute('aria-label', visible ? 'Mostrar senha' : 'Ocultar senha');
    });

    document.getElementById('copyTemporaryPassword').addEventListener('click', async () => {
        try {
            await navigator.clipboard.writeText(passwordInput.value);
            copyStatus.textContent = 'Senha copiada.';
        } catch {
            passwordInput.type = 'text';
            passwordInput.select();
            const copied = document.execCommand('copy');
            passwordInput.type = 'password';
            copyStatus.textContent = copied ? 'Senha copiada.' : 'Selecione e copie a senha manualmente.';
        }
    });

    function closeDialog() {
        dialog.close();
        clearDialog();
    }

    document.getElementById('closePasswordReset').addEventListener('click', closeDialog);
    document.getElementById('finishPasswordReset').addEventListener('click', closeDialog);
    dialog.addEventListener('click', (event) => {
        if (event.target === dialog) closeDialog();
    });
    dialog.addEventListener('close', clearDialog);
})();
