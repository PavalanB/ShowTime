// ShowTime Frontend Client Application
let currentTenant = 'ALL';
let selectedShow = null;
let selectedSeats = [];
let currentLockToken = null;
let lockExpiresAt = null;
let lockTimerInterval = null;
let stompClient = null;

let loggedInUser = null;
let authToken = null;
let allLoadedShows = [];
let activeDateFilter = 'ALL';

document.addEventListener('DOMContentLoaded', () => {
    // Show login by default
});

// --- Auth & Routing ---
async function submitLogin() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();
    
    if(!username || !password) return;

    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password})
        });

        if (res.ok) {
            loggedInUser = await res.json();
            authToken = loggedInUser.token;
            document.getElementById('loginView').style.display = 'none';
            document.getElementById('mainAppContent').style.display = 'block';
            document.getElementById('navUsername').textContent = loggedInUser.fullName;
            
            setupRoleBasedUI();
        } else {
            const err = await res.json();
            document.getElementById('loginError').textContent = err.error || "Login Failed";
        }
    } catch(err) {
        document.getElementById('loginError').textContent = "Network error connecting to auth service.";
    }
}

function toggleAuthForm() {
    const loginForm = document.getElementById('formLogin');
    const registerForm = document.getElementById('formRegister');
    const errorMsg = document.getElementById('loginError');
    errorMsg.textContent = "";
    if (loginForm.style.display === 'none') {
        loginForm.style.display = 'block';
        registerForm.style.display = 'none';
    } else {
        loginForm.style.display = 'none';
        registerForm.style.display = 'block';
    }
}

function toggleTheatreFields() {
    const role = document.getElementById('regRole').value;
    document.getElementById('theatreFields').style.display = role === 'ROLE_THEATRE_OWNER' ? 'block' : 'none';
}

async function submitRegister() {
    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value.trim();
    const fullName = document.getElementById('regFullName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const role = document.getElementById('regRole').value;
    const theatreName = document.getElementById('regTheatreName').value.trim();
    const theatreCity = document.getElementById('regTheatreCity').value.trim();

    if(!username || !password || !fullName) return;

    try {
        const res = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password, fullName, email, role, theatreName, theatreCity})
        });

        if (res.ok) {
            alert("Registration successful! Please sign in.");
            toggleAuthForm();
        } else {
            const err = await res.json();
            document.getElementById('loginError').textContent = err.error || "Registration Failed";
        }
    } catch(err) {
        document.getElementById('loginError').textContent = "Network error connecting to auth service.";
    }
}

function logout() {
    loggedInUser = null;
    authToken = null;
    document.getElementById('loginView').style.display = 'flex';
    document.getElementById('mainAppContent').style.display = 'none';
}

async function setupRoleBasedUI() {
    const tabsContainer = document.getElementById('navTabsContainer');
    tabsContainer.innerHTML = '';
    
    if (loggedInUser.role === 'ROLE_THEATRE_OWNER') {
        currentTenant = loggedInUser.tenantId;
        document.getElementById('navTenantBox').style.display = 'none'; // Lock tenant for owner
        document.getElementById('navRoleBadge').textContent = 'Owner Portal';
        
        tabsContainer.innerHTML = `
            <div class="nav-tab active" onclick="switchView('ownerView', this)">Owner Dashboard</div>
            <div class="nav-tab" onclick="switchView('adminView', this)">Analytics & BI</div>
        `;
        switchView('ownerView');
        populateAdminMovieSelect();
        renderManageMoviesList();
        renderManageShowsList();
    } else {
        document.getElementById('navTenantBox').style.display = 'flex';
        document.getElementById('navRoleBadge').textContent = 'Client Portal';
        
        tabsContainer.innerHTML = `
            <div class="nav-tab active" onclick="switchView('bookingView', this)">🎬 Book Tickets</div>
            <div class="nav-tab" onclick="switchView('myBookingsView', this); loadMyBookings();">🎟️ My Bookings</div>
        `;
        
        // Dynamically fetch all theatres/tenants
        try {
            const res = await fetch('/api/tenants');
            const tenants = await res.json();
            const select = document.getElementById('tenantSelect');
            let optionsHtml = '<option value="ALL">🌐 All Cinemas (Browse All)</option>';
            optionsHtml += tenants.map(t => `<option value="${t.tenantId}">🏢 ${t.name} (${t.city})</option>`).join('');
            select.innerHTML = optionsHtml;
            currentTenant = 'ALL';
            select.value = 'ALL';
        } catch(e) {
            console.error("Failed to load cinemas", e);
        }
        
        switchView('bookingView');
        onTenantChanged(); // Loads shows
    }
}

