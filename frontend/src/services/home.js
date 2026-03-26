import { apiFetch } from "./api";

export const homeService = {
    getAll: () => apiFetch("/api/home/admin"),

    create: (data) => 
        apiFetch("/api/home/admin", { 
            method: "POST", 
            body: JSON.stringify(data) 
        }),

    update: (id, data) => 
        apiFetch(`/api/home/admin/${id}`, { 
            method: "PUT", 
            body: JSON.stringify(data) 
        }),

    delete: (id) => apiFetch(`/api/home/admin/${id}`, { method: "DELETE" }),
}