const API_URL = '/api';

// Pobieranie i zapisywanie tokena
function getToken() {
    return localStorage.getItem('jwtToken');
}

function setToken(token) {
    localStorage.setItem('jwtToken', token);
}

function removeToken() {
    localStorage.removeItem('jwtToken');
}

// Strażnik dostępu do stron zabezpieczonych
function requireAuth() {
    const token = getToken();
    if (!token) {
        window.location.href = '/login.html';
    }
}

// Przekierowanie zalogowanego użytkownika (jeśli jest np. na login.html)
function redirectIfAuthenticated() {
    const token = getToken();
    if (token) {
        window.location.href = '/index.html';
    }
}

// Obsługa logowania
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const errorDiv = document.getElementById('errorMessage');

    errorDiv.classList.add('hidden');

    try {
        const res = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!res.ok) throw new Error('Nieprawidłowy email lub hasło.');

        const data = await res.json();
        setToken(data.token);
        window.location.href = '/index.html';
    } catch (err) {
        errorDiv.innerText = err.message;
        errorDiv.classList.remove('hidden');
    }
}

// Obsługa rejestracji
async function handleRegister(e) {
    e.preventDefault();
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const errorDiv = document.getElementById('errorMessage');
    const successDiv = document.getElementById('successMessage');

    errorDiv.classList.add('hidden');
    successDiv.classList.add('hidden');

    try {
        const res = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ firstName, lastName, email, password })
        });

        if (!res.ok) throw new Error('Błąd rejestracji. Konto o podanym emailu może już istnieć.');

        successDiv.innerText = 'Konto zostało utworzone! Przekierowanie do logowania...';
        successDiv.classList.remove('hidden');

        setTimeout(() => {
            window.location.href = '/login.html';
        }, 1500);
    } catch (err) {
        errorDiv.innerText = err.message;
        errorDiv.classList.remove('hidden');
    }
}

// Wylogowanie
function logout() {
    removeToken();
    window.location.href = '/login.html';
}