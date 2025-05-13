/**
 * 
 */
let maxDate = new Date();
maxDate = maxDate.setMonth(maxDate.getMonth() + 3);
  document.addEventListener("DOMContentLoaded", function() {

//日付選択
flatpickr('#fromDateToReserve', {
	  mode: "single", 
	locale: 'ja',
	minDate: 'today',
});

// 時間選択
flatpickr("#fromTimeToReserve", {
  enableTime: true,
  noCalendar: true,
  dateFormat: "H:i",
});

});