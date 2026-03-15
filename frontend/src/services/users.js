import { apiFetch } from "./api";

export const usersService = {
    getAll: () => apiFetch("/api/users"),

    getById: (userId) => apiFetch(`/api/users/${userId}`),

    create: (userData) =>
        apiFetch("/api/users", {
            method: "POST",
            body: JSON.stringify(userData),
        }),

    update: (id, data) =>
        apiFetch(`/api/users/${id}`, {
            method: "PUT",
            body: JSON.stringify(data),
        }),
};
