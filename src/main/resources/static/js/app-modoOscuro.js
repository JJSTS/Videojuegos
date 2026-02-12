function setCookie(name, value, days) {
    let expires = "";
    if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

function getCookie(name) {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

const temaGuardado = getCookie('modoOscuro');
if (temaGuardado) {
    document.documentElement.setAttribute('data-bs-theme', temaGuardado);
}

document.addEventListener('DOMContentLoaded', () => {
    const toggleButton = document.getElementById('darkModeToggle');
    const icon = toggleButton ? toggleButton.querySelector('i') : null;

    const updateIcon = (theme) => {
        if (!icon) return;
        if (theme === 'dark') {
            icon.classList.remove('bi-moon-fill');
            icon.classList.add('bi-sun-fill');
        } else {
            icon.classList.remove('bi-sun-fill');
            icon.classList.add('bi-moon-fill');
        }
    };

    const currentTheme = document.documentElement.getAttribute('data-bs-theme') || 'light';
    updateIcon(currentTheme);

    if (toggleButton) {
        toggleButton.addEventListener('click', (e) => {
            e.preventDefault();
            const currentTheme = document.documentElement.getAttribute('data-bs-theme') || 'light';
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';

            document.documentElement.setAttribute('data-bs-theme', newTheme);
            setCookie('modoOscuro', newTheme, 365); // Guardar por 1 año
            updateIcon(newTheme);
        });
    }
});
