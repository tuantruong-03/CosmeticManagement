document.addEventListener("DOMContentLoaded", async function(event) {
    const productsElement = document.getElementById("products-by-category");
    const categoryNameElement = document.getElementById("category-name");
    const categoryName = categoryNameElement.getAttribute("data-categoryName");
    const totalProducts = categoryNameElement.getAttribute("data-totalProductsByCategory");
    const pageNext = document.querySelector("#page-next");
    const pagePrevious = document.querySelector("#page-previous");

    const pageSize = 12;
    let currentPage = 0;
    let totalPage = generatePageButtons(totalProducts, pageSize);
    let pageNumber = document.querySelectorAll(".page-number");

    // Check if URL contains page parameter
    // If it has, update the currentPage
    const urlParams = new URLSearchParams(window.location.search);
    const pageParam = urlParams.get('page');
    if (pageParam !== null) {
        currentPage = parseInt(pageParam) - 1; // Subtract 1 because page index starts from 0
    }

	  // Variable to store fetched data
    let fetchedProducts = await fetchData();
    
    handlePagination();

    // Fetch data from server
    async function fetchData() {
        const response = await fetch(`/api/products?category_name=${categoryName}`);
        const data = await response.json();
        return data;
    }

    // Create page buttons
    function generatePageButtons(totalProducts, pageSize) {
        window.scroll(0, 0);

        let totalPage = Math.ceil(totalProducts / pageSize);
        const pagination = document.getElementById('products-by-category-pagination');
        let html = "";

        for (let i = 0; i < totalPage; ++i) {
            html += `<li class="page-item">
                        <button class="page-link page-btn-size page-number" >${i + 1}</button>
                    </li>`
        }

        pagination.lastElementChild.insertAdjacentHTML('beforebegin', html);
        return totalPage;
    }

    async function handlePageClick(pageNumber) {
        const startIndex = pageNumber * pageSize;
        const endIndex = Math.min(startIndex + pageSize, totalProducts);
        const productsOnPage = fetchedProducts.slice(startIndex, endIndex);

        let html = "";
        for (let product of productsOnPage) {
            html += `<div class="col-6 col-md-6 col-lg-3 mb-3">
                            <div class="card position-relative">
                                <div class="position-absolute top-0 end-0 mt-2 me-2">
                                    <i class="bi bi-heart" style="font-size: 1.5rem;"></i>
                                </div>
                                <div class="position-absolute top-0 end-0 mt-icon me-2">
                                    <i class="bi bi-cart" style="font-size: 1.5rem;"></i>
                                </div>
                                <a class="card-product-img" href="/product/${product.id}">
                                    <img src="${product.imageLink}" class="card-img-top" />
                                </a>
                                <div class="card-body">
                                    <h5 class="card-title text-overflow-ellipsis">${product.name}</h5>
                                    <p class="card-text fst-italic lead text-nowrap text-overflow-ellipsis"
                                        style="font-size: 16px" >
                                        ${product.description}
                                    </p>
                                    <p class="card-text">$${product.price}</p>
                                    <a href="#" class="btn btn-primary">Buy Now</a>
                                </div>
                            </div>
                        </div>`
        }

        productsElement.insertAdjacentHTML("afterbegin", html);
        window.scroll(0, 0);
    }

    async function handlePagination() {
        await handlePageClick(currentPage);

        pageNumber = Array.from(pageNumber);

        for (const page of pageNumber) {
            page.addEventListener("click", async function(event) {
                const currentPage = pageNumber.indexOf(event.currentTarget); //Get index of clickedPage
                pageNumber[currentPage].focus();
                await handlePageClick(currentPage);
                updateURL(currentPage + 1); // Update URL with page number
            })
        }
    }

    // Update URL with page number
    function updateURL(pageNumber) {
        const newURL = window.location.pathname + '?page=' + pageNumber;
        history.pushState({page: pageNumber}, '', newURL);
    }
});
