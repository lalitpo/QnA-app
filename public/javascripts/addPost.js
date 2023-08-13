const tagsContainer = document.getElementById('tagsContainer');
const tagsInput = document.getElementById('tagsInput');

// Function to create a tag element
function createTagElement(text) {
    const tagElement = document.createElement('div');
    tagElement.classList.add('add-post-tag');
    tagElement.textContent = text;

    const removeTagButton = document.createElement('span');
    removeTagButton.classList.add('remove-tag');
    removeTagButton.textContent = 'x';
    removeTagButton.addEventListener('click', () => {
        tagsContainer.removeChild(tagElement);
    });

    tagElement.appendChild(removeTagButton);
    return tagElement;
}

tagsInput.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        const currentValue = tagsInput.value.trim();
        if (currentValue) {
            const tagElement = createTagElement(currentValue);
            tagsContainer.appendChild(tagElement);
            tagsInput.value = '';
        }
    }
});

tagsContainer.addEventListener('mouseover', function(event) {
    if (event.target.classList.contains('add-post-tag')) {
        const removeTagButton = event.target.querySelector('.remove-tag');
        if (removeTagButton) {
            removeTagButton.style.display = 'inline';
        }
    }
});

tagsContainer.addEventListener('mouseout', function(event) {
    if (event.target.classList.contains('add-post-tag')) {
        const removeTagButton = event.target.querySelector('.remove-tag');
        if (removeTagButton) {
            removeTagButton.style.display = 'none';
        }
    }
});

tagsContainer.addEventListener('click', function(event) {
    if (event.target.classList.contains('remove-tag')) {
        const tagElement = event.target.closest('.add-post-tag');
        if (tagElement) {
            tagsContainer.removeChild(tagElement);
        }
    }
});





const postSubmitButton = document.getElementById('submitBtn');
const myForm = document.getElementById('postForm');


postSubmitButton.addEventListener('click', function(event) {
    // Prevent the default form submission
    event.preventDefault();

    // Remove the 'required' attribute from the tags input
    tagsInput.removeAttribute('required');

    // Submit the form
    myForm.submit();
});