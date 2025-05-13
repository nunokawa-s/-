/**
 * 
 */
// star-rating.js
document.addEventListener('DOMContentLoaded', function () {
    const stars = document.querySelectorAll('.star');
    const ratingInput = document.getElementById('rating');

    stars.forEach(star => {
        star.addEventListener('mouseover', function () {
            const value = parseInt(star.getAttribute('data-value'));
            highlightStars(value);
        });

        star.addEventListener('mouseout', function () {
            const currentValue = parseInt(ratingInput.value);
            highlightStars(currentValue);
        });

        star.addEventListener('click', function () {
            const value = parseInt(star.getAttribute('data-value'));
            ratingInput.value = value; // 選択された評価をhiddenのinputにセット
            highlightStars(value);
        });
    });

    
});