document.addEventListener('DOMContentLoaded', () => {
    requireAuth();
    loadUserProfile();
    loadAppointments();
});

let appointmentsData = [];

// [singular, plural]
const UNIT_LABELS = {
    DAYS: ['day', 'days'],
    WEEKS: ['week', 'weeks'],
    MONTHS: ['month', 'months'],
    YEARS: ['year', 'years']
};

function unitLabel(unit, amount) {
    const labels = UNIT_LABELS[unit];
    if (!labels) return escapeHtml(unit || '');
    return amount === 1 ? labels[0] : labels[1];
}

// Protects against XSS when inserting text into innerHTML
function escapeHtml(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// 1. Load the user's profile
async function loadUserProfile() {
    const token = getToken();

    try {
        const res = await fetch(`${API_URL}/users/me`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) throw new Error('Session expired');

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

// 2. Load appointments from the backend
async function loadAppointments() {
    const token = getToken();
    const container = document.getElementById('appointmentsList');

    try {
        const res = await fetch(`${API_URL}/appointments`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) throw new Error('Failed to load appointments');

        appointmentsData = await res.json();
        renderAppointments(appointmentsData);
    } catch (err) {
        if (container) {
            container.innerHTML = `<p class="text-slate-400 text-sm italic">No appointments to display.</p>`;
        }
    }
}

// 3. Render appointments on the page
function renderAppointments(appointments) {
    const container = document.getElementById('appointmentsList');
    if (!container) return;

    if (!appointments || appointments.length === 0) {
        container.innerHTML = `<p class="text-slate-400 text-sm italic">No appointments to display.</p>`;
        return;
    }

    container.innerHTML = appointments.map(app => {
        // Fields match the Appointment.java entity
        const title = escapeHtml(app.name || 'Untitled');
        const nextDate = escapeHtml(app.nextDate || 'No date');
        const previousDate = app.previousDate ? escapeHtml(app.previousDate) : null;
        const interval = app.timePeriod > 0 ? app.timePeriod : null;
        const id = escapeHtml(app.id);

        const intervalText = interval
            ? `Every ${interval} ${unitLabel(app.timeUnit, interval)}`
            : 'No repeat';
        const previousText = previousDate ? ` • last: ${previousDate}` : '';

        return `
            <div class="p-4 rounded-xl border bg-slate-900/80 border-slate-700 flex justify-between items-center transition hover:border-indigo-500/50">
                <div>
                    <h4 class="font-bold text-white">${title}</h4>
                    <p class="text-sm text-indigo-300">📅 Next: ${nextDate} (${intervalText})${previousText}</p>
                </div>
                <div class="flex gap-2">
                    <button onclick="editAppointment('${id}')" class="text-xs bg-slate-700 hover:bg-slate-600 text-white px-3 py-1.5 rounded transition">Edit</button>
                    <button onclick="deleteAppointment('${id}')" class="text-xs bg-red-600/80 hover:bg-red-600 text-white px-3 py-1.5 rounded transition">Delete</button>
                </div>
            </div>
        `;
    }).join('');
}

// 4. Modal handling
function openModal(appointment = null) {
    const form = document.getElementById('appointmentForm');
    if (form) form.reset();

    document.getElementById('appointmentId').value = '';

    if (appointment) {
        document.getElementById('modalTitle').innerText = 'Edit appointment';
        document.getElementById('appointmentId').value = appointment.id;
        document.getElementById('title').value = appointment.name || '';
        document.getElementById('appointmentDate').value = appointment.previousDate || '';
        document.getElementById('intervalValue').value = appointment.timePeriod > 0 ? appointment.timePeriod : 1;
        document.getElementById('timeUnit').value = appointment.timeUnit || 'MONTHS';
    } else {
        document.getElementById('modalTitle').innerText = 'Add new appointment';
    }

    document.getElementById('appointmentModal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('appointmentModal').classList.add('hidden');
}

// 5. Save an appointment (POST for new, PUT for edit)
async function handleFormSubmit(e) {
    e.preventDefault();
    const token = getToken();

    const id = document.getElementById('appointmentId').value;

    // Field names must match the Appointment.java entity
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
            throw new Error(`Error (${res.status}): ${errorText || 'Failed to save the appointment.'}`);
        }

        closeModal();
        loadAppointments();
    } catch (err) {
        alert(err.message);
    }
}

// 6. Edit and delete
function editAppointment(id) {
    // id is a UUID (string), so compare as strings
    const app = appointmentsData.find(a => String(a.id) === String(id));
    if (app) openModal(app);
}

async function deleteAppointment(id) {
    if (!confirm('Are you sure you want to delete this appointment?')) return;
    const token = getToken();

    try {
        const res = await fetch(`${API_URL}/appointments/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) throw new Error();
        loadAppointments();
    } catch {
        alert('Error while deleting the appointment.');
    }
}