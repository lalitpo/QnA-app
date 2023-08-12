document.addEventListener("DOMContentLoaded", function() {
    const dynamicDateElements = document.querySelectorAll(".dynamic-date");

    dynamicDateElements.forEach(element => {
        const uploadDate = element.closest(".post").getAttribute("data-upload-date");
        element.textContent = formatDate(uploadDate);
    });

    function formatDate(dateString) {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(dateString).toLocaleDateString(undefined, options);
    }

    const sortingSelect = document.getElementById("sorting");
    const postsContainer = document.querySelector('.posts-container');

    sortingSelect.addEventListener("change", function() {
        const sortingCriteria = sortingSelect.value;
        sortPosts(postsContainer, sortingCriteria);
    });

    function sortPosts(container, criteria) {
        const posts = Array.from(container.querySelectorAll(".post"));

        posts.sort((a, b) => {
            if (criteria === "date") {
                const dateA = new Date(a.dataset.uploadDate);
                const dateB = new Date(b.dataset.uploadDate);
                return dateB - dateA;
            } else if (criteria === "votes") {
                const votesA = parseInt(a.querySelector(".votes").textContent);
                const votesB = parseInt(b.querySelector(".votes").textContent);
                return votesB - votesA;
            }
        });

        posts.forEach(post => {
            container.appendChild(post);
        });
    }

});