import { apiFetch } from "./api";

export const homeService = {
    getAll: () => apiFetch("/api/home/admin"),
    create: (data) => apiFetch("/api/home", { method: "POST", body: JSON.stringify(data) }),
    update: (id, data) => apiFetch(`/api/home/${id}`, { method: "PUT", body: JSON.stringify(data) }),
}