function switchView(viewId, triggerElement) {
    document.querySelectorAll('.section-view').forEach(v => v.classList.remove('active'));
    document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
    
    const target = document.getElementById(viewId);
    if(target) {
        target.classList.add('active');
    }
    
    if (triggerElement && triggerElement.classList) {
        triggerElement.classList.add('active');
    } else if (window.event && window.event.target && window.event.target.classList) {
        window.event.target.classList.add('active');
    }
}

function onTenantChanged() {
    if (loggedInUser && loggedInUser.role === 'ROLE_THEATRE_OWNER') {
        currentTenant = loggedInUser.tenantId;
        document.getElementById('theatreBannerTitle').textContent = "Owner Dashboard";
        document.getElementById('theatreBannerSubtitle').textContent = "Manage your movies, screens, shows, and dynamic pricing.";
    } else {
        const sel = document.getElementById('tenantSelect');
        currentTenant = sel ? sel.value : 'ALL';
        if (currentTenant === 'ALL') {
            document.getElementById('theatreBannerTitle').textContent = "All Cinemas & Locations";
            document.getElementById('theatreBannerSubtitle').textContent = "Browse all running movies across all connected theatres and locations.";
        } else {
            document.getElementById('theatreBannerTitle').textContent = sel.options[sel.selectedIndex]?.text || currentTenant;
            document.getElementById('theatreBannerSubtitle').textContent = "Select a movie and showtime to view real-time seat availability.";
        }
    }
    
    document.getElementById('seatStageContainer').style.display = 'none';
    clearLockTimer();
    selectedSeats = [];
    currentLockToken = null;
    
    if(loggedInUser && loggedInUser.role === 'ROLE_CUSTOMER') {
        loadTenantShows();
    }
}

// --- Client Booking Flow ---
async function loadTenantShows() {
    try {
        const url = (currentTenant && currentTenant !== 'ALL') ? `/api/shows?tenantId=${currentTenant}` : '/api/shows';
        const res = await fetch(url);
        allLoadedShows = await res.json();
        applyMovieFilters();
    } catch (err) {
        console.error("Failed to load shows:", err);
    }
}

function setDateFilter(filterType, element) {
    activeDateFilter = filterType;
    document.querySelectorAll('#dateFilterChips .filter-chip').forEach(btn => btn.classList.remove('active'));
    if (element) element.classList.add('active');
    applyMovieFilters();
}

function applyMovieFilters() {
    const searchInput = document.getElementById('movieSearchInput');
    const searchTerm = searchInput ? searchInput.value.toLowerCase().trim() : '';
    const genreSelect = document.getElementById('genreFilterSelect');
    const selectedGenre = genreSelect ? genreSelect.value : 'ALL';

    const now = new Date();
    const todayStr = now.toISOString().split('T')[0];
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tomorrowStr = tomorrow.toISOString().split('T')[0];

    let filtered = allLoadedShows.filter(s => {
        // Search filter
        if (searchTerm) {
            const title = (s.movieTitle || '').toLowerCase();
            const genre = (s.genre || '').toLowerCase();
            const lang = (s.language || '').toLowerCase();
            const theatre = (s.theatreName || '').toLowerCase();
            const city = (s.theatreCity || '').toLowerCase();
            if (!title.includes(searchTerm) && !genre.includes(searchTerm) && !lang.includes(searchTerm) && !theatre.includes(searchTerm) && !city.includes(searchTerm)) {
                return false;
            }
        }

        // Genre filter
        if (selectedGenre !== 'ALL') {
            const genre = (s.genre || '').toLowerCase();
            if (!genre.includes(selectedGenre.toLowerCase())) return false;
        }

        // Date filter
        if (s.startTime) {
            const showDate = s.startTime.split('T')[0];
            if (activeDateFilter === 'TODAY' && showDate !== todayStr) return false;
            if (activeDateFilter === 'TOMORROW' && showDate !== tomorrowStr) return false;
        }

        return true;
    });

    renderMoviesAndShows(filtered);
}

