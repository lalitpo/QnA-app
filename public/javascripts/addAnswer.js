

const answerSubmitButton = document.getElementById('submitAnswer');

answerSubmitButton.addEventListener('click', function() {
    const currentDate = new Date();
    const formattedDate = currentDate.toISOString();

    const hiddenDateInput = document.createElement('input');
    hiddenDateInput.type = 'hidden';
    hiddenDateInput.name = 'submissionDate';
    hiddenDateInput.value = formattedDate;

    const answerForm = document.getElementById('answerForm');
    answerForm.appendChild(hiddenDateInput);
});