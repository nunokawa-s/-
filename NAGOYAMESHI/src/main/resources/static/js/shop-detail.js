document.addEventListener('DOMContentLoaded', function() {
    const body = document.querySelector('body');
    const openingHoursMap = JSON.parse(body.dataset.openingHours);
    const reserveDateInput = document.querySelector('#fromDateToReserve');
    const reserveTimeSelect = document.querySelector('#reserveTime');

    flatpickr(reserveDateInput, {
        dateFormat: 'Y-m-d',
        locale: 'ja',
        minDate: 'today',
        onChange: function(dateStr) { // selectedDates と instance を削除
            populateTimeOptions(dateStr);
        }
    });

    function populateTimeOptions(selectedDate) {
        reserveTimeSelect.innerHTML = '<option value="">時間を選択</option>';
        const dayOfWeek = getDayOfWeek(selectedDate);
        if (openingHoursMap && openingHoursMap[dayOfWeek]) {
            const hoursRange = openingHoursMap[dayOfWeek];
            const [startTime, endTime] = hoursRange.split('-');
            if (startTime && endTime) {
                const startHour = parseInt(startTime.split(':')[0]);
                const endHour = parseInt(endTime.split(':')[0]);
                for (let hour = startHour; hour < endHour; hour++) {
                    const formattedHour = String(hour).padStart(2, '0');
                    reserveTimeSelect.innerHTML += `<option value="${formattedHour}:00">${formattedHour}:00</option>`;
                    reserveTimeSelect.innerHTML += `<option value="${formattedHour}:30">${formattedHour}:30</option>`;
                }
                const endMinute = parseInt(endTime.split(':')[1]);
                const formattedEndHour = String(endHour).padStart(2, '0');
                if (endMinute > 0) {
                    reserveTimeSelect.innerHTML += `<option value="${formattedEndHour}:${String(endMinute).padStart(2, '0')}">${formattedEndHour}:${String(endMinute).padStart(2, '0')}</option>`;
                }
            }
        } else {
            reserveTimeSelect.innerHTML += '<option value="" disabled>本日は休業です</option>';
        }
    }

    function getDayOfWeek(dateString) {
        const date = new Date(dateString);
        const dayIndex = date.getDay(); // 0:日, 1:月, ..., 6:土
        const daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
        return daysOfWeek[dayIndex];
    }
});