function renderMoviesAndShows(shows) {
    const container = document.getElementById('moviesContainer');
    const badge = document.getElementById('moviesCountBadge');
    container.innerHTML = '';

    if (!shows || shows.length === 0) {
        if (badge) badge.textContent = '0 shows available';
        container.innerHTML = `
            <div class="glass-panel" style="grid-column: 1 / -1; padding: 2.5rem; text-align: center;">
                <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🎬</div>
                <h3 style="margin-bottom: 0.5rem; color: #fff;">No Shows Found</h3>
                <p style="color: var(--text-secondary); font-size: 0.9rem;">
                    ${currentTenant === 'ALL' ? 'No shows currently scheduled across any cinema. Log in as an Owner to schedule shows.' : 'No shows currently scheduled for this theatre. Try choosing "All Cinemas" in the top bar.'}
                </p>
            </div>
        `;
        return;
    }

    if (badge) badge.textContent = `${shows.length} showtimes available`;

    const moviesMap = {};
    shows.forEach(s => {
        if (!moviesMap[s.movieId]) {
            moviesMap[s.movieId] = {
                id: s.movieId,
                title: s.movieTitle || 'Movie Title',
                posterUrl: s.posterUrl || 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400',
                genre: s.genre || 'Action',
                language: s.language || 'English',
                rating: s.rating || 'U/A',
                durationMinutes: s.durationMinutes || 120,
                shows: []
            };
        }
        moviesMap[s.movieId].shows.push(s);
    });

    Object.values(moviesMap).forEach(movie => {
        const card = document.createElement('div');
        card.className = 'movie-card glass-panel';

        let showPillsHtml = movie.shows.map(s => {
            const time = new Date(s.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            const dateStr = new Date(s.startTime).toLocaleDateString([], { month: 'short', day: 'numeric' });
            const theatreTag = (currentTenant === 'ALL' && s.theatreName) 
                ? `<div style="font-size:0.7rem; color: var(--accent-gold); font-weight: 700; margin-bottom: 2px;">🏢 ${s.theatreName}</div>` 
                : '';
            const screenTag = s.screenName 
                ? `<div style="font-size:0.68rem; color: var(--accent-blue); margin-top: 1px;">${s.screenName}</div>` 
                : '';

            return `
                <button class="showtime-pill" onclick="selectShowById(${s.id})">
                    ${theatreTag}
                    <div style="font-weight: 700; font-size: 0.95rem; color: #fff;">${time}</div>
                    <div style="font-size: 0.7rem; color: var(--text-secondary);">${dateStr}</div>
                    ${screenTag}
                    <div style="font-size: 0.7rem; color: var(--accent-green); margin-top: 2px;">₹${s.standardPrice} - ₹${s.vipPrice}</div>
                </button>
            `;
        }).join('');

        card.innerHTML = `
            <img src="${movie.posterUrl}" onerror="this.src='https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400'" class="movie-poster" alt="${movie.title}">
            <div class="movie-info">
                <div class="movie-title">${movie.title}</div>
                <div class="movie-meta">
                    <span class="movie-tag">${movie.language}</span>
                    <span class="movie-tag">${movie.genre}</span>
                    <span class="movie-tag">${movie.rating}</span>
                    <span class="movie-tag" style="background: rgba(245, 158, 11, 0.15); color: var(--accent-gold); border-color: rgba(245, 158, 11, 0.3);">⏱️ ${movie.durationMinutes}m</span>
                </div>
                <div style="font-size: 0.8rem; color: var(--text-secondary); margin-bottom: 0.6rem;">Available Showtimes:</div>
                <div class="showtime-pills">
                    ${showPillsHtml}
                </div>
            </div>
        `;
        container.appendChild(card);
    });
}

function selectShowById(showId) {
    const show = allLoadedShows.find(s => s.id === showId);
    if (!show) return;
    const time = new Date(show.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    selectShow(show.id, show.movieTitle || 'Movie', show.screenName || 'Main Screen', time, show.theatreFullName || show.theatreName || currentTenant);
}

async function selectShow(showId, movieTitle, screenName, showTime, theatreLabel) {
    selectedShow = { id: showId, movieTitle, screenName, showTime, theatreLabel };
    selectedSeats = [];
    currentLockToken = null;
    clearLockTimer();
    updateSummaryUI();
    document.getElementById('btnLockSeats').style.display = 'block';
    document.getElementById('btnProceedPayment').style.display = 'none';
    const btnCancel = document.getElementById('btnCancelHold');
    if (btnCancel) btnCancel.style.display = 'none';

    document.getElementById('seatStageContainer').style.display = 'block';
    document.getElementById('selectedShowHeading').textContent = `${movieTitle} • ${screenName}`;
    document.getElementById('selectedShowMeta').textContent = `Showtime: ${showTime} | Cinema: ${theatreLabel || currentTenant.toUpperCase()}`;

    connectWebSocket(showId);
    await refreshSeats(showId);
    document.getElementById('seatStageContainer').scrollIntoView({ behavior: 'smooth' });
}

async function refreshSeats(showId) {
    try {
        const res = await fetch(`/api/shows/${showId}/seats`);
        const data = await res.json();
        renderSeatGrid(data.seats, data.totalRows, data.seatsPerRow);
    } catch (err) {
        console.error("Failed to load seats:", err);
    }
}

function renderSeatGrid(seats, totalRows, seatsPerRow) {
    const matrix = document.getElementById('seatMatrix');
    matrix.innerHTML = '';

    const rowMap = {};
    seats.forEach(s => {
        if (!rowMap[s.rowName]) rowMap[s.rowName] = [];
        rowMap[s.rowName].push(s);
    });

    Object.keys(rowMap).sort().forEach(rowName => {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'seat-row';

        const label = document.createElement('span');
        label.className = 'row-label';
        label.textContent = rowName;
        rowDiv.appendChild(label);

        rowMap[rowName].sort((a, b) => a.seatCol - b.seatCol).forEach(seat => {
            const seatDiv = document.createElement('div');
            seatDiv.className = `seat tier-${seat.category.toLowerCase()}`;
            seatDiv.dataset.seatNumber = seat.seatNumber;
            seatDiv.dataset.price = seat.price;
            seatDiv.dataset.category = seat.category;
            seatDiv.textContent = seat.seatCol;

            // Render status
            if (seat.status === 'BOOKED') {
                seatDiv.classList.add('booked');
                seatDiv.title = `Seat ${seat.seatNumber} is Booked`;
            } else if (seat.status === 'HELD') {
                if (currentLockToken && seat.lockedByUserId == loggedInUser.userId && selectedSeats.includes(seat.seatNumber)) {
                    seatDiv.classList.add('held-self');
                    seatDiv.title = `Seat ${seat.seatNumber} (Locked for Checkout)`;
                } else {
                    seatDiv.classList.add('held-other');
                    seatDiv.title = `Seat ${seat.seatNumber} (Held by another customer)`;
                }
            } else {
                seatDiv.classList.add('available');
                seatDiv.title = `Seat ${seat.seatNumber} - ${seat.category} (₹${seat.price})`;
                
                // If it is in the local selected array
                if (selectedSeats.includes(seat.seatNumber) && (!currentLockToken)) {
                    seatDiv.classList.add('selected');
                }
                
                seatDiv.onclick = () => {
                    if (currentLockToken) {
                        alert("Please cancel your current hold before selecting different seats.");
                        return;
                    }
                    toggleLocalSeatSelection(seat.seatNumber, seatDiv);
                };
            }

            rowDiv.appendChild(seatDiv);
        });

        matrix.appendChild(rowDiv);
    });
}

// Fix: Local selection logic, prevents immediate backend locking / race condition
function toggleLocalSeatSelection(seatNumber, element) {
    if (selectedSeats.includes(seatNumber)) {
        selectedSeats = selectedSeats.filter(s => s !== seatNumber);
        element.classList.remove('selected');
    } else {
        selectedSeats.push(seatNumber);
        element.classList.add('selected');
    }
    updateSummaryUI();
}

async function lockSelectedSeats() {
    // Simplified: Skip lock and go straight to payment
    openPaymentModal();
}

async function releaseCurrentHold() {
    // Holding feature removed - no lock to release
    currentLockToken = null;
    clearLockTimer();
    selectedSeats = [];
    document.getElementById('btnLockSeats').style.display = 'block';
    document.getElementById('btnProceedPayment').style.display = 'none';
    const btnCancel = document.getElementById('btnCancelHold');
    if (btnCancel) btnCancel.style.display = 'none';
    updateSummaryUI();
    refreshSeats(selectedShow.id);
}

function updateSummaryUI() {
    const count = selectedSeats.length;
    document.getElementById('summaryCount').textContent = count;
    document.getElementById('summarySeats').textContent = count > 0 ? selectedSeats.join(', ') : 'None';

    let subtotal = 0;
    selectedSeats.forEach(seatNum => {
        const seatEl = document.querySelector(`.seat[data-seat-number="${seatNum}"]`);
        if (seatEl) subtotal += parseFloat(seatEl.dataset.price);
    });

    const gst = count > 0 ? subtotal * 0.18 : 0;
    const total = subtotal + gst;

    document.getElementById('summarySubtotal').textContent = `₹${subtotal.toFixed(2)}`;
    document.getElementById('summaryGst').textContent = `₹${gst.toFixed(2)}`;
    document.getElementById('summaryTotal').textContent = `₹${total.toFixed(2)}`;

    // Show/hide payment button based on seat selection (no lock step anymore)
    const btnLock = document.getElementById('btnLockSeats');
    const btnPayment = document.getElementById('btnProceedPayment');
    if (count > 0) {
        if (btnLock) btnLock.style.display = 'none';
        if (btnPayment) btnPayment.style.display = 'block';
    } else {
        if (btnLock) btnLock.style.display = 'block';
        if (btnPayment) btnPayment.style.display = 'none';
    }
    
    document.getElementById('modalPayableAmount').textContent = `₹${total.toFixed(2)}`;
}

// Lock Countdown Timer (disabled - no locking feature)
function startLockTimer() {
    // Timer feature removed
    clearLockTimer();
}

function updateTimerDisplay() {
    // Timer feature removed
}

function clearLockTimer() {
    if (lockTimerInterval) clearInterval(lockTimerInterval);
    const timerBox = document.getElementById('lockTimerBox');
    if (timerBox) timerBox.style.display = 'none';
    const btnCancel = document.getElementById('btnCancelHold');
    if (btnCancel && !currentLockToken) btnCancel.style.display = 'none';
}

function connectWebSocket(showId) {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect();
    }
    try {
        const socket = new SockJS('/ws-seats');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;
        stompClient.connect({}, () => {
            stompClient.subscribe(`/topic/seats/${showId}`, (message) => {
                refreshSeats(showId);
            });
        });
    } catch (e) {}
}

function openPaymentModal() {
    document.getElementById('paymentModal').classList.add('active');
}

function closePaymentModal() {
    document.getElementById('paymentModal').classList.remove('active');
}

async function submitPayment() {
    const simulateFailure = document.getElementById('chkSimulateFailure').checked;
    const paymentMethod = document.getElementById('paymentMethod').value;
    const amount = parseFloat(document.getElementById('summaryTotal').textContent.replace('₹', ''));

    try {
        // 1. Create Booking in PENDING status
        const bookingRes = await fetch('/api/bookings', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                showId: selectedShow.id,
                customerId: loggedInUser.userId,
                lockToken: null,  // No lock feature - booking directly
                seats: selectedSeats
            })
        });

        if (!bookingRes.ok) {
            alert(`Booking Error`);
            return;
        }
        const booking = await bookingRes.json();

        // 2. Authorize Payment
        const paymentRes = await fetch('/api/bookings/confirm', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                bookingReference: booking.bookingReference,
                amount: amount,
                paymentMethod: paymentMethod,
                simulateFailure: simulateFailure
            })
        });

        closePaymentModal();

        if (paymentRes.status === 402) {
            alert("❌ Payment Failed! The simulated payment gateway declined the charge. Your held seats have been safely released.");
            clearLockTimer();
            selectedSeats = [];
            currentLockToken = null;
            document.getElementById('btnLockSeats').style.display = 'block';
            document.getElementById('btnProceedPayment').style.display = 'none';
            updateSummaryUI();
            refreshSeats(selectedShow.id);
            return;
        }

        const data = await paymentRes.json();
        if (data.status === 'SUCCESS') {
            displayTicket(data.booking, data.ticket);
            clearLockTimer();
            selectedSeats = [];
            currentLockToken = null;
            document.getElementById('btnLockSeats').style.display = 'block';
            document.getElementById('btnProceedPayment').style.display = 'none';
            updateSummaryUI();
            refreshSeats(selectedShow.id);
        }

    } catch (err) {
        alert("Transaction could not be completed.");
    }
}

