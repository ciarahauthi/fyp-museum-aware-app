import { apiFetch } from "./api";

export const categoriesService = {
    getAll: () => apiFetch("/api/categories"),

    getById: (id) => apiFetch(`/api/categories/${id}`),

    create: (data) =>
        apiFetch("/api/categories", {
            method: "POST",
            body: JSON.stringify(data),
        }),

    update: (id, data) =>
        apiFetch(`/api/categories/${id}`, {
            method: "PUT",
            body: JSON.stringify(data),
        }),

    delete: (id) =>
        apiFetch(`/api/categories/${id}`, { method: "DELETE" }),
};
