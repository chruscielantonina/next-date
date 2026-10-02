document.addEventListener('DOMContentLoaded', () => {
    requireAuth();
    loadUserProfile();
    loadAppointments();
});

let appointmentsData = [];

const UNIT_LABELS = {
    DAYS: 'dni',
    WEEKS: 'tygodni',
    MONTHS: 'miesięcy',
    YEARS: 'lat'
};

// Zabezpieczenie przed XSS przy wstawianiu tekstu do innerHTML
function escapeHtml(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// 1. Pobieranie danych profilu użytkownika
async function loadUserProfile() {
    const token = getToken();

    try {
        const res = await fetch(`${API_URL}/users/me`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) throw new Error('Sesja wygasła');

        const user = await res.json();

        const userNameElem = document.getElementById('userName');
        const dashUserHeaderElem = document.getElementById('dashUserHeader');
        const dashEmailElem = document.getElementById('dashEmail');

        if (userNameElem) userNameElem.innerText = `${user.firstName} ${user.lastName}`;
        if (dashUserHeaderElem) dashUserHeaderElem.innerText = user.firstName;
        if (dashEmailElem) dashEmailElem.innerText = user.email;

    } catch (err) {
        logout();
    }
}

// 2. Pobieranie wizyt z bazy
async function loadAppointments() {
    const token = getToken();
    const container = document.getElementById('appointmentsList');

    try {
        const res = await fetch(`${API_URL}/appointments`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) throw new Error('Błąd pobierania wizyt');

        appointmentsData = await res.json();
        renderAppointments(appointmentsData);
    } catch (err) {
        if (container) {
            container.innerHTML = `<p class="text-slate-400 text-sm italic">Brak wizyt do wyświetlenia.</p>`;
        }
    }
}

// 3. Renderowanie wizyt na stronie
function renderAppointments(appointments) {
    const container = document.getElementById('appointmentsList');
    if (!container) return;

    if (!appointments || appointments.length === 0) {
        container.innerHTML = `<p class="text-slate-400 text-sm italic">Brak wizyt do wyświetlenia.</p>`;
        return;
    }

    container.innerHTML = appointments.map(app => {
        // Pola zgodne z encją Appointment.java
        const title = escapeHtml(app.name || 'Brak tytułu');
        const nextDate = escapeHtml(app.nextDate || 'Brak daty');
        const previousDate = app.previousDate ? escapeHtml(app.previousDate) : null;
        const interval = app.timePeriod > 0 ? app.timePeriod : null;
        const unit = UNIT_LABELS[app.timeUnit] || escapeHtml(app.timeUnit || '');
        const id = escapeHtml(app.id);

        const intervalText = interval ? `Co ${interval} ${unit}` : 'Bez cyklu';
        const previousText = previousDate ? ` • ostatnia: ${previousDate}` : '';

        return `
            <div class="p-4 rounded-xl border bg-slate-900/80 border-slate-700 flex justify-between items-center transition hover:border-indigo-500/50">
                <div>
                    <h4 class="font-bold text-white">${title}</h4>
                    <p class="text-sm text-indigo-300">📅 Następna: ${nextDate} (${intervalText})${previousText}</p>
                </div>
                <div class="flex gap-2">
                    <button onclick="editAppointment('${id}')" class="text-xs bg-slate-700 hover:bg-slate-600 text-white px-3 py-1.5 rounded transition">Edytuj</button>
                    <button onclick="deleteAppointment('${id}')" class="text-xs bg-red-600/80 hover:bg-red-600 text-white px-3 py-1.5 rounded transition">Usuń</button>
                </div>
            </div>
        `;
    }).join('');
}

// 4. Obsługa wyskakującego okienka (Modal)
function openModal(appointment = null) {
    const form = document.getElementById('appointmentForm');
    if (form) form.reset();

    document.getElementById('appointmentId').value = '';

    if (appointment) {
        document.getElementById('modalTitle').innerText = 'Edytuj wizytę';
        document.getElementById('appointmentId').value = appointment.id;
        document.getElementById('title').value = appointment.name || '';
        document.getElementById('appointmentDate').value = appointment.previousDate || '';
        document.getElementById('intervalValue').value = appointment.timePeriod > 0 ? appointment.timePeriod : 1;
        document.getElementById('timeUnit').value = appointment.timeUnit || 'MONTHS';
    } else {
        document.getElementById('modalTitle').innerText = 'Dodaj nową wizytę';
    }

    document.getElementById('appointmentModal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('appointmentModal').classList.add('hidden');
}

// 5. Zapisywanie wizyty (POST dla nowej, PUT dla edycji)
async function handleFormSubmit(e) {
    e.preventDefault();
    const token = getToken();

    const id = document.getElementById('appointmentId').value;

    // Nazwy pól muszą się zgadzać z encją Appointment.java
    const payload = {
        name: document.getElementById('title').value,
        previousDate: document.getElementById('appointmentDate').value,
        timePeriod: parseInt(document.getElementById('intervalValue').value, 10),
        timeUnit: document.getElementById('timeUnit').value
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_URL}/appointments/${id}` : `${API_URL}/appointments`;

    try {
        const res = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Błąd (${res.status}): ${errorText || 'Nie udało się zapisać wizyty.'}`);
        }

        closeModal();
        loadAppointments();
    } catch (err) {
        alert(err.message);
    }
}

// 6. Edycja i Usuwanie
function editAppointment(id) {
    // id to UUID (string), więc porównujemy jako string
    const app = appointmentsData.find(a => String(a.id) === String(id));
    if (app) openModal(app);
}

async function deleteAppointment(id) {
    if (!confirm('Czy na pewno chcesz usunąć tę wizytę?')) return;
    const token = getToken();

    try {
        const res = await fetch(`${API_URL}/appointments/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) throw new Error();
        loadAppointments();
    } catch {
        alert('Błąd podczas usuwania wizyty.');
    }
}