function displayTicket(booking, ticket) {
    document.getElementById('ticketMovie').textContent = selectedShow.movieTitle;
    document.getElementById('ticketTheatre').textContent = currentTenant.toUpperCase();
    document.getElementById('ticketScreen').textContent = selectedShow.screenName;
    document.getElementById('ticketTime').textContent = selectedShow.showTime;
    document.getElementById('ticketSeats').textContent = booking.seatsCsv;
    document.getElementById('ticketBookingRef').textContent = booking.bookingReference;
    document.getElementById('ticketRef').textContent = ticket ? ticket.ticketReference : 'TCK-GENERATED';
    document.getElementById('ticketTotal').textContent = `₹${booking.totalAmount.toFixed(2)}`;
    document.getElementById('ticketModal').classList.add('active');
}

function closeTicketModal() {
    document.getElementById('ticketModal').classList.remove('active');
}

// --- Owner API Features ---
async function handleCreateMovie(e) {
    e.preventDefault();
    const payload = {
        title: document.getElementById('movTitle').value,
        description: document.getElementById('movDesc').value,
        durationMinutes: document.getElementById('movDuration').value,
        genre: document.getElementById('movGenre').value,
        language: document.getElementById('movLang').value,
        rating: document.getElementById('movRating').value,
        posterUrl: document.getElementById('movPoster').value,
    };
    
    try {
        const res = await fetch('/api/movies', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if(res.ok) {
            alert("Movie created successfully!");
            document.getElementById('formAddMovie').reset();
            populateAdminMovieSelect(); // Refresh dropdown
            renderManageMoviesList(); // Refresh list
        } else {
            alert("Error creating movie.");
        }
    } catch(e) {
        console.error(e);
    }
}

async function populateAdminMovieSelect() {
    try {
        const res = await fetch('/api/movies');
        const movies = await res.json();
        const select = document.getElementById('schedMovieId');
        if (select) {
            select.innerHTML = movies.map(m => `<option value="${m.id}">${m.title} (ID: ${m.id})</option>`).join('');
        }
    } catch (e) {
        console.error("Failed to load movies for admin dropdown", e);
    }
}

async function handleScheduleShow(e) {
    e.preventDefault();
    const payload = {
        tenantId: loggedInUser.tenantId,
        movieId: document.getElementById('schedMovieId').value,
        screenId: document.getElementById('schedScreenId').value,
        startTime: document.getElementById('schedTime').value,
        standardPrice: document.getElementById('schedStdPrice').value,
        premiumPrice: document.getElementById('schedPremPrice').value,
        vipPrice: document.getElementById('schedVipPrice').value,
    };
    
    try {
        const res = await fetch('/api/shows', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if(res.ok) {
            alert("Show scheduled successfully!");
            document.getElementById('formScheduleShow').reset();
            renderManageShowsList();
        } else {
            const errData = await res.json();
            alert("Error scheduling show: " + (errData.error || res.statusText));
        }
    } catch(e) {
        console.error(e);
        alert("Network or server error when scheduling show.");
    }
}

async function renderManageShowsList() {
    const container = document.getElementById('manageShowsList');
    if (!container || !loggedInUser || !loggedInUser.tenantId) return;
    container.innerHTML = '<div style="color: var(--text-secondary);">Loading scheduled shows...</div>';

    try {
        const res = await fetch(`/api/shows?tenantId=${loggedInUser.tenantId}`);
        const shows = await res.json();

        if (!shows || shows.length === 0) {
            container.innerHTML = '<p style="color: var(--text-secondary); font-size: 0.9rem;">No shows scheduled for your theatre yet. Use the "Schedule a Show" form above.</p>';
            return;
        }

        container.innerHTML = shows.map(s => {
            const timeStr = s.startTime ? new Date(s.startTime).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' }) : '-';
            return `
                <div class="glass-panel" style="display: flex; justify-content: space-between; align-items: center; padding: 1rem 1.25rem; border-radius: 8px; flex-wrap: wrap; gap: 1rem;">
                    <div>
                        <div style="font-weight: 700; font-size: 1.1rem; color: #fff; margin-bottom: 0.25rem;">
                            ${s.movieTitle || 'Movie ID: ' + s.movieId}
                        </div>
                        <div style="font-size: 0.85rem; color: var(--accent-blue); margin-bottom: 0.25rem;">
                            🖥️ ${s.screenName || 'Screen ' + s.screenId} • 🕒 ${timeStr}
                        </div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary);">
                            Prices: Standard: <strong style="color:#fff;">₹${s.standardPrice}</strong> | Premium: <strong style="color:#fff;">₹${s.premiumPrice}</strong> | VIP: <strong style="color:#fff;">₹${s.vipPrice}</strong>
                        </div>
                    </div>
                    <div>
                        <button class="btn-primary" style="background: #ef4444; border: 1px solid #dc2626; font-size: 0.85rem; padding: 0.5rem 1rem;" onclick="deleteShow(${s.id})">
                            🗑️ Cancel / Delete Show
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    } catch(e) {
        console.error("Failed to load owner shows", e);
        container.innerHTML = '<div style="color: var(--accent-red);">Failed to load scheduled shows.</div>';
    }
}

async function deleteShow(showId) {
    if (!confirm("Are you sure you want to delete this show? Any unbooked seats for this show will be removed.")) return;
    try {
        const res = await fetch(`/api/shows/${showId}`, { method: 'DELETE' });
        if (res.ok) {
            alert("Show deleted successfully!");
            renderManageShowsList();
            if (loggedInUser && loggedInUser.role === 'ROLE_CUSTOMER') {
                loadTenantShows();
            }
        } else {
            const err = await res.json();
            alert("Error deleting show: " + (err.error || res.statusText));
        }
    } catch(e) {
        console.error("Delete show error", e);
        alert("Failed to delete show.");
    }
}

async function renderManageMoviesList() {
    try {
        const res = await fetch('/api/movies');
        const movies = await res.json();
        const container = document.getElementById('manageMoviesList');
        if(!container) return;
        
        if (movies.length === 0) {
            container.innerHTML = '<p style="color: var(--text-secondary);">No movies created yet.</p>';
            return;
        }

        container.innerHTML = movies.map(m => `
            <div style="display: flex; align-items: center; justify-content: space-between; padding: 1rem; background: rgba(255,255,255,0.05); border-radius: 8px;">
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <img src="${m.posterUrl}" onerror="this.src='https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400'" style="width: 50px; height: 75px; object-fit: cover; border-radius: 4px;">
                    <div>
                        <div style="font-weight: 600; font-size: 1.1rem;">${m.title}</div>
                        <div style="font-size: 0.85rem; color: var(--text-secondary);">${m.language} • ${m.genre} • ${m.durationMinutes} mins</div>
                    </div>
                </div>
                <div>
                    <button class="btn-primary" style="background: #ef4444;" onclick="deleteMovie(${m.id})">Delete</button>
                </div>
            </div>
        `).join('');
    } catch (e) {
        console.error("Error loading manage movies list", e);
    }
}

async function deleteMovie(id) {
    if(!confirm("Are you sure you want to delete this movie? This cannot be undone.")) return;
    
    try {
        const res = await fetch('/api/movies/' + id, { method: 'DELETE' });
        if(res.ok) {
            alert("Movie deleted!");
            renderManageMoviesList();
            populateAdminMovieSelect();
        } else {
            alert("Error deleting movie. It may be linked to scheduled shows.");
        }
    } catch (e) {
        console.error("Delete error", e);
    }
}

// --- Customer My Bookings ---
async function loadMyBookings() {
    const list = document.getElementById('myBookingsList');
    if (!list || !loggedInUser) return;
    list.innerHTML = '<div style="color: var(--text-secondary); padding: 1rem;">Loading your bookings...</div>';

    try {
        const res = await fetch(`/api/bookings/customer/${loggedInUser.userId}`);
        const bookings = await res.json();

        if (!bookings || bookings.length === 0) {
            list.innerHTML = `
                <div class="glass-panel" style="padding: 2.5rem; text-align: center;">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🎟️</div>
                    <h3 style="margin-bottom: 0.5rem; color: #fff;">No Bookings Found</h3>
                    <p style="color: var(--text-secondary); font-size: 0.9rem; margin-bottom: 1.5rem;">You haven't booked any movie tickets yet.</p>
                    <button class="btn-primary" onclick="switchView('bookingView')">Browse Movies & Book Tickets</button>
                </div>
            `;
            return;
        }

        list.innerHTML = bookings.map(b => {
            const dateStr = b.showTime ? new Date(b.showTime).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' }) : 'Scheduled Show';
            const statusColor = b.status === 'CONFIRMED' ? 'var(--accent-green)' : (b.status === 'PENDING' ? 'var(--accent-gold)' : 'var(--accent-red)');
            const poster = b.posterUrl || 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400';
            
            return `
                <div class="glass-panel" style="display: flex; gap: 1.5rem; padding: 1.25rem; border-radius: 12px; align-items: center; justify-content: space-between; flex-wrap: wrap;">
                    <div style="display: flex; gap: 1.25rem; align-items: center;">
                        <img src="${poster}" onerror="this.src='https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=400'" style="width: 70px; height: 100px; object-fit: cover; border-radius: 8px; border: 1px solid var(--border-color);" alt="${b.movieTitle || 'Movie'}">
                        <div>
                            <div style="display: flex; gap: 0.5rem; align-items: center; margin-bottom: 0.25rem;">
                                <h3 style="font-size: 1.15rem; color: #fff;">${b.movieTitle || 'Movie Ticket'}</h3>
                                <span style="font-size: 0.7rem; padding: 0.15rem 0.5rem; border-radius: 4px; background: rgba(255,255,255,0.1); color: var(--accent-blue);">${b.language || 'Cinema'}</span>
                            </div>
                            <div style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 0.3rem;">
                                🏢 <strong>${b.theatreName || 'Theatre'}</strong> (${b.theatreCity || ''}) • ${b.screenName || 'Main Screen'}
                            </div>
                            <div style="font-size: 0.85rem; color: #fff; margin-bottom: 0.3rem;">
                                🕒 ${dateStr}
                            </div>
                            <div style="font-size: 0.85rem; color: var(--accent-gold);">
                                Seats: <strong>${b.seatsCsv}</strong>
                            </div>
                        </div>
                    </div>

                    <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 0.5rem;">
                        <span style="font-size: 0.8rem; font-weight: 700; padding: 0.25rem 0.75rem; border-radius: 20px; background: rgba(255,255,255,0.05); color: ${statusColor}; border: 1px solid ${statusColor};">
                            ● ${b.status}
                        </span>
                        <div style="font-size: 1.1rem; font-weight: 800; color: var(--accent-green);">
                            ₹${parseFloat(b.totalAmount).toFixed(2)}
                        </div>
                        <div style="font-size: 0.75rem; color: var(--text-secondary);">
                            Ref: <code>${b.bookingReference}</code>
                        </div>
                        <button class="btn-primary" style="padding: 0.4rem 0.8rem; font-size: 0.8rem; margin-top: 0.3rem;" onclick="viewPastBookingTicket('${b.bookingReference}')">
                            🎫 View Ticket
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    } catch (e) {
        console.error("Failed to load customer bookings", e);
        list.innerHTML = '<div style="color: var(--accent-red); padding: 1rem;">Failed to load your bookings.</div>';
    }
}

async function viewPastBookingTicket(bookingRef) {
    try {
        const res = await fetch(`/api/bookings/${bookingRef}`);
        const data = await res.json();
        if (res.ok) {
            const b = data.booking;
            const t = data.ticket;
            
            const showRes = await fetch('/api/shows');
            const shows = await showRes.json();
            const show = shows.find(s => s.id === b.showId) || {};

            document.getElementById('ticketMovie').textContent = show.movieTitle || 'Movie Ticket';
            document.getElementById('ticketTheatre').textContent = show.theatreName || b.tenantId;
            document.getElementById('ticketScreen').textContent = show.screenName || 'Screen 1';
            document.getElementById('ticketTime').textContent = show.startTime ? new Date(show.startTime).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' }) : 'Confirmed';
            document.getElementById('ticketSeats').textContent = b.seatsCsv;
            document.getElementById('ticketBookingRef').textContent = b.bookingReference;
            document.getElementById('ticketRef').textContent = t ? t.ticketReference : 'TKT-' + b.bookingReference;
            document.getElementById('ticketTotal').textContent = `₹${parseFloat(b.totalAmount).toFixed(2)}`;

            document.getElementById('ticketModal').style.display = 'flex';
        } else {
            alert("Could not load ticket details.");
        }
    } catch(e) {
        console.error(e);
    }
}

function printTicket() {
    window.print();
}
