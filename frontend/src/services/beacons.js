import { apiFetch } from "./api";

export const beaconsService = {
    getAll: () => apiFetch("/api/beacons"),

    getById: (id) => apiFetch(`/api/beacons/${id}`),

    create: (data) =>
        apiFetch("/api/beacons", {
            method: "POST",
            body: JSON.stringify(data),
        }),

    update: (id, data) =>
        apiFetch(`/api/beacons/${id}`, {
            method: "PUT",
            body: JSON.stringify(data),
        }),

    delete: (id) =>
        apiFetch(`/api/beacons/${id}`, {
            method: "DELETE",
        }),
};
